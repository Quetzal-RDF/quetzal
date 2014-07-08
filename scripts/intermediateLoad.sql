-- PDA: delimiter is  ! :  launch this script with  db2 -vtd! -f SetOrientedLoad.sql
--PDA:  some management
CONNECT TO T3!

DROP TABLE ThinTable!

DROP TABLE predicate_mapping!

-- these steps are done separately for direct and reverse tables
-- PDA: Assuming a thin table with 3 columns
create table ThinTable(subject varchar(118) not null, predicate varchar(118) not null , object varchar(118) not null)
!
create index ThinTableidx on ThinTable(subject)!
create index ThinTableidx1 on ThinTable(predicate)!
create index ThinTableidx2 on ThinTable(object)!

insert into ThinTable VALUES ('http://subject2','http://predicateA','http://object1'),
                             ('http://subject2','http://predicateB','http://object2'),
                             ('http://subject2','http://predicateC','http://object3'),
                             ('http://subject1','http://predicateA','http://object1'),
                             ('http://subject1','http://predicateB','http://object2'),
                             ('http://subject1','http://predicateC','http://object3'),
                             ('http://subject1','http://predicateD','http://object4'),
                             ('Charles Flint', 'born', '1850'),
                             ('Charles Flint', 'died', '1934'),
                             ('Charles Flint', 'founder', 'IBM'),
                             ('Larry Page', 'born', '1973'),
                             ('Larry Page', 'founder', 'Google'),
                             ('Larry Page', 'board', 'Google'),
                             ('Larry Page', 'home', 'Palo Alto'),
                             ('Android', 'developer', 'Google'),
                             ('Android', 'version', '4.1'),
                             ('Android', 'kernel', 'Linux'),
                             ('Android', 'preceded', '4.0'),
                             ('Android', 'graphics', 'OpenGL'),
                             ('Google', 'industry', 'Software'),
                             ('Google', 'industry', 'Internet'),
                             ('Google', 'employees', '54,604'),
                             ('Google', 'HQ', 'Mountain View'),
                             ('IBM', 'industry', 'Software'),
                             ('IBM', 'industry', 'Hardware'),
                             ('IBM', 'industry', 'Services'),
                             ('IBM', 'employees', '433,362'),
                             ('IBM', 'HQ', 'Armonk')
!
--PDA: let's calculate all predicates positions and store it in a table
-- We have two mapping functions to map predicates to columns
-- Notice that to insert subject 2 into DPH, its possible to put the subject's data in without spills if you consider BOTH mapping functions, regardless of the order in which one processes subject 2's triples.
-- For subject 1, its impossible to not spill (4 predicates and 3 columns :)).
-- The assumption is that DPH has 3 columns, 0, 1, 2

create table predicate_mapping ( predicate varchar(118), map1 integer, map2 integer) !
create index predicate_mapping_idx1 on predicate_mapping(predicate)!
create index predicate_mapping_idx2 on predicate_mapping(map1)!
create index predicate_mapping_idx3 on predicate_mapping(map2)!

insert into predicate_mapping VALUES ('http://predicateA',0,1),
                                     ('http://predicateB',0,2),
                                     ('http://predicateC',1,2),
                                     ('http://predicateD',0,2)
!


create table subject_mapping ( subject varchar(118), map1 integer, map2 integer) !
create index subject_mapping_idx1 on subject_mapping(subject)!
create index subject_mapping_idx2 on subject_mapping(map1)!
create index subject_mapping_idx3 on subject_mapping(map2)!

CREATE   VARIABLE V_NBCOLS INTEGER DEFAULT 3
!
CREATE   VARIABLE V_GRAPH VARCHAR(20) DEFAULT 'TEST'
!
CREATE   VARIABLE V_DEFAULT_TYPE INTEGER DEFAULT 10100
!



select * from thintable!
-------------------------------------------------------------
-----Automatic mapping of predicates 
-------------------------------------------------------------
DELETE PREDICATE_MAPPING !
--WHERE PREDICATE NOT LIKE 'http:%'!

