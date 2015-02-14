package com.omtg2sql.omtg.relationships;

public abstract class OMTGRelationship implements Cloneable {

	private String name;
	private String type;
	private String class1, class2;
	
	public OMTGRelationship(String name, String type, String class1, String class2) {
		this.name = name;
		this.type = type;
		this.class1 = class1;
		this.class2 = class2;
	}

	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public boolean typeEquals(String type) {
		return this.type.equalsIgnoreCase(type);
	}
	
	public String getClass1() {
		return class1;
	}

	public void setClass1(String class1) {
		this.class1 = class1;
	}

	public String getClass2() {
		return class2;
	}

	public void setClass2(String class2) {
		this.class2 = class2;
	}
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}