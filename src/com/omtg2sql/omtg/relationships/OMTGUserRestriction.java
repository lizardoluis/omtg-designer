package com.omtg2sql.omtg.relationships;

import java.util.List;

public class OMTGUserRestriction extends OMTGRelationship {

	private List<String> spatialRelation;
	private String distance, unit;
	private boolean spatialRelationCanOccur;

	public OMTGUserRestriction(String class1, String class2,
			List<String> spatialRelation, boolean spatialRelationCanOccur) {
		super(class1 + "-" + spatialRelation + "-" + class2, "user-restriction", class1, class2);
		this.spatialRelation = spatialRelation;
		this.spatialRelationCanOccur = spatialRelationCanOccur;
	}

	public boolean isSpatialRelationCanOccur() {
		return spatialRelationCanOccur;
	}

	public void setSpatialRelationCanOccur(boolean spatialRelationCanOccur) {
		this.spatialRelationCanOccur = spatialRelationCanOccur;
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
}
