
CREATE FUNCTION %s_RDF_MUL(ex1 VARCHAR(118), ex2 VARCHAR(118))
RETURNS VARCHAR(118)
BEGIN
	DECLARE type1 VARCHAR(10);
	DECLARE type2 VARCHAR(10);
	DECLARE stringv1 VARCHAR(118);
	DECLARE stringv2 VARCHAR(118);
	DECLARE pos1 INT;
	DECLARE pos2 INT;

	SET pos1=LOCATE('^^',ex1);
	IF NOT (pos1 = 0) THEN 
		SET pos2=LOCATE('#',ex1,pos1);
	END IF;
	IF ((pos1 = 0) OR (pos2 = 0)) THEN 
		return '';
	ELSE
		SET type1=SUBSTR(ex1,pos2+1);
		SET stringv1=SUBSTR(ex1,1,pos1-1);
	END IF;

	SET pos1=LOCATE('^^',ex2);
	IF NOT (pos1 = 0) THEN 
		SET pos2=LOCATE('#',ex2,pos1);
	END IF;
	IF ((pos1 = 0) OR (pos2 = 0)) THEN 
		return '';
	ELSE
		SET type2=SUBSTR(ex2,pos2+1);
		SET stringv2=SUBSTR(ex2,1,pos1-1);
	END IF;

	IF type1='integer' OR type1='nonPositiveInteger' OR type1='negativeInteger' OR type1='long' OR type1='int' OR type1='short' OR type1='byte' OR type1='nonNegativeInteger' OR type1='unsignedLong' OR type1='unsignedInt' OR type1='unsignedShort' or type1='unsignedByte' OR type1='positiveInteger' THEN
		IF type2='integer' OR type2='nonPositiveInteger' OR type2='negativeInteger' OR type2='long' OR type2='int' OR type2='short' OR type2='byte' OR type2='nonNegativeInteger' OR type2='unsignedLong' OR type2='unsignedInt' OR type2='unsignedShort' or type2='unsignedByte' OR type2='positiveInteger' THEN
			return (CAST(stringv1 as INTEGER) * CAST(stringv2 as INTEGER)) || '^^http://www.w3.org/2001/XMLSchema#integer';
		ELSEIF type2='decimal' OR type2='float' THEN
			return (CAST(stringv1 as DECIMAL) * CAST(stringv2 as DECIMAL)) || '^^http://www.w3.org/2001/XMLSchema#decimal';
		ELSEIF type2='double' THEN
			return (CAST(stringv1 as DOUBLE) * CAST(stringv2 as DOUBLE)) || '^^http://www.w3.org/2001/XMLSchema#double';
		END IF;	
	ELSEIF type1='decimal' OR type1='float' THEN
		IF type2='decimal' OR type2='float' THEN
			return (CAST(stringv1 as DECIMAL) * CAST(stringv2 as DECIMAL)) || '^^http://www.w3.org/2001/XMLSchema#decimal';
		ELSEIF type2='double' THEN
			return (CAST(stringv1 as DOUBLE) * CAST(stringv2 as DOUBLE)) || '^^http://www.w3.org/2001/XMLSchema#double';
		END IF;	
	ELSEIF type1='double' THEN 
		IF type2='double' THEN
			return (CAST(stringv1 as DOUBLE) * CAST(stringv2 as DOUBLE)) || '^^http://www.w3.org/2001/XMLSchema#double';
		END IF;		
	END IF;
	
	RETURN '';	
END

