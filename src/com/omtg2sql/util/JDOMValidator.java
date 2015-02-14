package com.omtg2sql.util;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class JDOMValidator {

	private Logger logger;

	public JDOMValidator() throws Exception{
	}

	public Document createSaxBuilder(String schemaUrl, String xmlDocumentUrl) throws Exception {

		this.logger = Logger.getLogger("Log");
		Document doc = null;

		try {
			//Create SAXBuilder object
			SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser", true);

			//Set SAXBuilder parser to be a validating parser 
			saxBuilder.setValidation(true);
			saxBuilder.setFeature("http://apache.org/xml/features/validation/schema", true);
			saxBuilder.setFeature("http://apache.org/xml/features/validation/schema-full-checking",true);
			saxBuilder.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", schemaUrl);

			//Create a ErrorHandler and set ErrorHandler on parser.
			Validator handler = new Validator();
			saxBuilder.setErrorHandler(handler);
			//Parse XML Document
			doc = saxBuilder.build(xmlDocumentUrl);
			//Output Validation Errors
			if (handler.validationError) {
				logger.info("XML Document is not valid. " + handler.saxParseException.getMessage());
				System.out.println("XML Document has error: " + handler.validationError + " " + handler.saxParseException.getMessage());
				throw new Exception("XML Document has error: " + handler.validationError + " " + handler.saxParseException.getMessage());
			}
			logger.info("XML Document is valid");

		} catch (JDOMException e) {
			logger.info(xmlDocumentUrl + " is not well-formed");
			System.out.println(xmlDocumentUrl + " is not well-formed");
//			throw new Exception(xmlDocumentUrl + " is not well-formed");
			return null;
		} catch (IOException e) {
			logger.info("Could not check " + xmlDocumentUrl);
			System.out.println("Could not check " + xmlDocumentUrl);
			return null;
//			throw new Exception("Could not check " + xmlDocumentUrl);
		}
		return doc;
	}
	
	public Document createSaxBuilder(String schemaUrl, StringReader xmlDocument) throws Exception {

		this.logger = Logger.getLogger("Log");
		Document doc = null;

		try {
			//Create SAXBuilder object
			SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser", true);

			//Set SAXBuilder parser to be a validating parser 
			saxBuilder.setValidation(true);
			saxBuilder.setFeature("http://apache.org/xml/features/validation/schema", true);
 			saxBuilder.setFeature("http://apache.org/xml/features/validation/schema-full-checking",true);
			saxBuilder.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", schemaUrl);

			//Create a ErrorHandler and set ErrorHandler on parser.
			Validator handler = new Validator();
			saxBuilder.setErrorHandler(handler);
			//Parse XML Document
			doc = saxBuilder.build(xmlDocument);
			//Output Validation Errors
			if (handler.validationError) {
				logger.info("XML Document is not valid. " + handler.saxParseException.getMessage());
				throw new Exception("XML Document has error: " + handler.validationError + " " + handler.saxParseException.getMessage());
			}
			logger.info("XML Document is valid");

		} catch (JDOMException e) {
			logger.info(xmlDocument + " is not well-formed");
			e.printStackTrace();
			throw new Exception(xmlDocument + " is not well-formed");
		} catch (IOException e) {
			logger.info("Could not check " + xmlDocument);
			throw new Exception("Could not check " + xmlDocument);
		}
		return doc;
	}

	//Error Handler class
	private class Validator extends DefaultHandler {
		public boolean validationError = false;

		public SAXParseException saxParseException = null;

		public void error(SAXParseException exception) throws SAXException {
			validationError = true;
			saxParseException = exception;
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			validationError = true;
			saxParseException = exception;
		}

		public void warning(SAXParseException exception) throws SAXException {
		}
	}
}