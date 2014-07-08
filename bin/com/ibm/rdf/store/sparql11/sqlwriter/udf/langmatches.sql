
CREATE FUNCTION %s_RDF_LANGMATCHES(ex1 VARCHAR(118), ex2 VARCHAR(118))
RETURNS BOOLEAN 
BEGIN
	DECLARE lang1 VARCHAR(118);
	DECLARE lang2 VARCHAR(118);
	DECLARE type1 VARCHAR(118);
	DECLARE type2 VARCHAR(118);
	DECLARE pos1 INT;
	
	SET ex1 = UCASE(STRIP(ex1,B,'"'));
	SET ex2 = UCASE(STRIP(ex2,B,'"'));
	
	IF (ex1 = '@NOTALANGTAG@') THEN
		--SIGNAL SQLSTATE '0D000' SET MESSAGE_TEXT = 'Not a Language Tag.';
		RETURN FALSE;
	END IF;
	
	IF ( (ex1 != '') AND (ex2 = '*') ) THEN
		RETURN TRUE;
	END IF;
	
	SET pos1 = LOCATE('-', ex1);
	IF (pos1 > 0) THEN
		SET lang1 = SUBSTR(ex1, 1, pos1-1);
		SET type1 = SUBSTR(ex1, pos1+1);	
	ELSE
		SET lang1 = ex1;
		SET type1 = '';
	END IF;
	
	SET pos1 = LOCATE('-', ex2);
	IF (pos1 > 0) THEN
		SET lang2 = SUBSTR(ex2, 1, pos1-1);
		SET type2 = SUBSTR(ex2, pos1+1);	
	ELSE
		SET lang2 = ex2;
		SET type2 = '';
	END IF;
	
	IF (lang1 = lang2) THEN
		IF (type2 = '') THEN
			RETURN TRUE;
		ELSE
			IF (type1 = type2) THEN
				RETURN TRUE;
			ELSE 
				RETURN FALSE;
			END IF;
		END IF;
	ELSE 
		RETURN FALSE;	
	END IF;
END

