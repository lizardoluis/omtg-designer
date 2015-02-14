package com.omtg2sql.omtg.relationships;

import java.util.List;

public class OMTGConceptualGeneralization extends OMTGGeneralization {

//	private static final String DISJOINT = "disjoint";
//	private static final String OVERLAPPING = "overlapping";

	/*
	 * completeness = (t)/(p) total, partial disjointness = (d)/(o) disjoint,
	 * overlapping
	 */
	private String disjointness, scaleShape;

	public OMTGConceptualGeneralization(String scaleShape, String disjointness,
			String superClass, List<String> subclasses) {
		super(disjointness, "conceptual-generalization", superClass, subclasses);
		this.scaleShape = scaleShape;
		this.disjointness = disjointness;
	}

	public String getDisjointness() {
		return disjointness;
	}

	public void setDisjointness(String disjointness) {
		this.disjointness = disjointness;
	}

	public String getScaleShape() {
		return scaleShape;
	}

	public void setScaleShape(String scaleShape) {
		this.scaleShape = scaleShape;
	}
}