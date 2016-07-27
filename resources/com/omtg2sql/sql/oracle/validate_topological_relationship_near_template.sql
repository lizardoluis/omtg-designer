<COMMENT>
CREATE OR REPLACE TRIGGER val_top_rel_<VAL_TOP_REL_NAME>
   BEFORE INSERT OR UPDATE ON <B_TABLE_NAME>
   REFERENCING NEW AS NEW OLD AS OLD
   FOR EACH ROW
DECLARE
   w_rowid rowid;
BEGIN
   SELECT rowid INTO w_rowid
      FROM <A_TABLE_NAME> w
      WHERE SDO_WITHIN_DISTANCE(w.geom, :NEW.geom, 'distance=<DISTANCE>') = 'TRUE' AND rownum <= 1;
EXCEPTION
   WHEN NO_DATA_FOUND THEN
      RAISE_APPLICATION_ERROR(-20001, 'Topological relationship between <A_TABLE_NAME> and <B_TABLE_NAME> <B_TABLE_KEYS>is not near with distance=<DISTANCE>');
END;
/