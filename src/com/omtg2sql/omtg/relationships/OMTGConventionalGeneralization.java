package com.omtg2sql.omtg.relationships;

import java.util.List;

public class OMTGConventionalGeneralization extends OMTGGeneralization {

	/*
	 * completeness = (t)/(p) total, partial
	 * disjointness = (d)/(o) disjoint, overlapping
	 */
	private String completeness;

	public OMTGConventionalGeneralization(String completeness, String disjointness, String superClass, List<String> subclasses) {
		super(disjointness, "conventional-generalization", superClass, subclasses);
		this.completeness = completeness;
	}
	
	public boolean isTotal() {
		return completeness.equalsIgnoreCase("total");
	}
	
	public boolean isPartial() {
		return completeness.equalsIgnoreCase("partial");
	}
	
	public String getCompleteness() {
		return completeness;
	}

	public void setCompleteness(String completeness) {
		this.completeness = completeness;
	}
}