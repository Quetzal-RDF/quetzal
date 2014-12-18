CREATE OR REPLACE FUNCTION typeof(lit varchar(200)) RETURNS CHAR(1) AS $$
 DECLARE v FLOAT;
         d TIMESTAMP;
BEGIN
        BEGIN
                BEGIN
                v := lit::float;
                EXCEPTION WHEN OTHERS THEN
                        BEGIN
                        d := lit::timestamp;
                        EXCEPTION WHEN OTHERS THEN
                                BEGIN
                                RETURN 'V';
                                END;
                        END;
                        RETURN 'T';
                END;
                RETURN 'D';
        END;
END;
$$ LANGUAGE plpgsql;
