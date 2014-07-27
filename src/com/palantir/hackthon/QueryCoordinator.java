package com.palantir.hackthon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created at 7/26/14
 *
 * @author Jian Fang (jfang@rocketfuelinc.com)
 */
public class QueryCoordinator {
    private QueryWorker[] workers;
    private BlockingQueue<Object> resultList;
    private ExecutorService pool;

    public QueryCoordinator(int workerNum, String fileName) throws IOException {
        List<Long> startOffsets = QueryHelper.splitFile(fileName, workerNum);
        workers = new QueryWorker[startOffsets.size() - 1];
        for(int i = 0; i < startOffsets.size() - 1; i++){
            workers[i] = new QueryWorker(fileName, startOffsets.get(i), startOffsets.get(i + 1) - 1, resultList);
        }
        resultList = new LinkedBlockingDeque<Object>();
        pool = Executors.newFixedThreadPool(workers.length);
    }

    public int average(String prefix) {
        List<List<Long>> results = new ArrayList<List<Long>>();
        int counter = workers.length;
        for(QueryWorker worker : workers){
            worker.setQueryType("average");
            worker.setQueryParameters(new String[]{prefix});
            pool.execute(worker);
        }
        while(counter != 0){
            results.add((List<Long>)(resultList.poll()));
        }
        return QueryHelper.averageQueryAggregate(results);
    }
}