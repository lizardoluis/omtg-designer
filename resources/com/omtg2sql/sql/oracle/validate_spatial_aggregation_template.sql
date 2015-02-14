<COMMENT>
CREATE OR REPLACE FUNCTION val_spa_agr_<VAL_SPA_AGR_NAME>
RETURN VARCHAR IS

	 geom_w      			MDSYS.SDO_GEOMETRY;
	 geom_join      		MDSYS.SDO_GEOMETRY;
	 count_parts            NUMBER;
	 count_parts_validation NUMBER;
	 p_columns				VARCHAR(500);
	 w_columns				VARCHAR(500);
	 p_contains_error	   	BOOLEAN;
	 
BEGIN
	 
	 FOR r IN (SELECT w.rowid as w_rowid, p.rowid as p_rowid, p.geom as p_geom
	 	   	      FROM <WHOLE_TABLE_NAME> w, <PART_TABLE_NAME> p
			      WHERE SDO_RELATE(w.geom, p.geom, 'MASK=CONTAINS+COVERS+OVERLAPBDYINTERSECT') = 'TRUE') LOOP
	 	 
	 	 INSERT INTO sa_aux (w_rowid, p_rowid, p_geom) 
		    VALUES (r.w_rowid, r.p_rowid, r.p_geom);
		  
	 END LOOP;
	 COMMIT;
	 
	 SELECT count(*) 
	    INTO count_parts 
		FROM <PART_TABLE_NAME>;
	 SELECT count(*) 
	 	INTO count_parts_validation 
		FROM sa_aux;
		
	 IF (count_parts != count_parts_validation) THEN
	 	
	 	INSERT INTO spatial_error (type, error)
		   VALUES ('Spatial Aggregation Error', 'Some parts are not related to any whole');
	    p_contains_error := TRUE;
	    COMMIT;
	 END IF;
	 
	 -- 1. Pi intersection W = Pi, for all i such as 0 <= i <= n
	 FOR w IN (SELECT distinct w_rowid FROM sa_aux) LOOP
	 
	 	 SELECT geom INTO geom_w 
		 	FROM <WHOLE_TABLE_NAME> 
			WHERE rowid = w.w_rowid;
	 	 
	 	 FOR p IN (SELECT sa.p_rowid
		  	 	       FROM sa_aux sa
					   WHERE sa.w_rowid = w.w_rowid AND
					         SDO_RELATE(sa.p_geom, SDO_GEOM.SDO_INTERSECTION(geom_w, sa.p_geom, 0.5), 'MASK=EQUAL') != 'TRUE') LOOP
	 	 	
	 	 	SELECT <PART_TABLE_KEYS> INTO p_columns
	 	 	FROM <PART_TABLE_NAME>
	 	 	WHERE rowid = p.p_rowid;
	 	 	
	 	 	SELECT <WHOLE_TABLE_KEYS> INTO w_columns
	 	 	FROM <WHOLE_TABLE_NAME>
	 	 	WHERE rowid = w.w_rowid;
	 	 	 
	 	 	INSERT INTO spatial_error (type, error)
		       VALUES ('Spatial Aggregation Error', '<PART_TABLE_NAME>'||p_columns||' intersection <WHOLE_TABLE_NAME>'||w_columns|| ' is not equal to part');
			p_contains_error := TRUE;
			COMMIT;
			 
		 END LOOP;
	 END LOOP;
	 
	 -- 3. ((Pi touch Pj) or (Pi disjoint Pj)) = T for all i, j such as i != j
	 FOR w IN (SELECT distinct w_rowid FROM sa_aux) LOOP
	 
	 	 SELECT geom INTO geom_w 
		 	FROM <WHOLE_TABLE_NAME> 
			WHERE rowid = w.w_rowid;
	 	 
	 	 FOR p IN (SELECT sa1.p_rowid as p_rowid1, sa2.p_rowid as p_rowid2
		  	 	       FROM sa_aux sa1, sa_aux sa2
					   WHERE sa1.w_rowid = w.w_rowid AND sa2.w_rowid = w.w_rowid AND sa1.p_rowid != sa2.p_rowid AND
					         SDO_RELATE(sa1.p_geom, sa2.p_geom, 'MASK=TOUCH') != 'TRUE' AND -- not touch and not disjoint
							 SDO_RELATE(sa1.p_geom, sa2.p_geom, 'MASK=ANYINTERACT') = 'TRUE') LOOP
	 	 	
	 	 	SELECT <PART_TABLE_KEYS> INTO p_columns
	 	 	FROM <PART_TABLE_NAME>
	 	 	WHERE rowid = p.p_rowid1;
	 	 	
	 	 	SELECT <PART_TABLE_KEYS> INTO w_columns
	 	 	FROM <PART_TABLE_NAME>
	 	 	WHERE rowid = p.p_rowid2;
	 	 	
	 	 	INSERT INTO spatial_error (type, error)
		       VALUES ('Spatial Aggregation Error', 'Spatial relation between parts <PART_TABLE_NAME>'||p_columns||' and'||w_columns||' is not touch or disjoint');
			p_contains_error := TRUE;
			COMMIT;
			 
		 END LOOP;
	 END LOOP;
	 
	 -- 2. (W intersection all P) = W
	 FOR w IN (SELECT distinct w_rowid FROM sa_aux) LOOP
	 	 
	 	 geom_join := NULL;
	 	 
	 	 FOR p IN (SELECT sa.p_rowid, sa.p_geom
		  	 	      FROM sa_aux sa
					  WHERE sa.w_rowid = w.w_rowid) LOOP
					   
	 	 	 geom_join := join_geometry(p.p_geom, geom_join);
			 
		 END LOOP;

	 	 FOR ww IN (SELECT * 
		 	   	       FROM <WHOLE_TABLE_NAME> 
					   WHERE rowid = w.w_rowid AND
					  		 SDO_RELATE(geom, SDO_GEOM.SDO_INTERSECTION(geom, geom_join, 0.5), 'MASK=EQUAL') != 'TRUE') LOOP
	 	 	
			SELECT <WHOLE_TABLE_KEYS> INTO w_columns
	 	 	FROM <WHOLE_TABLE_NAME>
	 	 	WHERE rowid = w.w_rowid;
		    
	 	 	INSERT INTO spatial_error (type, error)
		       VALUES ('Spatial Aggregation Error', '<WHOLE_TABLE_NAME>'||w_columns||' intersection all its parts is not equal to whole');
			p_contains_error := TRUE;
			COMMIT;
		 
		 END LOOP;
	 END LOOP;
	 
	 DELETE FROM sa_aux;
	 COMMIT;
	 
	 IF (p_contains_error = TRUE) THEN
	    RETURN 'Not valid! See table Spatial_Error for more details.';
     ELSE
 	    RETURN 'Valid! No errors were found.';
     END IF;
	 
EXCEPTION
    WHEN NO_DATA_FOUND THEN
	 	INSERT INTO spatial_error (type, error)
		   VALUES ('Spatial Aggregation Error', 'No data found');
		COMMIT;
		RETURN 'Not valid! See table Spatial_Error for more details.';
END;
/