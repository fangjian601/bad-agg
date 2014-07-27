package com.palantir.hackthon;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

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

    public static Map<String, Integer> precomputedComputeRangeMax(Map<String, List<DataItem>> data, int startAge, int endAge) {
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

    public static Map<String, List<DataItem>> readFromOffsets(String fileName, long beginOffset, long endOffset){

    	Map<String, List<DataItem>> resultMap = new HashMap<String, List<DataItem>>();
    	
    	try {
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

    public static Map<String, Integer> rangeMaxQuery(List<List<String>> data, int startAge, int endAge) {
        Map<String,Integer> countsByState = new HashMap<String, Integer>();
        for (List<String> row : data) {
            int age = Integer.parseInt(row.get(1));
            String state = row.get(2);
            if (age >= startAge && age <= endAge) {
                Integer count = countsByState.get(state);
                if (count != null) {
                    countsByState.put(state, count + 1);
                } else {
                    countsByState.put(state, 1);
                }
            }
        }

        return countsByState;
    }


}
