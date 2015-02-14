package com.omtg2sql.core;

import java.io.StringWriter;
import java.util.List;

import com.omtg2sql.omtg.classes.OMTGCardinality;
import com.omtg2sql.omtg.classes.OMTGClass;
import com.omtg2sql.omtg.model.OMTGSchema;
import com.omtg2sql.omtg.model.OMTGSchemaLoader;
import com.omtg2sql.omtg.relationships.OMTGConceptualGeneralization;
import com.omtg2sql.omtg.relationships.OMTGConventionalGeneralization;
import com.omtg2sql.omtg.relationships.OMTGConventionalRelationship;
import com.omtg2sql.omtg.relationships.OMTGGeneralization;
import com.omtg2sql.omtg.relationships.OMTGRelationship;
import com.omtg2sql.omtg.relationships.OMTGTopologicalRelationship;
import com.omtg2sql.omtg.relationships.OMTGUserRestriction;
import com.omtg2sql.sql.oracle.SQLGenerator;



public class OMTG2SQL {

	private OMTGSchema omtSchema;
	private SQLGenerator sqlGenerator;

	public OMTG2SQL(OMTGSchema omtgSchema, String xmlDocName) {
		this.omtSchema = omtgSchema;
		xmlDocName = xmlDocName.replace(".xml", "");
		this.sqlGenerator = new SQLGenerator("./"+xmlDocName+"-ddl-structures.sql", "./"+xmlDocName+"-static-control.sql", 
		"./"+xmlDocName+"-dynamic-control.sql");
	}
	
	public OMTG2SQL(OMTGSchema omtgSchema, StringWriter ddlSW, StringWriter staticSW, StringWriter dynamicSW) {
		this.omtSchema = omtgSchema;
		this.sqlGenerator = new SQLGenerator(ddlSW, staticSW, dynamicSW);
	}

	public void mapOMTGSchemaToSQL() throws CloneNotSupportedException {

		// map all classes not in generalization
		for (OMTGClass omtgClass : omtSchema.getClassesNotInGeneralization()) {
			sqlGenerator.mapClass(omtgClass);
		}

		//generalization
		for (OMTGRelationship omtgRel : omtSchema.getRelationships()) {

			if (omtgRel.typeEquals("conventional-generalization")) {
				OMTGClass superClass = omtSchema.getClass(omtgRel.getClass1());
				List<OMTGClass> subClasses = omtSchema.getSubClasses((OMTGGeneralization)omtgRel);
				if (((OMTGConventionalGeneralization)omtgRel).isTotal()) {
					if (((OMTGConventionalGeneralization)omtgRel).isDisjoint()) {
						sqlGenerator.mapConventionalGeneralizationTotalDisjoint((OMTGConventionalGeneralization)omtgRel, superClass, subClasses);
					}
					else {
						sqlGenerator.mapConventionalGeneralizationTotalOverlap((OMTGConventionalGeneralization)omtgRel, superClass, subClasses);
					}
				}
				else {
					if (((OMTGConventionalGeneralization)omtgRel).isDisjoint()) {
						sqlGenerator.mapConventionalGeneralizationPartialDisjoint((OMTGConventionalGeneralization)omtgRel, superClass, subClasses);
					}
					else {
						sqlGenerator.mapConventionalGeneralizationPartialOverlap((OMTGConventionalGeneralization)omtgRel, superClass, subClasses);
					}
				}
			}

			else if (omtgRel.typeEquals("conceptual-generalization")) {

				OMTGClass superClass = omtSchema.getClass(omtgRel.getClass1());
				List<OMTGClass> subClasses = omtSchema.getSubClasses((OMTGGeneralization)omtgRel);

				if (((OMTGConceptualGeneralization)omtgRel).isDisjoint()) {
					sqlGenerator.mapConceptualGeneralizationDisjoint((OMTGConceptualGeneralization)omtgRel, superClass, subClasses);
				}
				else {
					sqlGenerator.mapConceptualGeneralizationOverlap((OMTGConceptualGeneralization)omtgRel, superClass, subClasses);
				}
			}
		}

		List<OMTGClass> omtgSuperClasses = omtSchema.getSuperClassesOfTotalGeneralization();

		//map the others relationship
		for (OMTGRelationship omtgRel : omtSchema.getRelationshipsWithoutGeneralization()) {

			OMTGClass omtgClassA = omtSchema.getClass(omtgRel.getClass1());
			OMTGClass omtgClassB = omtSchema.getClass(omtgRel.getClass2());
			boolean omtgClassesIsTesselation = omtgClassA.isTesselation() || omtgClassB.isTesselation();
			
			List<OMTGClass> omtgClassAList = null;
			if (omtgSuperClasses.contains(omtgClassA)) {
				omtgClassAList = omtSchema.getSubClasses(omtgClassA);
			}
			List<OMTGClass> omtgClassBList = null;
			if (omtgSuperClasses.contains(omtgClassB)) {
				omtgClassBList = omtSchema.getSubClasses(omtgClassB);
			}

			if (omtgRel.typeEquals("conventional-relationship")) {

				if (((OMTGConventionalRelationship)omtgRel).cardinalityIsEqual(OMTGCardinality.ONE_TO_ONE)) {

					sqlGenerator.mapConventionalRelationship11(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
				}
				else if (((OMTGConventionalRelationship)omtgRel).cardinalityIsEqual(OMTGCardinality.ONE_TO_MANY)) {

					sqlGenerator.mapConventionalRelationship1N(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
				}
				else if (((OMTGConventionalRelationship)omtgRel).cardinalityIsEqual(OMTGCardinality.MANY_TO_ONE)) {

					sqlGenerator.mapConventionalRelationshipN1(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
				}
				// Relationship is Many to Many
				else {
					sqlGenerator.mapConventionalRelationshipMN(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
				}
			}
			else if (omtgRel.typeEquals("topological-relationship") && !omtgClassesIsTesselation) {

				sqlGenerator.mapTopologicalRelationship((OMTGTopologicalRelationship)omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
			}
			else if (omtgRel.typeEquals("conventional-aggregation")) {

				sqlGenerator.mapConventionalAggregation(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);

			}
			else if (omtgRel.typeEquals("spatial-aggregation") && !omtgClassesIsTesselation) {

				sqlGenerator.mapSpatialAggregation(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
			}
			else if (omtgRel.typeEquals("network")) {

				if (omtgClassB.getType().equalsIgnoreCase("node")) {

					// omtgClassA = arc, omtgClassB = node
					sqlGenerator.mapNetwork(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
				}
				else {
					// omtgClassA = node, omtgClassB = arc
					sqlGenerator.mapNetwork(omtgRel, omtgClassB, omtgClassA, omtgClassBList, omtgClassAList);
				}
			}
			else if (omtgRel.typeEquals("user-restriction") && !omtgClassesIsTesselation) {

				sqlGenerator.mapUserRestriction((OMTGUserRestriction)omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
			}
		}
		sqlGenerator.close();
	}

	/*
	 * restricoes da generalizacao
	 * restricao da triangulacao
	 */

	public static void main(String[] args) throws Exception {

		try {
			
			String xmlDoc = "/home/lizardo/case-study-1.xml";

			OMTGSchemaLoader omtgLoader = new OMTGSchemaLoader(xmlDoc);
			OMTGSchema omtgSchema = omtgLoader.getOMTGModel();

			OMTG2SQL omtg2sql = new OMTG2SQL(omtgSchema, xmlDoc);

			omtg2sql.mapOMTGSchemaToSQL();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}