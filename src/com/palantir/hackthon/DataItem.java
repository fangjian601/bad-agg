package com.palantir.hackthon;

public class DataItem implements Comparable<DataItem> {
	private String name;
	private int age;
	
	public DataItem(String name, int age) {
		this.name = name;
		this.age = age;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getAge() {
		return this.age;
	}
	
	@Override
	public int compareTo(DataItem t) {
		return this.age < t.age ? -1 : this.age == t.age ? 0 : 1;
	}
}
