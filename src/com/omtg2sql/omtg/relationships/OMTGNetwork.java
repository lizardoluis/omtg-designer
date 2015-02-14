package com.omtg2sql.omtg.relationships;

public class OMTGNetwork extends OMTGRelationship {
	
	public OMTGNetwork(String name, String class1, String class2) {
		super("network-" + name + "-" + class1 + "-" + class2, "network", class1, class2);
	}
}