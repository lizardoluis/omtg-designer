<COMMENT>
CREATE OR REPLACE FUNCTION val_pla_sub_<VAL_PLA_SUB_NAME>
RETURN VARCHAR IS

	 p1_columns 	  VARCHAR(500);
	 p2_columns 	  VARCHAR(500);
	 p_contains_error BOOLEAN;
	 
BEGIN
	 
	 -- 1. ((Pi touch Pj) or (Pi disjoint Pj)) = T for all i, j such as i != j	 
	 FOR p IN (SELECT ps1.rowid as rowid1, ps2.rowid as rowid2
  	 	   	      FROM <PLANAR_SUB_TABLE_NAME> ps1, <PLANAR_SUB_TABLE_NAME> ps2
                  WHERE ps1.rowid != ps2.rowid AND
                        SDO_RELATE(ps1.geom, ps2.geom, 'MASK=TOUCH') != 'TRUE' AND -- not touch and not disjoint
						SDO_RELATE(ps1.geom, ps2.geom, 'MASK=ANYINTERACT') = 'TRUE') LOOP

 	 	SELECT <PLANAR_SUB_TABLE_KEYS> INTO p1_columns
 	 	   FROM <PLANAR_SUB_TABLE_NAME>
 	 	   WHERE rowid = p.rowid1;
 	 	
 	 	SELECT <PLANAR_SUB_TABLE_KEYS> INTO p2_columns
 	 	   FROM <PLANAR_SUB_TABLE_NAME>
 	 	   WHERE rowid = p.rowid2;

	    INSERT INTO spatial_error (type, error) 
           VALUES ('Planar Subdivision Error', 'Spatial relation between <PLANAR_SUB_TABLE_NAME>'||p1_columns||' and'||p2_columns||' is not touch or disjoint');
	    p_contains_error := TRUE;
	    COMMIT;
			 
	 END LOOP;
	 
	IF (p_contains_error = TRUE) THEN
	   RETURN 'Not valid! See table Spatial_Error for more details.';
    ELSE
 	   RETURN 'Valid! No errors were found.';
    END IF;
	 
EXCEPTION
    WHEN NO_DATA_FOUND THEN
	 	INSERT INTO spatial_error (type, error) 
		   VALUES ('Planar Subdivision Error', 'No data found');
		COMMIT;
		RETURN 'Not valid! See table Spatial_Error for more details.';
END;
/