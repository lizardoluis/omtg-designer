/**
 * 
 */
package com.omtgdesigner.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.omtg2sql.omtg.classes.OMTGAttribute;
import com.omtg2sql.omtg.classes.OMTGCardinality;
import com.omtg2sql.omtg.classes.OMTGClass;
import com.omtg2sql.omtg.model.OMTGSchema;
import com.omtg2sql.omtg.relationships.OMTGConceptualGeneralization;
import com.omtg2sql.omtg.relationships.OMTGConventionalAggregation;
import com.omtg2sql.omtg.relationships.OMTGConventionalGeneralization;
import com.omtg2sql.omtg.relationships.OMTGConventionalRelationship;
import com.omtg2sql.omtg.relationships.OMTGNetwork;
import com.omtg2sql.omtg.relationships.OMTGRelationship;
import com.omtg2sql.omtg.relationships.OMTGSpatialAggregation;
import com.omtg2sql.omtg.relationships.OMTGTopologicalRelationship;
import com.omtg2sql.omtg.relationships.OMTGUserRestriction;

/**
 * @author lizardo
 * 
 */
public class XMLParser {

	/**
	 * The API to obtain DOM Document instances from an XML document.
	 */
	private SAXBuilder builder;
	
	/**
	 * Validates the XML document;
	 */
	private XMLValidator xmlValidator;

	/**
	 * Constructor
	 */
	public XMLParser() {
		builder = new SAXBuilder();
		xmlValidator = XMLValidator.getInstance();
	}

