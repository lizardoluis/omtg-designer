package com.omtg2sql.sql.postgis;

/**
 * Mapper class for converting OMTG types to native PostgreSQL/PostGIS types.
 * Uses standard PostGIS GEOMETRY type with type constraints instead of
 * ast-postgis custom types.
 */
public class OMTG2PostgisMapper {

	/**
	 * Maps OMTG attribute types to PostgreSQL native types.
	 * 
	 * @param attributeType The OMTG attribute type
	 * @param length The length/precision of the attribute
	 * @param scale The scale for numeric types
	 * @return The corresponding PostgreSQL type
	 */
	public static String mapAttributeType(String attributeType, String length, String scale) {

		if (attributeType.equalsIgnoreCase("varchar")) {
			return "VARCHAR(" + (length == null ? "50" : length) + ")";
		}
		if (attributeType.equalsIgnoreCase("real")) {
			return "DOUBLE PRECISION";
		}
		if (attributeType.equalsIgnoreCase("time")) {
			return "TIMESTAMP";
		}
		if (attributeType.equalsIgnoreCase("text")) {
			return "TEXT";
		}
		if (attributeType.equalsIgnoreCase("integer")) {
			return "INTEGER";
		}
		if (attributeType.equalsIgnoreCase("boolean")) {
			return "BOOLEAN";
		}
		if (attributeType.equalsIgnoreCase("date")) {
			return "DATE";
		}
		return attributeType.toUpperCase();
	}

	/**
	 * Maps OMTG class types to PostGIS geometry types.
	 * Uses native PostGIS GEOMETRY type with type parameter.
	 * 
	 * @param classType The OMTG class type
	 * @return The corresponding PostGIS geometry type specification
	 */
	public static String mapClassType(String classType) {

		if (classType.equalsIgnoreCase("point")) {
			return "GEOMETRY(POINT, 4326)";
		} else if (classType.equalsIgnoreCase("line")) {
			return "GEOMETRY(LINESTRING, 4326)";
		} else if (classType.equalsIgnoreCase("polygon")) {
			return "GEOMETRY(POLYGON, 4326)";
		} else if (classType.equalsIgnoreCase("un-line")) {
			return "GEOMETRY(LINESTRING, 4326)";
		} else if (classType.equalsIgnoreCase("bi-line")) {
			return "GEOMETRY(LINESTRING, 4326)";
		} else if (classType.equalsIgnoreCase("node")) {
			return "GEOMETRY(POINT, 4326)";
		} else if (classType.equalsIgnoreCase("TIN")) {
			return "GEOMETRY(POLYGON, 4326)";
		} else if (classType.equalsIgnoreCase("isolines")) {
			return "GEOMETRY(LINESTRING, 4326)";
		} else if (classType.equalsIgnoreCase("planar-subdivision")) {
			return "GEOMETRY(POLYGON, 4326)";
		} else if (classType.equalsIgnoreCase("sample")) {
			return "GEOMETRY(POINT, 4326)";
		} else if (classType.equalsIgnoreCase("tesselation")) {
			return "RASTER";
		} else if (classType.equalsIgnoreCase("conventional")) {
			return "";
		}
		return "GEOMETRY";
	}

	/**
	 * Maps OMTG class types to simple PostGIS geometry type name (for constraints).
	 * 
	 * @param classType The OMTG class type
	 * @return The simple geometry type name
	 */
	public static String mapClassTypeSimple(String classType) {

		if (classType.equalsIgnoreCase("point")) {
			return "POINT";
		} else if (classType.equalsIgnoreCase("line")) {
			return "LINESTRING";
		} else if (classType.equalsIgnoreCase("polygon")) {
			return "POLYGON";
		} else if (classType.equalsIgnoreCase("un-line")) {
			return "LINESTRING";
		} else if (classType.equalsIgnoreCase("bi-line")) {
			return "LINESTRING";
		} else if (classType.equalsIgnoreCase("node")) {
			return "POINT";
		} else if (classType.equalsIgnoreCase("TIN")) {
			return "POLYGON";
		} else if (classType.equalsIgnoreCase("isolines")) {
			return "LINESTRING";
		} else if (classType.equalsIgnoreCase("planar-subdivision")) {
			return "POLYGON";
		} else if (classType.equalsIgnoreCase("sample")) {
			return "POINT";
		}
		return "GEOMETRY";
	}

	/**
	 * Maps spatial relation names to PostGIS function names.
	 * 
	 * @param spatialRelation The OMTG spatial relation name
	 * @return The corresponding PostGIS function name
	 */
	public static String mapSpatialRelation(String spatialRelation) {

		if (spatialRelation.equalsIgnoreCase("contains")) {
			return "ST_Contains";
		} else if (spatialRelation.equalsIgnoreCase("containsproperly")) {
			return "ST_ContainsProperly";
		} else if (spatialRelation.equalsIgnoreCase("covers")) {
			return "ST_Covers";
		} else if (spatialRelation.equalsIgnoreCase("coveredby")) {
			return "ST_CoveredBy";
		} else if (spatialRelation.equalsIgnoreCase("crosses")) {
			return "ST_Crosses";
		} else if (spatialRelation.equalsIgnoreCase("disjoint")) {
			return "ST_Disjoint";
		} else if (spatialRelation.equalsIgnoreCase("intersects")) {
			return "ST_Intersects";
		} else if (spatialRelation.equalsIgnoreCase("overlaps")) {
			return "ST_Overlaps";
		} else if (spatialRelation.equalsIgnoreCase("touches")) {
			return "ST_Touches";
		} else if (spatialRelation.equalsIgnoreCase("within")) {
			return "ST_Within";
		} else if (spatialRelation.equalsIgnoreCase("equals")) {
			return "ST_Equals";
		}
		return "ST_Intersects";
	}
}
