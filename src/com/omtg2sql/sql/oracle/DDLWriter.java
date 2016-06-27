package com.omtg2sql.sql.oracle;

import java.io.StringWriter;
import java.util.List;

import com.omtg2sql.sql.SQLWriter;
import com.omtg2sql.util.FormatSQL;

public class DDLWriter extends SQLWriter {

	private boolean create_sa_aux_table, create_spatial_error_table;

	public DDLWriter(String sqlFilePath) {
		super(sqlFilePath);
		create_sa_aux_table = true;
		create_spatial_error_table = true;
	}

	public DDLWriter(StringWriter sw) {
		super(sw);
		create_sa_aux_table = true;
		create_spatial_error_table = true;
	}

	public void appendCreateTable(String tableName, List<String> columnsName, List<String> columnsType,
			List<String> length, List<String> scale, List<String> keysName, List<Boolean> notNullColumns,
			List<String> defaultColumns, String spatialType, List<List<String>> domainColumns, List<String> sizeColumns,
			boolean hasDomain) {

		appendComment("Create table " + tableName);
		appendSQL("CREATE TABLE " + tableName + " (");
		appendColumns(columnsName, columnsType, length, scale, notNullColumns, defaultColumns, spatialType, sizeColumns,
				keysName.size(), hasDomain);
		appendCheckConsraints(domainColumns, columnsName, keysName.size());
		if (keysName.size() > 0)
			appendSQL("CONSTRAINT pk_" + tableName + " PRIMARY KEY (" + FormatSQL.toString(keysName) + ")", 2);
		appendSQL(");");
		appendSQL("/");
	}

	public void appendCreateTable(String tableName, String tableA, String tableB, List<String> columnsNameM,
			List<String> columnsTypeM, List<String> lengthM, List<String> scaleM, List<String> columnsNameN,
			List<String> columnsTypeN, List<String> lengthN, List<String> scaleN) {

		appendComment("Create table " + tableName);
		appendSQL("CREATE TABLE " + tableName + " (");
		appendColumnsWithComma(columnsNameM, columnsTypeM, lengthM, scaleM, tableA);
		appendColumnsWithComma(columnsNameN, columnsTypeN, lengthN, scaleN, tableB);
		appendSQL("CONSTRAINT pk_" + tableName + " PRIMARY KEY (" + FormatSQL.toString(columnsNameM, tableA) + ","
				+ FormatSQL.toString(columnsNameN, tableB) + ")", 2);
		appendSQL(");");
		appendSQL("/");
	}

	public void appendAlterTableAddColumn(String relationshipName, String mainTableName, String secTableName,
			List<String> keysName, List<String> keysType, List<String> length, List<String> scale) {

		appendComment("Add new column (foreign key) on table " + mainTableName + " due " + relationshipName);
		appendSQL("ALTER TABLE " + mainTableName + " ADD (");
		appendColumns(keysName, keysType, length, scale, secTableName);
		appendSQL(");");
		appendSQL("/");
	}

	private void createMultivaluedTable(String tableName, String baseTableName, List<String> keysName,
			List<String> keysType, List<String> keysLength, List<String> keysScale, String multivaluedColumnName,
			String multivaluedColumnType, String multivaluedColumnLength, String multivaluedColumnScale) {

		appendComment("Create multivalued table " + tableName);
		appendSQL("CREATE TABLE " + tableName + " (");
		appendColumnsWithComma(keysName, keysType, keysLength, keysScale, baseTableName);
		appendColumn(multivaluedColumnName, OMTG2OracleSQLMapper.mapAttributeType(multivaluedColumnType,
				multivaluedColumnLength, multivaluedColumnScale), true);
		appendSQL("CONSTRAINT pk_" + tableName + " PRIMARY KEY (" + FormatSQL.toString(keysName, baseTableName) + ","
				+ multivaluedColumnName + "),", 2);
		appendSQL("CONSTRAINT fk_" + tableName + "_ref_" + baseTableName, 2);
		appendSQL("FOREIGN KEY (" + FormatSQL.toString(keysName, baseTableName) + ")", 4);
		appendSQL("REFERENCES " + baseTableName + "(" + FormatSQL.toString(keysName) + ")", 4);
		appendSQL(");");
		appendSQL("/");
	}

