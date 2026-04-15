<COMMENT>
-- Trigger function to validate DISTANT topological relationship
CREATE OR REPLACE FUNCTION trg_val_top_rel_<VAL_TOP_REL_NAME>_distant_func()
RETURNS TRIGGER AS $$
DECLARE
    found BOOLEAN;
BEGIN
    SELECT EXISTS (
        SELECT 1
        FROM <A_TABLE_NAME> a
        WHERE NOT ST_DWithin(a.geom, NEW.geom, <DISTANCE>)
    ) INTO found;
    
    IF NOT found THEN
        RAISE EXCEPTION 'Topological relationship DISTANT between <A_TABLE_NAME> and <B_TABLE_NAME> (<B_TABLE_KEYS_FORMAT>) is not satisfied (distance=<DISTANCE>)'<B_TABLE_KEYS_ARGS>;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger
DROP TRIGGER IF EXISTS trg_val_top_rel_<VAL_TOP_REL_NAME>_distant ON <B_TABLE_NAME>;
CREATE TRIGGER trg_val_top_rel_<VAL_TOP_REL_NAME>_distant
    BEFORE INSERT OR UPDATE ON <B_TABLE_NAME>
    FOR EACH ROW
    EXECUTE FUNCTION trg_val_top_rel_<VAL_TOP_REL_NAME>_distant_func();