INSERT INTO PREDICATE_MAPPING 
 SELECT distinct *  FROM (
  SELECT predicate ,
        MOD(length(trim(predicate)),3),  
        CASE WHEN MOD(length(trim(predicate)),3) =2 THEN   0 
             WHEN MOD(length(trim(predicate)),3) =1 THEN 2 ELSE 1 END
  FROM  THINTABLE ) T
  --WHERE PREDICATE NOT LIKE 'http:%'
 !
 
 SELECT * FROM PREDICATE_MAPPING!
-------------------------------------------------------------
-----Automatic mapping of subjects 
-------------------------------------------------------------
DELETE SUBJECT_MAPPING !
--WHERE SUBJECT NOT LIKE 'http:%'!

INSERT INTO SUBJECT_MAPPING 
 SELECT distinct *  FROM (
  SELECT subject ,
        MOD(length(trim(subject)),3),  
        CASE WHEN MOD(length(trim(subject)),3) =2 THEN   0 
             WHEN MOD(length(trim(subject)),3) =1 THEN 2 ELSE 1 END
  FROM  THINTABLE ) T
  --WHERE PREDICATE NOT LIKE 'http:%'
 !
  SELECT * FROM SUBJECT_MAPPING!

 ------------------------------------------------------------------
 ------------  FINAL STATS  ---------------------------------------
 ------------------------------------------------------------------
select count(*), 'nb_rows_in_thin_table'  From thintable
union
select count(*), 'nb_rows_in_predicate_mapping'  From predicate_mapping        
union
select count(*), 'nb_rows_in_subject_mapping'  From subject_mapping!        

-----------------------------------------------------------------------------------------------------------------------------------------------------        

-- 1. sort file and split it into N sorted chunks, keeping each subject together
--(currently done with Unix sort --parallel to sort, and a gawk script to split the output)
-- PDA: No NEED to do that in SQL .  will be taken care of in teh SQL operations directly . 

--2. for each chunk
--PDA:  let's try first without any chunking !!!
 --  2.1  compute edge sets for each subject
--   	This step generates a table with lines consisting of every distinct set of predicates indicent on any subject.  
--   	Each line is associated with a count of how many subjects have that specific edge set.
     --PDA: ok let's do that. 
    SELECT subject subject_having_to_spill,count( distinct predicate) 
      FROM ThinTable 
     GROUP BY subject having count(distinct predicate) >v_NBCOLS
   !   
     SELECT TT.subject, count(   MAP1), count(   MAP2)
       FROM PREDICATE_MAPPING PM, ThinTable TT
      WHERE TT.predicate=PM.predicate
      GROUP BY subject
     !

   --2.2  record which predicates are multi valued
   --This is simply a list of all predicates that have any subject with more than one triple with that predicate.

 --3. combine edge set chunks: This step combines the edge sets generated in 2.1 by summing the counts across all N chunks.
  --PDA: no need to do this in this SQL scripts for now. 

--4. compute column assignments for predicates using edge sets
   --PDA:  don't understand how this is done !!  

--5. assign ids for all types and locales used in dataset
     --PDA:  not sure this is the right alogorithm here to get sall datatypes first. 
     --anyway,  insert them first into the RDFSTORE1_DT type table. 
--    INSERT INTO RDFSTORE1_DT
--      SELECT row_number() over() + mx, subject
--       FROM (
--       SELECT distinct subject 
--         FROM ThinTable 
--        WHERE subject not in (SELECT distinct datatype_name from RDFSTORE1_DT) and subject like 'http://www.w3.org/%#%' or subject like 'rdf:%'
--       UNION 
--       SELECT distinct predicate 
--         FROM ThinTable
--        WHERE predicate not in (SELECT distinct datatype_name from RDFSTORE1_DT) and predicate like 'http://www.w3.org/%#%' or predicate like 'rdf:%'
--       UNION 
--       SELECT distinct object 
--         FROM ThinTable
--        WHERE object not in (SELECT distinct datatype_name from RDFSTORE1_DT) and object like 'http://www.w3.org/2001/%#%' or object like 'rdf:%'
--        ) T, (select max(DATATYPE_ID) mx FROM RDFSTORE1_DT) 
--!        
    