	public void appendAlterTableAddForeignKeyConstraints(String relationshipName, String mainTableName,
			String secTableName, List<String> keysName) {

		appendComment("Add foreign key constraint on table " + mainTableName + " due " + relationshipName);
		appendSQL("ALTER TABLE " + mainTableName + " ADD");
		appendSQL("CONSTRAINT fk_" + FormatSQL.validateLengthName(mainTableName) + "_ref_"
				+ FormatSQL.validateLengthName(secTableName), 2);
		appendSQL("FOREIGN KEY (" + FormatSQL.toString(keysName, secTableName) + ")", 2);
		appendSQL("REFERENCES " + secTableName + "(" + FormatSQL.toString(keysName) + ");", 2);
		appendSQL("/");
	}

	public void appendAlterTableAddForeignKeyConstraintsMN(String relationshipName, String mainTableName,
			String tableNameM, List<String> keysNameTableM, String tableNameN, List<String> keysNameTableN) {

		appendAlterTableAddForeignKeyConstraints(relationshipName, mainTableName, tableNameM, keysNameTableM);
		appendAlterTableAddForeignKeyConstraints(relationshipName, mainTableName, tableNameN, keysNameTableN);
	}

	private void appendColumn(String columnName, String columnType, String defaultValue, boolean notNullValue) {

		appendColumn(columnName, columnType, defaultValue, notNullValue, null, true);
	}

	private void appendColumn(String columnName, String columnType, boolean comma) {

		appendColumn(columnName, columnType, null, false, null, comma);
	}

	private void appendColumnWithComma(String columnName, String columnType, String prefix, boolean hasComma) {

		appendColumn(columnName, columnType, null, false, prefix, hasComma);
	}

	private void appendColumn(String columnName, String columnType, String defaultValue, boolean notNullValue,
			String prefix, boolean hasComma) {

		String sql = columnName.trim() + " " + columnType.trim();
		if (prefix != null) {
			sql = prefix + "_" + sql;
		}

		if (defaultValue != null) {
			sql += " DEFAULT '" + defaultValue + "'";
		}

		if (notNullValue == true) {
			sql += " NOT NULL";
		}
		if (hasComma)
			sql += ",";

		appendSQL(sql, 2);
	}

	private void appendColumns(List<String> columns, List<String> types, List<String> length, List<String> scale,
			String prefix) {

		for (int i = 0; i < columns.size(); i++) {
			appendColumnWithComma(columns.get(i),
					OMTG2OracleSQLMapper.mapAttributeType(types.get(i), length.get(i), scale.get(i)), prefix,
					i == columns.size() - 1 ? false : true);
		}
	}

	private void appendColumnsWithComma(List<String> columns, List<String> types, List<String> length,
			List<String> scale, String prefix) {

		for (int i = 0; i < columns.size(); i++) {
			appendColumnWithComma(columns.get(i),
					OMTG2OracleSQLMapper.mapAttributeType(types.get(i), length.get(i), scale.get(i)), prefix, true);
		}
	}

	private void appendColumns(List<String> columnsName, List<String> columnsType, List<String> length,
			List<String> scale, List<Boolean> notNullColumns, List<String> defaultColumns, String spatialType,
			List<String> sizeColumns, int numberPrimaryKeys, boolean hasDomain) {

		for (int i = 0; i < columnsName.size(); i++) {

			// not append the column that is multivalued
			if (sizeColumns.get(i) == null || sizeColumns.get(i).equalsIgnoreCase("1")) {
				appendColumn(columnsName.get(i),
						OMTG2OracleSQLMapper.mapAttributeType(columnsType.get(i), length.get(i), scale.get(i)),
						defaultColumns.get(i), notNullColumns.get(i));
			}
		}

		// append the spatial column

		if (spatialType.equalsIgnoreCase("tesselation")) {
			if (numberPrimaryKeys > 0 || hasDomain) {
				appendColumn("geom", "MDSYS.SDO_GEORASTER", true);
			} else {
				appendColumn("geom", "MDSYS.SDO_GEORASTER", false);
			}
		} else if (!spatialType.equalsIgnoreCase("conventional")) {
			if (numberPrimaryKeys > 0 || hasDomain) {
				appendColumn("geom", "MDSYS.SDO_GEOMETRY", true);
			} else {
				appendColumn("geom", "MDSYS.SDO_GEOMETRY", false);
			}
		}
	}

