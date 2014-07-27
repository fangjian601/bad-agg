package com.palantir.hackthon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created at 7/26/14
 *
 * @author Jian Fang (jfang@rocketfuelinc.com)
 */
public class QueryWorker implements Runnable {
    private String queryType;
    private List<String> queryParameters;
    private Map<String, List<DataItem>> data;
    private BlockingQueue<Object> resultList;

    public QueryWorker(String fileName, long beginOffset, long endOffset, BlockingQueue<Object> resultList)
            throws IOException{
        this.data = QueryHelper.readFromOffsets(fileName, beginOffset, endOffset);
        this.resultList = resultList;
    }

    public void setQueryParameters(String[] queryParameters) {
        this.queryParameters = new ArrayList<String>();
        Collections.addAll(this.queryParameters, queryParameters);
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    @Override
    public void run() {
        Object result = null;
        if(queryType.toLowerCase().equals("average")){
            resultList.offer(QueryHelper.averageQuery(data, queryParameters.get(0)));
        } else if(queryType.toLowerCase().equals("top10")){
            List<DataItem> items = data.get(queryParameters.get(0));
            resultList.offer(QueryHelper.top10Query(items));
        } else if(queryType.toLowerCase().equals("rangemax")){
            int startAge = Integer.valueOf(queryParameters.get(0));
            int endAge = Integer.valueOf(queryParameters.get(1));
            resultList.offer(QueryHelper.rangeMaxQuery(data, startAge, endAge));
        }
        if(result != null) {resultList.offer(result);}
    }
}
