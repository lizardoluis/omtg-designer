package com.omtg2sql.core;

import java.io.StringWriter;
import java.util.List;

import com.omtg2sql.omtg.classes.OMTGCardinality;
import com.omtg2sql.omtg.classes.OMTGClass;
import com.omtg2sql.omtg.model.OMTGSchema;
import com.omtg2sql.omtg.relationships.OMTGConceptualGeneralization;
import com.omtg2sql.omtg.relationships.OMTGConventionalGeneralization;
import com.omtg2sql.omtg.relationships.OMTGConventionalRelationship;
import com.omtg2sql.omtg.relationships.OMTGGeneralization;
import com.omtg2sql.omtg.relationships.OMTGRelationship;
import com.omtg2sql.omtg.relationships.OMTGTopologicalRelationship;
import com.omtg2sql.sql.astpostgis.AstPostgisGenerator;



public class OMTG2AstPostgis {

	private OMTGSchema omtSchema;
	private AstPostgisGenerator astPostgisGenerator;

	public OMTG2AstPostgis(OMTGSchema omtgSchema, String xmlDocName) {
		this.omtSchema = omtgSchema;
		xmlDocName = xmlDocName.replace(".xml", "");
		this.astPostgisGenerator = new AstPostgisGenerator("./"+xmlDocName+"-ddl-structures.sql", "./"+xmlDocName+"-static-control.sql", 
		"./"+xmlDocName+"-dynamic-control.sql");
	}
	
	public OMTG2AstPostgis(OMTGSchema omtgSchema, StringWriter ddlSW, StringWriter staticSW, StringWriter dynamicSW) {
		this.omtSchema = omtgSchema;
		this.astPostgisGenerator = new AstPostgisGenerator(ddlSW, staticSW, dynamicSW);
	}

	public void mapOMTGSchemaToAstPostgis() throws CloneNotSupportedException {

		// map all classes not in generalization
		for (OMTGClass omtgClass : omtSchema.getClassesNotInGeneralization()) {
			astPostgisGenerator.mapClass(omtgClass);
		}

		//generalization
		for (OMTGRelationship omtgRel : omtSchema.getRelationships()) {

			if (omtgRel.typeEquals("conventional-generalization")) {
				OMTGClass superClass = omtSchema.getClass(omtgRel.getClass1());
				List<OMTGClass> subClasses = omtSchema.getSubClasses((OMTGGeneralization)omtgRel);
				if (((OMTGConventionalGeneralization)omtgRel).isTotal()) {
					if (((OMTGConventionalGeneralization)omtgRel).isDisjoint()) {
						astPostgisGenerator.mapConventionalGeneralizationTotalDisjoint((OMTGConventionalGeneralization)omtgRel, superClass, subClasses);
					}
					else {
						astPostgisGenerator.mapConventionalGeneralizationTotalOverlap((OMTGConventionalGeneralization)omtgRel, superClass, subClasses);
					}
				}
				else {
					if (((OMTGConventionalGeneralization)omtgRel).isDisjoint()) {
						astPostgisGenerator.mapConventionalGeneralizationPartialDisjoint((OMTGConventionalGeneralization)omtgRel, superClass, subClasses);
					}
					else {
						astPostgisGenerator.mapConventionalGeneralizationPartialOverlap((OMTGConventionalGeneralization)omtgRel, superClass, subClasses);
					}
				}
			}

			else if (omtgRel.typeEquals("conceptual-generalization")) {

				OMTGClass superClass = omtSchema.getClass(omtgRel.getClass1());
				List<OMTGClass> subClasses = omtSchema.getSubClasses((OMTGGeneralization)omtgRel);

				if (((OMTGConceptualGeneralization)omtgRel).isDisjoint()) {
					astPostgisGenerator.mapConceptualGeneralizationDisjoint((OMTGConceptualGeneralization)omtgRel, superClass, subClasses);
				}
				else {
					astPostgisGenerator.mapConceptualGeneralizationOverlap((OMTGConceptualGeneralization)omtgRel, superClass, subClasses);
				}
			}
		}

		List<OMTGClass> omtgSuperClasses = omtSchema.getSuperClassesOfTotalGeneralization();

		//map other relationships
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
					astPostgisGenerator.mapConventionalRelationship11(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
				}
				else if (((OMTGConventionalRelationship)omtgRel).cardinalityIsEqual(OMTGCardinality.ONE_TO_MANY)) {
					astPostgisGenerator.mapConventionalRelationship1N(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
				}
				else if (((OMTGConventionalRelationship)omtgRel).cardinalityIsEqual(OMTGCardinality.MANY_TO_ONE)) {
					astPostgisGenerator.mapConventionalRelationshipN1(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
				}
				// Relationship is Many to Many
				else {
					astPostgisGenerator.mapConventionalRelationshipMN(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
				}
			}
			else if (omtgRel.typeEquals("topological-relationship") && !omtgClassesIsTesselation) {

				astPostgisGenerator.mapTopologicalRelationship((OMTGTopologicalRelationship)omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
			}
			else if (omtgRel.typeEquals("conventional-aggregation")) {

				astPostgisGenerator.mapConventionalAggregation(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);

			}
			else if (omtgRel.typeEquals("spatial-aggregation") && !omtgClassesIsTesselation) {

				astPostgisGenerator.mapSpatialAggregation(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
			}
			else if (omtgRel.typeEquals("network")) {
				
				if(omtgClassA.getType() == omtgClassB.getType()){
					astPostgisGenerator.mapNetwork(omtgRel, omtgClassA, omtgClassAList);
				}

				else if (omtgClassB.getType().equalsIgnoreCase("node")) {

					// omtgClassA = arc, omtgClassB = node
					astPostgisGenerator.mapNetwork(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
				}
				else {
					// omtgClassA = node, omtgClassB = arc
					astPostgisGenerator.mapNetwork(omtgRel, omtgClassB, omtgClassA, omtgClassBList, omtgClassAList);
				}
			}
		}
		astPostgisGenerator.close();
	}
}

