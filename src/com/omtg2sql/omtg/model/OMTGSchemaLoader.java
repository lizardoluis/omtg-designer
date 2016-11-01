package com.omtg2sql.omtg.model;
import java.io.IOException;
import java.util.Map;

import com.omtg2sql.omtg.classes.OMTGClass;
import com.omtg2sql.omtg.relationships.OMTGRelationship;
import com.omtg2sql.util.XMLParser;


public class OMTGSchemaLoader {

	private XMLParser xmlParser;

	public OMTGSchemaLoader(String xmlfilePath) throws SecurityException, IOException {
		this.xmlParser = new XMLParser(xmlfilePath);
		this.xmlParser.createOMTGSchema(xmlfilePath);
	}
	
//	public OMTGSchemaLoader(StringReader xml) throws SecurityException, IOException {
//		this.xmlParser = new XMLParser("OMTG-Tool");
//		this.xmlParser.createOMTGSchema(xml);
//	}

	public OMTGSchema getOMTGModel() throws CloneNotSupportedException {
		Map<String, OMTGClass> classes = xmlParser.getClasses();
		Map<String, OMTGRelationship> relationships = xmlParser.getRelationships();
		return new OMTGSchema(classes, relationships);
	}
}