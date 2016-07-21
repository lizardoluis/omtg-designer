package com.omtg2sql.sql.oracle;

public class OMTG2OracleSQLMapper {

	public static String mapAttributeType(String attributeType, String length, String scale) {
		
		if (attributeType.equalsIgnoreCase("varchar")) {
			return "VARCHAR2(" + (length==null?"50":length) + ")";
		}
		if (attributeType.equalsIgnoreCase("real")) {
			return "NUMBER";
		}
		if (attributeType.equalsIgnoreCase("time")) {
			return "TIMESTAMP";
		}
		if (attributeType.equalsIgnoreCase("text")) {
			return "VARCHAR2(300)";
		}
		else return attributeType.toUpperCase();

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