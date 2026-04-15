<COMMENT>
-- Function to validate arc-arc network constraint
CREATE OR REPLACE FUNCTION val_network_<VAL_NETWOK_NAME>()
RETURNS TEXT AS $$
DECLARE
    arc_rec RECORD;
    connected BOOLEAN;
    has_error BOOLEAN := FALSE;
BEGIN
    -- Check each arc is connected to at least one other arc
    FOR arc_rec IN SELECT ctid, geom, <ARC_TABLE_KEYS> as keys FROM <ARC_TABLE_NAME> LOOP
        SELECT EXISTS (
            SELECT 1
            FROM <ARC_TABLE_NAME> a
            WHERE a.ctid != arc_rec.ctid
              AND (ST_Equals(ST_StartPoint(arc_rec.geom), ST_StartPoint(a.geom))
                OR ST_Equals(ST_StartPoint(arc_rec.geom), ST_EndPoint(a.geom))
                OR ST_Equals(ST_EndPoint(arc_rec.geom), ST_StartPoint(a.geom))
                OR ST_Equals(ST_EndPoint(arc_rec.geom), ST_EndPoint(a.geom)))
        ) INTO connected;
        
        IF NOT connected THEN
            INSERT INTO spatial_error (error_type, error_message)
            VALUES ('Arc-Arc Network Error', 
                    'Arc <ARC_TABLE_NAME> ' || arc_rec.keys || ' is not connected to any other arc');
            has_error := TRUE;
        END IF;
    END LOOP;
    
    IF has_error THEN
        RETURN 'Not valid! See table spatial_error for more details.';
    ELSE
        RETURN 'Valid! No errors were found.';
    END IF;
    
EXCEPTION
    WHEN OTHERS THEN
        INSERT INTO spatial_error (error_type, error_message)
        VALUES ('Arc-Arc Network Error', 'Error: ' || SQLERRM);
        RETURN 'Not valid! See table spatial_error for more details.';
END;
$$ LANGUAGE plpgsql;

