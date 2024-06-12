package adaptertool.adapter;

import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class FileModifier {

    private static final String SERVLET_COMPONENT_SCAN_ANNOTATION_Full = "org.springframework.boot.web.servlet.ServletComponentScan";
    private static final String ENABLE_DISCOVERY_CLIENT_ANNOTATION_Full = "org.springframework.cloud.client.discovery.EnableDiscoveryClient";
    private static final String SERVLET_COMPONENT_SCAN_ANNOTATION = "ServletComponentScan";
    private static final String ENABLE_DISCOVERY_CLIENT_ANNOTATION = "EnableDiscoveryClient";
    private static final String FILTER_PACKAGE = "com.septemberhx.common.filter";
    private static final String SERVICE_POM_SUFFIX = "service";
    private static final String CONFIG_FOLDER_NAME = "config";
    private static final String AUTO_CONFIG_FILE_NAME = "LoggableAutoConfiguration.java";

    public static void modifyJavaFiles(List<File> javaFiles) {
        for (File javaFile : javaFiles) {
            modifyFile(javaFile);
        }
    }
    public static void modifyPomFiles(List<File> pomFiles) throws XmlPullParserException, IOException {
        for (File pomFile : pomFiles) {
            modifyPomFile(pomFile);
        }
    }


    private static void modifyFile(File javaFile) {
        try {
            String javaFilePath = javaFile.getAbsolutePath();
            Charset charset = Charset.forName("UTF-8");
            CompilationUnit cu = StaticJavaParser.parse(new File(javaFilePath), charset);
            System.out.println(cu.getClass());
            List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

            for (ClassOrInterfaceDeclaration clazz : classes) {
                if (isControllerClass(clazz)) {
                    addImportsAndExtends(cu, clazz);
                    addMRestApiTypeAnnotation(clazz);
                }
                if (isReposityInterface(clazz)) {
                    addLoggableAnnotation(cu, clazz);
                }
                if (isServiceClass(clazz)) {
                    processMethods(clazz);
                }

                // Check if this is the Spring Boot application class
                if (isSpringBootApplicationClass(clazz)) {
                    addSpringBootAnnotations(clazz, cu);
                    // 获取启动类所在路径
                    String startupClassPath = javaFile.getParent();
                    File configFolder = new File(startupClassPath + File.separator + CONFIG_FOLDER_NAME);
                    if (!configFolder.exists()) {
                        // 创建config文件夹
                        boolean created = configFolder.mkdirs();
                        if (created) {
                            System.out.println("config文件夹已创建");
                        } else {
                            System.out.println("无法创建config文件夹");
                            return;
                        }
                    }
                    String cofigPath = startupClassPath + File.separator + CONFIG_FOLDER_NAME;
                    String autoConfigFilePath = cofigPath + File.separator + AUTO_CONFIG_FILE_NAME;
                    System.out.println("autoConfigFilePath"+autoConfigFilePath);
                    int javaIndex = autoConfigFilePath.indexOf("java" + File.separator);
                    if (javaIndex != -1) {
                        String relativePath = cofigPath.substring(javaIndex + 5);


                        File autoConfigFile = new File(autoConfigFilePath);
                        if (!autoConfigFile.exists()) {
                            // 创建LoggableAutoConfiguration.java文件
                            try {
                                FileWriter writer = new FileWriter(autoConfigFile);
                                writer.write(getAutoConfigurationContent(relativePath));
                                writer.close();
                                System.out.println("LoggableAutoConfiguration.java文件已创建");
                            } catch (IOException e) {
                                System.out.println("无法创建LoggableAutoConfiguration.java文件");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("LoggableAutoConfiguration.java文件已存在");
                        }
                    }
                }
            }


            String modifiedContent = cu.toString();
            writeModifiedFile(javaFile, modifiedContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getAutoConfigurationContent(String relativePath) {
        relativePath = relativePath.replace(File.separator, ".");

        return "package " + relativePath + ";\n\n" +
                "import com.mbs.mclient.aspect.LoggingAspect;\n" +
                "import org.springframework.context.annotation.Bean;\n" +
                "import org.springframework.context.annotation.Configuration;\n" +
                "import org.springframework.context.annotation.EnableAspectJAutoProxy;\n\n" +
                "@Configuration\n" +
                "@EnableAspectJAutoProxy\n" +
                "public class LoggableAutoConfiguration {\n\n" +
                "    @Bean\n" +
                "    public LoggingAspect loggingAspect() {\n" +
                "        return new LoggingAspect();\n" +
                "    }\n" +
                "}";
    }

    private static void modifyPomFile(File pomFile) throws IOException, XmlPullParserException {
        String POM_FILE_PATH = pomFile.getAbsolutePath();
        FileReader fileReader = new FileReader(POM_FILE_PATH);
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(fileReader);
        fileReader.close();

        boolean isServicePom = false;
        String artifactId = model.getArtifactId();
        if (artifactId.endsWith(SERVICE_POM_SUFFIX)) {
            isServicePom = true;
        }

        if (isServicePom) {
            Dependency mclientDependency = createDependency("MBS", "MClient", "1.0.1", "compile");
            Dependency commonDependency = createDependency("MBS", "common", "1.0-SNAPSHOT", "compile");
            Dependency springBootDependency = createDependency("org.springframework.boot", "spring-boot-configuration-processor", null, null, "true");

            model.addDependency(mclientDependency);
            model.addDependency(commonDependency);
            model.addDependency(springBootDependency);

            FileWriter fileWriter = new FileWriter(POM_FILE_PATH);
            MavenXpp3Writer writer = new MavenXpp3Writer();
            writer.write(fileWriter, model);
            fileWriter.close();
        }
    }





    private static boolean isControllerClass(ClassOrInterfaceDeclaration clazz) {
        return clazz.getAnnotationByName("Controller").isPresent()
                || clazz.getAnnotationByName("RestController").isPresent();
    }

    private static boolean isReposityInterface(ClassOrInterfaceDeclaration clazz) {
        String interfaceName = clazz.getNameAsString();
        return interfaceName.endsWith("Repository");
    }

    private static boolean isServiceClass(ClassOrInterfaceDeclaration clazz) {
        for (AnnotationExpr annotation : clazz.getAnnotations()) {
            if (annotation instanceof MarkerAnnotationExpr) {
                MarkerAnnotationExpr markerAnnotation = (MarkerAnnotationExpr) annotation;
                if (markerAnnotation.getName().asString().equals("Service")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void processMethods(ClassOrInterfaceDeclaration clazz) {
        // 遍历类中的方法
        for (MethodDeclaration method : clazz.getMethods()) {
            // 遍历方法的参数
            for (Parameter parameter : method.getParameters()) {
                // 检查参数类型是否为 HttpHeaders
                if (isHttpHeadersParameter(parameter)) {
                    // 获取参数名
                    NameExpr parameterName = new NameExpr(parameter.getNameAsString());
                    // 在方法体的第一行添加参数名.remove("Host"); 的代码
                    method.getBody().ifPresent(body -> {
                        body.getStatements().add(0, new ExpressionStmt(
                                new MethodCallExpr(parameterName, "remove").addArgument("\"Host\"")
                        ));
                    });
                }
            }
        }
    }

    private static boolean isHttpHeadersParameter(Parameter parameter) {
        // 检查参数类型是否为 HttpHeaders
        return parameter.getType().isClassOrInterfaceType()
                && ((ClassOrInterfaceType) parameter.getType()).getName().asString().equals("HttpHeaders");
    }
    private static void addLoggableAnnotation(CompilationUnit cu, ClassOrInterfaceDeclaration clazz) {
        // 添加 import 语句
        ImportDeclaration importDeclaration = new ImportDeclaration("com.mbs.mclient.annotation.Loggable", false, false);
        cu.addImport(importDeclaration);

        // 添加 @Loggable 注解到接口方法
        for (MethodDeclaration method : clazz.getMethods()) {
            AnnotationExpr loggableAnnotation = new MarkerAnnotationExpr(
                    String.valueOf(new ClassOrInterfaceType("Loggable"))
            );
            method.addAnnotation(loggableAnnotation);
        }
    }
    private static void addImportsAndExtends(CompilationUnit cu, ClassOrInterfaceDeclaration clazz) {
        cu.addImport("com.mbs.mclient.annotation.MRestApiType");
        cu.addImport("com.mbs.mclient.base.MObject");
        clazz.addExtendedType(new ClassOrInterfaceType("MObject"));
    }

    private static void addMRestApiTypeAnnotation(ClassOrInterfaceDeclaration clazz) {
        for (MethodDeclaration method : clazz.getMethods()) {
            method.addAnnotation(new MarkerAnnotationExpr("MRestApiType"));
        }
    }

    private static boolean isSpringBootApplicationClass(ClassOrInterfaceDeclaration clazz) {
        Optional<MethodDeclaration> mainMethod = clazz.findFirst(MethodDeclaration.class, m ->
                m.isPublic()
                        && m.isStatic()
                        && m.getType().equals(VoidType.class)
                        && m.getNameAsString().equals("main")
                        && m.getParameters().size() == 1
                        && m.getParameter(0).getType().asString().equals("String[]"));

        if (mainMethod.isPresent()) {
            return true;
        } else {
            // 检查类中是否直接定义了 main 方法
            for (BodyDeclaration<?> member : clazz.getMembers()) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration method = (MethodDeclaration) member;
                    if (method.isPublic()
                            && method.isStatic()
                            && method.getType().getClass().equals(VoidType.class)
                            && method.getNameAsString().equals("main")
                            && method.getParameters().size() == 1
                            && method.getParameter(0).getType().asString().equals("String[]")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static void addSpringBootAnnotations(ClassOrInterfaceDeclaration clazz, CompilationUnit cu) {
        cu.addImport(SERVLET_COMPONENT_SCAN_ANNOTATION_Full);
        cu.addImport(ENABLE_DISCOVERY_CLIENT_ANNOTATION_Full);
        clazz.addAnnotation(createAnnotationExpr(SERVLET_COMPONENT_SCAN_ANNOTATION, FILTER_PACKAGE));
        clazz.addAnnotation(createAnnotationExpr(ENABLE_DISCOVERY_CLIENT_ANNOTATION));
        System.out.println("Added Spring Boot annotations to " + clazz.getNameAsString());
    }
    private static AnnotationExpr createAnnotationExpr(String annotationName, String... values) {
        if (values.length == 0) {
            return new MarkerAnnotationExpr(new Name(annotationName));
        } else if (values.length == 1) {
            return new SingleMemberAnnotationExpr(new Name(annotationName), new StringLiteralExpr(values[0]));
        } else {
            NodeList<MemberValuePair> memberValuePairs = NodeList.nodeList();
            for (int i = 0; i < values.length; i += 2) {
                String name = values[i];
                String value = values[i + 1];
                memberValuePairs.add(new MemberValuePair(name, new StringLiteralExpr(value)));
            }
            return new NormalAnnotationExpr(new Name(annotationName), memberValuePairs);
        }
    }

    private static Dependency createDependency(String groupId, String artifactId, String version, String scope) {
        Dependency dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        dependency.setScope(scope);
        return dependency;
    }

    private static Dependency createDependency(String groupId, String artifactId, String version, String scope, String optional) {
        Dependency dependency = createDependency(groupId, artifactId, version, scope);
        dependency.setOptional(optional);
        return dependency;
    }

    private static void writeModifiedFile(File javaFile, String modifiedContent) {
        System.out.println(javaFile.toString());
        FileWriter writer = null;
        try {
            writer = new FileWriter(javaFile);
            writer.write(modifiedContent);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}