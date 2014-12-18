CREATE or replace function readfile(dir VARHAR(200), file VACHAR(200))
returns CLOB
language sql contains sql
BEGIN
  DECLARE    v_textfile       UTL_FILE.FILE_TYPE;
  DECLARE    v_textline       VARCHAR(200);
  DECLARE SQLCODE INTEGER DEFAULT 0;
  DECLARE SQLSTATE CHAR(5) DEFAULT '00000';
  DECLARE SQLSTATE1 CHAR(5) DEFAULT '00000';
  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'SET SQLSTATE1 = SQLSTATE;

  SET v_textfile = UTL_FILE.FOPEN(v_dirAlias,v_filename,'r');

  loop1: LOOP
    CALL UTL_FILE.GET_LINE(v_textfile, v_textline);
    IF SQLSTATE1 = '02000' THEN -- NO DATA FOUND
      LEAVE loop1;
    END IF;
    CALL DBMS_OUTPUT.PUT_LINE(v_textline);
  END LOOP;
  CALL DBMS_OUTPUT.PUT_LINE('End of file ' || v_filename || ' - ' || v_count
        || ' records retrieved');
  CALL UTL_FILE.FCLOSE(v_textfile);
END@
