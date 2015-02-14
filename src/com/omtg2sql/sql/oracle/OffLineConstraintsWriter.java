package com.omtg2sql.sql.oracle;

import java.io.StringWriter;
import java.util.List;
import java.util.Scanner;

import com.omtg2sql.util.FormatSQL;

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
	private final String VALIDATE_TIN_TEMPLATE = "validate_tin_template.sql";

	private final String AUX_GET_POINT_FUNCTION = "aux_get_point.sql";
	private final String AUX_JOIN_GEOMETRY_FUNCTION = "aux_join_geometry.sql";

	private Scanner in;
	private boolean create_aux_get_point_function,
	create_aux_join_geometry_function;

	public OffLineConstraintsWriter(String sqlFilePath) {
		super(sqlFilePath);
		create_aux_get_point_function = true;
		create_aux_join_geometry_function = true;
	}

	public OffLineConstraintsWriter(StringWriter sw) {
		super(sw);
		create_aux_get_point_function = true;
		create_aux_join_geometry_function = true;
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

	private String processPlanarSubdivisionConstraint(String sql,
			String planarSubTableName, List<String> planarSubTableKeys) {

		if (sql.contains(COMMENT)) {

			sql = FormatSQL.replaceAll(sql, COMMENT,
					"-- Validate the planar subdivision on "
							+ planarSubTableName);
		}

		if (sql.contains(VAL_PLA_SUB_NAME)) {

			sql = FormatSQL.replaceAll(sql, VAL_PLA_SUB_NAME,
					FormatSQL.validateLengthName(planarSubTableName));
		}

		if (sql.contains(PLANAR_SUB_TABLE_NAME)) {

			sql = FormatSQL.replaceAll(sql, PLANAR_SUB_TABLE_NAME,
					planarSubTableName);
		}

		if (sql.contains(PLANAR_SUB_TABLE_KEYS)) {

			sql = FormatSQL.replaceAll(sql, PLANAR_SUB_TABLE_KEYS,
					FormatSQL.columnsToString(planarSubTableKeys));
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
					FormatSQL.validateLengthName(arcTableName) + "_"
							+ FormatSQL.validateLengthName(nodeTableName));
		}

		if (sql.contains(ARC_TABLE_NAME)) {

			sql = FormatSQL.replaceAll(sql, ARC_TABLE_NAME, arcTableName);
		}

		if (sql.contains(NODE_TABLE_NAME)) {

			sql = FormatSQL.replaceAll(sql, NODE_TABLE_NAME, nodeTableName);
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

	private String processIsolineConstraint(String sql,
			String isolineTableName, List<String> isolineTableKeys) {

		if (sql.contains(COMMENT)) {

			sql = FormatSQL.replaceAll(sql, COMMENT,
					"-- Validate the isoline on " + isolineTableName);
		}

		if (sql.contains(VAL_ISOLINE_NAME)) {

			sql = FormatSQL.replaceAll(sql, VAL_ISOLINE_NAME,
					FormatSQL.validateLengthName(isolineTableName));
		}

		if (sql.contains(ISOLINE_TABLE_NAME)) {

			sql = FormatSQL.replaceAll(sql, ISOLINE_TABLE_NAME,
					isolineTableName);
		}

		if (sql.contains(ISOLINE_TABLE_NAME)) {

			sql = FormatSQL.replaceAll(sql, ISOLINE_TABLE_NAME,
					isolineTableName);
		}

		if (sql.contains(ISOLINE_TABLE_KEYS)) {

			sql = FormatSQL.replaceAll(sql, ISOLINE_TABLE_KEYS,
					FormatSQL.columnsToString(isolineTableKeys));
		}

		return sql;
	}

	private String processTINConstraint(String sql, String TINTableName,
			List<String> TINTableKeys) {

		if (sql.contains(COMMENT)) {

			sql = FormatSQL.replaceAll(sql, COMMENT, "-- Validate the TIN on "
					+ TINTableName);
		}

		if (sql.contains(VAL_TIN_NAME)) {

			sql = FormatSQL.replaceAll(sql, VAL_TIN_NAME,
					FormatSQL.validateLengthName(TINTableName));
		}

		if (sql.contains(TIN_TABLE_NAME)) {

			sql = FormatSQL.replaceAll(sql, TIN_TABLE_NAME, TINTableName);
		}

		if (sql.contains(TIN_TABLE_KEYS)) {

			sql = FormatSQL.replaceAll(sql, TIN_TABLE_KEYS,
					FormatSQL.columnsToString(TINTableKeys));
		}

		return sql;
	}

	public void appendIsolineConstraint(String isolineTableName,
			List<String> isolineTableKeys) {

		readFile(VALIDATE_ISOLINE_TEMPLATE);

		while (in.hasNextLine()) {

			String sql = in.nextLine();
			sql = processIsolineConstraint(sql, isolineTableName,
					isolineTableKeys);
			appendSQL(sql);
		}

		in.close();
	}

	public void appendPlanarSubdivisionConstraint(String planarSubTableName,
			List<String> planarSubTableKeys) {

		readFile(VALIDATE_PLANAR_SUBDIVISION_TEMPLATE);

		while (in.hasNextLine()) {

			String sql = in.nextLine();
			sql = processPlanarSubdivisionConstraint(sql, planarSubTableName,
					planarSubTableKeys);
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

	public void appendGetPointFunction() {

		if (create_aux_get_point_function) {

			readFile(AUX_GET_POINT_FUNCTION);

			while (in.hasNextLine()) {

				String sql = in.nextLine();
				appendSQL(sql);
			}

			in.close();
			// create only one time the aux_get_point function
			create_aux_get_point_function = false;
		}
	}

	public void appendJoinGeometryFunction() {

		if (create_aux_join_geometry_function) {

			readFile(AUX_JOIN_GEOMETRY_FUNCTION);

			while (in.hasNextLine()) {

				String sql = in.nextLine();
				appendSQL(sql);
			}

			in.close();
			// create only one time the aux_join_geometry function
			create_aux_join_geometry_function = false;
		}
	}
}