package com.omtg2sql.sql.postgis;

import java.io.IOException;
import java.io.StringWriter;

import com.omtg2sql.omtg.classes.OMTGClass;
import com.omtg2sql.sql.oracle.DDLWriter;
import com.omtg2sql.sql.oracle.OffLineConstraintsWriter;
import com.omtg2sql.sql.oracle.OnLineConstraintsWriter;

public class PostGISGenerator {
	
	private PostGISDDLWriter ddl;
	private OffLineConstraintsWriter offLineConstraints;
	private OnLineConstraintsWriter onLineConstraints;
	
	public PostGISGenerator(String ddlFilePath, String offLineConstraintsFilePath,
			String onLineConstraintsFilePath) {

		this.ddl = new PostGISDDLWriter(ddlFilePath);
	}
	
	public PostGISGenerator(StringWriter ddlSW, StringWriter offLineConstraintsSW,
			StringWriter onLineConstraintsSW) {

		this.ddl = new PostGISDDLWriter(ddlSW);
	}

	public void mapClass(OMTGClass omtgClass) {
		createTable(omtgClass);
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
			createSpatialClassConstraint(omtgClass);
//			createInsertIntoUserSdoGeomMetadata(omtgClass);
//			createSpatialIndex(omtgClass);
		}
	}
	
	
	public void close() {
		try {
			ddl.close();
//			offLineConstraints.close();
//			onLineConstraints.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}