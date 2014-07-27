import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.palantir.hackthon.DataItem;

public class RangeMax {
	private RangeMax() {

	}

	public static Map<String, Integer> computeRangeMax(List<List<String>> data, int startAge, int endAge) {
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
		Iterator<Entry<String, List<DataItem>>> it = data.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, List<DataItem>> stateData = (Entry<String, List<DataItem>>)it.next();
	        int left = bsearchLeft(startAge, stateData.getValue());
	        int right = bsearchRight(endAge, stateData.getValue());
	        countsByState.put(stateData.getKey(), right-left + 1);
	        
//	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    
	    return countsByState;
	}
	
	public static void main(String[] args) {
		Map<String, List<DataItem>> data = new HashMap<String, List<DataItem>>();
		List<DataItem> l = new ArrayList<DataItem>();
		l.add(new DataItem("Bob", 2, "CA"));
		l.add(new DataItem("B", 10, "CA"));
		l.add(new DataItem("Bo", 10, "CA"));
		l.add(new DataItem("Bill", 10, "CA"));
		l.add(new DataItem("A", 15, "CA"));
		l.add(new DataItem("Bc", 20, "CA"));
		l.add(new DataItem("C", 25, "CA"));
		l.add(new DataItem("D", 25, "CA"));
		l.add(new DataItem("E", 25, "CA"));
		l.add(new DataItem("F", 25, "CA"));
		l.add(new DataItem("G", 25, "CA"));
		l.add(new DataItem("H", 26, "CA"));
		data.put("CA", l);
		System.out.println(precomputedComputeRangeMax(data, 10, 25).get("CA"));
		
	}
}