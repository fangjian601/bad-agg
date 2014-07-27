package com.palantir.hackthon;

import java.io.IOException;
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
        this.queryParameters.clear();
        Collections.addAll(this.queryParameters, queryParameters);
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    @Override
    public void run() {
        Object result = null;
        if(queryType.toLowerCase().equals("average")){

        } else if(queryType.toLowerCase().equals("top10")){

        } else if(queryType.toLowerCase().equals("rangemax")){

        }
        if(result != null) {resultList.offer(result);}
    }
}
