CREATE OR REPLACE FUNCTION get_point(geom SDO_GEOMETRY, point_number NUMBER DEFAULT 1) 
RETURN SDO_GEOMETRY IS

	g  MDSYS.SDO_GEOMETRY;
	d  NUMBER;
	p  NUMBER;
	px NUMBER;
	py NUMBER;

BEGIN
	-- Get the number of dimensions from the gtype
	d := SUBSTR (geom.SDO_GTYPE, 1, 1);
	-- Verify that the point exists
	IF point_number < 1 OR point_number > geom.SDO_ORDINATES.COUNT()/d THEN
		RETURN NULL;
	END IF;
	-- Get index in ordinates array
	p := (point_number-1) * d + 1;
	-- Extract the X and Y coordinates of the desired point
	px := geom.SDO_ORDINATES(p);
	py := geom.SDO_ORDINATES(p+1);
	-- Construct and return the point
	RETURN MDSYS.SDO_GEOMETRY (2001,geom.SDO_SRID,SDO_POINT_TYPE (px,py,NULL),NULL,NULL);
END;
/