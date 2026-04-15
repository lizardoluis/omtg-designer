package com.omtg2sql.sql.astpostgis;

import java.io.StringWriter;
import java.util.List;
import java.util.Scanner;

import com.omtg2sql.sql.SQLWriter;
import com.omtg2sql.util.FormatSQL;

public class OffLineConstraintsWriter extends SQLWriter {

	private final String WHOLE_TABLE_NAME = "<WHOLE_TABLE_NAME>";
	private final String PART_TABLE_NAME = "<PART_TABLE_NAME>";
	private final String WHOLE_TABLE_KEYS = "<WHOLE_TABLE_KEYS>";
	private final String PART_TABLE_KEYS = "<PART_TABLE_KEYS>";
	private final String VAL_SPA_AGR_NAME = "<VAL_SPA_AGR_NAME>";
	private final String ARC_TABLE_NAME = "<ARC_TABLE_NAME>";
	private final String NODE_TABLE_NAME = "<NODE_TABLE_NAME>";
	private final String ARC_TABLE_KEYS = "<ARC_TABLE_KEYS>";
	private final String NODE_TABLE_KEYS = "<NODE_TABLE_KEYS>";
	private final String VAL_NETWOK_NAME = "<VAL_NETWOK_NAME>";
	private final String COMMENT = "<COMMENT>";
	private final String VALIDATE_SPATIAL_AGGREGATION_TEMPLATE = "validate_spatial_aggregation_template.sql";
	private final String VALIDATE_NETWORK_TEMPLATE = "validate_arc_node_network_template.sql";
	private final String VALIDATE_ARC_ARC_NETWORK_TEMPLATE = "validate_arc_arc_network_template.sql";

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

			sql = FormatSQL
					.replaceAll(sql, COMMENT,
							"-- Validate the spatial aggregation between the whole "
									+ wholeTableName + " and the part "
									+ partTableName);
		}

		if (sql.contains(VAL_SPA_AGR_NAME)) {

			sql = FormatSQL.replaceAll(sql, VAL_SPA_AGR_NAME,
					FormatSQL.validateLengthName(wholeTableName) + "_"
							+ FormatSQL.validateLengthName(partTableName));
		}

		if (sql.contains(WHOLE_TABLE_NAME)) {

			sql = FormatSQL.replaceAll(sql, WHOLE_TABLE_NAME, wholeTableName);
		}

		if (sql.contains(PART_TABLE_NAME)) {

			sql = FormatSQL.replaceAll(sql, PART_TABLE_NAME, partTableName);
		}

		if (sql.contains(WHOLE_TABLE_KEYS)) {

			sql = FormatSQL.replaceAll(sql, WHOLE_TABLE_KEYS,
					FormatSQL.columnsToString(wholeTableKeys));
		}

		if (sql.contains(PART_TABLE_KEYS)) {

			sql = FormatSQL.replaceAll(sql, PART_TABLE_KEYS,
					FormatSQL.columnsToString(partTableKeys));
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
					FormatSQL.columnsToString(arcTableKeys));
		}

		if (sql.contains(NODE_TABLE_KEYS)) {

			sql = FormatSQL.replaceAll(sql, NODE_TABLE_KEYS,
					FormatSQL.columnsToString(nodeTableKeys));
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
					(FormatSQL.validateLengthName(arcTableName).toLowerCase()));
		}

		if (sql.contains(ARC_TABLE_NAME)) {

			sql = FormatSQL.replaceAll(sql, ARC_TABLE_NAME, arcTableName.toLowerCase());
		}

		if (sql.contains(ARC_TABLE_KEYS)) {

			sql = FormatSQL.replaceAll(sql, ARC_TABLE_KEYS,
					FormatSQL.columnsToString(arcTableKeys));
		}

		return sql;
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

