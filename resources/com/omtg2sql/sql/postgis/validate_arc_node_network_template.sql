<COMMENT>
-- Function to validate arc-node network constraint
CREATE OR REPLACE FUNCTION val_network_<VAL_NETWOK_NAME>()
RETURNS TEXT AS $$
DECLARE
    arc_rec RECORD;
    node_count INTEGER;
    node_found BOOLEAN;
    initial_vertex GEOMETRY;
    final_vertex GEOMETRY;
    has_error BOOLEAN := FALSE;
    arc_keys TEXT;
    node_keys TEXT;
BEGIN
    -- Check each arc has nodes at its endpoints
    FOR arc_rec IN SELECT ctid, geom, <ARC_TABLE_KEYS> as keys FROM <ARC_TABLE_NAME> LOOP
        -- Get initial and final vertices
        initial_vertex := ST_StartPoint(arc_rec.geom);
        final_vertex := ST_EndPoint(arc_rec.geom);
        
        -- Check initial vertex
        SELECT COUNT(*) INTO node_count
        FROM <NODE_TABLE_NAME>
        WHERE ST_Equals(geom, initial_vertex);
        
        IF node_count = 0 THEN
            INSERT INTO spatial_error (error_type, error_message)
            VALUES ('Arc-Node Network Error', 
                    'Initial vertex of arc <ARC_TABLE_NAME> ' || arc_rec.keys || ' is not related to any node');
            has_error := TRUE;
        ELSIF node_count > 1 THEN
            INSERT INTO spatial_error (error_type, error_message)
            VALUES ('Arc-Node Network Error', 
                    'Initial vertex of arc <ARC_TABLE_NAME> ' || arc_rec.keys || ' is related to many nodes');
            has_error := TRUE;
        END IF;
        
        -- Check final vertex
        SELECT COUNT(*) INTO node_count
        FROM <NODE_TABLE_NAME>
        WHERE ST_Equals(geom, final_vertex);
        
        IF node_count = 0 THEN
            INSERT INTO spatial_error (error_type, error_message)
            VALUES ('Arc-Node Network Error', 
                    'Final vertex of arc <ARC_TABLE_NAME> ' || arc_rec.keys || ' is not related to any node');
            has_error := TRUE;
        ELSIF node_count > 1 THEN
            INSERT INTO spatial_error (error_type, error_message)
            VALUES ('Arc-Node Network Error', 
                    'Final vertex of arc <ARC_TABLE_NAME> ' || arc_rec.keys || ' is related to many nodes');
            has_error := TRUE;
        END IF;
    END LOOP;
    
    -- Check each node is connected to at least one arc
    FOR arc_rec IN SELECT ctid, geom, <NODE_TABLE_KEYS> as keys FROM <NODE_TABLE_NAME> LOOP
        SELECT EXISTS (
            SELECT 1
            FROM <ARC_TABLE_NAME> a
            WHERE ST_Equals(arc_rec.geom, ST_StartPoint(a.geom))
               OR ST_Equals(arc_rec.geom, ST_EndPoint(a.geom))
        ) INTO node_found;
        
        IF NOT node_found THEN
            INSERT INTO spatial_error (error_type, error_message)
            VALUES ('Arc-Node Network Error', 
                    'Node <NODE_TABLE_NAME> ' || arc_rec.keys || ' is not related to any vertex');
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
        VALUES ('Arc-Node Network Error', 'Error: ' || SQLERRM);
        RETURN 'Not valid! See table spatial_error for more details.';
END;
$$ LANGUAGE plpgsql;

