<COMMENT>
-- Function to validate planar subdivision constraint
CREATE OR REPLACE FUNCTION val_planar_sub_<VAL_PLA_SUB_NAME>()
RETURNS TEXT AS $$
DECLARE
    rec RECORD;
    other_rec RECORD;
    has_error BOOLEAN := FALSE;
BEGIN
    -- Check planar subdivision polygons don't overlap (only touch or disjoint)
    FOR rec IN SELECT ctid, geom, <PLANAR_SUB_TABLE_KEYS> as keys FROM <PLANAR_SUB_TABLE_NAME> LOOP
        FOR other_rec IN 
            SELECT ctid, geom, <PLANAR_SUB_TABLE_KEYS> as keys 
            FROM <PLANAR_SUB_TABLE_NAME> 
            WHERE ctid != rec.ctid
        LOOP
            IF ST_Overlaps(rec.geom, other_rec.geom) THEN
                INSERT INTO spatial_error (error_type, error_message)
                VALUES ('Planar Subdivision Error', 
                        'Polygons <PLANAR_SUB_TABLE_NAME> ' || rec.keys || ' and ' || other_rec.keys || ' overlap');
                has_error := TRUE;
            END IF;
        END LOOP;
    END LOOP;
    
    -- Check there are no gaps in the subdivision (optional - can be expensive)
    -- This is a simplified check; full gap detection requires more complex logic
    
    IF has_error THEN
        RETURN 'Not valid! See table spatial_error for more details.';
    ELSE
        RETURN 'Valid! No errors were found.';
    END IF;
    
EXCEPTION
    WHEN OTHERS THEN
        INSERT INTO spatial_error (error_type, error_message)
        VALUES ('Planar Subdivision Error', 'Error: ' || SQLERRM);
        RETURN 'Not valid! See table spatial_error for more details.';
END;
$$ LANGUAGE plpgsql;

