package com.omtg2sql.sql.postgis;

import java.io.StringWriter;
import java.util.List;

import com.omtg2sql.sql.SQLWriter;
import com.omtg2sql.util.FormatSQL;

/**
 * DDL Writer for native PostgreSQL/PostGIS.
 * Generates CREATE TABLE statements using native PostGIS GEOMETRY types.
 */
public class DDLWriter extends SQLWriter {

	private boolean create_spatial_error_table;

	public DDLWriter(String sqlFilePath) {
		super(sqlFilePath);
		create_spatial_error_table = true;
	}

	public DDLWriter(StringWriter sw) {
		super(sw);
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
		appendCheckConstraints(domainColumns, columnsName, keysName.size());
		if (keysName.size() > 0)
			appendSQL("CONSTRAINT pk_" + tableName + " PRIMARY KEY (" + FormatSQL.toString(keysName) + ")", 2);
		appendSQL(");");
		appendNewLine();
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
		appendNewLine();
	}

	public void appendAlterTableAddColumn(String relationshipName, String mainTableName, String secTableName,
			List<String> keysName, List<String> keysType, List<String> length, List<String> scale) {

		appendComment("Add new column (foreign key) on table " + mainTableName + " due " + relationshipName);
		appendSQL("ALTER TABLE " + mainTableName);

		for (int i = 0; i < keysName.size(); i++) {
			appendColumn("ADD COLUMN", keysName.get(i),
					OMTG2PostgisMapper.mapAttributeType(keysType.get(i), length.get(i), scale.get(i)),
					null, false, secTableName, i == keysName.size() - 1 ? false : true);
		}

		appendNewLine();
	}

	private void createMultivaluedTable(String tableName, String baseTableName, List<String> keysName,
			List<String> keysType, List<String> keysLength, List<String> keysScale, String multivaluedColumnName,
			String multivaluedColumnType, String multivaluedColumnLength, String multivaluedColumnScale) {

		appendComment("Create multivalued table " + tableName);
		appendSQL("CREATE TABLE " + tableName + " (");
		appendColumnsWithComma(keysName, keysType, keysLength, keysScale, baseTableName);
		appendColumn(multivaluedColumnName, OMTG2PostgisMapper.mapAttributeType(multivaluedColumnType,
				multivaluedColumnLength, multivaluedColumnScale), true);
		appendSQL("CONSTRAINT pk_" + tableName + " PRIMARY KEY (" + FormatSQL.toString(keysName, baseTableName) + ","
				+ multivaluedColumnName + "),", 2);
		appendSQL("CONSTRAINT fk_" + tableName + "_ref_" + baseTableName, 2);
		appendSQL("FOREIGN KEY (" + FormatSQL.toString(keysName, baseTableName) + ")", 4);
		appendSQL("REFERENCES " + baseTableName + "(" + FormatSQL.toString(keysName) + ")", 4);
		appendSQL(");");
		appendNewLine();
	}

	public void appendAlterTableAddForeignKeyConstraints(String relationshipName, String mainTableName,
			String secTableName, List<String> keysName) {

		appendComment("Add foreign key constraint on table " + mainTableName + " due " + relationshipName);
		appendSQL("ALTER TABLE " + mainTableName + " ADD");
		appendSQL("CONSTRAINT fk_" + FormatSQL.validateLengthName(mainTableName) + "_ref_"
				+ FormatSQL.validateLengthName(secTableName), 2);
		appendSQL("FOREIGN KEY (" + FormatSQL.toString(keysName, secTableName) + ")", 2);
		appendSQL("REFERENCES " + secTableName + "(" + FormatSQL.toString(keysName) + ");", 2);
		appendNewLine();
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

	private void appendColumn(String addColumn, String columnName, String columnType, String defaultValue,
			boolean notNullValue, String prefix, boolean hasComma) {

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
		else
			sql += ";";

		appendSQL(addColumn + " " + sql, 2);
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

	private void appendColumnsWithComma(List<String> columns, List<String> types, List<String> length,
			List<String> scale, String prefix) {

		for (int i = 0; i < columns.size(); i++) {
			appendColumnWithComma(columns.get(i),
					OMTG2PostgisMapper.mapAttributeType(types.get(i), length.get(i), scale.get(i)), prefix, true);
		}
	}

	private void appendColumns(List<String> columnsName, List<String> columnsType, List<String> length,
			List<String> scale, List<Boolean> notNullColumns, List<String> defaultColumns, String spatialType,
			List<String> sizeColumns, int numberPrimaryKeys, boolean hasDomain) {

		String geomType = OMTG2PostgisMapper.mapClassType(spatialType);
		boolean hasSpatialColumn = !spatialType.equalsIgnoreCase("conventional") && !geomType.isEmpty();
		boolean hasFollowingContent = hasSpatialColumn || numberPrimaryKeys > 0 || hasDomain;

		int lastColumnIndex = -1;
		for (int i = columnsName.size() - 1; i >= 0; i--) {
			if (sizeColumns.get(i) == null || sizeColumns.get(i).equalsIgnoreCase("1")) {
				lastColumnIndex = i;
				break;
			}
		}

		for (int i = 0; i < columnsName.size(); i++) {

			// not append the column that is multivalued
			if (sizeColumns.get(i) == null || sizeColumns.get(i).equalsIgnoreCase("1")) {
				boolean needsComma = (i != lastColumnIndex) || hasFollowingContent;
				appendColumn(columnsName.get(i),
						OMTG2PostgisMapper.mapAttributeType(columnsType.get(i), length.get(i), scale.get(i)),
						defaultColumns.get(i), notNullColumns.get(i), null, needsComma);
			}
		}

		// append the spatial column using native PostGIS GEOMETRY type
		if (hasSpatialColumn) {
			if (numberPrimaryKeys > 0 || hasDomain) {
				appendColumn("geom", geomType, true);
			} else {
				appendColumn("geom", geomType, false);
			}
		}
	}

	public void appendCreateSpatialIndex(String tableName) {

		appendComment("Create the spatial index on geom column of " + tableName);
		appendSQL("CREATE INDEX sidx_" + tableName.toLowerCase());
		appendSQL("ON " + tableName, 2);
		appendSQL("USING GIST (geom);", 2);
		appendNewLine();
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

	private void appendCheckConstraints(List<List<String>> domainColumns, List<String> columnsName,
			int numberOfPrimaryKeys) {

		for (int i = 0; i < domainColumns.size(); i++) {

			if (domainColumns.get(i) != null) {

				String check = "CONSTRAINT check_" + columnsName.get(i).toLowerCase() + " CHECK (" + columnsName.get(i)
						+ " IN (" + FormatSQL.toStringWithTokenSeparator(domainColumns.get(i)) + "))";

				if (numberOfPrimaryKeys > 0) {
					appendSQL(check + ",", 2);
				} else {
					appendSQL(check, 2);
				}
			}
		}
	}

	public void createSpatialErrorTable() {

		if (create_spatial_error_table) {

			appendComment("Create the spatial_error table to store spatial integrity constraint error messages");
			appendSQL("CREATE TABLE IF NOT EXISTS spatial_error (");
			appendColumn("id", "SERIAL PRIMARY KEY", true);
			appendColumn("error_type", "VARCHAR(100)", true);
			appendColumn("error_message", "VARCHAR(500)", true);
			appendColumn("created_at", "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", false);
			appendSQL(");");
			appendNewLine();
		}

		create_spatial_error_table = false;
	}
}
