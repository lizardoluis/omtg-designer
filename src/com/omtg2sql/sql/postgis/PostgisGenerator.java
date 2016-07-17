package com.omtg2sql.sql.postgis;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import com.omtg2sql.omtg.classes.OMTGAttribute;
import com.omtg2sql.omtg.classes.OMTGClass;
import com.omtg2sql.omtg.relationships.OMTGConceptualGeneralization;
import com.omtg2sql.omtg.relationships.OMTGConventionalGeneralization;
import com.omtg2sql.omtg.relationships.OMTGGeneralization;
import com.omtg2sql.omtg.relationships.OMTGRelationship;
import com.omtg2sql.omtg.relationships.OMTGTopologicalRelationship;
import com.omtg2sql.omtg.relationships.OMTGUserRestriction;

public class PostgisGenerator {

	private DDLWriter ddl;
	private OffLineConstraintsWriter offLineConstraints;
	private OnLineConstraintsWriter onLineConstraints;

	public PostgisGenerator(String ddlFilePath, String offLineConstraintsFilePath,
			String onLineConstraintsFilePath) {

		this.ddl = new DDLWriter(ddlFilePath);
		this.offLineConstraints = new OffLineConstraintsWriter(
				offLineConstraintsFilePath);
		this.onLineConstraints = new OnLineConstraintsWriter(
				onLineConstraintsFilePath);
	}

	public PostgisGenerator(StringWriter ddlSW, StringWriter offLineConstraintsSW,
			StringWriter onLineConstraintsSW) {

		this.ddl = new DDLWriter(ddlSW);
		this.offLineConstraints = new OffLineConstraintsWriter(
				offLineConstraintsSW);
		this.onLineConstraints = new OnLineConstraintsWriter(
				onLineConstraintsSW);
	}
	
	private void createForeignKeyColumns(OMTGRelationship rel,
			OMTGClass omtgClassSec, OMTGClass omtgClassMain) {

		ddl.appendAlterTableAddColumn(rel.getName(), omtgClassMain.getName(),
				omtgClassSec.getName(), omtgClassSec.getKeysName(),
				omtgClassSec.getKeysType(), omtgClassSec.getKeysLength(),
				omtgClassSec.getKeysScale());
	}

	private void createForeignKeyConstraints(OMTGRelationship rel,
			OMTGClass omtgClassSec, OMTGClass omtgClassMain) {

		ddl.appendAlterTableAddForeignKeyConstraints(rel.getName(),
				omtgClassMain.getName(), omtgClassSec.getName(),
				omtgClassSec.getKeysName());
	}

	private void createForeignKeyConstraintsNN(OMTGRelationship rel,
			OMTGClass omtgClassM, OMTGClass omtgClassN) {

		ddl.appendAlterTableAddForeignKeyConstraintsMN(rel.getName(),
				omtgClassM.getName() + "_" + omtgClassN.getName(),
				omtgClassM.getName(), omtgClassM.getKeysName(),
				omtgClassN.getName(), omtgClassN.getKeysName());
	}

//	private void createInsertIntoUserSdoGeomMetadata(OMTGClass omtgClass) {
//
//		ddl.appendInsertIntoUserSdoGeomMetadata(omtgClass.getName(), null);
//	}

	private void createSpatialIndex(OMTGClass omtgClass) {

		ddl.appendCreateSpatialIndex(omtgClass.getName());
	}

	public void createTable(OMTGClass omtgClass) {

		ddl.appendCreateTable(omtgClass.getName(),
				omtgClass.getAttributesName(), omtgClass.getAttributesType(),
				omtgClass.getAttributeLength(), omtgClass.getAttributeScale(),
				omtgClass.getKeysName(), omtgClass.getAttributeNotNulls(),
				omtgClass.getAttributeDefault(), omtgClass.getType(),
				omtgClass.getAttributeDomain(), omtgClass.getAttributeSize(),
				omtgClass.hasAttributeWithDomain());

		ddl.createMultivaluedTables(omtgClass.getName(),
				omtgClass.getAttributesName(), omtgClass.getAttributesType(),
				omtgClass.getAttributeLength(), omtgClass.getAttributeScale(),
				omtgClass.getKeysName(), omtgClass.getKeysType(),
				omtgClass.getKeysLength(), omtgClass.getKeysScale(),
				omtgClass.getAttributeSize());

		if (omtgClass.isSpatial() && !omtgClass.isTesselation()) {
//			createSpatialClassConstraint(omtgClass);
			//createInsertIntoUserSdoGeomMetadata(omtgClass);
			createSpatialIndex(omtgClass);
		}
	}

