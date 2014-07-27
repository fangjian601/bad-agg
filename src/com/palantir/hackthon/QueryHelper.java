package com.palantir.hackthon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * Created at 7/26/14
 *
 * @author Jian Fang (jfang@rocketfuelinc.com)
 */
public class QueryHelper {
	
	static final String DELIM = ",";

    private static int bsearchLeft(int age, List<DataItem> l){
        if (age < 0) {
            return -1;
        }

        int lo = 0;
        int hi = l.size() - 1;
        int mid = 0;
        while (lo <= hi) {
            // Key is in a[lo..hi] or not present.
            mid = lo + ((hi - lo) >>> 1);
            if      (age < l.get(mid).getAge()) hi = mid - 1;
            else if (age > l.get(mid).getAge()) lo = mid + 1;
            else break;
        }

        int index = mid;
        while (index >= 0 && age == l.get(index).getAge()) {
            index--;
        }
        return index + 1;
    }

    /**
     * Max_heap implemented by PriorityQueue
     * */
    private static class FixSizedPriorityQueue<E extends Comparable> {
        private PriorityQueue<E> queue;
        private int maxSize;

        public FixSizedPriorityQueue(int maxSize) {
            if (maxSize <= 0)
                throw new IllegalArgumentException();
            this.maxSize = maxSize;
            this.queue = new PriorityQueue<E>(maxSize, new Comparator<E>() {
                @SuppressWarnings("unchecked")
                @Override
                public int compare(E o1, E o2) {
                    return (o1.compareTo(o2));
                }
            });
        }

        @SuppressWarnings("unchecked")
        public void add(E e) {
            if (queue.size() < maxSize) {
                queue.add(e);
            } else {
                E peek = queue.peek();
                if (e.compareTo(peek) > 0) {
                    queue.poll();
                    queue.add(e);
                }
            }
        }

        @SuppressWarnings("unchecked")
        public List<E> sortedList() {
            List<E> list = new ArrayList<E>(queue);
            Collections.sort(list);
            return list;
        }
    }

    private static int bsearchRight(int age, List<DataItem> l){
        if (age < 0) {
            return -1;
        }

        int lo = 0;
        int hi = l.size() - 1;
        int mid = 0;
        while (lo <= hi) {
            // Key is in a[lo..hi] or not present.
            mid = lo + ((hi - lo) >>> 1);
            if      (age < l.get(mid).getAge()) hi = mid - 1;
            else if (age > l.get(mid).getAge()) lo = mid + 1;
            else break;
        }

        int index = mid;
        while (index < l.size() && age == l.get(index).getAge()) {
            index++;
        }
        return index - 1;
    }

    public static Map<String, List<DataItem>> readFromOffsets(String fileName, long beginOffset, long endOffset){

    	Map<String, List<DataItem>> resultMap = new HashMap<String, List<DataItem>>();
    	
    	try {
    		@SuppressWarnings("resource")
			RandomAccessFile file = new RandomAccessFile(new File(fileName), "r");
    		
    		long realStart;
    		// Check to see if the 1st of the line
    		file.seek(beginOffset - 1);
    		String line = file.readLine();
    		if (line.length() == 0) {
    			realStart = beginOffset;
    		} else {
    			long temp = file.getFilePointer();
    			realStart = temp;
    		}
    		file.seek(realStart);
    		
    		// Start to read
    		long curPointer = file.getFilePointer();
    		while (curPointer <= endOffset) {
    			String curLine = file.readLine();
    			if (curLine == null || curLine.length() == 0) break;
    			
    			// Process
    			String[] strings = curLine.split(DELIM);
    			if (resultMap.get(strings[2]) == null) {
    				List<DataItem> newList = new ArrayList<DataItem>();
    				newList.add(new DataItem(strings[0], Integer.valueOf(strings[1])));
    				resultMap.put(strings[2], newList);
    			} else {
    				List<DataItem> curList = resultMap.get(strings[2]);
    				curList.add(new DataItem(strings[0], Integer.valueOf(strings[1])));
    			}
    			
    			curPointer = file.getFilePointer();
    		}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return resultMap;
		} catch (IOException e) {
			e.printStackTrace();
			return resultMap;
		}

        for(Map.Entry<String, List<DataItem>> entry : resultMap.entrySet()){
            Collections.sort(entry.getValue());
        }
    	
        return resultMap;
    }

