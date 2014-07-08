
CREATE FUNCTION %s_RDF_STR(s VARCHAR(118))
RETURNS VARCHAR(118)
BEGIN
	DECLARE value VARCHAR(118);
	DECLARE pos INT;

	-- String
	IF SUBSTR(s, 1, 1) = '"' THEN
		RETURN s;
	END IF;
	
	-- Typed Literal
	SET pos = LOCATE('^^', s);
	IF NOT (pos = 0) THEN  
		RETURN '"'||SUBSTR(s, 1, pos-1)||'"';
	END IF;
	
	-- Literal with Language
	SET pos = LOCATE('@', s);
	IF NOT (pos = 0) THEN 
		RETURN '"'||SUBSTR(s, 1, pos-1)||'"';
	END IF;
	
	-- others
	RETURN '"'||s||'"';
END