	public void createTableFromRelationship(OMTGClass omtgClassM,
			OMTGClass omtgClassN) {

		ddl.appendCreateTable(
				omtgClassM.getName() + "_" + omtgClassN.getName(),
				omtgClassM.getName(), omtgClassN.getName(),
				omtgClassM.getKeysName(), omtgClassM.getKeysType(),
				omtgClassM.getAttributeLength(),
				omtgClassM.getAttributeScale(), omtgClassN.getKeysName(),
				omtgClassN.getKeysType(), omtgClassN.getAttributeLength(),
				omtgClassN.getAttributeScale());
	}

	private void createTopologicalRelationshipConstraint(
			OMTGTopologicalRelationship rel, OMTGClass omtgClassA,
			OMTGClass omtgClassB) {

		if (rel.getSpatialRelation().size() == 1
				&& rel.getSpatialRelation().get(0).equalsIgnoreCase("near")) {

			onLineConstraints.appendTopologicalRelationshipNearConstraint(
					omtgClassA.getName(), omtgClassB.getName(),
					omtgClassB.getKeysName(), rel.getDistance(), rel.getUnit());
		} else {

			onLineConstraints.appendTopologicalRelationshipConstraint(
					omtgClassA.getName(), omtgClassB.getName(),
					omtgClassB.getKeysName(), rel.getSpatialRelation());
		}
	}

	private void createUserRestrictionConstraint(OMTGUserRestriction rel,
			OMTGClass omtgClassA, OMTGClass omtgClassB) {

		if (rel.getSpatialRelation().size() == 1
				&& rel.getSpatialRelation().get(0).equalsIgnoreCase("near")) {

			onLineConstraints.appendUserRestrictionNearConstraint(
					omtgClassA.getName(), omtgClassB.getName(),
					omtgClassB.getKeysName(), rel.getDistance(), rel.getUnit(),
					rel.isSpatialRelationCanOccur());
		} else {

			onLineConstraints.appendUserRestrictionConstraint(
					omtgClassA.getName(), omtgClassB.getName(),
					omtgClassB.getKeysName(), rel.getSpatialRelation(),
					rel.isSpatialRelationCanOccur());
		}
	}

//	private void createSpatialClassConstraint(OMTGClass omtgClass) {
//
//		if (omtgClass.isIsoline()) {
//			ddl.createSpatialErrorTable();
//			offLineConstraints.appendIsolineConstraint(omtgClass.getName(),
//					omtgClass.getKeysName());
//		} else if (omtgClass.isPlanarSubdivision()) {
//			ddl.createSpatialErrorTable();
//			offLineConstraints.appendPlanarSubdivisionConstraint(
//					omtgClass.getName(), omtgClass.getKeysName());
//		} else if (omtgClass.isTIN()) {
//			ddl.createSpatialErrorTable();
//			offLineConstraints.appendTINConstraint(omtgClass.getName(),
//					omtgClass.getKeysName());
//		}
//	}

//	private void createSpatialAggregationConstraint(OMTGRelationship omtgRel,
//			OMTGClass omtgClassWhole, OMTGClass omtgClassPart) {
//
//		ddl.createSpatialErrorTable();
//		ddl.createSaAuxTable();
//		ddl.appendInsertIntoUserSdoGeomMetadata("Sa_aux", null, "p_geom");
//		ddl.appendCreateSpatialIndex("Sa_aux");
//		offLineConstraints.appendJoinGeometryFunction();
//		offLineConstraints.appendSpatialAggregationConstraint(
//				omtgClassWhole.getName(), omtgClassWhole.getKeysName(),
//				omtgClassPart.getName(), omtgClassPart.getKeysName());
//	}

//	private void createTINConstraint(OMTGRelationship omtgRel,
//			OMTGClass omtgClass) {
//
//		ddl.createSpatialErrorTable();
//		offLineConstraints.appendTINConstraint(omtgClass.getName(),
//				omtgClass.getKeysName());
//	}

	private void createNetworkConstraint(OMTGRelationship omtgRel,
			OMTGClass omtgArc, OMTGClass omtgNode) {

//		ddl.createSpatialErrorTable();
//		offLineConstraints.appendGetPointFunction();
		offLineConstraints.appendNetworkConstraint(omtgArc.getName(),
				omtgArc.getKeysName(), omtgNode.getName(),
				omtgNode.getKeysName());
	}
	
