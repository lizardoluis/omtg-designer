<COMMENT>
-- Function to validate isoline constraint
CREATE OR REPLACE FUNCTION val_isoline_<VAL_ISOLINE_NAME>()
RETURNS TEXT AS $$
DECLARE
    rec RECORD;
    other_rec RECORD;
    has_error BOOLEAN := FALSE;
BEGIN
    -- Check isolines don't cross each other (only touch at endpoints or disjoint)
    FOR rec IN SELECT ctid, geom, <ISOLINE_TABLE_KEYS> as keys FROM <ISOLINE_TABLE_NAME> LOOP
        FOR other_rec IN 
            SELECT ctid, geom, <ISOLINE_TABLE_KEYS> as keys 
            FROM <ISOLINE_TABLE_NAME> 
            WHERE ctid != rec.ctid
        LOOP
            IF ST_Crosses(rec.geom, other_rec.geom) THEN
                INSERT INTO spatial_error (error_type, error_message)
                VALUES ('Isoline Error', 
                        'Isolines <ISOLINE_TABLE_NAME> ' || rec.keys || ' and ' || other_rec.keys || ' cross each other');
                has_error := TRUE;
            END IF;
        END LOOP;
    END LOOP;
    
    IF has_error THEN
        RETURN 'Not valid! See table spatial_error for more details.';
    ELSE
        RETURN 'Valid! No errors were found.';
    END IF;
    
EXCEPTION
    WHEN OTHERS THEN
        INSERT INTO spatial_error (error_type, error_message)
        VALUES ('Isoline Error', 'Error: ' || SQLERRM);
        RETURN 'Not valid! See table spatial_error for more details.';
END;
$$ LANGUAGE plpgsql;

