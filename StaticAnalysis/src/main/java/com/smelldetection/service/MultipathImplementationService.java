package com.smelldetection.service;

import com.smelldetection.base.context.MultiPathContext;
import com.smelldetection.base.factory.FileFactory;
import com.smelldetection.base.item.MultiPathItem;
import com.smelldetection.base.item.RequestItem;
import com.smelldetection.base.utils.ApiParserUtils;
import com.smelldetection.base.utils.Node;
import com.smelldetection.base.utils.NodeList;
import com.smelldetection.base.utils.NodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

/**
 * @description:
 * @author: xyc
 * @date: 2023-02-20 21:05
 */
@Service
public class MultipathImplementationService {
    @Autowired
    public FileFactory fileFactory;

    @Autowired
    public ApiParserUtils apiParserUtils;

    @Autowired
    public NodeUtils nodeUtils;

    public MultiPathContext getMultipathClass(RequestItem request) throws IOException {
        String path = request.getServicesPath();
        String servicesDirectory = new File(path).getAbsolutePath();
        List<String> servicesPath = fileFactory.getServicePaths(servicesDirectory);
        MultiPathContext multiPathContext = new MultiPathContext();


//        List<Node> ancestorInterfaceList = new ArrayList<>();
//        Set<String> abstractSet = new HashSet<>();
        // get ancestor Interface to build tree ancestor Node
        for (String svc : servicesPath) {
            Map<Node, Integer> differentNodeMap = new HashMap<>();
            NodeUtils nodeUtils1 =new NodeUtils();
            List<Node> ancestorInterfaceList = new ArrayList<>();
            Set<String> abstractSet = new HashSet<>();
            List<String> applicationYamlOrPropertities = fileFactory.getApplicationYamlOrPropertities(svc);
            Yaml yaml = new Yaml();
            String serviceName = "";
            Set<String> entitySet = new HashSet<>();
            if (applicationYamlOrPropertities.size() == 0)
                continue;
            for (String app : applicationYamlOrPropertities) {
                System.out.println(app);
                if (app.endsWith("yaml") || app.endsWith("yml")) {
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
            List<String> javaFiles = fileFactory.getJavaFiles(svc);
            for (String javafile : javaFiles) {
                File file = new File(javafile);
                apiParserUtils.getInterface(file,ancestorInterfaceList,abstractSet, serviceName);
            }
            List<Node> temporaryList = new ArrayList<>();
            NodeList nodeList = new NodeList();
            nodeList.setAncestorList(ancestorInterfaceList);
            nodeList.setTemporaryList(temporaryList);
            nodeList.setNodeNum(ancestorInterfaceList.size());
            nodeList.setAbstractSet(abstractSet);
            for(Node node: ancestorInterfaceList)
                for (String javafile : javaFiles) {
                    File file = new File(javafile);
                    apiParserUtils.buildTree(file, nodeList, serviceName);
                }
            for(Node node: nodeList.getAncestorList()){
                nodeUtils1.queryDifferentNode(node, differentNodeMap);
                nodeUtils1.bianLi(node);
            }


            for(Node node: differentNodeMap.keySet()){
                if(differentNodeMap.get(node) > 1){
                    nodeUtils1.routeResult = new ArrayList<>();
                    for(Node ancesotr: nodeList.getAncestorList()){
                        nodeUtils1.queryRouteByName(ancesotr, node.getName(), new ArrayList<>());
                    }
                    List<List<String>> uniqueRouteResult = new ArrayList<>();
                    Set<List<String>> set = new HashSet<>();
                    for (List<String> list : nodeUtils1.routeResult) {
                        if (!set.contains(list)) {
                            uniqueRouteResult.add(list);
                            set.add(list);
                        }
                    }
                    if(uniqueRouteResult.size() <=1)
                        continue;
                    MultiPathItem multiPathItem = new MultiPathItem();
                    multiPathItem.setName(node.getName());
                    multiPathItem.setServiceName(node.getServiceName());
                    multiPathItem.setRouteList(new ArrayList<>(uniqueRouteResult));
                    if(!uniqueRouteResult.isEmpty())
                        multiPathContext.getMultiItems().add(multiPathItem);
                }
            }
        }
        if(!multiPathContext.getMultiItems().isEmpty())
            multiPathContext.setStatus(true);
        return multiPathContext;
    }
//    public boolean isNormalMultipath(List<String> list1, List<String> list2){
//        int count = 0;
//        int size1 = list1.size();
//        int size2 =list2.size();
//        for(int i=0; i< size1; i++){
//            for(int j=0; j<size2; j++){
//                if(list1.get(i).equals(list2.get(j)))
//                    count++;
//            }
//        }
//        return count ==1 || (size1 == size2 && size1 == count) ? false : true;
//    }
}
