package com.omtg2sql.omtg.relationships;
import com.omtg2sql.omtg.classes.OMTGCardinality;

public class OMTGSpatialAggregation extends OMTGRelationship {
	
	private OMTGCardinality cardinality1, cardinality2;
	
	public OMTGSpatialAggregation(String class1, String class2) {
		super("spatial-aggregation-" + class1 + "-" + class2, "spatial-aggregation", class1, class2);
		this.cardinality1 = new OMTGCardinality("1", "1");
		this.cardinality2 = new OMTGCardinality("n", "n");
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