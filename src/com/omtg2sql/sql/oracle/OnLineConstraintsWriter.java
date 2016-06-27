package com.omtg2sql.sql.oracle;

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

	private final String OPERATOR = "<OPERATOR>";
	private final String IS_ISNOT = "<IS_ISNOT>";

	private final String DISTANCE = "<DISTANCE>";
	private final String UNIT = "<UNIT>";

	private final String SUPERCLASS_TABLE_NAME = "<SUPERCLASS_TABLE_NAME>";
	private final String SUPERCLASS_TABLE_KEYS = "<SUPERCLASS_TABLE_KEYS>";
	private final String SUBCLASS_TABLE_NAME = "<SUBCLASS_TABLE_NAME>";
	private final String SUBCLASS_TABLE_KEYS = "<SUBCLASS_TABLE_KEYS>";
	private final String SUBCLASSES_TABLE_NAMES = "<SUBCLASSES_TABLE_NAMES>";

	private final String SELECTS = "<SELECTS>";
	private final String DISJOINT_CONDITION = "<DISJOINT_CONDITION>";
	private final String VAL_DISJOINT_GEN_NAME = "<VAL_DISJOINT_GEN_NAME>";
	private final String PARTIAL_CONDITION = "<PARTIAL_CONDITION>";
	private final String VAL_PARTIAL_GEN_NAME = "<VAL_PARTIAL_GEN_NAME>";

	private final String COMMENT = "<COMMENT>";

	private final String VALIDATE_TOPOLOGICAL_RELATIONSHIP_TEMPLATE = "validate_topological_relationship_template.sql";

	private final String VALIDATE_TOPOLOGICAL_RELATIONSHIP_NEAR_TEMPLATE = "validate_topological_relationship_near_template.sql";

	private final String VALIDATE_DISJOINT_GENERALIZATION_TEMPLATE = "validate_disjoint_generalization_template.sql";

	private final String VALIDATE_PARTIAL_GENERALIZATION_TEMPLATE = "validate_partial_generalization_template.sql";

	private final String VALIDATE_USER_RESTRICTION_NEAR_TEMPLATE = "validate_user_restriction_near_template.sql";

	private final String VALIDATE_USER_RESTRICTION_TEMPLATE = "validate_user_restriction_template.sql";

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

