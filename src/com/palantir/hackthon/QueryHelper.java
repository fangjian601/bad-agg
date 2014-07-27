package com.palantir.hackthon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created at 7/26/14
 *
 * @author Jian Fang (jfang@rocketfuelinc.com)
 */
public class QueryHelper {
	
	static final String DELIM = ",";
	
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
}
