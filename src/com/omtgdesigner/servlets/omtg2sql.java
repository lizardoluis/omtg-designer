package com.omtgdesigner.servlets;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.omtg2sql.core.OMTG2SQL;
import com.omtg2sql.omtg.model.OMTGSchema;
import com.omtgdesigner.xml.XMLParser;

/**
 * Servlet implementation class omtg2sql
 */
public class omtg2sql extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public omtg2sql() {
        super();
    }
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {			
			// Receives the XML and reads it
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(request.getInputStream()));

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
				OMTG2SQL omtg2sql = new OMTG2SQL(omtgSchema, ddlSW, staticSW, dynamicSW);
				omtg2sql.mapOMTGSchemaToSQL();
				
				// *********************
				//  Remove this
				
//				OMTG2PostGIS omtg2sql = new OMTG2PostGIS(omtgSchema, ddlSW, staticSW, dynamicSW);
//				omtg2sql.mapOMTGSchemaToSQL();
//				
//				System.out.println(xmlString);
//				System.out.println();
//				System.out.println("DDL");
//				System.out.println();
//				System.out.println(ddlSW.toString());
//				System.out.println();
//				System.out.println();
//				System.out.println("Static");
//				System.out.println();
//				System.out.println(staticSW.toString());
//				System.out.println();
//				System.out.println();
//				System.out.println("Dynamic");
//				System.out.println();
//				System.out.println(dynamicSW.toString());
				//***************

				byte[] byteBuffer = createZipFile(ddlSW, staticSW, dynamicSW);

				// Sets HTTP header
				response.setContentType("text/plain; charset=x-user-defined"); 
				response.setContentLength(byteBuffer.length);
				response.setHeader("Content-Disposition", "attachment; filename=\"OMTG.zip\"");

				// Writes the response data
				ServletOutputStream servletOutputStream = response.getOutputStream(); 
				servletOutputStream.write(byteBuffer);
				servletOutputStream.close();
			}

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param ddlSW
	 * @param staticSW
	 * @param dynamicSW
	 * @return
	 */
	private byte[] createZipFile(StringWriter ddlSW, StringWriter staticSW, StringWriter dynamicSW){

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

		try {			

			zipOutputStream.putNextEntry(new ZipEntry("omtg-ddl-structure.sql"));
			byte[] ddlSWBuffer = ddlSW.toString().getBytes();			
			zipOutputStream.write(ddlSWBuffer);
			zipOutputStream.closeEntry();

			if(staticSW.toString().getBytes().length > 0){
				zipOutputStream.putNextEntry(new ZipEntry("omtg-static-control.sql"));
				byte[] staticSWBuffer = staticSW.toString().getBytes();			
				zipOutputStream.write(staticSWBuffer);
				zipOutputStream.closeEntry();
			}

			if(dynamicSW.toString().getBytes().length > 0){
				zipOutputStream.putNextEntry(new ZipEntry("omtg-dynamic-control.sql"));
				byte[] dynamicSWBuffer = dynamicSW.toString().getBytes();			
				zipOutputStream.write(dynamicSWBuffer);
				zipOutputStream.closeEntry();
			}

			zipOutputStream.close();
			byteArrayOutputStream.close();		

			return byteArrayOutputStream.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
