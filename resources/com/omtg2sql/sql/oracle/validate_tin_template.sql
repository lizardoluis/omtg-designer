<COMMENT>
CREATE OR REPLACE FUNCTION val_tin_<VAL_TIN_NAME>
RETURN VARCHAR IS

	 p1_columns 	  VARCHAR(500);
	 p2_columns 	  VARCHAR(500);
	 p_contains_error BOOLEAN;
	 
BEGIN
	 
	 -- 1. ((Pi touch Pj) or (Pi disjoint Pj)) = T for all i, j such as i != j	 
	 FOR p IN (SELECT tin1.rowid as rowid1, tin2.rowid as rowid2
  	 	   	      FROM <TIN_TABLE_NAME> tin1, <TIN_TABLE_NAME> tin2
                  WHERE tin1.rowid != tin2.rowid AND
                        SDO_RELATE(tin1.geom, tin2.geom, 'MASK=TOUCH') != 'TRUE' AND -- not touch and not disjoint
						SDO_RELATE(tin1.geom, tin2.geom, 'MASK=ANYINTERACT') = 'TRUE') LOOP

 	 	SELECT <TIN_TABLE_KEYS> INTO p1_columns
 	 	   FROM <TIN_TABLE_NAME>
 	 	   WHERE rowid = p.rowid1;
 	 	
 	 	SELECT <TIN_TABLE_KEYS> INTO p2_columns
 	 	   FROM <TIN_TABLE_NAME>
 	 	   WHERE rowid = p.rowid2;

	    INSERT INTO spatial_error (type, error) 
           VALUES ('TIN Error', 'Spatial relation between TIN <TIN_TABLE_NAME>'||p1_columns||' and'||p2_columns||' is not touch or disjoint');
	    p_contains_error := TRUE;
	    COMMIT;
			 
	 END LOOP;
	 
	 -- 2.
	 FOR p IN (SELECT tin.rowid as rowid1
  	 	   	      FROM <TIN_TABLE_NAME> tin
                  WHERE SDO_UTIL.GETNUMVERTICES(geom) != 3) LOOP

 	 	SELECT <TIN_TABLE_KEYS> INTO p1_columns
 	 	   FROM <TIN_TABLE_NAME>
 	 	   WHERE rowid = p.rowid1;

	    INSERT INTO spatial_error (type, error) 
           VALUES ('TIN Error', 'Polygon TIN <TIN_TABLE_NAME>'||p1_columns||' does not contain 3 vertex');
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
		   VALUES ('TIN Error', 'No data found');
		COMMIT;
		RETURN 'Not valid! See table Spatial_Error for more details.';
END;
/