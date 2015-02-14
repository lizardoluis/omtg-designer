package com.omtg2sql.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;

import com.omtg2sql.omtg.classes.OMTGAttribute;
import com.omtg2sql.omtg.classes.OMTGCardinality;
import com.omtg2sql.omtg.classes.OMTGClass;
import com.omtg2sql.omtg.relationships.OMTGConceptualGeneralization;
import com.omtg2sql.omtg.relationships.OMTGConventionalAggregation;
import com.omtg2sql.omtg.relationships.OMTGConventionalGeneralization;
import com.omtg2sql.omtg.relationships.OMTGConventionalRelationship;
import com.omtg2sql.omtg.relationships.OMTGNetwork;
import com.omtg2sql.omtg.relationships.OMTGRelationship;
import com.omtg2sql.omtg.relationships.OMTGSpatialAggregation;
import com.omtg2sql.omtg.relationships.OMTGTopologicalRelationship;
import com.omtg2sql.omtg.relationships.OMTGUserRestriction;

public class XMLParser {

	private Map<String, OMTGClass> classes;
	private Map<String, OMTGRelationship> relationships;
	private Logger logger;
	private Document doc;

	public XMLParser(String xmlDocName) throws SecurityException, IOException {
		this.classes = new HashMap<String, OMTGClass>();
		this.relationships = new HashMap<String, OMTGRelationship>();
		new Log(xmlDocName);
		this.logger = Logger.getLogger(xmlDocName+"-log");
		//this.generalizations = new HashMap<String, OMTGGeneralization>();		
	}

	private boolean getXMLDocument(String xmlFilePath) {

		//SAXBuilder parser = new SAXBuilder();

		try {
			doc = new JDOMValidator().createSaxBuilder("../omtg-schema-template/omtg-schema-template.xsd", xmlFilePath);
			//doc = parser.build(xmlFilePath);
			return true;
		} catch (Exception e) {

			try {
				doc = new JDOMValidator().createSaxBuilder("./omtg-schema-template/omtg-schema-template.xsd", xmlFilePath);
				return true;
			}
			catch (Exception e2) {
				return false;
			}
			
		}
	}

	private List<String> createAttributeDomain(Element element) {

		if (element.getChild("domain") == null) {
			return null;
		}

		List<String> domain = new ArrayList<String>();
		List<Element> domainList = element.getChild("domain").getChildren("value");
		for (Element domainElement : domainList) {

			domain.add(domainElement.getTextTrim());
		}

		logger.info("domain: " + domain);

		return domain;
	}

	private OMTGAttribute createOMTGAttribute(Element element) {

		String attributeName = element.getChildTextTrim("name");
		String attributeType = element.getChildTextTrim("type");
		String attributeKey = element.getChildTextTrim("key");
		String attributeLength = element.getChildTextTrim("length");
		String attributeScale = element.getChildTextTrim("scale");
		String attributeNotNull = element.getChildTextTrim("not-null");
		String attributeDefault = element.getChildTextTrim("default");
		String attributeSize = element.getChildTextTrim("size");

		logger.info("attributeName: " + attributeName);
		logger.info("attributeType: " + attributeType);
		logger.info("attributeLength: " + attributeLength);
		logger.info("attributeScale: " + attributeScale);
		logger.info("attributeKey: " + attributeKey);
		logger.info("attributeNotNull: " + attributeNotNull);
		logger.info("attributeDefault: " + attributeDefault);
		logger.info("attributeSize: " + attributeSize);

		List<String> domain = createAttributeDomain(element);

		OMTGAttribute attribute = new OMTGAttribute(attributeName, attributeType);

		if (attributeKey != null && attributeKey.equalsIgnoreCase("true")) {
			attribute.setIsKey();
		}

		if (attributeNotNull != null && attributeNotNull.equalsIgnoreCase("true")) {
			attribute.setIsNotNull();
		}

		attribute.setLength(attributeLength);
		attribute.setScale(attributeScale);
		attribute.setDefaultt(attributeDefault);
		attribute.setSize(attributeSize);
		attribute.setDomain(domain);

		return attribute;
	}

	private List<OMTGAttribute> createOMTGAttributes(Element element) {

		List<OMTGAttribute> attributes = new ArrayList<OMTGAttribute>();
		List<Element> attributesList = element.getChild("attributes").getChildren("attribute");
		for (Element attributeElement : attributesList) {

			OMTGAttribute attribute = createOMTGAttribute(attributeElement);
			attributes.add(attribute);
		}
		return attributes;
	}