	private void createNetworkConstraint(OMTGRelationship omtgRel,
			OMTGClass omtgArc) {

//		ddl.createSpatialErrorTable();
//		offLineConstraints.appendGetPointFunction();
		offLineConstraints.appendNetworkConstraint(omtgArc.getName(),
				omtgArc.getKeysName());
	}

	public void mapClass(OMTGClass omtgClass) {

		createTable(omtgClass);
	}

	public void mapRelationship(OMTGRelationship rel, OMTGClass omtgClass1a,
			OMTGClass omtgClass1b, List<OMTGClass> omtgClass1aList,
			List<OMTGClass> omtgClass1bList) {

		if (omtgClass1aList != null && omtgClass1bList != null) {
			for (OMTGClass omtgClassA : omtgClass1aList) {
				for (OMTGClass omtgClassB : omtgClass1bList) {

					createForeignKeyColumns(rel, omtgClassA, omtgClassB);
					createForeignKeyConstraints(rel, omtgClassA, omtgClassB);
				}
			}
		} else if (omtgClass1aList != null) {

			for (OMTGClass omtgClass : omtgClass1aList) {

				createForeignKeyColumns(rel, omtgClass, omtgClass1b);
				createForeignKeyConstraints(rel, omtgClass, omtgClass1b);
			}
		} else if (omtgClass1bList != null) {

			for (OMTGClass omtgClass : omtgClass1bList) {

				createForeignKeyColumns(rel, omtgClass1a, omtgClass);
				createForeignKeyConstraints(rel, omtgClass1a, omtgClass);
			}
		}

		if (omtgClass1aList == null && omtgClass1bList == null) {

			createForeignKeyColumns(rel, omtgClass1a, omtgClass1b);
			createForeignKeyConstraints(rel, omtgClass1a, omtgClass1b);
		}
	}

	public void mapConventionalRelationship11(OMTGRelationship rel,
			OMTGClass omtgClass1a, OMTGClass omtgClass1b,
			List<OMTGClass> omtgClass1aList, List<OMTGClass> omtgClass1bList) {

		mapRelationship(rel, omtgClass1a, omtgClass1b, omtgClass1aList,
				omtgClass1bList);
	}

	public void mapConventionalRelationship1N(OMTGRelationship rel,
			OMTGClass omtgClass1, OMTGClass omtgClassN,
			List<OMTGClass> omtgClass1List, List<OMTGClass> omtgClassNList) {

		mapRelationship(rel, omtgClass1, omtgClassN, omtgClass1List,
				omtgClassNList);
	}

	public void mapConventionalRelationshipN1(OMTGRelationship rel,
			OMTGClass omtgClassN, OMTGClass omtgClass1,
			List<OMTGClass> omtgClassNList, List<OMTGClass> omtgClass1List) {

		mapRelationship(rel, omtgClass1, omtgClassN, omtgClass1List,
				omtgClassNList);
	}

	public void mapConventionalRelationshipMN(OMTGRelationship rel,
			OMTGClass omtgClassM, OMTGClass omtgClassN,
			List<OMTGClass> omtgClassMList, List<OMTGClass> omtgClassNList) {

		if (omtgClassMList != null && omtgClassNList != null) {
			for (OMTGClass omtgClassA : omtgClassMList) {
				for (OMTGClass omtgClassB : omtgClassNList) {

					createTableFromRelationship(omtgClassA, omtgClassB);
					createForeignKeyConstraintsNN(rel, omtgClassA, omtgClassB);
				}
			}
		} else if (omtgClassMList != null) {

			for (OMTGClass omtgClass : omtgClassMList) {

				createTableFromRelationship(omtgClass, omtgClassN);
				createForeignKeyConstraintsNN(rel, omtgClass, omtgClassN);
			}
		} else if (omtgClassNList != null) {

			for (OMTGClass omtgClass : omtgClassNList) {

				createTableFromRelationship(omtgClassM, omtgClass);
				createForeignKeyConstraintsNN(rel, omtgClassM, omtgClass);
			}
		}

		if (omtgClassMList == null && omtgClassNList == null) {

			createTableFromRelationship(omtgClassM, omtgClassN);
			createForeignKeyConstraintsNN(rel, omtgClassM, omtgClassN);
		}
	}

