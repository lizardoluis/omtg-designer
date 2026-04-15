package com.omtg2sql.sql.postgis;

import java.io.StringWriter;
import java.util.List;
import java.util.Scanner;

import com.omtg2sql.sql.SQLWriter;
import com.omtg2sql.util.FormatSQL;

/**
 * Online Constraints Writer for native PostgreSQL/PostGIS.
 * Generates triggers for enforcing spatial constraints using native PostGIS functions.
 */
public class OnLineConstraintsWriter extends SQLWriter {

	private final String B_TABLE_NAME = "<B_TABLE_NAME>";
	private final String A_TABLE_NAME = "<A_TABLE_NAME>";
	private final String B_TABLE_KEYS_FORMAT = "<B_TABLE_KEYS_FORMAT>";
	private final String B_TABLE_KEYS_ARGS = "<B_TABLE_KEYS_ARGS>";
	private final String SPATIAL_RELATION = "<SPATIAL_RELATION>";
	private final String SPATIAL_RELATION_FUNCTION = "<SPATIAL_RELATION_FUNCTION>";
	private final String VAL_TOP_REL_NAME = "<VAL_TOP_REL_NAME>";

	private final String OPERATOR = "<OPERATOR>";
	private final String NOT_OPERATOR = "<NOT_OPERATOR>";

	private final String DISTANCE = "<DISTANCE>";
	private final String UNIT = "<UNIT>";

	private final String SUPERCLASS_TABLE_NAME = "<SUPERCLASS_TABLE_NAME>";
	private final String SUBCLASS_TABLE_NAME = "<SUBCLASS_TABLE_NAME>";
	private final String SUBCLASS_TABLE_KEYS = "<SUBCLASS_TABLE_KEYS>";
	private final String SUBCLASSES_TABLE_NAMES = "<SUBCLASSES_TABLE_NAMES>";

	private final String SELECTS = "<SELECTS>";
	private final String DISJOINT_CONDITION = "<DISJOINT_CONDITION>";
	private final String VAL_DISJOINT_GEN_NAME = "<VAL_DISJOINT_GEN_NAME>";

	private final String COMMENT = "<COMMENT>";

	private final String VALIDATE_TOPOLOGICAL_RELATIONSHIP_TEMPLATE = "validate_topological_relationship_template.sql";
	private final String VALIDATE_TOPOLOGICAL_RELATIONSHIP_NEAR_TEMPLATE = "validate_topological_relationship_near_template.sql";
	private final String VALIDATE_TOPOLOGICAL_RELATIONSHIP_DISTANT_TEMPLATE = "validate_topological_relationship_distant_template.sql";
	private final String VALIDATE_DISJOINT_GENERALIZATION_TEMPLATE = "validate_disjoint_generalization_template.sql";
	private final String VALIDATE_USER_RESTRICTION_TEMPLATE = "validate_user_restriction_template.sql";
	private final String VALIDATE_USER_RESTRICTION_NEAR_TEMPLATE = "validate_user_restriction_near_template.sql";
	private final String VALIDATE_USER_RESTRICTION_DISTANT_TEMPLATE = "validate_user_restriction_distant_template.sql";

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
			sql = FormatSQL.replace(sql, UNIT, unit != null ? unit : "meters");
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
			sql = FormatSQL.replace(sql, UNIT, unit != null ? unit : "meters");
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

		if (sql.contains(SPATIAL_RELATION_FUNCTION)) {
			// Build PostGIS function call for the spatial relation
			String spatialFunc = buildSpatialRelationFunction(spatialRelation);
			sql = FormatSQL.replaceAll(sql, SPATIAL_RELATION_FUNCTION, spatialFunc);
		}

		if (sql.contains(SPATIAL_RELATION)) {
			sql = FormatSQL.replaceAll(sql, SPATIAL_RELATION,
					FormatSQL.toString(spatialRelation).toLowerCase());
		}

