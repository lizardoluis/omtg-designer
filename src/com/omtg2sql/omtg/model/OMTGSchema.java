package com.omtg2sql.omtg.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.omtg2sql.omtg.classes.OMTGClass;
import com.omtg2sql.omtg.relationships.OMTGConventionalGeneralization;
import com.omtg2sql.omtg.relationships.OMTGGeneralization;
import com.omtg2sql.omtg.relationships.OMTGRelationship;


public class OMTGSchema {

	private Map<String, OMTGClass> classes;
	private Map<String, OMTGRelationship> relationships;

	public OMTGSchema(Map<String, OMTGClass> classes, Map<String, OMTGRelationship> relationships) throws CloneNotSupportedException {
		this.classes = classes;
		this.relationships = relationships;
	}

	public OMTGClass getClass(String className) {
		return classes.get(className);
	}

	public List<OMTGClass> getClassesNotInGeneralization() {

		List<OMTGClass> classesNotInGeneralization = new ArrayList<OMTGClass>();
		List<OMTGClass> generalizationClasses = getClassesInGeneralization();

		for (OMTGClass omtgClass : classes.values()) {

			if (!generalizationClasses.contains(omtgClass)) {
				classesNotInGeneralization.add(omtgClass);
			}
		}

		return classesNotInGeneralization;
	}

	public List<OMTGClass> getClassesInGeneralization() {

		List<OMTGClass> generalizationClasses = new ArrayList<OMTGClass>();
		generalizationClasses.addAll(getSubClasses());
		generalizationClasses.addAll(getSuperClasses());

		return generalizationClasses;
	}

	public List<OMTGClass> getSuperClasses() {

		List<OMTGClass> superClasses = new ArrayList<OMTGClass>();

		for (OMTGRelationship omtgRel : relationships.values()) {
			if (omtgRel.typeEquals("conventional-generalization") || omtgRel.typeEquals("conceptual-generalization")) {

				OMTGClass superClass = getClass(((OMTGGeneralization)omtgRel).getSuperClass());
				superClasses.add(superClass);
			}
		}

		return superClasses;
	}

	public List<OMTGClass> getSuperClassesOfTotalGeneralization() {

		List<OMTGClass> superClasses = new ArrayList<OMTGClass>();

		for (OMTGRelationship omtgRel : relationships.values()) {
			
			if (omtgRel.typeEquals("conventional-generalization")) {

				if (((OMTGConventionalGeneralization)omtgRel).isTotal()) {
					OMTGClass superClass = getClass(((OMTGGeneralization)omtgRel).getSuperClass());
					superClasses.add(superClass);
				}

				if (omtgRel.typeEquals("conceptual-generalization")) {

					OMTGClass superClass = getClass(((OMTGGeneralization)omtgRel).getSuperClass());
					superClasses.add(superClass);
				}
			}
		}

		return superClasses;
	}

	public List<OMTGClass> getSubClasses() {

		List<OMTGClass> subClasses = new ArrayList<OMTGClass>();

		for (OMTGRelationship omtgRel : relationships.values()) {
			if (omtgRel.typeEquals("conventional-generalization") || omtgRel.typeEquals("conceptual-generalization")) {

				subClasses.addAll(getSubClasses((OMTGGeneralization)omtgRel));
			}
		}

		return subClasses;
	}

	public List<OMTGClass> getSubClasses(OMTGGeneralization rel) {

		List<OMTGClass> subClasses = new ArrayList<OMTGClass>();
		List<String> subClassesName = rel.getSubclasses();

		for (int i = 0; i < subClassesName.size(); i++) {

			subClasses.add(getClass(subClassesName.get(i)));
		}

		return subClasses;
	}

	public List<OMTGClass> getSubClasses(OMTGClass superClass) {

		for (OMTGRelationship omtgRel : relationships.values()) {
			if (omtgRel.typeEquals("conventional-generalization") || omtgRel.typeEquals("conceptual-generalization")) {

				if (((OMTGGeneralization)omtgRel).getSuperClass().equalsIgnoreCase(superClass.getName())) {

					return getSubClasses((OMTGGeneralization)omtgRel);
				}
			}
		}

		return null;
	}

	public OMTGRelationship getRelationship(String relationshipName) {
		return relationships.get(relationshipName);
	}

	public List<OMTGClass> getClasses(String relationshipName) {

		OMTGRelationship rel = relationships.get(relationshipName);
		List<OMTGClass> classesList = new ArrayList<OMTGClass>();

		classesList.add(classes.get(rel.getClass1()));
		if (rel.getClass2() != null) { // in generalization and conceptual-generalization class2 is null
			classesList.add(classes.get(rel.getClass2()));
			return classesList;
		}
		return null;
	}

	public List<OMTGRelationship> getRelationshipsWithoutGeneralization() {

		List<OMTGRelationship> r = new ArrayList<OMTGRelationship>();
		for (OMTGRelationship omtgRel : relationships.values()) {

			if (!omtgRel.typeEquals("conventional-generalization") && !omtgRel.typeEquals("conceptual-generalization")) {

				r.add(omtgRel);
			}
		}
		return r;
	}

	public List<OMTGClass> getClasses() {
		return new ArrayList<OMTGClass>(classes.values());
	}

	public List<OMTGRelationship> getRelationships() {
		return new ArrayList<OMTGRelationship>(relationships.values());
	}

	public List<OMTGRelationship> getRelationships(String className) {
		return classes.get(className).getRelationships();
	}

	public List<String> getClassesNames() {
		return new ArrayList<String>(classes.keySet());
	}

	public List<String> getRelationshipsNames() {
		return new ArrayList<String>(relationships.keySet());
	}
}