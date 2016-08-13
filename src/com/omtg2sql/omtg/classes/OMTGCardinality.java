package com.omtg2sql.omtg.classes;
import java.util.Scanner;

public class OMTGCardinality {

	public static final String ONE_TO_ONE = "(1,1)";
	public static final String ONE_TO_MANY = "(1,*)";
	public static final String MANY_TO_ONE = "(*,1)";
	public static final String MANY_TO_MANY = "(*,*)";
	public static final int PARTIAL = 0;
	public static final int TOTAL = 1;

	private String min;
	private String max;
	private int participation;

	public OMTGCardinality(String cardString) {
		Scanner in = new Scanner(cardString);
		in.useDelimiter(",");
		this.min = in.next();
		this.max = in.next();
		if (min.equals("0")) 
			this.participation = 0; //partial
		else this.participation = 1; //total
		
		in.close();
	}

	public OMTGCardinality(String min, String max) {
		this.min = min;
		this.max = max;
		if (min.equals("0")) 
			this.participation = 0; //partial
		else this.participation = 1; //total
	}

	public int getParticipation() {
		return participation;
	}

	public String getMin() {
		return min;
	}

	public String getMax() {
		return max;
	}

	public boolean isEqual(OMTGCardinality card) {
		if ((min.equals(card.getMin()) && max.equals(card.getMax())) ||
				(min.equals(card.getMax()) && max.equals(card.getMin()))) {
			return true;
		}
		return false;
	}

	public String toString() {
		return "(" + min + "," + max + ")";
	}
}