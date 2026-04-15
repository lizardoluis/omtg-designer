<COMMENT>
-- Function to validate TIN (Triangular Irregular Network) constraint
CREATE OR REPLACE FUNCTION val_tin_<VAL_TIN_NAME>()
RETURNS TEXT AS $$
DECLARE
    rec RECORD;
    other_rec RECORD;
    has_error BOOLEAN := FALSE;
    vertex_count INTEGER;
BEGIN
    -- Check each TIN polygon is a valid triangle (3 vertices + closing point)
    FOR rec IN SELECT ctid, geom, <TIN_TABLE_KEYS> as keys FROM <TIN_TABLE_NAME> LOOP
        vertex_count := ST_NPoints(rec.geom);
        
        -- A triangle should have exactly 4 points (3 vertices + closing point)
        IF vertex_count != 4 THEN
            INSERT INTO spatial_error (error_type, error_message)
            VALUES ('TIN Error', 
                    'Polygon <TIN_TABLE_NAME> ' || rec.keys || ' is not a valid triangle (has ' || vertex_count || ' vertices)');
            has_error := TRUE;
        END IF;
    END LOOP;
    
    -- Check TIN polygons don't overlap (only touch or disjoint)
    FOR rec IN SELECT ctid, geom, <TIN_TABLE_KEYS> as keys FROM <TIN_TABLE_NAME> LOOP
        FOR other_rec IN 
            SELECT ctid, geom, <TIN_TABLE_KEYS> as keys 
            FROM <TIN_TABLE_NAME> 
            WHERE ctid != rec.ctid
        LOOP
            IF ST_Overlaps(rec.geom, other_rec.geom) THEN
                INSERT INTO spatial_error (error_type, error_message)
                VALUES ('TIN Error', 
                        'TIN polygons <TIN_TABLE_NAME> ' || rec.keys || ' and ' || other_rec.keys || ' overlap');
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
        VALUES ('TIN Error', 'Error: ' || SQLERRM);
        RETURN 'Not valid! See table spatial_error for more details.';
END;
$$ LANGUAGE plpgsql;

