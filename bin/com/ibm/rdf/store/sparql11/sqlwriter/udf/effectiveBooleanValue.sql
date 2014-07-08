
CREATE FUNCTION %s_RDF_EBV(ex1 VARCHAR(118))
RETURNS BOOLEAN 
BEGIN 
	DECLARE type1 VARCHAR(10);
	DECLARE type2 VARCHAR(10);
	DECLARE stringv1 VARCHAR(118);
	DECLARE stringv2 VARCHAR(118);
	DECLARE pos1 INT;
	DECLARE pos2 INT;

	SET pos1 = LOCATE('^^', ex1);
	IF NOT (pos1 = 0) THEN 
		SET pos2 = LOCATE('#', ex1, pos1);
	END IF;
	IF ((pos1 = 0) OR (pos2 = 0)) THEN 
		SET pos1 = LOCATE('@', ex1);
		IF (pos1 = 0) THEN
			SET type1 = 'string'; 
			SET stringv1 = ex1;
		ELSE
			SET type1 = '';
		END IF;
	ELSE
		SET type1 = SUBSTR(ex1, pos2+1);
		SET stringv1 = SUBSTR(ex1, 1, pos1-1);
	END IF;

	IF type1 = 'boolean' THEN
		IF UCASE(stringv1) = 'TRUE' THEN
			RETURN TRUE;
		ELSE 
			RETURN FALSE;
		END IF;
	END IF;
	
	IF type1 = 'string' THEN
		IF stringv1 = '""' THEN
			RETURN FALSE;
		ELSEIF stringv1 = '' THEN
			RETURN FALSE;
		ELSE
			RETURN TRUE;
		END IF;
	END IF;
	
	IF type1='integer' OR type1='nonPositiveInteger' OR type1='negativeInteger' OR type1='long' OR type1='int' OR type1='short' OR type1='byte' OR type1='nonNegativeInteger' OR type1='unsignedLong' OR type1='unsignedInt' OR type1='unsignedShort' or type1='unsignedByte' OR type1='positiveInteger' 
		OR type1='decimal' OR type1='float' OR type1='double' THEN
		IF (CAST(stringv1 as DOUBLE) = CAST(0 as DOUBLE)) THEN
			RETURN FALSE;
		ELSE
			RETURN TRUE;
		END IF;
	END IF;
		
	RETURN FALSE;	
END

