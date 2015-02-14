package com.omtg2sql.omtg.classes;

import java.util.List;

public class OMTGAttribute implements Cloneable {

	private String name, type, length, scale, defaultt, size;
	private boolean isKey, isNotNull;
	private List<String> domain;

	public OMTGAttribute(String name, String type) {
		this.name = name;
		this.type = type;
		this.isKey = false;
		this.isNotNull = false;
		this.domain = null;
	}

	public String getDefaultValue() {
		return defaultt;
	}

	public void setDefaultt(String defaultt) {
		this.defaultt = defaultt;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setIsKey() {
		this.isKey = true;
	}

	public void setIsNotNull() {
		this.isNotNull = true;
	}

	public boolean isKey() {
		return isKey;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public boolean isNotNull() {
		return isNotNull;
	}

	public void setNotNull(boolean notNull) {
		this.isNotNull = notNull;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}

	public boolean hasDomain() {
		return domain != null;
	}

	public List<String> getDomain() {
		return domain;
	}

	public void setDomain(List<String> enumeration) {
		this.domain = enumeration;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}