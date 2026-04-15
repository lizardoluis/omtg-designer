<COMMENT>
-- Trigger function to validate disjoint generalization constraint
CREATE OR REPLACE FUNCTION trg_val_disjoint_gen_<VAL_DISJOINT_GEN_NAME>_func()
RETURNS TRIGGER AS $$
DECLARE
    n BOOLEAN;
BEGIN
<SELECTS>
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger
DROP TRIGGER IF EXISTS trg_val_disjoint_gen_<VAL_DISJOINT_GEN_NAME> ON <SUBCLASS_TABLE_NAME>;
CREATE TRIGGER trg_val_disjoint_gen_<VAL_DISJOINT_GEN_NAME>
    BEFORE INSERT OR UPDATE ON <SUBCLASS_TABLE_NAME>
    FOR EACH ROW
    EXECUTE FUNCTION trg_val_disjoint_gen_<VAL_DISJOINT_GEN_NAME>_func();

