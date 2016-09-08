package model;

import java.util.*;

public class Customer {
	private String name;
	private int impressions;
	private int revenue;
	
	public Customer(String sName, int sImpressions, int sRevenue) {
		name = sName;
		impressions = sImpressions;
		revenue = sRevenue;
	}
	
	public String getName() {
		return name;
	}
	
	public int getImpressions() {
		return impressions;
	}
	
	public int getRevenue() {
		return revenue;
	}
}