-- 6. for each chunk
--PDA: no need to do that  yet

  --6.1  compute layout of triples in rows, using column assignments
  --PDA: NO NEED TO DO THIS in a separate step. do all in 6.2.
  DELETE from RDFSTORE1_DPH WHERE GRAPH='TEST' !
  
   --6.2 write primary  table, hashing long strings
   --first pass.. insert everything with map1.....
   
-------------------------------------------------------------------------------------
---- HEre is the real procedure 
-------------------------------------------------------------------------------------
DROP  TABLE SESSION.SP_SPILLS
!
CREATE OR REPLACE PROCEDURE LOAD_TRIPLES_FROM_THIN_TABLE (  OUT nbrecs INTEGER )
  RESULT SETS 0 LANGUAGE SQL
BEGIN 
   DECLARE vfound integer default null;
   DECLARE vprop0 VARCHAR(200) DEFAULT null;
   DECLARE vprop1 VARCHAR(200) DEFAULT null;
   DECLARE vprop2 VARCHAR(200) DEFAULT null;
   DECLARE vdtyp  SMALLINT     DEFAULT null;
   SET nbrecs=0;
   --5. assign ids for all types and locales used in dataset
   --PDA:  not sure this is the right alogorithm here to get sall datatypes first. 
   --anyway,  insert them first into the RDFSTORE1_DT type table. 
   INSERT INTO RDFSTORE1_DT
      SELECT row_number() over() + mx, subject
       FROM (
       SELECT distinct subject 
         FROM ThinTable 
        WHERE subject not in (SELECT distinct datatype_name from RDFSTORE1_DT) and subject like 'http://www.w3.org/%#%' or subject like 'rdf:%'
       UNION 
       SELECT distinct predicate 
         FROM ThinTable
        WHERE predicate not in (SELECT distinct datatype_name from RDFSTORE1_DT) and predicate like 'http://www.w3.org/%#%' or predicate like 'rdf:%'
       UNION 
       SELECT distinct object 
         FROM ThinTable
        WHERE object not in (SELECT distinct datatype_name from RDFSTORE1_DT) and object like 'http://www.w3.org/2001/%#%' or object like 'rdf:%'
        ) T, (select max(DATATYPE_ID) mx FROM RDFSTORE1_DT) ;
   --Calculate the subjects that need to spills first and put them into a session table to be reused later.
   DECLARE GLOBAL TEMPORARY TABLE SESSION.SP_SPILLS ( subject_having_to_spill VARCHAR(255), spills integer default 0)ON COMMIT PRESERVE ROWS;
    INSERT INTO SESSION.SP_SPILLS    
      SELECT subject subject_having_to_spill,count( distinct predicate) spills
        FROM ThinTable 
       GROUP BY subject having count(distinct predicate) >v_NBCOLS; 
        
   FOR v1  AS ( SELECT T.subject,T.object,T.predicate,p.map1,p.map2,COALESCE(DT.DATATYPE_ID, V_DEFAULT_TYPE) DATATYPE_ID,1 subject_having_to_spill
                 FROM predicate_mapping P,ThinTable T
                 LEFT OUTER JOIN  SESSION.SP_SPILLS   ON (T.subject=subject_having_to_spill)
                 LEFT OUTER JOIN  RDFSTORE1_DT DT  ON (T.predicate = DT.datatype_name )
                 --LEFT OUTER JOIN  RDFSTORE1_DPH DH ON (T.OBJECT = DH.ENTRY AND GID=V_GRAPH)
                WHERE T.predicate=P.predicate   ) 
    DO
       CALL DBMS_OUTPUT.PUT_LINE( 'inserting...'||COALESCE(v1.subject,'<empty subject>')||':'||COALESCE(v1.object,'<empty object>')||':'||COALESCE(v1.predicate,'<empty predicate>')||':'||TO_CHAR(v1.map1)||':'||TO_CHAR(v1.map2)||':'||TO_CHAR(v1.DATATYPE_ID )||':'||TO_CHAR(v1.subject_having_to_spill));
       ---DEAL with DPH first....
       SELECT 1,prop0,prop1,prop2 INTO vfound,vprop0,vprop1,vprop2 
         FROM RDFSTORE1_DPH 
        WHERE ENTRY = V1.SUBJECT AND GID=V_GRAPH;
       
       IF ( vfound is null OR (vfound=1 AND vprop0 is not null and vprop1 is not null  and vprop2 is not null )) THEN 
            INSERT  INTO RDFSTORE1_DPH(ENTRY,GID,SPILL,PROP0,VAL0,TYP0,PROP1,VAL1,TYP1,PROP2,VAL2,TYP2)
               VALUES ( v1.subject, V_GRAPH , COALESCE(subject_having_to_spill, 0) ,
                      (CASE WHEN V1.map1=0 THEN V1.predicate ELSE NULL END) ,
                      (CASE WHEN V1.map1=0 THEN V1.object    ELSE NULL END), 
                      (CASE WHEN V1.map1=0 THEN V1.DATATYPE_ID ELSE NULL  END),
                      (CASE WHEN V1.map1=1 THEN V1.predicate ELSE NULL END),
                      (CASE WHEN V1.map1=1 THEN V1.object    ELSE NULL END), 
                      (CASE WHEN V1.map1=1 THEN V1.DATATYPE_ID ELSE NULL END),
                      (CASE WHEN V1.map1=2 THEN V1.predicate ELSE NULL END),
                      (CASE WHEN V1.map1=2 THEN V1.object    ELSE NULL END), 
                      (CASE WHEN V1.map1=2 THEN COALESCE(V1.DATATYPE_ID,V_DEFAULT_TYPE) ELSE NULL END) );
             --CALL DBMS_OUTPUT.PUT_LINE( 'insert  record '|| nbrecs );
        ELSEIF ( vprop0 is null AND V1.map1=0) THEN
            UPDATE RDFSTORE1_DPH SET (prop0,val0,typ0) = (v1.predicate, v1.object,v1.DATATYPE_ID) WHERE  ENTRY=V1.subject and GID=V_GRAPH;
           -- CALL DBMS_OUTPUT.PUT_LINE( 'updated  record '|| nbrecs );
        ELSEIF ( vprop1 is null AND V1.map1=1) THEN
            UPDATE RDFSTORE1_DPH SET (prop1,val1,typ1) = (v1.predicate, v1.object,v1.DATATYPE_ID) WHERE  ENTRY=V1.subject and GID=V_GRAPH;
            --CALL DBMS_OUTPUT.PUT_LINE( 'updated  record '|| nbrecs );
        ELSEIF ( vprop2 is null AND V1.map1=2) THEN
            UPDATE RDFSTORE1_DPH SET (prop2,val2,typ2) = (v1.predicate, v1.object,v1.DATATYPE_ID) WHERE  ENTRY=V1.subject and GID=V_GRAPH;
            --CALL DBMS_OUTPUT.PUT_LINE( 'updated  record '|| nbrecs );
         ELSE
            IF ( vprop0 is null AND V1.map2=0) THEN
                 UPDATE RDFSTORE1_DPH SET (prop0,val0,typ0) = (v1.predicate, v1.object,v1.DATATYPE_ID) WHERE  ENTRY=V1.subject and GID=V_GRAPH;
                -- CALL DBMS_OUTPUT.PUT_LINE( 'updated  record '|| nbrecs );
             ELSEIF ( vprop1 is null AND V1.map2=1) THEN
                 UPDATE RDFSTORE1_DPH SET (prop1,val1,typ1) = (v1.predicate, v1.object,v1.DATATYPE_ID) WHERE  ENTRY=V1.subject and GID=V_GRAPH;
                -- CALL DBMS_OUTPUT.PUT_LINE( 'updated  record '|| nbrecs );
             ELSEIF ( vprop2 is null AND V1.map2=2) THEN
                 UPDATE RDFSTORE1_DPH SET (prop2,val2,typ2) = (v1.predicate, v1.object,v1.DATATYPE_ID) WHERE  ENTRY=V1.subject and GID=V_GRAPH;
                -- CALL DBMS_OUTPUT.PUT_LINE( 'updated  record '|| nbrecs );
             ELSE
               CALL DBMS_OUTPUT.PUT_LINE( 'skipped record '|| to_CHAR(nbrecs)|| ' '||v1.subject ||' '||v1.predicate||' '||v1.object||' map1='||to_CHAR(V1.map1)|| ' map2='||to_CHAR(v1.map2));
             END IF ;
        END IF;
       ---DEAL with RPH now....
        SELECT 1,prop0,prop1,prop2, DATATYPE_ID INTO vfound,vprop0,vprop1,vprop2,vdtyp 
          FROM RDFSTORE1_RPH 
          LEFT OUTER JOIN  RDFSTORE1_DT DT  ON (predicate = DT.datatype_name )
         WHERE ENTRY = V1.PREDICATE AND GID=V_GRAPH ; --BRANCH OFFICE J2
         
        IF ( vfound is null OR (vfound=1 AND vprop0 is not null and vprop1 is not null  and vprop2 is not null )) THEN 
            INSERT  INTO RDFSTORE1_RPH(ENTRY,NUMENTRY,DTENTRY,TYP,GID,SPILL,PROP0,VAL0,PROP1,VAL1,PROP2,VAL2)
               VALUES ( v1.predicate, 0, null, 716, V_GRAPH , 
                      COALESCE(vfound, 0), -- spill,
                      (CASE WHEN V1.map1=0 THEN V1.predicate ELSE NULL END),-- prop0,
                      (CASE WHEN V1.map1=0 THEN V1.subject    ELSE NULL END),-- val0, 
                      (CASE WHEN V1.map1=1 THEN V1.predicate ELSE NULL END),-- prop1,
                      (CASE WHEN V1.map1=1 THEN V1.subject    ELSE NULL END),-- val1, 
                      (CASE WHEN V1.map1=2 THEN V1.predicate ELSE NULL END),-- prop2,
                      (CASE WHEN V1.map1=2 THEN V1.subject    ELSE NULL END) --val2 
                      );
             --CALL DBMS_OUTPUT.PUT_LINE( 'insert  record '|| nbrecs );
        END IF;
        SET nbrecs=nbrecs+1;    
    END FOR;
    COMMIT;    
    --CALL DBMS_OUTPUT.PUT_LINE( 'nb records...'|| nbrecs );
    DROP  TABLE SESSION.SP_SPILLS;
