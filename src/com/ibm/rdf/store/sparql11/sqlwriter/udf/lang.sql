
CREATE FUNCTION %s_RDF_LANG(ex1 VARCHAR(118))
RETURNS VARCHAR(118)
BEGIN
	DECLARE lang VARCHAR(118);
	DECLARE type VARCHAR(118);
	DECLARE pos1 INT;
	DECLARE pos2 INT;

	-- null value
	IF ex1 IS NULL THEN
		RETURN '"@NotALangTag@"';
	END IF;
	
	-- valid value
	SET pos1 = LOCATE('@', ex1);
	IF NOT (pos1 = 0) THEN 
		SET lang = SUBSTR(ex1, pos1+1);
		RETURN UPPER(lang); 
	END IF;

	-- blank
	SET type = SUBSTR(ex1, 1, 2);
	IF type = '_:' THEN
		RETURN '"@NotALangTag@"';
	END IF;
	
	-- string
	SET pos1 = LOCATE('"', ex1);
	IF (pos1 = 1) THEN
		RETURN '""';
	END IF;
	
	-- typed string
	SET pos1 = LOCATE('^^', ex1);
	IF (pos1 > 1) THEN
		RETURN '""';
	END IF;

	-- number
	IF (UPPER(ex1) = LOWER(ex1)) THEN
		RETURN '""';
	END IF;
	
	RETURN '"@NotALangTag@"';	
END

