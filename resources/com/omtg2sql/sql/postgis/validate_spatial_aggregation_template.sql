<COMMENT>
-- Function to validate spatial aggregation between whole and part
CREATE OR REPLACE FUNCTION val_spa_agr_<VAL_SPA_AGR_NAME>()
RETURNS TEXT AS $$
DECLARE
    rec RECORD;
    whole_rec RECORD;
    part_rec RECORD;
    geom_join GEOMETRY;
    count_parts INTEGER;
    count_parts_validation INTEGER;
    p_columns TEXT;
    w_columns TEXT;
    has_error BOOLEAN := FALSE;
BEGIN
    -- Check if all parts are related to some whole
    SELECT COUNT(*) INTO count_parts FROM <PART_TABLE_NAME>;
    
    SELECT COUNT(DISTINCT p.ctid) INTO count_parts_validation
    FROM <WHOLE_TABLE_NAME> w, <PART_TABLE_NAME> p
    WHERE ST_Contains(w.geom, p.geom) OR ST_Covers(w.geom, p.geom) OR ST_Overlaps(w.geom, p.geom);
    
    IF count_parts != count_parts_validation THEN
        INSERT INTO spatial_error (error_type, error_message)
        VALUES ('Spatial Aggregation Error', 'Some parts are not related to any whole');
        has_error := TRUE;
    END IF;
    
    -- 1. Check Pi intersection W = Pi for all parts
    FOR whole_rec IN SELECT ctid as w_ctid, geom as w_geom FROM <WHOLE_TABLE_NAME> LOOP
        FOR part_rec IN 
            SELECT p.ctid as p_ctid, p.geom as p_geom, <PART_TABLE_KEYS> as p_keys
            FROM <PART_TABLE_NAME> p
            WHERE (ST_Contains(whole_rec.w_geom, p.geom) OR ST_Covers(whole_rec.w_geom, p.geom) OR ST_Overlaps(whole_rec.w_geom, p.geom))
              AND NOT ST_Equals(p.geom, ST_Intersection(whole_rec.w_geom, p.geom))
        LOOP
            SELECT <WHOLE_TABLE_KEYS> INTO w_columns FROM <WHOLE_TABLE_NAME> WHERE ctid = whole_rec.w_ctid;
            
            INSERT INTO spatial_error (error_type, error_message)
            VALUES ('Spatial Aggregation Error', 
                    '<PART_TABLE_NAME> ' || part_rec.p_keys || ' intersection <WHOLE_TABLE_NAME> ' || w_columns || ' is not equal to part');
            has_error := TRUE;
        END LOOP;
    END LOOP;
    
    -- 2. Check parts touch or are disjoint (no overlap between parts)
    FOR whole_rec IN SELECT ctid as w_ctid, geom as w_geom FROM <WHOLE_TABLE_NAME> LOOP
        FOR rec IN
            SELECT p1.ctid as p1_ctid, p2.ctid as p2_ctid, 
                   (SELECT <PART_TABLE_KEYS> FROM <PART_TABLE_NAME> WHERE ctid = p1.ctid) as p1_keys,
                   (SELECT <PART_TABLE_KEYS> FROM <PART_TABLE_NAME> WHERE ctid = p2.ctid) as p2_keys
            FROM <PART_TABLE_NAME> p1, <PART_TABLE_NAME> p2
            WHERE (ST_Contains(whole_rec.w_geom, p1.geom) OR ST_Covers(whole_rec.w_geom, p1.geom) OR ST_Overlaps(whole_rec.w_geom, p1.geom))
              AND (ST_Contains(whole_rec.w_geom, p2.geom) OR ST_Covers(whole_rec.w_geom, p2.geom) OR ST_Overlaps(whole_rec.w_geom, p2.geom))
              AND p1.ctid != p2.ctid
              AND NOT ST_Touches(p1.geom, p2.geom)
              AND ST_Intersects(p1.geom, p2.geom)
        LOOP
            INSERT INTO spatial_error (error_type, error_message)
            VALUES ('Spatial Aggregation Error', 
                    'Spatial relation between parts <PART_TABLE_NAME> ' || rec.p1_keys || ' and ' || rec.p2_keys || ' is not touch or disjoint');
            has_error := TRUE;
        END LOOP;
    END LOOP;
    
    -- 3. Check W intersection all P = W
    FOR whole_rec IN SELECT ctid as w_ctid, geom as w_geom FROM <WHOLE_TABLE_NAME> LOOP
        geom_join := NULL;
        
        FOR part_rec IN 
            SELECT p.geom as p_geom
            FROM <PART_TABLE_NAME> p
            WHERE ST_Contains(whole_rec.w_geom, p.geom) OR ST_Covers(whole_rec.w_geom, p.geom) OR ST_Overlaps(whole_rec.w_geom, p.geom)
        LOOP
            IF geom_join IS NULL THEN
                geom_join := part_rec.p_geom;
            ELSE
                geom_join := ST_Union(geom_join, part_rec.p_geom);
            END IF;
        END LOOP;
        
        IF geom_join IS NOT NULL AND NOT ST_Equals(whole_rec.w_geom, ST_Intersection(whole_rec.w_geom, geom_join)) THEN
            SELECT <WHOLE_TABLE_KEYS> INTO w_columns FROM <WHOLE_TABLE_NAME> WHERE ctid = whole_rec.w_ctid;
            
            INSERT INTO spatial_error (error_type, error_message)
            VALUES ('Spatial Aggregation Error', 
                    '<WHOLE_TABLE_NAME> ' || w_columns || ' intersection all its parts is not equal to whole');
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
        VALUES ('Spatial Aggregation Error', 'Error: ' || SQLERRM);
        RETURN 'Not valid! See table spatial_error for more details.';
END;
$$ LANGUAGE plpgsql;

