package com.smelldetection.service;

import com.smelldetection.base.context.CyclicReferenceContext;
import com.smelldetection.base.factory.FileFactory;
import com.smelldetection.base.item.RequestItem;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import lombok.NoArgsConstructor;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


/**
 *
 */
@Service
@NoArgsConstructor
public class CyclicReferenceService {
    public FileFactory fileFactory;
    public Map<String, Set<String>> extensionAndImplementation;
    public List<String> projectClasses;
    public static final String objectClassName = "java.lang.Object";
    public List<String> projectNames;
    private CyclicReferenceContext context;

    @Autowired
    public CyclicReferenceService(FileFactory fileFactory){
        this.fileFactory=fileFactory;
    }

    private String resolveProjectNames(List<String> applicationYamlOrPropertities) throws IOException, XmlPullParserException {
        String serviceName = "";
        for (String app : applicationYamlOrPropertities) {
            if (app.endsWith("yaml") || app.endsWith("yml")) {
                Yaml yaml = new Yaml();
                Map map = yaml.load(new FileInputStream(app));
                Map m1 = (Map) map.get("spring");
                Map m2 = (Map) m1.get("application");
                serviceName = (String) m2.get("name");
            } else {
                InputStream in = new BufferedInputStream(new FileInputStream(app));
                Properties p = new Properties();
                p.load(in);
                serviceName = (String) p.get("spring.application.name");
            }

        }
        return serviceName;

    }
    public void resolvePackageName(List<String> pomFilePaths) throws IOException, XmlPullParserException {
        for (String pomFilePath : pomFilePaths) {
            final File pomFile = new File(pomFilePath);
            MavenXpp3Reader mavenReader = new MavenXpp3Reader();
            Model mavenModel = mavenReader.read(new FileReader(pomFile));
            String groupId=mavenModel.getParent()==null?mavenModel.getGroupId():mavenModel.getParent().getGroupId();
            if(groupId=="")
                projectNames.add(mavenModel.getArtifactId());
            else if(mavenModel.getArtifactId() =="")
                projectNames.add(groupId);
            else projectNames.add(groupId+"."+mavenModel.getArtifactId());
        }
    }

