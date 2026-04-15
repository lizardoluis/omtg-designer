package com.omtg2sql.sql.postgis;

import java.io.StringWriter;
import java.util.List;
import java.util.Scanner;

import com.omtg2sql.sql.SQLWriter;
import com.omtg2sql.util.FormatSQL;

/**
 * Offline Constraints Writer for native PostgreSQL/PostGIS.
 * Generates stored procedures and functions for validating spatial constraints
 * using native PostGIS functions.
 */
public class OffLineConstraintsWriter extends SQLWriter {

	private final String WHOLE_TABLE_NAME = "<WHOLE_TABLE_NAME>";
	private final String PART_TABLE_NAME = "<PART_TABLE_NAME>";
	private final String WHOLE_TABLE_KEYS = "<WHOLE_TABLE_KEYS>";
	private final String PART_TABLE_KEYS = "<PART_TABLE_KEYS>";
	private final String VAL_SPA_AGR_NAME = "<VAL_SPA_AGR_NAME>";

	private final String PLANAR_SUB_TABLE_NAME = "<PLANAR_SUB_TABLE_NAME>";
	private final String PLANAR_SUB_TABLE_KEYS = "<PLANAR_SUB_TABLE_KEYS>";
	private final String VAL_PLA_SUB_NAME = "<VAL_PLA_SUB_NAME>";

	private final String ISOLINE_TABLE_NAME = "<ISOLINE_TABLE_NAME>";
	private final String ISOLINE_TABLE_KEYS = "<ISOLINE_TABLE_KEYS>";
	private final String VAL_ISOLINE_NAME = "<VAL_ISOLINE_NAME>";

	private final String TIN_TABLE_NAME = "<TIN_TABLE_NAME>";
	private final String TIN_TABLE_KEYS = "<TIN_TABLE_KEYS>";
	private final String VAL_TIN_NAME = "<VAL_TIN_NAME>";

	private final String ARC_TABLE_NAME = "<ARC_TABLE_NAME>";
	private final String NODE_TABLE_NAME = "<NODE_TABLE_NAME>";
	private final String ARC_TABLE_KEYS = "<ARC_TABLE_KEYS>";
	private final String NODE_TABLE_KEYS = "<NODE_TABLE_KEYS>";
	private final String VAL_NETWOK_NAME = "<VAL_NETWOK_NAME>";

	private final String COMMENT = "<COMMENT>";

	private final String VALIDATE_SPATIAL_AGGREGATION_TEMPLATE = "validate_spatial_aggregation_template.sql";
	private final String VALIDATE_PLANAR_SUBDIVISION_TEMPLATE = "validate_planar_subdivision_template.sql";
	private final String VALIDATE_ISOLINE_TEMPLATE = "validate_isoline_template.sql";
	private final String VALIDATE_NETWORK_TEMPLATE = "validate_arc_node_network_template.sql";
	private final String VALIDATE_ARC_ARC_NETWORK_TEMPLATE = "validate_arc_arc_network_template.sql";
	private final String VALIDATE_TIN_TEMPLATE = "validate_tin_template.sql";

	private Scanner in;

	public OffLineConstraintsWriter(String sqlFilePath) {
		super(sqlFilePath);
	}

	public OffLineConstraintsWriter(StringWriter sw) {
		super(sw);
	}

	private void readFile(String validationFilePath) {
		in = new Scanner(getClass().getResourceAsStream(validationFilePath));
	}

	private String processSpatialAggegationConstraint(String sql,
			String wholeTableName, List<String> wholeTableKeys,
			String partTableName, List<String> partTableKeys) {

		if (sql.contains(COMMENT)) {
			sql = FormatSQL.replaceAll(sql, COMMENT,
					"-- Validate the spatial aggregation between the whole "
							+ wholeTableName + " and the part " + partTableName);
		}

		if (sql.contains(VAL_SPA_AGR_NAME)) {
			sql = FormatSQL.replaceAll(sql, VAL_SPA_AGR_NAME,
					(FormatSQL.validateLengthName(wholeTableName) + "_"
							+ FormatSQL.validateLengthName(partTableName)).toLowerCase());
		}

		if (sql.contains(WHOLE_TABLE_NAME)) {
			sql = FormatSQL.replaceAll(sql, WHOLE_TABLE_NAME, wholeTableName.toLowerCase());
		}

		if (sql.contains(PART_TABLE_NAME)) {
			sql = FormatSQL.replaceAll(sql, PART_TABLE_NAME, partTableName.toLowerCase());
		}

		if (sql.contains(WHOLE_TABLE_KEYS)) {
			sql = FormatSQL.replaceAll(sql, WHOLE_TABLE_KEYS,
					FormatSQL.columnsToStringWithFallback(wholeTableKeys));
		}

		if (sql.contains(PART_TABLE_KEYS)) {
			sql = FormatSQL.replaceAll(sql, PART_TABLE_KEYS,
					FormatSQL.columnsToStringWithFallback(partTableKeys));
		}

		return sql;
	}

