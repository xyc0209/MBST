package com.badsmell.utils;


import com.badsmell.analysisentity.cdabs.*;
import com.badsmell.analysisentity.graph.Digraph;
import com.badsmell.context.CircleDependencyContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * author：yang
 * date：2022/12/20
 * 循环依赖的判断
 */
public class CircleDependencyAnalysisUtils {

    private static Logger logger = LogManager.getLogger(CircleDependencyAnalysisUtils.class);

    /**
     * @return 分析循环依赖架构异味
     */
    public static CircleDependencyContext analysisCDABS(RequestUserSet requestUserSet){

        CircleDependencyContext circleDependencyContext = new CircleDependencyContext();

        List<AnalysisResult> analysisResults = new LinkedList<>();

        for (int i=0;i<requestUserSet.getRequestUsers().size();i++){
            //按用户ID分置分析结果

            //获取该用户每条请求链进行分析
            //使用迭代器遍历该用户下的每一个请求链

            AnalysisResult analysisResult = analysisRequestChain(requestUserSet.getRequestUsers().get(i));

            analysisResults.add(analysisResult);
        }

        circleDependencyContext.setValueMap(analysisResults);
        System.out.println(analysisResults);
        if(!circleDependencyContext.getValueMap().isEmpty())
            circleDependencyContext.setStatus(true);
        return circleDependencyContext;
    }


    /**
     * @param requestChains
     * @return
     * 针对某一用户在该时段内所有请求的循环依赖分析
     */
    private static AnalysisResult analysisRequestChain(RequestChains requestChains){

        AnalysisResult analysisResult = new AnalysisResult();
        analysisResult.setReqChainMap(new HashMap<String, RequestCD>());
        analysisResult.setUserId(requestChains.getRequestUser());
        Boolean isContainCircle = false;
        logger.info("RequestChains:"+requestChains.getRequestUser());
        Iterator<Map.Entry<String,List<ReceivedLog>>> iterator = requestChains.getRequestChainSet().entrySet().iterator();

        while (iterator.hasNext()){
            Map.Entry<String,List<ReceivedLog>> entry = iterator.next();
            List<ReceivedLog> tmpReceivedLog = entry.getValue();
            //链路不完整，第一个parentId不为null
            if (tmpReceivedLog.size()==0||!"null".equals(tmpReceivedLog.get(0).getParentId())){
                System.out.println("date time"+tmpReceivedLog.get(0).getLogDateTime());
//                analysisResult.getCircleDescribeTreeMap().put(entry.getKey(),null);
                logger.info("链路不完整，第一个parentId不为null");
            }
            else {

                CircleDescribeTree headNode = buildTree(tmpReceivedLog);
                Digraph digraph = buildDiGraph(headNode);
                Boolean isCircle = digraph.hasCycle();
                RequestCD requestCD = new RequestCD();
                requestCD.setIsCircle(isCircle);
                if (isCircle){
                    isContainCircle = true;
                    requestCD.setDigraph(digraph);
                    requestCD.setTreeRoot(headNode);
                }else {
                    requestCD.setTreeRoot(null);
                    requestCD.setDigraph(null);
                }
                analysisResult.getReqChainMap().put(headNode.getId(),requestCD);
            }
        }
        analysisResult.setIsContainCircle(isContainCircle);
        return analysisResult;
    }

    /**
     * @param receivedLogList
     * @return
     * 构建链路请求的多叉树
     */
    private static CircleDescribeTree buildTree(List<ReceivedLog> receivedLogList){

        CircleDescribeTree treeRoot = new CircleDescribeTree();
        Map<String,CircleDescribeTree> map = new HashMap<>();

        for (int i = 0; i < receivedLogList.size(); i++) {
            CircleDescribeTree node = new CircleDescribeTree();
            node.setId(receivedLogList.get(i).getId());
            node.setParentId(receivedLogList.get(i).getParentId());
            node.setServiceName(receivedLogList.get(i).getServiceName());
            node.setServiceInterface(receivedLogList.get(i).getLogMethodName());
            node.setInstanceIpAddr(receivedLogList.get(i).getLogFromIpAddr());
            map.put(node.getId(), node);
        }



        Iterator<Map.Entry<String,CircleDescribeTree>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String,CircleDescribeTree> entry = iterator.next();
            CircleDescribeTree node = entry.getValue();
            if ("null".equals(node.getParentId())){
                treeRoot.getSubRequest().put(node.getId(),node);
            }else {
                CircleDescribeTree parentNode = map.get(node.getParentId());
                parentNode.getSubRequest().put(node.getId(),node);
            }

        }
        //返回第一个请求，作为头节点  第一个请求的Id与整个请求链的traceId相同
        return treeRoot.getSubRequest().get(receivedLogList.get(0).getTraceId());
    }

    private static Digraph buildDiGraph(CircleDescribeTree circleDescribeTree){

        Deque<CircleDescribeTree> circleDescribeTreeDeque = new LinkedList<>();
        circleDescribeTreeDeque.add(circleDescribeTree);
        Digraph digraph = new Digraph();
        while (!circleDescribeTreeDeque.isEmpty()){
            CircleDescribeTree tmpTree = circleDescribeTreeDeque.pollFirst();
            //判断子节点是否为空
            if (!tmpTree.getSubRequest().isEmpty()){

                for (Map.Entry<String,CircleDescribeTree> entry:
                     tmpTree.getSubRequest().entrySet()) {
                    digraph.addEdge(tmpTree.getServiceName(),entry.getValue().getServiceName());
                    circleDescribeTreeDeque.add(entry.getValue());
                }
            }

        }
        return digraph;
    }

}
