package com.omtg2sql.omtg.relationships;
import java.util.List;

import com.omtg2sql.omtg.classes.OMTGCardinality;


public class OMTGTopologicalRelationship extends OMTGRelationship {
	
	private List<String> spatialRelation; 
	private String distance, unit;
	private OMTGCardinality cardinality1, cardinality2;

	public OMTGTopologicalRelationship(List<String> spatialRelation, String distance, String unit, String class1, OMTGCardinality cardinality1, String class2, OMTGCardinality cardinality2) {
		super("topological-relationship-" + spatialRelation + "-" + class1 + "-" + class2, "topological-relationship", class1, class2);
		this.spatialRelation = spatialRelation;
		this.distance = distance;
		this.unit = unit;
		this.cardinality1 = cardinality1;
		this.cardinality2 = cardinality2;
	}

	public List<String> getSpatialRelation() {
		return spatialRelation;
	}

	public void setSpatialRelation(List<String> spatialRelation) {
		this.spatialRelation = spatialRelation;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public OMTGCardinality getCardinality1() {
		return cardinality1;
	}

	public void setCardinality1(OMTGCardinality cardinality1) {
		this.cardinality1 = cardinality1;
	}

	public OMTGCardinality getCardinality2() {
		return cardinality2;
	}

	public void setCardinality2(OMTGCardinality cardinality2) {
		this.cardinality2 = cardinality2;
	}
}