END
!




SET SERVEROUTPUT ON!
CALL DBMS_OUTPUT.ENABLE(1000000)!
 
BEGIN
  DECLARE vT1            BIGINT;  
  DECLARE vDiff          DECIMAL(5,2);  
  DECLARE vnbrows        INTEGER;
  DELETE from RDFSTORE1_DPH WHERE GRAPH='TEST';
  COMMIT;
  SET vT1 = DBMS_UTILITY.GET_TIME;
  CALL LOAD_TRIPLES_FROM_THIN_TABLE(  vnbrows );
  SET vDiff = (1.0*(DBMS_UTILITY.GET_TIME - vT1)/100.0)+0.001;
  CALL DBMS_OUTPUT.PUT_LINE( 'Loaded '|| TO_CHAR(vnbrows) || ' triples in '||TO_CHAR(vdiff,'99999.99')||' seconds '|| TO_CHAR( DECIMAL(vnbrows/vDiff,6,2))||' triples/msec');
  SELECT count(*) INTO vnbrows FROM RDFSTORE1_DPH;
  CALL DBMS_OUTPUT.PUT_LINE( 'Resulting number of rows loaded into  RDFSTORE1_DPH = '||TO_CHAR(vnbrows,'99999'));
END
!

 
        
        
        
        
        
quit!
        
        
        
