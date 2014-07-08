CREATE FUNCTION %s_RDF_LE(ex1 VARCHAR(118), ex2 VARCHAR(118))
RETURNS Boolean	
BEGIN
DECLARE type1 VARCHAR(10);
DECLARE type2 VARCHAR(10);
DECLARE stringv1 VARCHAR(118);
DECLARE stringv2 VARCHAR(118);
DECLARE pos INT;
DECLARE pos2 INT;

SET pos=LOCATE('^^',ex1);
IF NOT (pos = 0) THEN
	SET pos2=LOCATE('#',ex1,pos);
END IF;
IF ((pos = 0) OR (pos2 = 0)) THEN 
	SET type1='string';
	SET stringv1=ex1;
ELSE
	SET type1=SUBSTR(ex1,pos2+1);
	SET stringv1=SUBSTR(ex1,1,pos-1);
END IF;

SET pos=LOCATE('^^',ex2);
IF NOT (pos = 0) THEN
	SET pos2=LOCATE('#',ex2,pos);
END IF;
IF ((pos = 0) OR (pos2 = 0)) THEN 
	SET type2='string';
	SET stringv2=ex2;
ELSE
	SET type2=SUBSTR(ex2,pos2+1);
	SET stringv2=SUBSTR(ex2,1,pos-1);
END IF;

IF type1='integer' OR type1='decimal' OR type1='float' OR type1='double' OR type1='nonPositiveInteger' OR type1='negativeInteger' OR type1='long' OR type1='int' OR type1='short' OR type1='byte' OR type1='nonNegativeInteger' OR type1='unsignedLong' OR type1='unsignedInt' OR type1='unsignedShort' or type1='unsignedByte' OR type1='positiveInteger' THEN
	IF type2='integer' OR type2='decimal' OR type2='float' OR type2='double' OR type2='nonPositiveInteger' OR type2='negativeInteger' OR type2='long' OR type2='int' OR type2='short' OR type2='byte' OR type2='nonNegativeInteger' OR type2='unsignedLong' OR type2='unsignedInt' OR type2='unsignedShort' or type2='unsignedByte' OR type2='positiveInteger' THEN
		IF CAST(stringv1 as DOUBLE) <= CAST(stringv2 as DOUBLE) THEN
			RETURN TRUE;
		ELSE
			RETURN FALSE;
		END IF;	
	ELSE
		RETURN FALSE;
	END IF;
ELSEIF type1='string' THEN
	IF type2='string' THEN
		IF stringv1 <= stringv2 THEN
			RETURN TRUE;
		ELSE
			RETURN FALSE;
		END IF;
	ELSE
		RETURN FALSE;
	END IF;	
ELSEIF type1='date' THEN
	IF type2='date' THEN
		IF CAST(stringv1 AS DATE) <= CAST(stringv2 AS DATE) THEN
			RETURN TRUE;
		ELSE
			RETURN FALSE;
		END IF;
	ELSE
		RETURN FALSE;
	END IF;		
ELSE
		RETURN FALSE;
END IF;		
END

