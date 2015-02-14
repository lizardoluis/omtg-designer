CREATE OR REPLACE FUNCTION join_geometry(part_geometry MDSYS.SDO_GEOMETRY, old_geometry MDSYS.SDO_GEOMETRY)
RETURN MDSYS.SDO_GEOMETRY IS
	 
	 new_geometry MDSYS.SDO_GEOMETRY;  

BEGIN
	 
	 SELECT SDO_GEOM.sdo_union(old_geometry, 
	 	MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', -180, 180, 0.5),MDSYS.SDO_DIM_ELEMENT('Y', -90, 90, 0.5)),
		part_geometry, 
		MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', -180, 180, 0.5),MDSYS.SDO_DIM_ELEMENT('Y', -90, 90, 0.5))) 
	 INTO new_geometry
	 FROM dual;
	 
	 RETURN new_geometry;

END;
/
