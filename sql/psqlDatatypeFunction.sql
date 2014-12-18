CREATE OR REPLACE FUNCTION db2inst1.datatype(var text, xmlSchemaType text) RETURNS text AS $$
DECLARE x BOOLEAN;
DECLARE y NUMERIC;
DECLARE z TIMESTAMP WITH TIME ZONE;
DECLARE m DATE;

BEGIN
    IF var IS NULL THEN
        RETURN NULL;
    ELSIF db2inst1.iscastable(var, xmlSchemaType)=true THEN
    	RETURN xmlSchemaType;
    ELSE
        RETURN 'http://www.w3.org/2001/XMLSchema#string';
    END IF;
EXCEPTION WHEN others THEN
    RETURN 'http://www.w3.org/2001/XMLSchema#string';
END;
$$ LANGUAGE plpgsql IMMUTABLE;