package com.palantir.hackthon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class MyAgg {
    
	/**
	 * getTop10OldestByState
	 * */
    public static List<DataItem> getTop10OldestByState(List<List<String>> data, String state) {
    	int maxSize = 10;
    	FixSizedPriorityQueue<DataItem> myHeap = new FixSizedPriorityQueue<DataItem>(maxSize);
    	for (List<String> row : data) {
    		//creat a new DataItem
    		DataItem item = new DataItem(row.get(0), Integer.valueOf(row.get(1)));
    		myHeap.add(item);
    	}
		return myHeap.sortedList();
    }
    
    
	/**
	 * K-way merges
	 * */
    public static List<DataItem> getMergedTop10(List<List<DataItem>> data) {
    	List<DataItem> result = new ArrayList<DataItem>();
    	int k = data.size() - 1;
    	int[] index = new int[data.size()];
    	
    	int count = 0;
    	DataItem curMax = data.get(0).get(index[0]);
    	int curIndex = 0;
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
        	result.add(curMax);
        	count++;
    	}
    	
    	return result;
    }
    
    

	/**
	 * Max_heap implemented by PriorityQueue
	 * */
	public static class FixSizedPriorityQueue<E extends Comparable> {
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
}