package com.omtg2sql.omtg.relationships;

import java.util.List;

public class OMTGGeneralization extends OMTGRelationship {
	
	private static final String TOTAL = "total";
	private static final String PARTIAL = "partial";
	private static final String DISJOINT = "disjoint";

	/*
	 * completeness = (t)/(p) total, partial
	 * disjointness = (d)/(o) disjoint, overlapping
	 */
	private String superClass, disjointness;
	private List<String> subclasses;

	public OMTGGeneralization(String disjointness, String type, String superClass, List<String> subclasses) {
		super("generalization-" + superClass, type, superClass, null);
		this.superClass = superClass;
		this.disjointness = disjointness;
		this.subclasses = subclasses;
	}
	
	public String getSuperClass() {
		return superClass;
	}

	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}

	public String getDisjointness() {
		return disjointness;
	}

	public void setDisjointness(String disjointness) {
		this.disjointness = disjointness;
	}

	public List<String> getSubclasses() {
		return subclasses;
	}

	public void setSubclasses(List<String> subclasses) {
		this.subclasses = subclasses;
	}

	public static String getTotal() {
		return TOTAL;
	}

	public static String getPartial() {
		return PARTIAL;
	}

	public static String getDisjoint() {
		return DISJOINT;
	}

	public boolean isDisjoint() {
		return disjointness.equalsIgnoreCase(DISJOINT);
	}
}