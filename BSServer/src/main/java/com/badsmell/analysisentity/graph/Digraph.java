package com.badsmell.analysisentity.graph;

//有向图


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * author: yang
 * date:2022/12/20
 * 存放有向图
 */
public class Digraph {
    private static Logger logger = LogManager.getLogger(Digraph.class);

    private int V;  //图中顶点数目
    private int E;   //边的个数
    private Map<String,List<String>> adj; //邻接表 service-keyValue调用的服务Deque<String>
    public Digraph(){
        this.V = 0;
        this.E = 0;
        this.adj = new HashMap<>();

    }

    public int getV(){
        return this.V;
    }
    public int getE(){
        return this.E;
    }

    public void addEdge(String v,String w){
        //有向边起点不存在，创建添加，并存储邻接点
        if (!adj.containsKey(v)){
            List<String> adjNodes = new LinkedList<>();
            adjNodes.add(w);
            adj.put(v,adjNodes);
            this.V++;
            this.E++;
        }
        else {
            List<String> adjNodes = adj.get(v);
            adjNodes.add(w);
            adj.put(v,adjNodes);
            this.E++;
        }
        //如果尾节点不存在，创建
        if (!adj.containsKey(w)){
            List<String> adjNodes = new LinkedList<>();
            adj.put(w,adjNodes);
            this.V++;
        }
    }

    public List<String> getAdj(String v){
        return adj.get(v);
    }

    /**
     * @return
     * 获取反向图
     */
    private Digraph reverse(){
        Digraph digraph = new Digraph();

        Iterator<Map.Entry<String,List<String>>> iterator = adj.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String,List<String>> entry = iterator.next();
            for (int i=0;i<entry.getValue().size();i++){
                digraph.addEdge(entry.getValue().get(i),entry.getKey());
            }
        }

        return digraph;
    }


    /**
     * @return
     * 判断有向图中是否存在环
     */
    public Boolean hasCycle(){
        Map<String,List<String>> adjClone = new HashMap<>() ;
        //深拷贝
        adjClone.putAll(adj);

        while (!adjClone.isEmpty()){
            System.out.println(adjClone);
            int execNum=0;
            Iterator<Map.Entry<String,List<String>>> iterator = adjClone.entrySet().iterator();
            while (iterator.hasNext()){
                //出度为0的点直接从Map中删除，并删除相应的有向边
                Map.Entry<String,List<String>> entry = iterator.next();
                if (entry.getValue().size()==0){
                    String removeNode = entry.getKey();
                    //Iterator是工作在一个独立的线程中，拥有一个 mutex锁，在工作的时候，不允许被迭代的对象被改变
//                    adjClone.remove(removeNode);
                    //迭代器是单向不可变的，原数据是可变的，删除后，迭代器不报错，用迭代器的remove()
                    iterator.remove();

                    for (Map.Entry<String,List<String>> entry1: adjClone.entrySet()) {
                        entry1.getValue().remove(removeNode);
                    }
                    execNum++;
                }


            }
            if (execNum==0){
                break;
            }

        }
        if (adjClone.isEmpty()){
            logger.info("不包含环");
            return false;
        }else{
            logger.info("包含环");
            return true;
        }

    }

    @Override
    public String toString() {
        return "Digraph{" +
                "V=" + V +
                ", E=" + E +
                ", adj=" + adj +
                '}';
    }
}