	public void appendInsertIntoUserSdoGeomMetadata(String tableName, String srid) {

		appendInsertIntoUserSdoGeomMetadata(tableName, srid, "geom");
	}

	public void appendInsertIntoUserSdoGeomMetadata(String tableName, String srid, String geomColumnName) {

		appendComment("Insert the geom column of " + tableName + " into metadata table USER_SDO_GEOM_METADATA");
		appendSQL("INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID)");
		appendSQL("VALUES ('" + tableName + "', '" + geomColumnName + "',", 2);
		appendSQL("MDSYS.SDO_DIM_ARRAY", 4);
		appendSQL("(MDSYS.SDO_DIM_ELEMENT('X', -180.000000000, 180.000000000, 0.005),", 6);
		appendSQL("MDSYS.SDO_DIM_ELEMENT('Y', -90.000000000, 90.000000000, 0.005)),", 6);
		appendSQL("'29100');", 4); // default srid 29100
		appendSQL("/");
	}

	public void appendCreateSpatialIndex(String tableName, String geomType) {

		appendCreateSpatialIndex(tableName, geomType, "geom");
	}

	public void appendCreateSpatialIndex(String tableName, String geomType, String geomColumnName) {

		appendComment("Create the spatial index on geom column of " + tableName);
		appendSQL("CREATE INDEX SIDX_" + tableName + " ON " + tableName + "(" + geomColumnName + ")");
		appendSQL("INDEXTYPE IS MDSYS.SPATIAL_INDEX", 2);
		appendSQL("PARAMETERS ('SDO_INDX_DIMS=2, LAYER_GTYPE=" + geomType + "');", 2);
		appendSQL("/");
	}

	public void createMultivaluedTables(String tableName, List<String> columnsName, List<String> columnsType,
			List<String> columnsLength, List<String> columnsScale, List<String> keysName, List<String> keysType,
			List<String> KeysLength, List<String> keysScale, List<String> sizeAttribute) {

		for (int i = 0; i < sizeAttribute.size(); i++) {

			// null and 1 = not multivalued attribute
			if (sizeAttribute.get(i) != null && !sizeAttribute.get(i).equals("1")) {
				createMultivaluedTable(columnsName.get(i) + "_" + tableName, tableName, keysName, keysType, KeysLength,
						keysScale, columnsName.get(i), columnsType.get(i), columnsLength.get(i), columnsScale.get(i));
			}
		}
	}

	private void appendCheckConsraints(List<List<String>> domainColumns, List<String> columnsName,
			int numberOfPrimaryKeys) {

		for (int i = 0; i < domainColumns.size(); i++) {

			if (domainColumns.get(i) != null) {

				String check = "CONSTRAINT CHECK_" + columnsName.get(i) + " CHECK (" + columnsName.get(i) + " IN ("
						+ FormatSQL.toStringWithTokenSeparator(domainColumns.get(i)) + "))";

				if (numberOfPrimaryKeys > 0) {
					appendSQL(check + ",", 2);
				} else {
					appendSQL(check, 2);
				}
			}
		}
	}

	public void createSaAuxTable() {

		if (create_sa_aux_table) {

			appendComment("Create the Sa_aux table to support spatial aggregation constraint");
			appendSQL("CREATE TABLE Sa_aux (");
			appendColumn("w_rowid", "ROWID", true);
			appendColumn("p_rowid", "ROWID", true);
			appendColumn("p_geom", "MDSYS.SDO_GEOMETRY", false);
			appendSQL(");");
			appendSQL("/");
		}

		create_sa_aux_table = false;
	}

	public void createSpatialErrorTable() {

		if (create_spatial_error_table) {

			appendComment("Create the Spatial_error table to contains spatial integrity constraint error messages");
			appendSQL("CREATE TABLE Spatial_error (");
			appendColumn("type", "VARCHAR2(100)", true);
			appendColumn("error", "VARCHAR2(500)", false);
			appendSQL(");");
			appendSQL("/");
		}

		create_spatial_error_table = false;
	}
}