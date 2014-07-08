
CREATE FUNCTION RDF_CONVERT(ex1 VARCHAR(118))
RETURNS DECFLOAT
BEGIN
	DECLARE type1 VARCHAR(10);
	DECLARE stringv1 VARCHAR(118);
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

	IF type1='integer' OR type1='nonPositiveInteger' OR type1='negativeInteger' OR type1='long' OR type1='int' OR type1='short' OR type1='byte' OR type1='nonNegativeInteger' OR type1='unsignedLong' OR type1='unsignedInt' OR type1='unsignedShort' or type1='unsignedByte' OR type1='positiveInteger' THEN
		return (CAST(stringv1 as INTEGER));
		
	ELSEIF type1='decimal' OR type1='float' THEN
		return (CAST(stringv1 as DOUBLE));
		
	ELSEIF type1='double' THEN 
		return (CAST(stringv1 as DOUBLE));
			
	END IF;
	
	RETURN '';	
END
/