		return sql;
	}

	private String buildSpatialRelationFunction(List<String> spatialRelations) {
		if (spatialRelations.size() == 1) {
			return OMTG2PostgisMapper.mapSpatialRelation(spatialRelations.get(0))
					+ "(a.geom, NEW.geom)";
		}

		// For multiple relations, use OR
		StringBuilder sb = new StringBuilder("(");
		for (int i = 0; i < spatialRelations.size(); i++) {
			if (i > 0) {
				sb.append(" OR ");
			}
			sb.append(OMTG2PostgisMapper.mapSpatialRelation(spatialRelations.get(i)))
					.append("(a.geom, NEW.geom)");
		}
		sb.append(")");
		return sb.toString();
	}

	private String processUserRestrictionNear(String sql, String aTableName,
			String bTableName, List<String> bTableKeys, String distance,
			String unit, boolean spatialRelationCanOccur) {

		sql = processTableNames(sql, aTableName, bTableName, bTableKeys);

		if (sql.contains(COMMENT)) {
			sql = FormatSQL.replaceAll(sql, COMMENT,
					"-- Validate the user-defined restriction NEAR "
							+ "between " + aTableName + " and " + bTableName);
		}

		if (sql.contains(DISTANCE)) {
			sql = FormatSQL.replace(sql, DISTANCE, distance);
		}

		if (sql.contains(UNIT)) {
			sql = FormatSQL.replace(sql, UNIT, unit != null ? unit : "meters");
		}

		if (sql.contains(OPERATOR)) {
			if (spatialRelationCanOccur) {
				sql = FormatSQL.replaceAll(sql, OPERATOR, "");
			} else {
				sql = FormatSQL.replaceAll(sql, OPERATOR, "NOT");
			}
		}

		if (sql.contains(NOT_OPERATOR)) {
			if (spatialRelationCanOccur) {
				sql = FormatSQL.replaceAll(sql, NOT_OPERATOR, "NOT");
			} else {
				sql = FormatSQL.replaceAll(sql, NOT_OPERATOR, "");
			}
		}

		return sql;
	}

	private String processUserRestrictionDistant(String sql, String aTableName,
			String bTableName, List<String> bTableKeys, String distance,
			String unit, boolean spatialRelationCanOccur) {

		sql = processTableNames(sql, aTableName, bTableName, bTableKeys);

		if (sql.contains(COMMENT)) {
			sql = FormatSQL.replaceAll(sql, COMMENT,
					"-- Validate the user-defined restriction DISTANT "
							+ "between " + aTableName + " and " + bTableName);
		}

		if (sql.contains(DISTANCE)) {
			sql = FormatSQL.replace(sql, DISTANCE, distance);
		}

		if (sql.contains(UNIT)) {
			sql = FormatSQL.replace(sql, UNIT, unit != null ? unit : "meters");
		}

		if (sql.contains(OPERATOR)) {
			if (spatialRelationCanOccur) {
				sql = FormatSQL.replaceAll(sql, OPERATOR, "");
			} else {
				sql = FormatSQL.replaceAll(sql, OPERATOR, "NOT");
			}
		}

		if (sql.contains(NOT_OPERATOR)) {
			if (spatialRelationCanOccur) {
				sql = FormatSQL.replaceAll(sql, NOT_OPERATOR, "NOT");
			} else {
				sql = FormatSQL.replaceAll(sql, NOT_OPERATOR, "");
			}
		}

		return sql;
	}

	private String processUserRestriction(String sql, String aTableName,
			String bTableName, List<String> bTableKeys,
			List<String> spatialRelation, boolean spatialRelationCanOccur) {

		sql = processTableNames(sql, aTableName, bTableName, bTableKeys);

		if (sql.contains(COMMENT)) {
			sql = FormatSQL.replaceAll(sql, COMMENT,
					"-- Validate the user-defined restriction " + "between "
							+ aTableName + " and " + bTableName);
		}

		if (sql.contains(SPATIAL_RELATION_FUNCTION)) {
			String spatialFunc = buildSpatialRelationFunction(spatialRelation);
			sql = FormatSQL.replaceAll(sql, SPATIAL_RELATION_FUNCTION, spatialFunc);
		}

		if (sql.contains(SPATIAL_RELATION)) {
			sql = FormatSQL.replaceAll(sql, SPATIAL_RELATION,
					FormatSQL.toString(spatialRelation).toLowerCase());
		}

		if (sql.contains(OPERATOR)) {
			if (spatialRelationCanOccur) {
				sql = FormatSQL.replaceAll(sql, OPERATOR, "");
			} else {
				sql = FormatSQL.replaceAll(sql, OPERATOR, "NOT");
			}
		}

		if (sql.contains(NOT_OPERATOR)) {
			if (spatialRelationCanOccur) {
				sql = FormatSQL.replaceAll(sql, NOT_OPERATOR, "NOT");
			} else {
				sql = FormatSQL.replaceAll(sql, NOT_OPERATOR, "");
			}
		}

		return sql;
	}

	private String generateSelectsDisjointConstraint(
			List<String> subClassesTableNames, List<String> subClassTableKeys,
			String superClassTableName, String subClassTableName) {

		String output = "";

		for (int i = 0; i < subClassesTableNames.size(); i++) {

			if (!subClassesTableNames.get(i).equalsIgnoreCase(subClassTableName)) {

				String select = "    SELECT EXISTS (\n"
						+ "        SELECT 1\n"
						+ "        FROM <SUBCLASSES_TABLE_NAMES>\n"
						+ "        WHERE <DISJOINT_CONDITION>\n"
						+ "    ) INTO n;\n";
				select = FormatSQL.replace(select, SUBCLASSES_TABLE_NAMES,
						FormatSQL.tableToString(subClassesTableNames.get(i)));
				select = FormatSQL.replace(select, DISJOINT_CONDITION,
						FormatSQL.keysToStringPostgis(subClassTableKeys, superClassTableName));

				String keysFormat = FormatSQL.columnsToFormatString(subClassTableKeys);
				String keysArgs = FormatSQL.columnsToArgsList(subClassTableKeys, "NEW.", superClassTableName);
				String argsClause = keysArgs.isEmpty() ? "" : ", " + keysArgs;

				String raise = "    IF n THEN\n"
						+ "      RAISE EXCEPTION 'Disjoint constraint of generalization on table "
						+ subClassTableName + " " + keysFormat + "is violated'" + argsClause + ";\n"
						+ "    END IF;\n";
				output += select + "\n" + raise + "\n";
			}
		}

		return output;
	}

	private String processDisjointConstraint(String sql,
			String subClassTableName, List<String> subClassTableKeys,
			List<String> subClassesTableNames, String superClassTableName) {

		if (sql.contains(COMMENT)) {
			sql = FormatSQL.replace(sql, COMMENT,
					"-- Validate the disjoint constraint on subclass " + subClassTableName);
		}

		if (sql.contains(VAL_DISJOINT_GEN_NAME)) {
			sql = FormatSQL.replace(sql, VAL_DISJOINT_GEN_NAME,
					FormatSQL.validateLengthName(subClassTableName).toLowerCase());
		}

		if (sql.contains(SUBCLASS_TABLE_NAME)) {
			sql = FormatSQL.replace(sql, SUBCLASS_TABLE_NAME, subClassTableName.toLowerCase());
		}

		if (sql.contains(SELECTS)) {
			sql = FormatSQL.replace(sql, SELECTS,
					generateSelectsDisjointConstraint(subClassesTableNames,
							subClassTableKeys, superClassTableName, subClassTableName));
		}

		return sql;
	}

	private String processTableNames(String sql, String aTableName,
			String bTableName, List<String> bTableKeys) {

		if (sql.contains(VAL_TOP_REL_NAME)) {
			sql = FormatSQL.replaceAll(sql, VAL_TOP_REL_NAME,
					(FormatSQL.validateLengthName(aTableName) + "_"
							+ FormatSQL.validateLengthName(bTableName)).toLowerCase());
		}

		if (sql.contains(B_TABLE_NAME)) {
			sql = FormatSQL.replaceAll(sql, B_TABLE_NAME, bTableName.toLowerCase());
		}

		if (sql.contains(A_TABLE_NAME)) {
			sql = FormatSQL.replaceAll(sql, A_TABLE_NAME, aTableName.toLowerCase());
		}

		if (sql.contains(B_TABLE_KEYS_FORMAT)) {
			sql = FormatSQL.replaceAll(sql, B_TABLE_KEYS_FORMAT,
					FormatSQL.columnsToFormatString(bTableKeys));
		}

		if (sql.contains(B_TABLE_KEYS_ARGS)) {
			String args = FormatSQL.columnsToArgsList(bTableKeys, "NEW.", "");
			if (!args.isEmpty()) {
				sql = FormatSQL.replaceAll(sql, B_TABLE_KEYS_ARGS, ", " + args);
			} else {
				sql = FormatSQL.replaceAll(sql, B_TABLE_KEYS_ARGS, "");
			}
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

	public void appendUserRestrictionNearConstraint(String aTableName,
			String bTableName, List<String> bTableKeys, String distance,
			String unit, boolean spatialRelationCanOccur) {

		readFile(VALIDATE_USER_RESTRICTION_NEAR_TEMPLATE);

		while (in.hasNextLine()) {
			String sql = in.nextLine();
			sql = processUserRestrictionNear(sql, aTableName, bTableName,
					bTableKeys, distance, unit, spatialRelationCanOccur);
			appendSQL(sql);
		}

		in.close();
	}

	public void appendUserRestrictionDistantConstraint(String aTableName,
			String bTableName, List<String> bTableKeys, String distance,
			String unit, boolean spatialRelationCanOccur) {

		readFile(VALIDATE_USER_RESTRICTION_DISTANT_TEMPLATE);

		while (in.hasNextLine()) {
			String sql = in.nextLine();
			sql = processUserRestrictionDistant(sql, aTableName, bTableName,
					bTableKeys, distance, unit, spatialRelationCanOccur);
			appendSQL(sql);
		}

		in.close();
	}

	public void appendUserRestrictionConstraint(String aTableName,
			String bTableName, List<String> bTableKeys,
			List<String> spatialRelations, boolean spatialRelationCanOccur) {

		readFile(VALIDATE_USER_RESTRICTION_TEMPLATE);

		while (in.hasNextLine()) {
			String sql = in.nextLine();
			sql = processUserRestriction(sql, aTableName, bTableName,
					bTableKeys, spatialRelations, spatialRelationCanOccur);
			appendSQL(sql);
		}

		in.close();
	}

	public void appendDisjointConstraint(String subClassTableName,
			List<String> subClassTableKeys, List<String> subClassesTableNames) {

		readFile(VALIDATE_DISJOINT_GENERALIZATION_TEMPLATE);

		while (in.hasNextLine()) {
			String sql = in.nextLine();
			sql = processDisjointConstraint(sql, subClassTableName,
					subClassTableKeys, subClassesTableNames, "");
			appendSQL(sql);
		}

		in.close();
	}

	public void appendDisjointConstraintWithPartial(String subClassTableName,
			List<String> subClassTableKeys, List<String> subClassesTableNames,
			String superClassTableName) {

		readFile(VALIDATE_DISJOINT_GENERALIZATION_TEMPLATE);

		while (in.hasNextLine()) {
			String sql = in.nextLine();
			sql = processDisjointConstraint(sql, subClassTableName,
					subClassTableKeys, subClassesTableNames, superClassTableName);
			appendSQL(sql);
		}

		in.close();
	}
}
