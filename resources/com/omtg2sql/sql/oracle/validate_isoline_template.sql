<COMMENT>
CREATE OR REPLACE FUNCTION val_isoline_<VAL_ISOLINE_NAME>
RETURN VARCHAR IS

	p1_columns 		 VARCHAR(500);
	p2_columns 		 VARCHAR(500);
	p_contains_error BOOLEAN;
 
BEGIN

	-- 1. ((Pi touch Pj) or (Pi disjoint Pj)) = T for all i, j such as i != j	 
	FOR p IN (SELECT iso1.rowid as rowid1, iso2.rowid as rowid2
				FROM <ISOLINE_TABLE_NAME> iso1, <ISOLINE_TABLE_NAME> iso2
				WHERE iso1.rowid != iso2.rowid AND
						SDO_RELATE(iso1.geom, iso2.geom, 'MASK=TOUCH') != 'TRUE' AND -- not touch and not disjoint
						SDO_RELATE(iso1.geom, iso2.geom, 'MASK=ANYINTERACT') = 'TRUE') LOOP
	 	
		SELECT <ISOLINE_TABLE_KEYS> INTO p1_columns
		FROM <ISOLINE_TABLE_NAME>
		WHERE rowid = p.rowid1;

		SELECT <ISOLINE_TABLE_KEYS> INTO p2_columns
		FROM <ISOLINE_TABLE_NAME>
		WHERE rowid = p.rowid2;

		INSERT INTO spatial_error (type, error)
			VALUES ('Isoline Error', 'Spatial relation between <ISOLINE_TABLE_NAME> '||p1_columns||' and '||p2_columns||' is not touch or disjoint');
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
			VALUES ('Isoline Error', 'No data found');
	COMMIT;
	RETURN 'Not valid! See table Spatial_Error for more details.';
END;
/