	private OMTGClass createOMTGClass(Element element) throws SecurityException, IOException {

		String className = element.getChildTextTrim("name");
		String classType = element.getChildTextTrim("type");

		logger.info("className: " + className);
		logger.info("classType: " + classType);

		List<OMTGAttribute> classAtrributes = createOMTGAttributes(element);

		return new OMTGClass(className, classType, classAtrributes);
	}

	private void createOMTGClasses(Element element) throws SecurityException, IOException {

		List<Element> classesList = element.getChild("classes").getChildren("class");
		for (Element classElement : classesList) {

			OMTGClass classOMTG = createOMTGClass(classElement);
			classes.put(classOMTG.getName(), classOMTG);
		}
	}

	private OMTGRelationship createOMTGConventionalRelationship(Element element) {

		String name = element.getChildTextTrim("name");

		String class1Name = element.getChildTextTrim("class1");
		String cardinality1Min = element.getChild("cardinality1").getChildText("min");
		String cardinality1Max = element.getChild("cardinality1").getChildText("max");
		OMTGCardinality cardinality1 = new OMTGCardinality(cardinality1Min, cardinality1Max);

		logger.info("name: " + name);
		logger.info("class1Name: " + class1Name);
		logger.info("cardinality1Min: " + cardinality1Min);
		logger.info("cardinality1Max: " + cardinality1Max);
		logger.info("cardinality1: " + cardinality1);

		String class2Name = element.getChildTextTrim("class2");
		String cardinality2Min = element.getChild("cardinality2").getChildText("min");
		String cardinality2Max = element.getChild("cardinality2").getChildText("max");
		OMTGCardinality cardinality2 = new OMTGCardinality(cardinality2Min, cardinality2Max);

		logger.info("class2Name: " + class2Name);
		logger.info("cardinality2Min: " + cardinality2Min);
		logger.info("cardinality2Max: " + cardinality2Max);
		logger.info("cardinality2: " + cardinality2);

		return new OMTGConventionalRelationship(name, class1Name, cardinality1, class2Name, cardinality2);
	}

	private OMTGRelationship createOMTGTopologicalRelationship(Element element) {

		String spatialRelation = null;
		String distance = null;
		String unit = null;
		
		List<String> spatialRelations = new ArrayList<String>();
		List<Element> spatialRelationList = element.getChild("spatial-relations").getChildren("spatial-relation");
		for (Element spatialRelationElement : spatialRelationList) {
			
			spatialRelation = spatialRelationElement.getTextTrim();
			spatialRelations.add(spatialRelation);
		}
		logger.info("spatialRelations: " + spatialRelations);
		
		List<Element> spatialRelationList2 = element.getChildren("spatial-relations");
		for (Element spatialRelationElement : spatialRelationList2) {
			
			distance = spatialRelationElement.getChildTextTrim("distance");
			unit = spatialRelationElement.getChildTextTrim("unit");
		}
		logger.info("distance: " + distance);
		logger.info("unit: " + unit);

		String class1Name = element.getChildTextTrim("class1");
		String cardinality1Min = element.getChild("cardinality1").getChildText("min");
		String cardinality1Max = element.getChild("cardinality1").getChildText("max");
		OMTGCardinality cardinality1 = new OMTGCardinality(cardinality1Min, cardinality1Max);

		logger.info("class1Name: " + class1Name);
		logger.info("cardinality1Min: " + cardinality1Min);
		logger.info("cardinality1Max: " + cardinality1Max);
		logger.info("cardinality1: " + cardinality1);

		String class2Name = element.getChildTextTrim("class2");
		String cardinality2Min = element.getChild("cardinality2").getChildText("min");
		String cardinality2Max = element.getChild("cardinality2").getChildText("max");
		OMTGCardinality cardinality2 = new OMTGCardinality(cardinality2Min, cardinality2Max);

		logger.info("class2Name: " + class2Name);
		logger.info("cardinality2Min: " + cardinality2Min);
		logger.info("cardinality2Max: " + cardinality2Max);
		logger.info("cardinality2: " + cardinality2);

		return new OMTGTopologicalRelationship(spatialRelations, distance, unit, class1Name, cardinality1, class2Name, cardinality2);
	}

	private OMTGRelationship createOMTGConventionalAggregation(Element element) {

		String class1Name = element.getChildTextTrim("class1");
		String class2Name = element.getChildTextTrim("class2");

		logger.info("class1Name: " + class1Name);
		logger.info("class2Name: " + class2Name);

		return new OMTGConventionalAggregation(class1Name, class2Name);
	}

	private OMTGRelationship createOMTGSpatialAggregation(Element element) {

		String class1Name = element.getChildTextTrim("class1");
		String class2Name = element.getChildTextTrim("class2");

		logger.info("class1Name: " + class1Name);
		logger.info("class2Name: " + class2Name);

		return new OMTGSpatialAggregation(class1Name, class2Name);
	}

