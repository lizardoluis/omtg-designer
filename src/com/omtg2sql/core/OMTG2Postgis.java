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
import com.omtg2sql.omtg.relationships.OMTGUserRestriction;
import com.omtg2sql.sql.postgis.PostgisGenerator;



public class OMTG2Postgis {

	private OMTGSchema omtSchema;
	private PostgisGenerator postgisGenerator;

	public OMTG2Postgis(OMTGSchema omtgSchema, String xmlDocName) {
		this.omtSchema = omtgSchema;
		xmlDocName = xmlDocName.replace(".xml", "");
		this.postgisGenerator = new PostgisGenerator("./"+xmlDocName+"-ddl-structures.sql", "./"+xmlDocName+"-static-control.sql", 
		"./"+xmlDocName+"-dynamic-control.sql");
	}
	
	public OMTG2Postgis(OMTGSchema omtgSchema, StringWriter ddlSW, StringWriter staticSW, StringWriter dynamicSW) {
		this.omtSchema = omtgSchema;
		this.postgisGenerator = new PostgisGenerator(ddlSW, staticSW, dynamicSW);
	}

	public void mapOMTGSchemaToPostgis() throws CloneNotSupportedException {

		// map all classes not in generalization
		for (OMTGClass omtgClass : omtSchema.getClassesNotInGeneralization()) {
			postgisGenerator.mapClass(omtgClass);
		}

		//generalization
		for (OMTGRelationship omtgRel : omtSchema.getRelationships()) {

			if (omtgRel.typeEquals("conventional-generalization")) {
				OMTGClass superClass = omtSchema.getClass(omtgRel.getClass1());
				List<OMTGClass> subClasses = omtSchema.getSubClasses((OMTGGeneralization)omtgRel);
				if (((OMTGConventionalGeneralization)omtgRel).isTotal()) {
					if (((OMTGConventionalGeneralization)omtgRel).isDisjoint()) {
						postgisGenerator.mapConventionalGeneralizationTotalDisjoint((OMTGConventionalGeneralization)omtgRel, superClass, subClasses);
					}
					else {
						postgisGenerator.mapConventionalGeneralizationTotalOverlap((OMTGConventionalGeneralization)omtgRel, superClass, subClasses);
					}
				}
				else {
					if (((OMTGConventionalGeneralization)omtgRel).isDisjoint()) {
						postgisGenerator.mapConventionalGeneralizationPartialDisjoint((OMTGConventionalGeneralization)omtgRel, superClass, subClasses);
					}
					else {
						postgisGenerator.mapConventionalGeneralizationPartialOverlap((OMTGConventionalGeneralization)omtgRel, superClass, subClasses);
					}
				}
			}

			else if (omtgRel.typeEquals("conceptual-generalization")) {

				OMTGClass superClass = omtSchema.getClass(omtgRel.getClass1());
				List<OMTGClass> subClasses = omtSchema.getSubClasses((OMTGGeneralization)omtgRel);

				if (((OMTGConceptualGeneralization)omtgRel).isDisjoint()) {
					postgisGenerator.mapConceptualGeneralizationDisjoint((OMTGConceptualGeneralization)omtgRel, superClass, subClasses);
				}
				else {
					postgisGenerator.mapConceptualGeneralizationOverlap((OMTGConceptualGeneralization)omtgRel, superClass, subClasses);
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
//					System.out.println("one_to_one");
					postgisGenerator.mapConventionalRelationship11(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
				}
				else if (((OMTGConventionalRelationship)omtgRel).cardinalityIsEqual(OMTGCardinality.ONE_TO_MANY)) {
//					System.out.println("one_to_many");
					postgisGenerator.mapConventionalRelationship1N(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
				}
				else if (((OMTGConventionalRelationship)omtgRel).cardinalityIsEqual(OMTGCardinality.MANY_TO_ONE)) {
//					System.out.println("many_to_one");
					postgisGenerator.mapConventionalRelationshipN1(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
				}
				// Relationship is Many to Many
				else {
//					System.out.println("many_to_many");
					postgisGenerator.mapConventionalRelationshipMN(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
				}
			}
			else if (omtgRel.typeEquals("topological-relationship") && !omtgClassesIsTesselation) {

				postgisGenerator.mapTopologicalRelationship((OMTGTopologicalRelationship)omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
			}
			else if (omtgRel.typeEquals("conventional-aggregation")) {

				postgisGenerator.mapConventionalAggregation(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);

			}
//			else if (omtgRel.typeEquals("spatial-aggregation") && !omtgClassesIsTesselation) {
//
//				postgisGenerator.mapSpatialAggregation(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
//			}
			else if (omtgRel.typeEquals("network")) {
				
				if(omtgClassA.getType() == omtgClassB.getType()){
					postgisGenerator.mapNetwork(omtgRel, omtgClassA, omtgClassAList);
				}

				else if (omtgClassB.getType().equalsIgnoreCase("node")) {

					// omtgClassA = arc, omtgClassB = node
					postgisGenerator.mapNetwork(omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
				}
				else {
					// omtgClassA = node, omtgClassB = arc
					postgisGenerator.mapNetwork(omtgRel, omtgClassB, omtgClassA, omtgClassBList, omtgClassAList);
				}
			}
			else if (omtgRel.typeEquals("user-restriction") && !omtgClassesIsTesselation) {

				postgisGenerator.mapUserRestriction((OMTGUserRestriction)omtgRel, omtgClassA, omtgClassB, omtgClassAList, omtgClassBList);
			}
		}
		postgisGenerator.close();
	}
}