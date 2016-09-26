<COMMENT>
CREATE TRIGGER <VAL_TOP_REL_NAME>_near_insert_update_trigger
  AFTER INSERT OR UPDATE ON <A_TABLE_NAME>
  FOR EACH STATEMENT
  EXECUTE PROCEDURE omtg_topologicalrelationship('<A_TABLE_NAME>', 'geom', '<B_TABLE_NAME>', 'geom', '<DISTANCE>');

  