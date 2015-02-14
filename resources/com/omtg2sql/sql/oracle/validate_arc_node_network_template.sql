<COMMENT>
CREATE OR REPLACE FUNCTION val_network_<VAL_NETWOK_NAME>
RETURN VARCHAR IS

	p_geom_initial_vertex  MDSYS.SDO_GEOMETRY;
	p_geom_final_vertex    MDSYS.SDO_GEOMETRY;
	p_geom_vertex    	   MDSYS.SDO_GEOMETRY;
	p_rowid_initial_vertex rowid;
	p_rowid_final_vertex   rowid;
	p_rowid_point		   rowid;
	p_keys			   	   VARCHAR(500);
	p_contains_error	   BOOLEAN;

BEGIN
	 
	-- 1.
	FOR reg IN (SELECT rowid, geom FROM <ARC_TABLE_NAME>) LOOP
	 
	 	p_geom_initial_vertex := get_point(reg.geom);
	 	p_geom_final_vertex := get_point(reg.geom, SDO_UTIL.GETNUMVERTICES(reg.geom));
	 	
	 	BEGIN

	 		SELECT rowid INTO p_rowid_initial_vertex
			   FROM <NODE_TABLE_NAME>
	 		   WHERE MDSYS.SDO_EQUAL(geom, p_geom_initial_vertex) = 'TRUE';

	 	EXCEPTION 
		    
			WHEN NO_DATA_FOUND THEN
				 SELECT <ARC_TABLE_KEYS> INTO p_keys
	 	 		 	FROM <ARC_TABLE_NAME>
	 	 		 	WHERE rowid = reg.rowid;
	 	 		 	
	 	 		 INSERT INTO spatial_error (type, error)
				 		VALUES ('Arc-Node Network Error', 'Initial vertex of arc <ARC_TABLE_NAME>'||p_keys||' is not related to any node');
				 p_contains_error := TRUE;
				 COMMIT;
		    
			WHEN TOO_MANY_ROWS THEN
				 SELECT <ARC_TABLE_KEYS> INTO p_keys
	 	 		 	FROM <ARC_TABLE_NAME>
	 	 		 	WHERE rowid = reg.rowid;
	 	 		 	
	 	 		 INSERT INTO spatial_error (type, error)
				 		VALUES ('Arc-Node Network Error', 'Initial vertex of arc <ARC_TABLE_NAME>'||p_keys||' is related to many nodes');
				 p_contains_error := TRUE;
				 COMMIT;
	 	END;
	 	
	 	BEGIN

	 		SELECT rowid INTO p_rowid_final_vertex
			   FROM <NODE_TABLE_NAME>
	 		   WHERE MDSYS.SDO_EQUAL(geom, p_geom_final_vertex) = 'TRUE';

	 	EXCEPTION 
		    
			WHEN NO_DATA_FOUND THEN
				 SELECT <ARC_TABLE_KEYS> INTO p_keys
	 	 		 	FROM <ARC_TABLE_NAME>
	 	 		 	WHERE rowid = reg.rowid;
	 	 		 	
	 	 		 INSERT INTO spatial_error (type, error)
				 		VALUES ('Arc-Node Network Error', 'Final vertex of arc <ARC_TABLE_NAME>'||p_keys||' is not related to any node');
				 p_contains_error := TRUE;
				 COMMIT;
		    
			WHEN TOO_MANY_ROWS THEN
				 SELECT <ARC_TABLE_KEYS> INTO p_keys
	 	 		 	FROM <ARC_TABLE_NAME>
	 	 		 	WHERE rowid = reg.rowid;
	 	 		 	
	 	 		 INSERT INTO spatial_error (type, error)
				 		VALUES ('Arc-Node Network Error', 'Final vertex of arc <ARC_TABLE_NAME>'||p_keys||' is related to many nodes');
				 p_contains_error := TRUE;
				 COMMIT;
	 	END;
	END LOOP;

	-- 2.
	FOR reg IN (SELECT rowid FROM <NODE_TABLE_NAME>) LOOP
	 	
	 	BEGIN
	 	
	 		SELECT a.rowid INTO p_rowid_point
			   FROM <ARC_TABLE_NAME> a, <NODE_TABLE_NAME> n
	 		   WHERE (MDSYS.SDO_EQUAL(n.geom, get_point(a.geom)) = 'TRUE' OR 
			   		 MDSYS.SDO_EQUAL(n.geom, get_point(a.geom, SDO_UTIL.GETNUMVERTICES(a.geom))) = 'TRUE') AND 
			   		 reg.rowid = n.rowid AND rownum <= 1;
	 		
	 	EXCEPTION
	 	
		    WHEN NO_DATA_FOUND THEN
				 SELECT <NODE_TABLE_KEYS> INTO p_keys
	 	 		 	FROM <NODE_TABLE_NAME>
	 	 		 	WHERE rowid = reg.rowid;
	 	 		 	
	 	 		 INSERT INTO spatial_error (type, error)
				 		VALUES ('Arc-Node Network Error', 'Node <NODE_TABLE_NAME>'||p_keys||' is not related to any vertex');
				 p_contains_error := TRUE;
				 COMMIT;
	 	END;
	END LOOP;
	
	IF (p_contains_error = TRUE) THEN
	   RETURN 'Not valid! See table Spatial_Error for more details.';
    ELSE
 	   RETURN 'Valid! No errors were found.';
    END IF;
	
END;
/