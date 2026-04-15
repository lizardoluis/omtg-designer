package com.omtgdesigner.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.omtgdesigner.xml.XMLValidator;

/**
 * Servlet implementation class XMLImporter
 */
public class XMLImporter extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Validates the XML document;
	 */
	private XMLValidator xmlValidator;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public XMLImporter() {
		xmlValidator = XMLValidator.getInstance();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// Receives the XML and reads it
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(request.getInputStream()));

		if (bufferedReader != null) {
			String xml = bufferedReader.readLine();

			if(!xmlValidator.validateXML("omtg-schema-template.xsd", xml)){
				response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			}
			else{
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
			}			
		}
	}
}
