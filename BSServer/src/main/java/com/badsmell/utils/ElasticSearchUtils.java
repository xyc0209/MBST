package com.badsmell.utils;


import com.mbs.common.log.DatabaseLog;
import com.mbs.common.log.MBaseLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: be used to query data from elasticsearch
 * @author: xyc
 * @date: 2023-03-06 21:42
 */
public class ElasticSearchUtils {

    private static Logger logger = LogManager.getLogger(ElasticSearchUtils.class);
    //设置默认查询的日志条数
    private static Integer SEARCH_VALUE = 100;

    /**
     * Get the logs which are generated at the time between start time and end time.
     * The logs will be processed here to generate MServiceBaseLogs in order to reduce the data size.
     *
     * @param client: The rest high level client of elasticsearch
     * @param indices: The indices that the logs belong to
     * @param startTime: The start of the time range
     * @param endTime: The end of the time range
     * @return List: Service logs
     */
    public static List<String> getLogsBetween(RestHighLevelClient client, String[] indices, DateTime startTime, DateTime endTime, String query) {
        List<String> logList = new ArrayList<>();
        SearchRequest sr = new SearchRequest(indices);
        final Scroll scroll = new Scroll(TimeValue.timeValueSeconds(1L));
        sr.scroll(scroll);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if(query.equals("sqlLog")){
            boolQueryBuilder.must(QueryBuilders.existsQuery(query));
            boolQueryBuilder.must(
                    QueryBuilders.rangeQuery("sqlLog.currentTimeMillis").from(startTime.getMillis()).to(endTime.getMillis()));
//            boolQueryBuilder.must(QueryBuilders.matchQuery("mclient.logType",query));
        }
        else {
            boolQueryBuilder.must(QueryBuilders.existsQuery("mclient"));
            boolQueryBuilder.must(
                    QueryBuilders.rangeQuery("mclient.logDateTimeInMills").from(startTime.getMillis()).to(endTime.getMillis()));
            boolQueryBuilder.must(QueryBuilders.matchQuery("mclient.logType", query));
        }
        searchSourceBuilder.query(boolQueryBuilder).size(SEARCH_VALUE);
        sr.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = client.search(sr, RequestOptions.DEFAULT);
            String scrollId = searchResponse.getScrollId();
            SearchHit[] searchHits = searchResponse.getHits().getHits();

            while (searchHits != null && searchHits.length > 0) {
                for (SearchHit hit : searchHits) {
//                    System.out.println("hit"+hit);
//                    logger.info(hit.getIndex() + "|" + hit.getSourceAsMap().getOrDefault("mclient", null));
                    try {
                        if(query.equals("sqlLog")) {
                            DatabaseLog databaseLog =DatabaseLog.getLogFromMap((Map<String, Object>) hit.getSourceAsMap().get("sqlLog"));
                            logList.add(databaseLog.toString());
                        }
                        else{
                            MBaseLog baseLog = MBaseLog.getLogFromMap((Map<String, Object>) hit.getSourceAsMap().get("mclient"));
                            if (baseLog != null) {
                                logList.add(baseLog.toString());
                                logger.info(baseLog.toString());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = searchResponse.getScrollId();
                searchHits = searchResponse.getHits().getHits();
            }

            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            boolean succeeded = clearScrollResponse.isSucceeded();
        } catch (IOException e) {
            logger.warn("Exception happens. Cannot fetch logs from elasticsearch!");
            logger.warn(e);
        }
        return logList;
    }

    public static void getLog(RestHighLevelClient client, String index, String id) {
        GetRequest rq = new GetRequest(index, id);
        try {
            GetResponse getResponse = client.get(rq, RequestOptions.DEFAULT);
            logger.debug(getResponse.getField("message"));
        } catch (IOException e) {
            logger.warn("Exception happens. Cannot get log from elasticsearch!");
            logger.warn(e);
        }
    }


}
