package com.omtg2sql.sql.astpostgis;

import java.io.StringWriter;
import java.util.List;
import java.util.Scanner;

import com.omtg2sql.sql.SQLWriter;
import com.omtg2sql.util.FormatSQL;

public class OnLineConstraintsWriter extends SQLWriter {

	private final String B_TABLE_NAME = "<B_TABLE_NAME>";
	private final String A_TABLE_NAME = "<A_TABLE_NAME>";
	private final String B_TABLE_KEYS = "<B_TABLE_KEYS>";
	private final String SPATIAL_RELATION = "<SPATIAL_RELATION>";
	private final String SPATIAL_RELATION_MASK = "<SPATIAL_RELATION_MASK>";
	private final String VAL_TOP_REL_NAME = "<VAL_TOP_REL_NAME>";

	private final String DISTANCE = "<DISTANCE>";
	private final String UNIT = "<UNIT>";

	private final String COMMENT = "<COMMENT>";

	private final String VALIDATE_TOPOLOGICAL_RELATIONSHIP_TEMPLATE = "validate_topological_relationship_template.sql";

	private final String VALIDATE_TOPOLOGICAL_RELATIONSHIP_NEAR_TEMPLATE = "validate_topological_relationship_near_template.sql";
	
	private final String VALIDATE_TOPOLOGICAL_RELATIONSHIP_DISTANT_TEMPLATE = "validate_topological_relationship_distant_template.sql";

	private Scanner in;

	public OnLineConstraintsWriter(String sqlFilePath) {
		super(sqlFilePath);
	}
	
	public OnLineConstraintsWriter(StringWriter sw) {
		super(sw);
	}

	private void readFile(String validationFilePath) {

		in = new Scanner(getClass().getResourceAsStream(validationFilePath));
	}

	private String processSpatialRelationNear(String sql, String aTableName,
			String bTableName, List<String> bTableKeys, String distance,
			String unit) {

		sql = processTableNames(sql, aTableName, bTableName, bTableKeys);

		if (sql.contains(COMMENT)) {

			sql = FormatSQL.replaceAll(sql, COMMENT,
					"-- Validate the topological relationship NEAR "
							+ "between " + aTableName + " and " + bTableName);
		}

		if (sql.contains(DISTANCE)) {

			sql = FormatSQL.replace(sql, DISTANCE, distance);
		}

		if (sql.contains(UNIT)) {

			sql = FormatSQL.replace(sql, UNIT, unit);
		}

		return sql;
	}
	
	private String processSpatialRelationDistant(String sql, String aTableName,
			String bTableName, List<String> bTableKeys, String distance,
			String unit) {

		sql = processTableNames(sql, aTableName, bTableName, bTableKeys);

		if (sql.contains(COMMENT)) {

			sql = FormatSQL.replaceAll(sql, COMMENT,
					"-- Validate the topological relationship DISTANT "
							+ "between " + aTableName + " and " + bTableName);
		}

		if (sql.contains(DISTANCE)) {

			sql = FormatSQL.replace(sql, DISTANCE, distance);
		}

		if (sql.contains(UNIT)) {

			sql = FormatSQL.replace(sql, UNIT, unit);
		}

		return sql;
	}

	private String processSpatialRelation(String sql, String aTableName,
			String bTableName, List<String> bTableKeys,
			List<String> spatialRelation) {

		sql = processTableNames(sql, aTableName, bTableName, bTableKeys);

		if (sql.contains(COMMENT)) {

			sql = FormatSQL.replaceAll(sql, COMMENT,
					"-- Validate the topological relationship " + "between "
							+ aTableName + " and " + bTableName);
		}

		if (sql.contains(SPATIAL_RELATION_MASK)) {

			sql = FormatSQL.replaceAll(sql, SPATIAL_RELATION_MASK,
					FormatSQL.toStringMask(spatialRelation));
		}

		if (sql.contains(SPATIAL_RELATION)) {

			sql = FormatSQL.replaceAll(sql, SPATIAL_RELATION,
					FormatSQL.toString(spatialRelation).toLowerCase());
		}

		return sql;
	}

	private String processTableNames(String sql, String aTableName,
			String bTableName, List<String> bTableKeys) {

		if (sql.contains(VAL_TOP_REL_NAME)) {

			sql = FormatSQL.replaceAll(
					sql,
					VAL_TOP_REL_NAME,
					FormatSQL.validateLengthName(aTableName) + "_"
							+ FormatSQL.validateLengthName(bTableName));
		}

		if (sql.contains(B_TABLE_NAME)) {

			sql = FormatSQL.replaceAll(sql, B_TABLE_NAME, bTableName);
		}

		if (sql.contains(A_TABLE_NAME)) {

			sql = FormatSQL.replaceAll(sql, A_TABLE_NAME, aTableName);
		}

		if (sql.contains(B_TABLE_KEYS)) {

			sql = FormatSQL.replaceAll(sql, B_TABLE_KEYS,
					FormatSQL.columnsToString2(bTableKeys, ":NEW.", ""));
		}

		return sql;
	}

	public void appendTopologicalRelationshipNearConstraint(String aTableName,
			String bTableName, List<String> bTableKeys, String distance,
			String unit) {

		readFile(VALIDATE_TOPOLOGICAL_RELATIONSHIP_NEAR_TEMPLATE);

		while (in.hasNextLine()) {

			String sql = in.nextLine();
			sql = processSpatialRelationNear(sql, aTableName, bTableName,
					bTableKeys, distance, unit);
			appendSQL(sql);
		}

		in.close();
	}
	
	public void appendTopologicalRelationshipDistantConstraint(String aTableName,
			String bTableName, List<String> bTableKeys, String distance,
			String unit) {

		readFile(VALIDATE_TOPOLOGICAL_RELATIONSHIP_DISTANT_TEMPLATE);

		while (in.hasNextLine()) {

			String sql = in.nextLine();
			sql = processSpatialRelationDistant(sql, aTableName, bTableName,
					bTableKeys, distance, unit);
			appendSQL(sql);
		}

		in.close();
	}

	public void appendTopologicalRelationshipConstraint(String aTableName,
			String bTableName, List<String> bTableKeys,
			List<String> spatialRelations) {

		readFile(VALIDATE_TOPOLOGICAL_RELATIONSHIP_TEMPLATE);

		while (in.hasNextLine()) {

			String sql = in.nextLine();
			sql = processSpatialRelation(sql, aTableName, bTableName,
					bTableKeys, spatialRelations);
			appendSQL(sql);
		}

		in.close();
	}

}

