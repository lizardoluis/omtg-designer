/**
 * 
 */
package com.omtgdesigner.xml;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * @author lizardo
 *
 */
public class XMLValidator {

	private static final XMLValidator xmlValidator = new XMLValidator();
	
	private SchemaFactory factory;
	
	/**
	 * Constructor
	 */
	private XMLValidator() {
		factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	}
	
	/**
	 * @return the instance of this singleton class.
	 */
	public static XMLValidator getInstance() {
		return xmlValidator;
	}

	/**
	 * @param xml
	 * @param schema
	 * @return
	 */
	public boolean validateXML(String xsdPath, String xml){
		try {         
            Schema schema = factory.newSchema(new StreamSource(getClass().getResourceAsStream(xsdPath)));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xml)));
        } catch (IOException | SAXException e) {
            return false;
        }
        return true;
	}	
}
