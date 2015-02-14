package com.omtg2sql.sql.oracle;

public class OMTG2OracleSQLMapper {

	public static String mapAttributeType(String attributeType, String length, String scale) {
		
		if (attributeType.equalsIgnoreCase("VARCHAR2")) {
			return attributeType.toUpperCase() + "(" + (length==null?"100":length) + ")";
		}
		else if (attributeType.equalsIgnoreCase("NUMBER")) {
			return attributeType.toUpperCase() + "(" + length + "," + (scale==null?"1":scale) + ")";
		}
		else if (attributeType.equalsIgnoreCase("date")) {
			return attributeType.toUpperCase();
		}
		return "VARCHAR2(100)";

	}

	public static String mapClassType(String classType) {

		if (classType.equalsIgnoreCase("point")) {
			return "POINT";
		}
		else if (classType.equalsIgnoreCase("line")) {
			return "LINE";
		}
		else if (classType.equalsIgnoreCase("polygon")) {
			return "POLYGON";
		}
		else if (classType.equalsIgnoreCase("un-line")) {
			return "LINESTRING";
		}
		else if (classType.equalsIgnoreCase("bi-line")) {
			return "LINESTRING";
		}
		else if (classType.equalsIgnoreCase("node")) {
			return "POINT";
		}
		else if (classType.equalsIgnoreCase("TIN")) {
			return "POLYGON";
		}
		else if (classType.equalsIgnoreCase("isolines")) {
			return "POLYGON";
		}
		else if (classType.equalsIgnoreCase("planar-subdivision")) {
			return "POLYGON";
		}
		else if (classType.equalsIgnoreCase("sample")) {
			return "POINT";
		}
		else if (classType.equalsIgnoreCase("tesselation")) {
			return "";
		}
		return "UNKNOWN_GEOMETRY";
	}
}