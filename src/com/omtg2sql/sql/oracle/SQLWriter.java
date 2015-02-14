package com.omtg2sql.sql.oracle;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

public class SQLWriter {

	private FileWriter fw;
	private BufferedWriter bw;

	public SQLWriter(String sqlFilePath) {

		try {
			this.fw = new FileWriter(sqlFilePath);
			this.bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public SQLWriter(StringWriter sw) {

		this.bw = new BufferedWriter(sw);
	}

	private String indent(int indentSize) {

		String indent = "";
		for (int i = 0; i < indentSize; i++) {
			indent += " ";
		}
		return indent;
	}

	public void appendComment(String comment) {

		try {
			bw.append("-- " + comment);
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void appendSQL(String sql) {

		try {
			bw.append(sql);
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void appendSQL(String sql, int indentSize) {

		try {
			bw.append(indent(indentSize) + sql);
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void appendNewLine() {

		try {
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() throws IOException {
		bw.flush();
		bw.close();
//		fw.close();
	}
}