    public static List<Long> splitFile(String fileName, int num){
        List<Long> result = new ArrayList<Long>();
        File file = new File(fileName);
        long fileSize = file.length();
        if(fileSize <= num){
            result.add(0l);
        } else {
            long size = fileSize / num;
            for(long offset = 0; offset < fileSize; offset += size){
                result.add(offset);
            }
        }
        result.add(fileSize - 1);
        return result;
    }

    public static List<Long> averageQuery(Map<String, List<DataItem>> data, String prefix){
        Long[] result = new Long[100];
        for (Map.Entry<String, List<DataItem>> entry : data.entrySet()){
            for(DataItem item : entry.getValue()){
                if(item.getName().startsWith(prefix) && item.getAge() >=0 && item.getAge() < 100){
                    result[item.getAge()] = result[item.getAge()] + 1;
                }
            }
        }
        return Arrays.asList(result);
    }

    public static int averageQueryAggregate(List<List<Long>> agesCounts){
        long sum = 0, count = 0;
        for(List<Long> agesCount : agesCounts){
            for(int i = 0; i < agesCount.size(); i++){
                count += agesCount.get(i);
                sum += (i * agesCount.get(i));
            }
        }
        return (int)(sum / count);
    }

    public static Map<String, Integer> rangeMaxQuery(Map<String, List<DataItem>> data,
            int startAge, int endAge) {
        Map<String,Integer> countsByState = new HashMap<String, Integer>();
        Iterator<Map.Entry<String, List<DataItem>>> it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<DataItem>> stateData = it.next();
            int left = bsearchLeft(startAge, stateData.getValue());
            int right = bsearchRight(endAge, stateData.getValue());
            countsByState.put(stateData.getKey(), right-left + 1);
        }

        return countsByState;
    }

    public static String rangeMaxQueryAggregate(List<Map<String, Integer>> results){
        Map<String, Integer> temp = new HashMap<String, Integer>();
        String maxState = null;
        int maxCount = 0;
        for(Map<String, Integer> result : results){
            for(Map.Entry<String, Integer> entry : result.entrySet()){
                Integer count = temp.get(entry.getKey());
                if(count == null){
                    count = entry.getValue();
                } else {
                    count = count + entry.getValue();
                }
                if(count > maxCount){
                    maxCount = count;
                    maxState = entry.getKey();
                }
                temp.put(entry.getKey(), count);
            }
        }
        return maxState;
    }

    /**
     * getTop10OldestByState
     * */
    public static List<DataItem> top10Query(List<DataItem> data) {
        int maxSize = 10;
        FixSizedPriorityQueue<DataItem> myHeap = new FixSizedPriorityQueue<DataItem>(maxSize);
        for (DataItem row : data) {
            myHeap.add(row);
        }
        return myHeap.sortedList();
    }


    /**
     * K-way merges
     * */
    public static List<String> top10QueryAggregate(List<List<DataItem>> data) {
        List<String> result = new ArrayList<String>();
        int k = data.size() - 1;
        int[] index = new int[data.size()];

        int count = 0;
        DataItem curMax;
        int curIndex;
        while (count < 10) {
            curMax = data.get(0).get(index[0]);
            curIndex = 0;
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).get(index[i]).getAge() > curMax.getAge()) {
                    curMax = data.get(i).get(index[i]);
                    curIndex = i;
                }
            }
            index[curIndex]++;
            result.add(curMax.getName());
            count++;
        }

        return result;
    }
}