//	private void readFile(String validationFilePath) {
//
//		JarFile jarFile;
//		try {
//			jarFile = new JarFile("omtg2sql.jar");
//			JarEntry entry = jarFile.getJarEntry(validationFilePath);
//			InputStream input = jarFile.getInputStream(entry);
//			InputStreamReader isr = new InputStreamReader(input);
//			BufferedReader br = new BufferedReader(isr);
//
//			in = new Scanner(br);
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//	}

	private String processSpatialRelationNear(String sql, String aTableName,
			String bTableName, List<String> bTableKeys, String distance,
			String unit) {

		sql = processTableNames(sql, aTableName, bTableName, bTableKeys);

		if (sql.contains(COMMENT)) {

			sql = FormatSQL.replaceAll(sql, COMMENT,
					"-- Validate the topological relationship near "
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
					FormatSQL.toString(spatialRelation));
		}

		return sql;
	}

	private String processUserRestrictionNear(String sql, String aTableName,
			String bTableName, List<String> bTableKeys, String distance,
			String unit, boolean spatialRelationCanOccur) {

		sql = processTableNames(sql, aTableName, bTableName, bTableKeys);

		if (sql.contains(COMMENT)) {

			sql = FormatSQL.replaceAll(sql, COMMENT,
					"-- Validate the user-defined restriction near "
							+ "between " + aTableName + " and " + bTableName);
		}

		if (sql.contains(DISTANCE)) {

			sql = FormatSQL.replace(sql, DISTANCE, distance);
		}

		if (sql.contains(UNIT)) {

			sql = FormatSQL.replace(sql, UNIT, unit);
		}

		if (sql.contains(OPERATOR)) {

			if (spatialRelationCanOccur)
				sql = FormatSQL.replaceAll(sql, OPERATOR, "=");
			else
				sql = FormatSQL.replaceAll(sql, OPERATOR, "!=");
		}

		if (sql.contains(IS_ISNOT)) {

			if (spatialRelationCanOccur)
				sql = FormatSQL.replaceAll(sql, IS_ISNOT, "is");
			else
				sql = FormatSQL.replaceAll(sql, IS_ISNOT, "is not");
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

		if (sql.contains(SPATIAL_RELATION_MASK)) {

			sql = FormatSQL.replaceAll(sql, SPATIAL_RELATION_MASK,
					FormatSQL.toStringMask(spatialRelation));
		}

		if (sql.contains(SPATIAL_RELATION)) {

			sql = FormatSQL.replaceAll(sql, SPATIAL_RELATION,
					FormatSQL.toString(spatialRelation));
		}

		if (sql.contains(OPERATOR)) {

			if (spatialRelationCanOccur)
				sql = FormatSQL.replaceAll(sql, OPERATOR, "=");
			else
				sql = FormatSQL.replaceAll(sql, OPERATOR, "!=");
		}

		if (sql.contains(IS_ISNOT)) {

			if (spatialRelationCanOccur)
				sql = FormatSQL.replaceAll(sql, IS_ISNOT, "is not");
			else
				sql = FormatSQL.replaceAll(sql, IS_ISNOT, "is");
		}

		return sql;
	}

	private String generateSelectsDisjointConstraint(
			List<String> subClassesTableNames, List<String> subClassTableKeys,
			String superClassTableName, String subClassTableName) {

		String output = "";

		for (int i = 0; i < subClassesTableNames.size(); i++) {

			if (!subClassesTableNames.get(i)
					.equalsIgnoreCase(subClassTableName)) {

				String select = "   SELECT count(*) INTO n"
						+ "\n      FROM <SUBCLASSES_TABLE_NAMES>"
						+ "\n      WHERE <DISJOINT_CONDITION>;";
				select = FormatSQL.replace(select, SUBCLASSES_TABLE_NAMES,
						FormatSQL.tableToString(subClassesTableNames.get(i)));
				select = FormatSQL.replace(select, DISJOINT_CONDITION,
						FormatSQL.keysToString(subClassTableKeys,
								superClassTableName));

				String raise = "   IF (n >= 1) THEN"
						+ "\n      RAISE_APPLICATION_ERROR(-20001, 'Disjoint constraint of generalization on table <SUBCLASS_TABLE_NAME> <SUBCLASS_TABLE_KEYS>is violated');"
						+ "\n   END IF;";
				raise = FormatSQL.replace(raise, SUBCLASS_TABLE_NAME,
						subClassTableName);
				raise = FormatSQL.replace(raise, SUBCLASS_TABLE_KEYS, FormatSQL
						.columnsToString2(subClassTableKeys, ":NEW.",
								superClassTableName));
				output += select + "\n\n" + raise + "\n\n";
			}
		}

		return output;
	}

	private String processDisjointConstraint(String sql,
			String subClassTableName, List<String> subClassTableKeys,
			List<String> subClassesTableNames, String superClassTableName) {

		if (sql.contains(COMMENT)) {

			sql = FormatSQL.replace(sql, COMMENT,
					"-- Validate the disjoint constraint on subclass "
							+ subClassTableName);
		}

		if (sql.contains(VAL_DISJOINT_GEN_NAME)) {

			sql = FormatSQL.replace(sql, VAL_DISJOINT_GEN_NAME,
					FormatSQL.validateLengthName(subClassTableName));
		}

		if (sql.contains(SUBCLASS_TABLE_NAME)) {

			sql = FormatSQL
					.replace(sql, SUBCLASS_TABLE_NAME, subClassTableName);
		}

		// if (sql.contains(SUBCLASSES_TABLE_NAMES)) {
		//
		// sql = FormatSQL.replace(sql, SUBCLASSES_TABLE_NAMES,
		// FormatSQL.tableToString(subClassesTableNames, subClassTableName));
		// }

		if (sql.contains(SELECTS)) {
			// -1 : removes the subClassTableName
			sql = FormatSQL.replace(
					sql,
					SELECTS,
					generateSelectsDisjointConstraint(subClassesTableNames,
							subClassTableKeys, superClassTableName,
							subClassTableName));
		}

		// if (sql.contains(DISJOINT_CONDITION)) {
		// // -1 : removes the subClassTableName
		// sql = FormatSQL.replace(sql, DISJOINT_CONDITION,
		// FormatSQL.keysToString(subClassTableKeys,
		// subClassesTableNames.size()-1,
		// superClassTableName, superClassTableName));
		// }

		// if (sql.contains(SUBCLASS_TABLE_KEYS)) {
		//
		// sql = FormatSQL.replace(sql, SUBCLASS_TABLE_KEYS,
		// FormatSQL.columnsToString2(subClassTableKeys, ":NEW.",
		// superClassTableName));
		// }

		return sql;
	}

	private String processPartialConstraint(String sql,
			String superClassTableName, List<String> subClassTableKeys,
			List<String> subClassesTableNames) {

		if (sql.contains(COMMENT)) {

			sql = FormatSQL.replace(sql, COMMENT,
					"-- Validate the partial constraint on superclass "
							+ superClassTableName);
		}

		if (sql.contains(VAL_PARTIAL_GEN_NAME)) {

			sql = FormatSQL.replace(sql, VAL_PARTIAL_GEN_NAME,
					FormatSQL.validateLengthName(superClassTableName));
		}

		if (sql.contains(SUPERCLASS_TABLE_NAME)) {

			sql = FormatSQL.replace(sql, SUPERCLASS_TABLE_NAME,
					superClassTableName);
		}

		if (sql.contains(SUBCLASSES_TABLE_NAMES)) {

			sql = FormatSQL.replace(sql, SUBCLASSES_TABLE_NAMES,
					FormatSQL.tableToString(subClassesTableNames));
		}

		if (sql.contains(PARTIAL_CONDITION)) {

			sql = FormatSQL.replace(sql, PARTIAL_CONDITION, FormatSQL
					.keysToString(subClassTableKeys,
							subClassesTableNames.size(), superClassTableName,
							""));
		}

		if (sql.contains(SUPERCLASS_TABLE_KEYS)) {

			sql = FormatSQL.replace(sql, SUPERCLASS_TABLE_KEYS,
					FormatSQL.columnsToString2(subClassTableKeys, ":NEW.", ""));
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
					subClassTableKeys, subClassesTableNames,
					superClassTableName);
			appendSQL(sql);
		}

		in.close();
	}

	public void appendPartialConstraint(String superClassTableName,
			List<String> subClassTableKeys, List<String> subClassesTableNames) {

		readFile(VALIDATE_PARTIAL_GENERALIZATION_TEMPLATE);

		while (in.hasNextLine()) {

			String sql = in.nextLine();
			sql = processPartialConstraint(sql, superClassTableName,
					subClassTableKeys, subClassesTableNames);
			appendSQL(sql);
		}

		in.close();
	}
}