	private String processPlanarSubdivisionConstraint(String sql,
			String planarSubTableName, List<String> planarSubTableKeys) {

		if (sql.contains(COMMENT)) {
			sql = FormatSQL.replaceAll(sql, COMMENT,
					"-- Validate the planar subdivision on " + planarSubTableName);
		}

		if (sql.contains(VAL_PLA_SUB_NAME)) {
			sql = FormatSQL.replaceAll(sql, VAL_PLA_SUB_NAME,
					FormatSQL.validateLengthName(planarSubTableName).toLowerCase());
		}

		if (sql.contains(PLANAR_SUB_TABLE_NAME)) {
			sql = FormatSQL.replaceAll(sql, PLANAR_SUB_TABLE_NAME, planarSubTableName.toLowerCase());
		}

		if (sql.contains(PLANAR_SUB_TABLE_KEYS)) {
			sql = FormatSQL.replaceAll(sql, PLANAR_SUB_TABLE_KEYS,
					FormatSQL.columnsToStringWithFallback(planarSubTableKeys));
		}

		return sql;
	}

	private String processNetworkConstraint(String sql, String arcTableName,
			List<String> arcTableKeys, String nodeTableName,
			List<String> nodeTableKeys) {

		if (sql.contains(COMMENT)) {
			sql = FormatSQL.replaceAll(sql, COMMENT,
					"-- Validate the network between the " + arcTableName
							+ " and " + nodeTableName);
		}

		if (sql.contains(VAL_NETWOK_NAME)) {
			sql = FormatSQL.replaceAll(sql, VAL_NETWOK_NAME,
					(FormatSQL.validateLengthName(arcTableName) + "_"
							+ FormatSQL.validateLengthName(nodeTableName)).toLowerCase());
		}

		if (sql.contains(ARC_TABLE_NAME)) {
			sql = FormatSQL.replaceAll(sql, ARC_TABLE_NAME, arcTableName.toLowerCase());
		}

		if (sql.contains(NODE_TABLE_NAME)) {
			sql = FormatSQL.replaceAll(sql, NODE_TABLE_NAME, nodeTableName.toLowerCase());
		}

		if (sql.contains(ARC_TABLE_KEYS)) {
			sql = FormatSQL.replaceAll(sql, ARC_TABLE_KEYS,
					FormatSQL.columnsToStringWithFallback(arcTableKeys));
		}

		if (sql.contains(NODE_TABLE_KEYS)) {
			sql = FormatSQL.replaceAll(sql, NODE_TABLE_KEYS,
					FormatSQL.columnsToStringWithFallback(nodeTableKeys));
		}

		return sql;
	}

	private String processNetworkConstraint(String sql, String arcTableName,
			List<String> arcTableKeys) {

		if (sql.contains(COMMENT)) {
			sql = FormatSQL.replaceAll(sql, COMMENT,
					"-- Validate the arc-arc network on " + arcTableName);
		}

		if (sql.contains(VAL_NETWOK_NAME)) {
			sql = FormatSQL.replaceAll(sql, VAL_NETWOK_NAME,
					FormatSQL.validateLengthName(arcTableName).toLowerCase());
		}

		if (sql.contains(ARC_TABLE_NAME)) {
			sql = FormatSQL.replaceAll(sql, ARC_TABLE_NAME, arcTableName.toLowerCase());
		}

		if (sql.contains(ARC_TABLE_KEYS)) {
			sql = FormatSQL.replaceAll(sql, ARC_TABLE_KEYS,
					FormatSQL.columnsToStringWithFallback(arcTableKeys));
		}

		return sql;
	}