	public void mapTopologicalRelationship(OMTGTopologicalRelationship rel,
			OMTGClass omtgClassA, OMTGClass omtgClassB,
			List<OMTGClass> omtgClassAList, List<OMTGClass> omtgClassBList) {

		if (omtgClassAList != null && omtgClassBList != null) {
			for (OMTGClass omtgClassAA : omtgClassAList) {
				for (OMTGClass omtgClassBB : omtgClassBList) {

					createTopologicalRelationshipConstraint(rel, omtgClassAA,
							omtgClassBB);
				}
			}
		} else if (omtgClassAList != null) {

			for (OMTGClass omtgClass : omtgClassAList) {

				createTopologicalRelationshipConstraint(rel, omtgClass,
						omtgClassB);
			}
		} else if (omtgClassBList != null) {

			for (OMTGClass omtgClass : omtgClassBList) {

				createTopologicalRelationshipConstraint(rel, omtgClassA,
						omtgClass);
			}
		}

		if (omtgClassAList == null && omtgClassBList == null) {

			createTopologicalRelationshipConstraint(rel, omtgClassA, omtgClassB);
		}
	}

	public void mapUserRestriction(OMTGUserRestriction rel,
			OMTGClass omtgClassA, OMTGClass omtgClassB,
			List<OMTGClass> omtgClassAList, List<OMTGClass> omtgClassBList) {

		if (omtgClassAList != null && omtgClassBList != null) {
			for (OMTGClass omtgClassAA : omtgClassAList) {
				for (OMTGClass omtgClassBB : omtgClassBList) {

					createUserRestrictionConstraint(rel, omtgClassAA,
							omtgClassBB);
				}
			}
		} else if (omtgClassAList != null) {

			for (OMTGClass omtgClass : omtgClassAList) {

				createUserRestrictionConstraint(rel, omtgClass, omtgClassB);
			}
		} else if (omtgClassBList != null) {

			for (OMTGClass omtgClass : omtgClassBList) {

				createUserRestrictionConstraint(rel, omtgClassA, omtgClass);
			}
		}

		if (omtgClassAList == null && omtgClassBList == null) {

			createUserRestrictionConstraint(rel, omtgClassA, omtgClassB);
		}
	}

	public void mapConventionalAggregation(OMTGRelationship rel,
			OMTGClass omtgClass1, OMTGClass omtgClassN,
			List<OMTGClass> omtgClass1List, List<OMTGClass> omtgClassNList) {

		mapRelationship(rel, omtgClass1, omtgClassN, omtgClass1List,
				omtgClassNList);
	}

//	public void mapSpatialAggregation(OMTGRelationship rel,
//			OMTGClass omtgClassWhole, OMTGClass omtgClassPart,
//			List<OMTGClass> omtgClassWholeList,
//			List<OMTGClass> omtgClassPartList) {
//
//		if (omtgClassWholeList != null && omtgClassPartList != null) {
//			for (OMTGClass omtgClassA : omtgClassWholeList) {
//				for (OMTGClass omtgClassB : omtgClassPartList) {
//
//					createSpatialAggregationConstraint(rel, omtgClassA,
//							omtgClassB);
//				}
//			}
//		} else if (omtgClassWholeList != null) {
//
//			for (OMTGClass omtgClass : omtgClassWholeList) {
//
//				createSpatialAggregationConstraint(rel, omtgClass,
//						omtgClassPart);
//			}
//		} else if (omtgClassPartList != null) {
//
//			for (OMTGClass omtgClass : omtgClassPartList) {
//
//				createSpatialAggregationConstraint(rel, omtgClassWhole,
//						omtgClass);
//			}
//		}
//
//		if (omtgClassWholeList == null && omtgClassPartList == null) {
//
//			createSpatialAggregationConstraint(rel, omtgClassWhole,
//					omtgClassPart);
//		}
//	}

//	public void mapTIN(OMTGRelationship omtgRel, OMTGClass omtgClass) {
//
//		createTINConstraint(omtgRel, omtgClass);
//	}

	public void mapNetwork(OMTGRelationship omtgRel, OMTGClass omtgArc,
			OMTGClass omtgNode, List<OMTGClass> omtgArcList,
			List<OMTGClass> omtgNodeList) {

		createNetworkConstraint(omtgRel, omtgArc, omtgNode);
	}
	
	public void mapNetwork(OMTGRelationship omtgRel, OMTGClass omtgArc, List<OMTGClass> omtgArcList) {

		createNetworkConstraint(omtgRel, omtgArc);
	}


