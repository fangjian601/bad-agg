package com.palantir.hackthon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
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
    	
        return resultMap;
    }
    
    public static void main(String[] args) {
    	String fileName = "1million";
    	long start = 8;
    	long end = 10000000;
    	
//		RandomAccessFile file = new RandomAccessFile(new File(fileName), "r");
//		
//		file.seek(start);
//		String startTempLine = file.readLine();
//		System.err.println("start pointer at: " + startTempLine.charAt(0));
//		
//		file.seek(end);
//		String endTempLine = file.readLine();
//		System.err.println("end pointer at: " + endTempLine.charAt(0));
		
		Map<String, List<DataItem>> map = readFromOffsets(fileName, start, end);
		System.out.println(map.size());
		
		for (String string : map.keySet()) {
			System.out.println(string + ", " + map.get(string).size());
		}
		
		
    }
}