	private String processIsolineConstraint(String sql,
			String isolineTableName, List<String> isolineTableKeys) {

		if (sql.contains(COMMENT)) {
			sql = FormatSQL.replaceAll(sql, COMMENT,
					"-- Validate the isoline on " + isolineTableName);
		}

		if (sql.contains(VAL_ISOLINE_NAME)) {
			sql = FormatSQL.replaceAll(sql, VAL_ISOLINE_NAME,
					FormatSQL.validateLengthName(isolineTableName).toLowerCase());
		}

		if (sql.contains(ISOLINE_TABLE_NAME)) {
			sql = FormatSQL.replaceAll(sql, ISOLINE_TABLE_NAME, isolineTableName.toLowerCase());
		}

		if (sql.contains(ISOLINE_TABLE_KEYS)) {
			sql = FormatSQL.replaceAll(sql, ISOLINE_TABLE_KEYS,
					FormatSQL.columnsToStringWithFallback(isolineTableKeys));
		}

		return sql;
	}

	private String processTINConstraint(String sql, String TINTableName,
			List<String> TINTableKeys) {

		if (sql.contains(COMMENT)) {
			sql = FormatSQL.replaceAll(sql, COMMENT,
					"-- Validate the TIN on " + TINTableName);
		}

		if (sql.contains(VAL_TIN_NAME)) {
			sql = FormatSQL.replaceAll(sql, VAL_TIN_NAME,
					FormatSQL.validateLengthName(TINTableName).toLowerCase());
		}

		if (sql.contains(TIN_TABLE_NAME)) {
			sql = FormatSQL.replaceAll(sql, TIN_TABLE_NAME, TINTableName.toLowerCase());
		}

		if (sql.contains(TIN_TABLE_KEYS)) {
			sql = FormatSQL.replaceAll(sql, TIN_TABLE_KEYS,
					FormatSQL.columnsToStringWithFallback(TINTableKeys));
		}

		return sql;
	}

	public void appendIsolineConstraint(String isolineTableName,
			List<String> isolineTableKeys) {

		readFile(VALIDATE_ISOLINE_TEMPLATE);

		while (in.hasNextLine()) {
			String sql = in.nextLine();
			sql = processIsolineConstraint(sql, isolineTableName, isolineTableKeys);
			appendSQL(sql);
		}

		in.close();
	}

	public void appendPlanarSubdivisionConstraint(String planarSubTableName,
			List<String> planarSubTableKeys) {

		readFile(VALIDATE_PLANAR_SUBDIVISION_TEMPLATE);

		while (in.hasNextLine()) {
			String sql = in.nextLine();
			sql = processPlanarSubdivisionConstraint(sql, planarSubTableName, planarSubTableKeys);
			appendSQL(sql);
		}

		in.close();
	}

	public void appendTINConstraint(String TINTableName,
			List<String> TINTableKeys) {

		readFile(VALIDATE_TIN_TEMPLATE);

		while (in.hasNextLine()) {
			String sql = in.nextLine();
			sql = processTINConstraint(sql, TINTableName, TINTableKeys);
			appendSQL(sql);
		}

		in.close();
	}

	public void appendSpatialAggregationConstraint(String wholeTableName,
			List<String> wholeTableKeys, String partTableName,
			List<String> partTableKeys) {

		readFile(VALIDATE_SPATIAL_AGGREGATION_TEMPLATE);

		while (in.hasNextLine()) {
			String sql = in.nextLine();
			sql = processSpatialAggegationConstraint(sql, wholeTableName,
					wholeTableKeys, partTableName, partTableKeys);
			appendSQL(sql);
		}

		in.close();
	}

	public void appendNetworkConstraint(String arcTableName,
			List<String> arcTableKeys, String nodeTableName,
			List<String> nodeTableKeys) {

		readFile(VALIDATE_NETWORK_TEMPLATE);

		while (in.hasNextLine()) {
			String sql = in.nextLine();
			sql = processNetworkConstraint(sql, arcTableName, arcTableKeys,
					nodeTableName, nodeTableKeys);
			appendSQL(sql);
		}

		in.close();
	}

	public void appendNetworkConstraint(String arcTableName,
			List<String> arcTableKeys) {

		readFile(VALIDATE_ARC_ARC_NETWORK_TEMPLATE);

		while (in.hasNextLine()) {
			String sql = in.nextLine();
			sql = processNetworkConstraint(sql, arcTableName, arcTableKeys);
			appendSQL(sql);
		}

		in.close();
	}
}
