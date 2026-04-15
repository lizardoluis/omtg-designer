package com.omtg2sql.sql.astpostgis;

public class OMTG2AstPostgisMapper {

	public static String mapAttributeType(String attributeType, String length, String scale) {
 
		if (attributeType.equalsIgnoreCase("varchar")) {
			return "VARCHAR(" + (length==null?"50":length) + ")";
		}
		else return attributeType.toUpperCase(); 
	}
}

