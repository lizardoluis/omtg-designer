package com.omtgdesigner.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.omtg2sql.core.OMTG2Postgis;
import com.omtg2sql.omtg.model.OMTGSchema;
import com.omtgdesigner.utils.Zip;
import com.omtgdesigner.xml.XMLParser;

/**
 * Servlet implementation class omtg2postgis.
 * Handles requests to convert OMTG schema to native PostgreSQL/PostGIS SQL.
 * Unlike the ast-postgis module, this generates SQL using native PostGIS
 * types (GEOMETRY) and functions (ST_*) without requiring the ast-postgis extension.
 */
public class omtg2postgis extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public omtg2postgis() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));

			if (bufferedReader != null) {
				String xmlString = bufferedReader.readLine();

				// Parses the received XML.
				XMLParser parser = new XMLParser();
				OMTGSchema omtgSchema = parser.parseOMTGSquema(xmlString);

				// Creates the SQL writers
				StringWriter ddlSW = new StringWriter();
				StringWriter staticSW = new StringWriter();
				StringWriter dynamicSW = new StringWriter();

				// Maps the omtg to native PostGIS SQL
				OMTG2Postgis omtg2postgis = new OMTG2Postgis(omtgSchema, ddlSW, staticSW, dynamicSW);
				omtg2postgis.mapOMTGSchemaToPostgis();

				byte[] byteBuffer = Zip.createZipFile(ddlSW, staticSW, dynamicSW);

				// Sets HTTP header
				response.setContentType("octet/stream; charset=UTF-16");
				response.setContentLength(byteBuffer.length);
				response.setHeader("Content-Disposition", "attachment; filename=\"OMTG-PostGIS.zip\"");

				// Writes the response data
				ServletOutputStream servletOutputStream = response.getOutputStream();
				servletOutputStream.write(byteBuffer);
				servletOutputStream.close();

			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
}
