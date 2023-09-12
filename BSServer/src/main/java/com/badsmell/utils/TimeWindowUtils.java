package com.badsmell.utils;

import org.elasticsearch.client.RestHighLevelClient;
import org.joda.time.DateTime;
import java.util.List;

/**
 * @description:
 * @author: xyc
 * @date: 2023-03-22 11:31
 */
public class TimeWindowUtils {
    public static List<String> getCurrentWindowLog(RestHighLevelClient client, String[] indices, DateTime startTime, DateTime endTime, String query, String query2){
        List<String> result1 =  ElasticSearchUtils.getLogsBetween(client, indices, startTime, endTime, query);
        List<String> result2 =  ElasticSearchUtils.getLogsBetween(client, indices, startTime, endTime, query2);
        result1.addAll(result2);
        return result1;
    }

    public static List<String> getSqlLog(RestHighLevelClient client, String[] indices, DateTime startTime, DateTime endTime){
        List<String> result =  ElasticSearchUtils.getLogsBetween(client, indices, startTime, endTime, "sqlLog");
        return result;
    }

}
