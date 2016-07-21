package com.omtgdesigner.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.omtg2sql.core.OMTG2Postgis;
import com.omtg2sql.omtg.model.OMTGSchema;
import com.omtgdesigner.utils.Zip;
import com.omtgdesigner.xml.XMLParser;

/**
 * Servlet implementation class omtg2postgis
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
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

				// Maps the omtg to sql
				OMTG2Postgis omtg2postgis = new OMTG2Postgis(omtgSchema, ddlSW, staticSW, dynamicSW);
				omtg2postgis.mapOMTGSchemaToPostgis();
				
				StringWriter constraints = dynamicSW.append(staticSW.toString());
				
				byte[] byteBuffer = Zip.createZipFile(ddlSW, constraints);
				
				// Sets HTTP header
				response.setContentType("text/plain; charset=x-user-defined"); 
				response.setContentLength(byteBuffer.length);
				response.setHeader("Content-Disposition", "attachment; filename=\"OMTG-Postgis.zip\"");

				// Writes the response data
				ServletOutputStream servletOutputStream = response.getOutputStream(); 
				servletOutputStream.write(byteBuffer);
				servletOutputStream.close();

			}
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