	private List<String> createSubclasses(Element element) {

		List<String> subclasses = new ArrayList<String>();
		List<Element> subclassesList = element.getChild("subclasses").getChildren("subclass");
		for (Element subclassElement : subclassesList) {

			subclasses.add(subclassElement.getTextTrim());
		}

		logger.info("subclasses: " + subclasses);

		return subclasses;
	}

	private OMTGRelationship createOMTGConventionalGeneralization(Element element) {

		String superclass = element.getChildTextTrim("superclass");
		String participation = element.getChildTextTrim("participation");
		String disjointness = element.getChildTextTrim("disjointness");

		logger.info("superclass: " + superclass);
		logger.info("participation: " + participation);
		logger.info("disjointness: " + disjointness);

		List<String> subclasses = createSubclasses(element); 

		return new OMTGConventionalGeneralization(participation, disjointness, superclass, subclasses);
	}

	private OMTGRelationship createOMTGConceptualGeneralization(Element element) {

		String superclass = element.getChildTextTrim("superclass");
		String scaleShape = element.getChildTextTrim("scale-shape");
		String disjointness = element.getChildTextTrim("disjointness");

		logger.info("superclass: " + superclass);
		logger.info("scaleShape: " + scaleShape);
		logger.info("disjointness: " + disjointness);

		List<String> subclasses = createSubclasses(element); 

		return new OMTGConceptualGeneralization(scaleShape, disjointness, superclass, subclasses);
	}

	private OMTGRelationship createOMTGNetwork(Element element) {

		String networkName = element.getChildTextTrim("name");
		String class1Name = element.getChildTextTrim("class1");
		String class2Name = element.getChildTextTrim("class2");

		logger.info("networkName: " + networkName);
		logger.info("class1Name: " + class1Name);
		logger.info("class2Name: " + class2Name);

		return new OMTGNetwork(networkName, class1Name, class2Name);
	}

	private OMTGRelationship createOMTGUserRestriction(Element element) {

		String class1Name = element.getChildTextTrim("class1");
		logger.info("class1Name: " + class1Name);
		
		String spatialRelation = null;
		String distance = null;
		String unit = null;
		
		List<String> spatialRelations = new ArrayList<String>();
		List<Element> spatialRelationList = element.getChild("spatial-relations").getChildren("spatial-relation");
		for (Element spatialRelationElement : spatialRelationList) {
			
			spatialRelation = spatialRelationElement.getTextTrim();
			spatialRelations.add(spatialRelation);
		}
		logger.info("spatialRelations: " + spatialRelations);
		
		List<Element> spatialRelationList2 = element.getChildren("spatial-relations");
		for (Element spatialRelationElement : spatialRelationList2) {
			
			distance = spatialRelationElement.getChildTextTrim("distance");
			unit = spatialRelationElement.getChildTextTrim("unit");
		}
		logger.info("distance: " + distance);
		logger.info("unit: " + unit);

		String canOccur = element.getChildTextTrim("can-occur");
		logger.info("canOccur: " + canOccur);

		String class2Name = element.getChildTextTrim("class2");
		logger.info("class2Name: " + class2Name);

		boolean spatialRelationCanOccur = false;
		if (canOccur.equalsIgnoreCase("true")) {
			spatialRelationCanOccur = true;
		}

		return new OMTGUserRestriction(class1Name, class2Name, spatialRelations, spatialRelationCanOccur);
	}

