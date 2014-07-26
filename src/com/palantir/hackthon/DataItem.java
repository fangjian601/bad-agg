package com.palantir.hackthon;

public class DataItem implements Comparable<DataItem> {
	private String name;
	private int age;
	private String state;
	
	public DataItem(String name, int age, String state) {
		this.name = name;
		this.age = age;
		this.state = state;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getAge() {
		return this.age;
	}
	
	public String getState() {
		return this.state;
	}
	
	@Override
	public int compareTo(DataItem t) {
		return this.age < t.age ? -1 : this.age == t.age ? 0 : 1;
	}
}