    private void resolveExtensionAndImplementation(String serviceName, List<String> javaFilePaths) throws FileNotFoundException, ClassNotFoundException {
        TypeSolver typeSolver = new CombinedTypeSolver();
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.setConfiguration(new ParserConfiguration().setSymbolResolver(symbolSolver));
        for (String filePath : javaFilePaths) {
            CompilationUnit cu = StaticJavaParser.parse(new File(filePath));
            for (TypeDeclaration<?> typeDeclaration : cu.getTypes()) {
                //get class extension
                String fullClassName = typeDeclaration.getFullyQualifiedName().isPresent() ? (String) typeDeclaration.getFullyQualifiedName().get() : null;
                if (fullClassName != null) {
                    //build a list containing all the full names of classes in this project
                    projectClasses.add(fullClassName);
                }
            }
        }
        for (String filePath : javaFilePaths) {
            CompilationUnit cu = StaticJavaParser.parse(new File(filePath));
            for (TypeDeclaration<?> typeDeclaration:cu.getTypes()) {
                //get class extension
                String fullClassName=typeDeclaration.getFullyQualifiedName().isPresent()? (String) typeDeclaration.getFullyQualifiedName().get() :null;
                if (fullClassName!=null){
                    //build a list containing all the full names of classes in this project
                   // projectClasses.add(fullClassName);
                    if(typeDeclaration.getClass().getName().equals("com.github.javaparser.ast.body.ClassOrInterfaceDeclaration")) {
                        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) typeDeclaration;
                        for (ClassOrInterfaceType classOrInterfaceType : classOrInterfaceDeclaration.getExtendedTypes()) {
                            for (String name : projectClasses) {
                                String[] str = name.split("\\.");
                                if (str[str.length - 1].equals(classOrInterfaceType.getName().asString())) {
                                    if (!extensionAndImplementation.containsKey(name)) {

                                        extensionAndImplementation.put(name, new HashSet<>());
                                    }
                                    extensionAndImplementation.get(name).add(fullClassName);
                                    break;
                                }
                            }
                        }
                        for (ClassOrInterfaceType classOrInterfaceType : classOrInterfaceDeclaration.getImplementedTypes()) {
                            for (String name : projectClasses) {
                                String[] str = name.split("\\.");
                                if (str[str.length - 1].equals(classOrInterfaceType.getName().asString())) {
                                    if (!extensionAndImplementation.containsKey(name)) {
                                        extensionAndImplementation.put(name, new HashSet<>());
                                    }
                                    extensionAndImplementation.get(name).add(fullClassName);
                                    break;
                                }
                            }
                        }
                        for (Node node : classOrInterfaceDeclaration.getChildNodes()) {
                            if (node.getClass().getCanonicalName().equals("com.github.javaparser.ast.body.ClassOrInterfaceDeclaration")) {
                                ClassOrInterfaceDeclaration c = (ClassOrInterfaceDeclaration) node;
                                for (ClassOrInterfaceType extendClass : c.getExtendedTypes()) {
                                    if (extendClass.getName().asString().equals(classOrInterfaceDeclaration.getName().asString())) {
                                        int length = classOrInterfaceDeclaration.getName().asString().length();
                                        String innerFullName = fullClassName.substring(0, fullClassName.length() - length) + c.getName().asString();
                                        context.addCyclicReference(serviceName, fullClassName, innerFullName);
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    public CyclicReferenceContext getCyclicReference(RequestItem request) throws IOException, ClassNotFoundException, XmlPullParserException {
        this.context=new CyclicReferenceContext();
        String path = request.getServicesPath();
        List<String> servicesPath = fileFactory.getServicePaths(path);
        for(String servicePath: servicesPath) {
            this.extensionAndImplementation=new HashMap<>();
            this.projectClasses=new ArrayList<>();
            this.projectNames=new ArrayList<>();
            String servicesDirectory = new File(servicePath).getAbsolutePath();
            List<String> applicationYamlOrPropertities = fileFactory.getApplicationYamlOrPropertities(servicePath);
            if(applicationYamlOrPropertities.size() == 0) continue;
            String serviceName = resolveProjectNames(applicationYamlOrPropertities);
            List<String> javaFilePaths = fileFactory.getJavaFiles(servicesDirectory);
            String packageName = fileFactory.getPackageName(servicesDirectory);
            resolveExtensionAndImplementation(serviceName, javaFilePaths);

            for (String filePath : javaFilePaths) {
                CompilationUnit cu = StaticJavaParser.parse(new File(filePath));
                String publicClassDeclared = null;
                //usually there is only one classOrInterfaceDeclaration in every java file
                for (TypeDeclaration<?> type : cu.getTypes()) {
                    if (type.isClassOrInterfaceDeclaration()) {
                        if (type.asClassOrInterfaceDeclaration().getFullyQualifiedName().isPresent())
                            publicClassDeclared = type.asClassOrInterfaceDeclaration().getFullyQualifiedName().get();
                    }
                }
                if (!extensionAndImplementation.containsKey(publicClassDeclared)) continue;
                final NodeList<ImportDeclaration> imports = cu.getImports();
                List<String> importedClassNames = imports.stream().map(ImportDeclaration::getNameAsString)
                        .filter(name -> name.startsWith(packageName)).collect(Collectors.toList());
                List<String> importedClassesWithStar = new ArrayList<>();
                //process import statements without *
                for (String importedClassName : importedClassNames) {
                    if (importedClassName.endsWith("*")) {
                        List<String> starClasses = projectClasses.stream()
                                .filter(className -> className.startsWith(importedClassName.substring(0, importedClassName.length() - 3)))
                                .collect(Collectors.toList());
                        importedClassesWithStar.addAll(starClasses);
                    }
                    if (extensionAndImplementation.get(publicClassDeclared).contains(importedClassName)) {
                        context.addCyclicReference(serviceName, publicClassDeclared, importedClassName);
                    }
                }
                //process import statements with *
                for (String importedClassWithStar : importedClassesWithStar) {
                    if (extensionAndImplementation.get(publicClassDeclared).contains(importedClassWithStar)) {
                        context.addCyclicReference(serviceName, publicClassDeclared, importedClassWithStar);
                    }
                }
            }
        }
        if(!context.getCyclicReference().isEmpty())
            context.setStatus(true);
        return context;
    }
}