	/**
	 * @param xml
	 * @return
	 */
	public OMTGSchema parseOMTGSquema(String xml) {

		try {		
			// validates de XML document
			if(!xmlValidator.validateXML("omtg-schema-template.xsd", xml)){
				System.out.println("Invalid XML document");
				return null;
			}
			
			Document document = builder.build(new InputSource(new StringReader(
					xml)));

			Element rootElement = document.getRootElement();
			Map<String, OMTGClass> classes = parseOMTGClasses(rootElement);
			Map<String, OMTGRelationship> relationships = parseOMTGRelationships(rootElement);

			return new OMTGSchema(classes, relationships);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @param element
	 * @return
	 */
	private Map<String, OMTGClass> parseOMTGClasses(Element element) {

		Map<String, OMTGClass> omtgClasses = new HashMap<String, OMTGClass>();

		@SuppressWarnings("unchecked")
		List<Element> classesList = element.getChild("classes").getChildren(
				"class");

		for (Element classElement : classesList) {
			OMTGClass classOMTG = parseOMTGClass(classElement);
			omtgClasses.put(classOMTG.getName(), classOMTG);
		}

		return omtgClasses;
	}

	/**
	 * @param element
	 * @return
	 */
	private OMTGClass parseOMTGClass(Element element) {

		String className = element.getChildTextTrim("name");
		String classType = element.getChildTextTrim("type");

//		System.out.println("className: " + className);
//		System.out.println("classType: " + classType);

		List<OMTGAttribute> classAtrributes = parseOMTGAttributes(element);

		return new OMTGClass(className, classType, classAtrributes);
	}

	/**
	 * @param element
	 * @return
	 */
	private List<OMTGAttribute> parseOMTGAttributes(Element element) {

		List<OMTGAttribute> attributes = new ArrayList<OMTGAttribute>();

		@SuppressWarnings("unchecked")
		List<Element> attributesList = element.getChild("attributes")
				.getChildren("attribute");
		for (Element attributeElement : attributesList) {

			OMTGAttribute attribute = parseOMTGAttribute(attributeElement);
			attributes.add(attribute);
		}
		return attributes;
	}

	/**
	 * @param element
	 * @return
	 */
	private OMTGAttribute parseOMTGAttribute(Element element) {

		String attributeName = element.getChildTextTrim("name");
		String attributeType = element.getChildTextTrim("type");
		String attributeKey = element.getChildTextTrim("key");
		String attributeLength = element.getChildTextTrim("length");
		String attributeScale = element.getChildTextTrim("scale");
		String attributeNotNull = element.getChildTextTrim("not-null");
		String attributeDefault = element.getChildTextTrim("default");
		String attributeSize = element.getChildTextTrim("size");

//		System.out.println("attributeName: " + attributeName);
//		System.out.println("attributeType: " + attributeType);
//		System.out.println("attributeLength: " + attributeLength);
//		System.out.println("attributeScale: " + attributeScale);
//		System.out.println("attributeKey: " + attributeKey);
//		System.out.println("attributeNotNull: " + attributeNotNull);
//		System.out.println("attributeDefault: " + attributeDefault);
//		System.out.println("attributeSize: " + attributeSize);

		List<String> domain = parseAttributeDomain(element);

		OMTGAttribute attribute = new OMTGAttribute(attributeName,
				attributeType);

		if (attributeKey != null && attributeKey.equalsIgnoreCase("true")) {
			attribute.setIsKey();
		}

		if (attributeNotNull != null
				&& attributeNotNull.equalsIgnoreCase("true")) {
			attribute.setIsNotNull();
		}

		attribute.setLength(attributeLength);
		attribute.setScale(attributeScale);
		attribute.setDefaultt(attributeDefault);
		attribute.setSize(attributeSize);
		attribute.setDomain(domain);

		return attribute;
	}

	/**
	 * @param element
	 * @return
	 */
	private List<String> parseAttributeDomain(Element element) {

		if (element.getChild("domain") == null) {
			return null;
		}

		List<String> domain = new ArrayList<String>();

		@SuppressWarnings("unchecked")
		List<Element> domainList = element.getChild("domain").getChildren(
				"value");
		for (Element domainElement : domainList) {

			domain.add(domainElement.getTextTrim());
		}

//		System.out.println("domain: " + domain);

		return domain;
	}

	/**
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, OMTGRelationship> parseOMTGRelationships(
			Element element) {

		Map<String, OMTGRelationship> relationships = new HashMap<String, OMTGRelationship>();

		OMTGRelationship rel = null;

		List<Element> relationshipsList = element.getChild("relationships")
				.getChildren("conventional");
		for (Element relationshipElement : relationshipsList) {
//			System.out.println("relationship conventional");
			rel = parseOMTGConventionalRelationship(relationshipElement);
			relationships.put(rel.getName(), rel);
		}

		relationshipsList = element.getChild("relationships").getChildren(
				"topological");
		for (Element relationshipElement : relationshipsList) {
//			System.out.println("relationship topological");
			rel = parseOMTGTopologicalRelationship(relationshipElement);
			relationships.put(rel.getName(), rel);
		}

		relationshipsList = element.getChild("relationships").getChildren(
				"conventional-aggregation");
		for (Element relationshipElement : relationshipsList) {
//			System.out.println("relationship conventional-aggregation");
			rel = parseOMTGConventionalAggregation(relationshipElement);
			relationships.put(rel.getName(), rel);
		}

		relationshipsList = element.getChild("relationships").getChildren(
				"spatial-aggregation");
		for (Element relationshipElement : relationshipsList) {
//			System.out.println("relationship spatial-aggregation");
			rel = parseOMTGSpatialAggregation(relationshipElement);
			relationships.put(rel.getName(), rel);
		}

		relationshipsList = element.getChild("relationships").getChildren(
				"network");
		for (Element relationshipElement : relationshipsList) {
//			System.out.println("relationship network");
			rel = parseOMTGNetwork(relationshipElement);
			relationships.put(rel.getName(), rel);
		}

		relationshipsList = element.getChild("relationships").getChildren(
				"generalization");
		for (Element relationshipElement : relationshipsList) {
//			System.out.println("relationship generalization");
			rel = parseOMTGConventionalGeneralization(relationshipElement);
			relationships.put(rel.getName(), rel);
		}

		relationshipsList = element.getChild("relationships").getChildren(
				"conceptual-generalization");
		for (Element relationshipElement : relationshipsList) {
//			System.out.println("relationship conceptual-generalization");
			rel = parseOMTGConceptualGeneralization(relationshipElement);
			relationships.put(rel.getName(), rel);
		}

		relationshipsList = element.getChild("relationships").getChildren(
				"user-restriction");
		for (Element relationshipElement : relationshipsList) {
//			System.out.println("user restriction");
			rel = parseOMTGUserRestriction(relationshipElement);
			relationships.put(rel.getName(), rel);
		}

		return relationships;
	}

	/**
	 * @param element
	 * @return
	 */
	private OMTGRelationship parseOMTGConventionalRelationship(Element element) {

		String name = element.getChildTextTrim("name");

		String class1Name = element.getChildTextTrim("class1");
		String cardinality1Min = element.getChild("cardinality1").getChildText(
				"min");
		String cardinality1Max = element.getChild("cardinality1").getChildText(
				"max");
		OMTGCardinality cardinality1 = new OMTGCardinality(cardinality1Min,
				cardinality1Max);

//		System.out.println("name: " + name);
//		System.out.println("class1Name: " + class1Name);
//		System.out.println("cardinality1Min: " + cardinality1Min);
//		System.out.println("cardinality1Max: " + cardinality1Max);
//		System.out.println("cardinality1: " + cardinality1);

		String class2Name = element.getChildTextTrim("class2");
		String cardinality2Min = element.getChild("cardinality2").getChildText(
				"min");
		String cardinality2Max = element.getChild("cardinality2").getChildText(
				"max");
		OMTGCardinality cardinality2 = new OMTGCardinality(cardinality2Min,
				cardinality2Max);

//		System.out.println("class2Name: " + class2Name);
//		System.out.println("cardinality2Min: " + cardinality2Min);
//		System.out.println("cardinality2Max: " + cardinality2Max);
//		System.out.println("cardinality2: " + cardinality2);

		return new OMTGConventionalRelationship(name, class1Name, cardinality1,
				class2Name, cardinality2);
	}

	/**
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private OMTGRelationship parseOMTGTopologicalRelationship(Element element) {

		String spatialRelation = null;
		String distance = null;
		String unit = null;

		List<String> spatialRelations = new ArrayList<String>();
		List<Element> spatialRelationList = element.getChild(
				"spatial-relations").getChildren("spatial-relation");
		for (Element spatialRelationElement : spatialRelationList) {

			spatialRelation = spatialRelationElement.getTextTrim();
			spatialRelations.add(spatialRelation);
		}
//		System.out.println("spatialRelations: " + spatialRelations);

		List<Element> spatialRelationList2 = element
				.getChildren("spatial-relations");
		for (Element spatialRelationElement : spatialRelationList2) {

			distance = spatialRelationElement.getChildTextTrim("distance");
			unit = spatialRelationElement.getChildTextTrim("unit");
		}
//		System.out.println("distance: " + distance);
//		System.out.println("unit: " + unit);

		String class1Name = element.getChildTextTrim("class1");
		String cardinality1Min = element.getChild("cardinality1").getChildText(
				"min");
		String cardinality1Max = element.getChild("cardinality1").getChildText(
				"max");
		OMTGCardinality cardinality1 = new OMTGCardinality(cardinality1Min,
				cardinality1Max);

//		System.out.println("class1Name: " + class1Name);
//		System.out.println("cardinality1Min: " + cardinality1Min);
//		System.out.println("cardinality1Max: " + cardinality1Max);
//		System.out.println("cardinality1: " + cardinality1);

		String class2Name = element.getChildTextTrim("class2");
		String cardinality2Min = element.getChild("cardinality2").getChildText(
				"min");
		String cardinality2Max = element.getChild("cardinality2").getChildText(
				"max");
		OMTGCardinality cardinality2 = new OMTGCardinality(cardinality2Min,
				cardinality2Max);

//		System.out.println("class2Name: " + class2Name);
//		System.out.println("cardinality2Min: " + cardinality2Min);
//		System.out.println("cardinality2Max: " + cardinality2Max);
//		System.out.println("cardinality2: " + cardinality2);

		return new OMTGTopologicalRelationship(spatialRelations, distance,
				unit, class1Name, cardinality1, class2Name, cardinality2);
	}

	/**
	 * @param element
	 * @return
	 */
	private OMTGRelationship parseOMTGConventionalAggregation(Element element) {

		String class1Name = element.getChildTextTrim("class1");
		String class2Name = element.getChildTextTrim("class2");

//		System.out.println("class1Name: " + class1Name);
//		System.out.println("class2Name: " + class2Name);

		return new OMTGConventionalAggregation(class1Name, class2Name);
	}

	/**
	 * @param element
	 * @return
	 */
	private OMTGRelationship parseOMTGSpatialAggregation(Element element) {

		String class1Name = element.getChildTextTrim("class1");
		String class2Name = element.getChildTextTrim("class2");

//		System.out.println("class1Name: " + class1Name);
//		System.out.println("class2Name: " + class2Name);

		return new OMTGSpatialAggregation(class1Name, class2Name);
	}

	/**
	 * @param element
	 * @return
	 */
	private OMTGRelationship parseOMTGNetwork(Element element) {

		String networkName = element.getChildTextTrim("name");
		String class1Name = element.getChildTextTrim("class1");
		String class2Name = element.getChildTextTrim("class2");

//		System.out.println("networkName: " + networkName);
//		System.out.println("class1Name: " + class1Name);
//		System.out.println("class2Name: " + class2Name);

		return new OMTGNetwork(networkName, class1Name, class2Name);
	}

	/**
	 * @param element
	 * @return
	 */
	private OMTGRelationship parseOMTGConventionalGeneralization(
			Element element) {

		String superclass = element.getChildTextTrim("superclass");
		String participation = element.getChildTextTrim("participation");
		String disjointness = element.getChildTextTrim("disjointness");

//		System.out.println("superclass: " + superclass);
//		System.out.println("participation: " + participation);
//		System.out.println("disjointness: " + disjointness);

		List<String> subclasses = parseSubclasses(element);

		return new OMTGConventionalGeneralization(participation, disjointness,
				superclass, subclasses);
	}

	/**
	 * @param element
	 * @return
	 */
	private OMTGRelationship parseOMTGConceptualGeneralization(Element element) {

		String superclass = element.getChildTextTrim("superclass");
		String scaleShape = element.getChildTextTrim("scale-shape");
		String disjointness = element.getChildTextTrim("disjointness");

//		System.out.println("superclass: " + superclass);
//		System.out.println("scaleShape: " + scaleShape);
//		System.out.println("disjointness: " + disjointness);

		List<String> subclasses = parseSubclasses(element);

		return new OMTGConceptualGeneralization(scaleShape, disjointness,
				superclass, subclasses);
	}

	/**
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private OMTGRelationship parseOMTGUserRestriction(Element element) {

		String class1Name = element.getChildTextTrim("class1");
//		System.out.println("class1Name: " + class1Name);

		String spatialRelation = null;
//		String distance = null;
//		String unit = null;

		List<String> spatialRelations = new ArrayList<String>();
		List<Element> spatialRelationList = element.getChild(
				"spatial-relations").getChildren("spatial-relation");
		for (Element spatialRelationElement : spatialRelationList) {

			spatialRelation = spatialRelationElement.getTextTrim();
			spatialRelations.add(spatialRelation);
		}
//		System.out.println("spatialRelations: " + spatialRelations);

//		List<Element> spatialRelationList2 = element
//				.getChildren("spatial-relations");
//		for (Element spatialRelationElement : spatialRelationList2) {

//			distance = spatialRelationElement.getChildTextTrim("distance");
//			unit = spatialRelationElement.getChildTextTrim("unit");
//		}
//		System.out.println("distance: " + distance);
//		System.out.println("unit: " + unit);

		String canOccur = element.getChildTextTrim("can-occur");
//		System.out.println("canOccur: " + canOccur);

		String class2Name = element.getChildTextTrim("class2");
//		System.out.println("class2Name: " + class2Name);

		boolean spatialRelationCanOccur = false;
		if (canOccur.equalsIgnoreCase("true")) {
			spatialRelationCanOccur = true;
		}

		return new OMTGUserRestriction(class1Name, class2Name,
				spatialRelations, spatialRelationCanOccur);
	}

	/**
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<String> parseSubclasses(Element element) {

		List<String> subclasses = new ArrayList<String>();
		List<Element> subclassesList = element.getChild("subclasses")
				.getChildren("subclass");
		for (Element subclassElement : subclassesList) {

			subclasses.add(subclassElement.getTextTrim());
		}

//		System.out.println("subclasses: " + subclasses);

		return subclasses;
	}
}
