package com.badsmell.analysisentity.cdabs;

import com.badsmell.analysisentity.cdabs.ReceivedLog;
import com.badsmell.analysisentity.cdabs.RequestChains;
import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * author: yang
 * date: 2022/12/14
 */
@Data
public class RequestUserSet {
    private List<RequestChains> requestUsers;

    public RequestUserSet(){
        requestUsers = new LinkedList<>();
    }

    public boolean handleLogs(List<String> logRecords){

        if (logRecords==null||logRecords.size()==0){
            System.out.println("logRecords can not be null and it's size must be greater than zero.");
            return false;
        }
        for (int i=0;i<logRecords.size();i++){
            ReceivedLog tempReceivedLog = new ReceivedLog();
            Boolean success = tempReceivedLog.resolveFromString(logRecords.get(i));
            if (success){
                String tmpUserId = tempReceivedLog.getLogUserId();
                System.out.println(tmpUserId);
                //包含这个用户 直接将日志记录塞到相应的链中
                Integer postion = -1;
                if ((postion = isContainUser(tmpUserId))!=-1){

                    putRecord(postion,tempReceivedLog);
                }
                else {
                    //创建一个请求链集合
                    createUser(tempReceivedLog);
                }

            }
            else {
                System.out.println("Log parse falied!!");
                return false;
            }
        }
        return true;
    }

    private Integer isContainUser(String user){
        for (int i = 0; i < requestUsers.size(); i++) {
            if (requestUsers.get(i).getRequestUser().equals(user))
                return i;
        }
        return -1;
    }

    /**
     * @param pos
     * @param receivedLog
     * put the record into the set of existed request chain of user in the position pos
     */
    private void putRecord(Integer pos, ReceivedLog receivedLog){
        RequestChains tmpRequestChain = requestUsers.get(pos);
        //判断这个用户之下是否存在链路请求traceId
        if (tmpRequestChain.getRequestChainSet().containsKey(receivedLog.getTraceId())){
            //存在就直接在链后添加记录
            tmpRequestChain.getRequestChainSet().get(receivedLog.getTraceId()).add(receivedLog);
        }
        else {
            //不存在就创建新记录
            List<ReceivedLog> newReceivedLog = new LinkedList<>();
            newReceivedLog.add(receivedLog);
            tmpRequestChain.getRequestChainSet().put(receivedLog.getTraceId(),newReceivedLog);
        }
    }

    /**
     * @param receivedLog a record of log
     *   used for creating a storage object for user request
     */
    private void createUser(ReceivedLog receivedLog){

        RequestChains requestChain = new RequestChains();
        requestChain.setRequestUser(receivedLog.getLogUserId());
        Map<String,List<ReceivedLog>> newChainSet = new HashMap<>();
        List<ReceivedLog> receivedLogList = new LinkedList<>();
        receivedLogList.add(receivedLog);
        newChainSet.put(receivedLog.getTraceId(),receivedLogList);
        requestChain.setRequestChainSet(newChainSet);
        requestUsers.add(requestChain);
    }

}