	public void mapGeneralizationTotal(OMTGGeneralization omtgRel,
			OMTGClass superClass, List<OMTGClass> subClasses)
			throws CloneNotSupportedException {

		for (OMTGClass omtgSubClass : subClasses) {

			// List<OMTGAttribute> newAttributes =
			// superClass.getKeysAttribute();
			List<OMTGAttribute> newAttributes = superClass
					.getAttributesWithPrefix(superClass.getName());

			// OMTGClass subClassAux = (OMTGClass)omtgSubClass.clone();
			// join superclass and subclass attributes to subClassAux
			newAttributes.addAll(omtgSubClass.getAttributes());
			omtgSubClass.setAttributes(newAttributes);

			mapClass(omtgSubClass);
		}
	}

	public void mapGeneralizationPartial(OMTGGeneralization omtgRel,
			OMTGClass superClass, List<OMTGClass> subClasses)
			throws CloneNotSupportedException {

		mapClass(superClass);

		for (OMTGClass subClass : subClasses) {

			mapClass(subClass);
			createForeignKeyColumns(omtgRel, superClass, subClass);
			createForeignKeyConstraints(omtgRel, superClass, subClass);
		}
	}

//	private void createDisjointConstraint(OMTGGeneralization omtgRel,
//			OMTGClass superClass, List<OMTGClass> subClasses) {
//
//		for (int i = 0; i < subClasses.size(); i++) {
//
//			OMTGClass subClass = subClasses.get(i);
//			onLineConstraints.appendDisjointConstraint(subClass.getName(),
//					subClass.getKeysName(), omtgRel.getSubclasses());
//		}
//	}

//	private void createDisjointConstraintWithPartial(
//			OMTGGeneralization omtgRel, OMTGClass superClass,
//			List<OMTGClass> subClasses) {
//
//		for (int i = 0; i < subClasses.size(); i++) {
//
//			OMTGClass subClass = subClasses.get(i);
////			onLineConstraints.appendDisjointConstraintWithPartial(
////					subClass.getName(), superClass.getKeysName(),
////					omtgRel.getSubclasses(), superClass.getName());
//		}
//	}

//	private void createPartialConstraint(
//			OMTGConventionalGeneralization omtgRel, OMTGClass superClass,
//			List<OMTGClass> subClasses) {
//
//		onLineConstraints.appendPartialConstraint(superClass.getName(),
//				superClass.getKeysName(), omtgRel.getSubclasses());
//	}

	public void mapConventionalGeneralizationTotalDisjoint(
			OMTGConventionalGeneralization omtgRel, OMTGClass superClass,
			List<OMTGClass> subClasses) throws CloneNotSupportedException {

		mapGeneralizationTotal(omtgRel, superClass, subClasses);
		//createDisjointConstraint(omtgRel, superClass, subClasses);
	}

	public void mapConventionalGeneralizationTotalOverlap(
			OMTGConventionalGeneralization omtgRel, OMTGClass superClass,
			List<OMTGClass> subClasses) throws CloneNotSupportedException {

		mapGeneralizationTotal(omtgRel, superClass, subClasses);
	}

	public void mapConventionalGeneralizationPartialDisjoint(
			OMTGConventionalGeneralization omtgRel, OMTGClass superClass,
			List<OMTGClass> subClasses) throws CloneNotSupportedException {

		mapGeneralizationPartial(omtgRel, superClass, subClasses);
		//createDisjointConstraintWithPartial(omtgRel, superClass, subClasses);
		// createPartialConstraint(omtgRel, superClass, subClasses);
	}

	public void mapConventionalGeneralizationPartialOverlap(
			OMTGConventionalGeneralization omtgRel, OMTGClass superClass,
			List<OMTGClass> subClasses) throws CloneNotSupportedException {

		mapGeneralizationPartial(omtgRel, superClass, subClasses);
		// createPartialConstraint(omtgRel, superClass, subClasses);
	}

	public void mapConceptualGeneralizationDisjoint(
			OMTGConceptualGeneralization omtgRel, OMTGClass superClass,
			List<OMTGClass> subClasses) throws CloneNotSupportedException {

		mapGeneralizationTotal(omtgRel, superClass, subClasses);
		//createDisjointConstraint(omtgRel, superClass, subClasses);
	}

	public void mapConceptualGeneralizationOverlap(
			OMTGConceptualGeneralization omtgRel, OMTGClass superClass,
			List<OMTGClass> subClasses) throws CloneNotSupportedException {

		mapGeneralizationTotal(omtgRel, superClass, subClasses);
	}

	public void close() {
		try {
			ddl.close();
			offLineConstraints.close();
			onLineConstraints.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}