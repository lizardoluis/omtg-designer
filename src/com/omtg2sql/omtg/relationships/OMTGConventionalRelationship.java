package com.omtg2sql.omtg.relationships;
import com.omtg2sql.omtg.classes.OMTGCardinality;

public class OMTGConventionalRelationship extends OMTGRelationship {
	
	private OMTGCardinality cardinality1, cardinality2;
	
	public OMTGConventionalRelationship(String name, String class1, OMTGCardinality cardinality1, String class2, OMTGCardinality cardinality2) {
		super("conventional-relationship-" + class1 + "-" + name + "-" + class2, "conventional-relationship", class1, class2);
		this.cardinality1 = cardinality1;
		this.cardinality2 = cardinality2;
	}
	
	public String getCardinality() {
		return "(" + cardinality1.getMax() + "," + cardinality2.getMax() + ")";
	}
	
	public boolean cardinalityIsEqual(String card) {
		return getCardinality().equalsIgnoreCase(card);
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