	private void createOMTGRelationships(Element element) {

		OMTGRelationship rel = null;
		List<Element> relationshipsList = element.getChild("relationships").getChildren("conventional");
		for (Element relationshipElement : relationshipsList) {
			logger.info("relationship conventional");
			rel = createOMTGConventionalRelationship(relationshipElement);
			relationships.put(rel.getName(), rel);
		}

		relationshipsList = element.getChild("relationships").getChildren("topological");
		for (Element relationshipElement : relationshipsList) {
			logger.info("relationship topological");
			rel = createOMTGTopologicalRelationship(relationshipElement);
			relationships.put(rel.getName(), rel);
		}

		relationshipsList = element.getChild("relationships").getChildren("conventional-aggregation");
		for (Element relationshipElement : relationshipsList) {
			logger.info("relationship conventional-aggregation");
			rel = createOMTGConventionalAggregation(relationshipElement);
			relationships.put(rel.getName(), rel);
		}

		relationshipsList = element.getChild("relationships").getChildren("spatial-aggregation");
		for (Element relationshipElement : relationshipsList) {
			logger.info("relationship spatial-aggregation");
			rel = createOMTGSpatialAggregation(relationshipElement);
			relationships.put(rel.getName(), rel);
		}

		relationshipsList = element.getChild("relationships").getChildren("network");
		for (Element relationshipElement : relationshipsList) {
			logger.info("relationship network");
			rel = createOMTGNetwork(relationshipElement);
			relationships.put(rel.getName(), rel);
		}

		relationshipsList = element.getChild("relationships").getChildren("generalization");
		for (Element relationshipElement : relationshipsList) {
			logger.info("relationship generalization");
			rel = createOMTGConventionalGeneralization(relationshipElement);
			relationships.put(rel.getName(), rel);
		}

		relationshipsList = element.getChild("relationships").getChildren("conceptual-generalization");
		for (Element relationshipElement : relationshipsList) {
			logger.info("relationship conceptual-generalization");
			rel = createOMTGConceptualGeneralization(relationshipElement);
			relationships.put(rel.getName(), rel);
		}

		relationshipsList = element.getChild("relationships").getChildren("user-restriction");
		for (Element relationshipElement : relationshipsList) {
			logger.info("user restriction");
			rel = createOMTGUserRestriction(relationshipElement);
			relationships.put(rel.getName(), rel);
		}
	}

	public void createOMTGSchema(String xmlFilePath) {

		try {

			boolean XMLisValid = getXMLDocument(xmlFilePath);

			if (XMLisValid) {

				Element rootElement = doc.getRootElement();

				logger.info("Reading xml document: " + xmlFilePath);

				createOMTGClasses(rootElement);
				createOMTGRelationships(rootElement);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<String, OMTGClass> getClasses() {
		return classes;
	}

	public Map<String, OMTGRelationship> getRelationships() {
		return relationships;
	}

	//	public static void main(String[] args) {
	//
	//		XMLParser xmlParser;
	//		try {
	//			xmlParser = new XMLParser();
	//			xmlParser.createOMTGSchema("XML/teste.xml");
	//		} catch (SecurityException e1) {
	//			// TODO Auto-generated catch block
	//			e1.printStackTrace();
	//		} catch (IOException e1) {
	//			e1.printStackTrace();
	//		} catch (JDOMException e) {
	//			e.printStackTrace();
	//		}
	//	}

	//		public static void main(String[] args) throws JDOMException, IOException {
	//	
	//			SAXBuilder parser = new SAXBuilder();
	//			Document doc = parser.build("XMLs/conceptual-model-document.xml");
	//	
	//			Element rootElement = doc.getRootElement();
	//	
	//			Element dbPropertiesElement = rootElement.getChild("db-properties");
	//	
	//			String dbType = dbPropertiesElement.getChildText("db-type");
	//		String database = dbPropertiesElement.getChildText("database");
	//		String username = dbPropertiesElement.getChildText("username");
	//		String password = dbPropertiesElement.getChildText("password");
	//		String portNumber = dbPropertiesElement.getChildText("port-number");
	//		String xmlDocName = dbPropertiesElement.getChildText("xml-doc-name");
	//
	//		List<Element> classesList = rootElement.getChild("conceptual-model").getChild("classes").getChildren("class");
	//		for (Element classElement : classesList) {
	//
	//			String name = classElement.getChildText("name");
	//			String type = classElement.getChildText("type");
	//			String tableName = classElement.getChildText("table-name");
	//			String geometryColumn = classElement.getChildText("geometry-column");
	//			String numberOfRegisters = classElement.getChildText("number-of-registers");
	//
	//			//System.out.println(name);
	//
	//			List<Element> attributesList = classElement.getChild("attributes").getChildren("attribute");
	//			for (Element attributeElement : attributesList) {
	//
	//				String nameAtt = attributeElement.getChildText("name");
	//				String typeAtt = attributeElement.getChildText("type");
	//				String columnNameAtt = attributeElement.getChildText("column-name");
	//
	//				//System.out.println(columnNameAtt);
	//
	//			}
	//		}
	//
	//		List<Element> relationsShipList = rootElement.getChild("conceptual-model").getChild("relationships").getChildren("relationship");
	//		for (Element relationshipElement : relationsShipList) {
	//
	//			String name = relationshipElement.getChildText("name");
	//			String class1 = relationshipElement.getChildText("class1");
	//			String min1 = relationshipElement.getChild("cardinality1").getChildText("min");
	//			String max1 = relationshipElement.getChild("cardinality1").getChildText("max");
	//			String min2 = relationshipElement.getChild("cardinality2").getChildText("min");
	//			String max2 = relationshipElement.getChild("cardinality2").getChildText("max");
	//
	//			System.out.println(name);
	//		}
	//	}
}