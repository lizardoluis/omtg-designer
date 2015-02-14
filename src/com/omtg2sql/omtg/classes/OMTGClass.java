package com.omtg2sql.omtg.classes;

import java.util.ArrayList;
import java.util.List;

import com.omtg2sql.omtg.relationships.OMTGGeneralization;
import com.omtg2sql.omtg.relationships.OMTGRelationship;


public class OMTGClass implements Cloneable {

	public static final String PLANAR_SUBDIVISION = "planar-subdivision";
	public static final String ISOLINES = "isolines";
	public static final String TESSELATION = "tesselation";
	public static final String TIN = "TIN";

	private String name, type;
	private List<OMTGRelationship> relationships;
	private List<OMTGAttribute> attributes;
	private OMTGGeneralization generalization;
	private boolean isSuperClass;

	public OMTGClass(String name, String type, List<OMTGAttribute> attributes) {
		this.name = name;
		this.type = type;
		this.attributes = attributes;
		this.relationships = new ArrayList<OMTGRelationship>();
		this.isSuperClass = false;
	}

	public String getName() {
		return name;
	}

	public List<OMTGAttribute> getAttributes() {
		return attributes;
	}

	public List<OMTGAttribute> getAttributesWithPrefix(String prefix) {

		prefix += "_";

		List<OMTGAttribute> attributesAux = new ArrayList<OMTGAttribute>();
		for (int i = 0; i < attributes.size(); i++) {

			OMTGAttribute newAttribute;
			try {
				newAttribute = (OMTGAttribute) attributes.get(i).clone();
				newAttribute.setName(prefix + newAttribute.getName());
				attributesAux.add(newAttribute);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}

		return attributesAux;
	}

	public void addRelationship(OMTGRelationship r) {
		relationships.add(r);
	}

	public List<OMTGRelationship> getRelationships() {
		return relationships;
	}

	public void addAttribute(OMTGAttribute a) {
		attributes.add(a);
	}

	public String toString() {
		return name;
	}

	public OMTGGeneralization getGeneralization() {
		return generalization;
	}

	public boolean hasGeneralization() {
		return generalization != null;
	}

	public void setGeneralization(OMTGGeneralization i) {
		generalization = i;
	}

	public boolean isSuperClass() {
		return isSuperClass;
	}

	public void setSuperClass(boolean isSuperClass) {
		this.isSuperClass = isSuperClass;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean typeEquals(String type) {
		return this.type.equalsIgnoreCase(type);
	}

	public boolean isSpatial() {
		return !typeEquals("conventional");
	}

	public boolean isPlanarSubdivision() {
		return type.equalsIgnoreCase(PLANAR_SUBDIVISION);
	}

	public boolean isIsoline() {
		return type.equalsIgnoreCase(ISOLINES);
	}

	public boolean isTesselation() {
		return type.equalsIgnoreCase(TESSELATION);
	}

	public boolean isTIN() {
		return type.equalsIgnoreCase(TIN);
	}

	public void setAttributes(List<OMTGAttribute> attributes) {
		this.attributes = attributes;
	}

	public List<String> getAttributesName() {
		List<String> attributesName = new ArrayList<String>();
		for (OMTGAttribute attribute : attributes) {
			attributesName.add(attribute.getName());
		}
		return attributesName;
	}

	public List<String> getAttributesType() {
		List<String> attributesType = new ArrayList<String>();
		for (OMTGAttribute attribute : attributes) {
			attributesType.add(attribute.getType());
		}
		return attributesType;
	}

	public List<String> getKeysName() {
		List<String> keys = new ArrayList<String>();
		for (OMTGAttribute attribute : attributes) {
			if (attribute.isKey() == true) {
				keys.add(attribute.getName());
			}
		}
		return keys;
	}

	public List<String> getKeysType() {
		List<String> keysType = new ArrayList<String>();
		for (OMTGAttribute attribute : attributes) {
			if (attribute.isKey() == true) {
				keysType.add(attribute.getType());
			}
		}
		return keysType;
	}

	public List<String> getKeysLength() {
		List<String> keysType = new ArrayList<String>();
		for (OMTGAttribute attribute : attributes) {
			if (attribute.isKey() == true) {
				keysType.add(attribute.getLength());
			}
		}
		return keysType;
	}

	public List<String> getKeysScale() {
		List<String> keysType = new ArrayList<String>();
		for (OMTGAttribute attribute : attributes) {
			if (attribute.isKey() == true) {
				keysType.add(attribute.getScale());
			}
		}
		return keysType;
	}

	public List<OMTGAttribute> getAttributeKeys() {
		List<OMTGAttribute> keys = new ArrayList<OMTGAttribute>();
		for (OMTGAttribute attribute : attributes) {
			if (attribute.isKey() == true) {
				keys.add(attribute);
			}
		}
		return keys;
	}

	public List<String> getAttributeLength() {
		List<String> lengthAttributes = new ArrayList<String>();
		for (OMTGAttribute attribute : attributes) {
			lengthAttributes.add(attribute.getLength());
		}
		return lengthAttributes;
	}

	public List<String> getAttributeScale() {
		List<String> scaleAttributes = new ArrayList<String>();
		for (OMTGAttribute attribute : attributes) {
			scaleAttributes.add(attribute.getScale());
		}
		return scaleAttributes;
	}

	public List<Boolean> getAttributeNotNulls() {
		List<Boolean> notNullsAttributes = new ArrayList<Boolean>();
		for (OMTGAttribute attribute : attributes) {
			notNullsAttributes.add(attribute.isNotNull());
		}
		return notNullsAttributes;
	}

	public List<String> getAttributeDefault() {
		List<String> defaultAttribute = new ArrayList<String>();
		for (OMTGAttribute attribute : attributes) {
			defaultAttribute.add(attribute.getDefaultValue());
		}
		return defaultAttribute;
	}

	public List<String> getAttributeSize() {
		List<String> sizeAttribute = new ArrayList<String>();
		for (OMTGAttribute attribute : attributes) {
			sizeAttribute.add(attribute.getSize());
		}
		return sizeAttribute;
	}

	public List<List<String>> getAttributeDomain() {
		List<List<String>> domain = new ArrayList<List<String>>();
		for (OMTGAttribute attribute : attributes) {
			domain.add(attribute.getDomain());
		}
		return domain;
	}

	public boolean hasAttributeWithDomain() {

		for (OMTGAttribute attribute : attributes) {
			if (attribute.getDomain() != null) {
				return true;
			}
		}
		return false;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}