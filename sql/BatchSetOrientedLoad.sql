-- PDA: delimiter is  ! :  launch this script with  db2 -vtd! -f BatchSetOrientedLoad.sql
--these steps are done separately for direct and reverse tables
--1. sort file and split it into N sorted chunks, keeping each subject together
--   (currently done with Unix sort --parallel to sort, and a gawk script to split the output)
--2. for each chunk
--   2.1  compute edge sets for each subject
--   	This step generates a table with lines consisting of every distinct set of predicates indicent on any subject.  Each line is associated with a count of how many subjects have that specific edge set.
--   2.2  record which predicates are multi valued
--   	This is simply a list of all predicates that have any subject with more than one triple with that predicate.
--3. combine edge set chunks
 --	This step combines the edge sets generated in 2.1 by summing the counts across all N chunks.
--4. compute column assignments for predicates using edge sets
--5. assign ids for all types and locales used in dataset
--6. for each chunk
--  6.1  compute layout of triples in rows, using column assignments
--  6.2 write primary and secondary table, hashing long strings
--  6.3 record predicate information
--PDA:  some management
CONNECT TO TestLoad!

DROP TABLE ThinTable!
DROP TABLE predicate_mapping!

TRUNCATE TABLE RDFSTORE1_DT IMMEDIATE!
TRUNCATE TABLE RDFSTORE1_DPH IMMEDIATE!
TRUNCATE TABLE RDFSTORE1_RPH IMMEDIATE!
TRUNCATE TABLE RDFSTORE1_LSTR IMMEDIATE!
TRUNCATE TABLE RDFSTORE1_RS IMMEDIATE!
TRUNCATE TABLE RDFSTORE1_DS IMMEDIATE!

commit!

-- these steps are done separately for direct and reverse tables
-- PDA: Assuming a thin table with 3 columns
create table ThinTable(subject varchar(118) not null, predicate varchar(118) not null , object varchar(118) not null)
!
create index ThinTableidx on ThinTable(subject)!
create index ThinTableidx1 on ThinTable(predicate)!
create index ThinTableidx2 on ThinTable(object)!

insert into ThinTable VALUES 
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


select substr(subject,1,20) subject ,substr(predicate,1,20) predicate,substr(object,1,20) object from thintable
!
-------------------------------------------------------------
-----KAVITHA Assume this step is unnecessary because predicates are mapped Automatic mapping of predicates 
-------------------------------------------------------------
--DELETE PREDICATE_MAPPING !
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
 
 SELECT substr(predicate,1,20) predicate,map1,map2 FROM PREDICATE_MAPPING!
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
  SELECT substr(subject,1,20) subject,map1,map2 FROM SUBJECT_MAPPING!

 ------------------------------------------------------------------
 ------------  FINAL STATS  ---------------------------------------
 ------------------------------------------------------------------
select count(*), 'nb_rows_in_thin_table'  From thintable
union
select count(*), 'nb_rows_in_predicate_mapping'  From predicate_mapping        
union
select count(*), 'nb_rows_in_subject_mapping'  From subject_mapping!        
 
 --------------------------------------------------------------------------
--------------  PRIME RDF DATATYPES ....  --------------------------------
--------------------------------------------------------------------------
INSERT INTO RDFSTORE1_DT ( DATATYPE_ID, DATATYPE_NAME )
    VALUES (700,''),
           (701,'http://www.w3.org/2001/XMLSchema#boolean'),
            (702,'http://www.w3.org/2001/XMLSchema#dateTime'),
            (703,'http://www.w3.org/2001/XMLSchema#positiveInteger'),
            (704,'http://www.w3.org/2001/XMLSchema#nonPositiveInteger'),
            (705,'http://www.w3.org/2001/XMLSchema#negativeInteger'),
            (706,'http://www.w3.org/2001/XMLSchema#long'),
            (707,'http://www.w3.org/2001/XMLSchema#int'),
            (708,'http://www.w3.org/2001/XMLSchema#short'),
            (709,'http://www.w3.org/2001/XMLSchema#byte'),
            (710,'http://www.w3.org/2001/XMLSchema#nonNegativeInteger'),
            (711,'http://www.w3.org/2001/XMLSchema#unsignedLong'),
            (712,'http://www.w3.org/2001/XMLSchema#unsignedInt'),
            (713,'http://www.w3.org/2001/XMLSchema#unsignedShort'),
            (714,'http://www.w3.org/2001/XMLSchema#unsignedByte'),
            (715,'http://www.w3.org/2001/XMLSchema#integer'),
            (716,'http://www.w3.org/2001/XMLSchema#decimal'),
            (717,'http://www.w3.org/2001/XMLSchema#float'),
            (718,'http://www.w3.org/2001/XMLSchema#double'),
            (719,'http://www.w3.org/2001/XMLSchema#string'),
            (10100,'BG'),
            (10100,'EN'),
            (10101,'BG-BG'),
            (10101,'EN-US'),
            (10102,'EN-AU'),
            (10103,'EN-GB'),
            (10200,'CS'),
            (10200,'DE'),
            (10201,'CS-CZ'),
            (10201,'DE-DE'),
            (10300,'DA'),
            (10300,'FR'),
            (10301,'DA-DK'),
            (10301,'FR-FR'),
            (10302,'FR-CA'),
            (10400,'ZZ'),
            (10500,'EL'),
            (10500,'XX'),
            (10501,'EL-GR'),
            (10501,'XX-EN'),
            (10700,'ES'),
            (10701,'ES-ES'),
            (10800,'FI'),
            (10801,'FI-FI'),
            (11000,'HR'),
            (11001,'HR-HR'),
            (11100,'HU'),
            (11101,'HU-HU'),
            (11200,'IT'),
            (11201,'IT-IT'),
            (11300,'JA'),
            (11301,'JA-JP'),
            (11400,'KO'),
            (11401,'KO-KR'),
            (11500,'NL'),
            (11501,'NL-NL'),
            (11600,'NO'),
            (11601,'NO-NO'),
            (11700,'PL'),
            (11701,'PL-PL'),
            (11800,'PT'),
            (11801,'PT-PT'),
            (11802,'PT-BR'),
            (11900,'RO'),
            (11901,'RO-RO'),
            (12000,'RU'),
            (12001,'RU-RU'),
            (12100,'SK'),
            (12101,'SK-SK'),
            (12200,'SL'),
            (12201,'SL-SL'),
            (12300,'SV'),
            (12301,'SV-SE'),
            (12400,'TR'),
            (12401,'TR-TR'),
            (12500,'ZH'),
            (12501,'ZH-CN'),
            (12502,'ZH-TW')
 !




------------------------------------------------------------------
------------  MULTIVALUE PREDICATES  ---------------------------
------------------------------------------------------------------
 DROP TABLE MULTIVALUE_PREDICATES!
--2: CREATE TABLE MULTIVALUE_PREDICATES(LIDID INT NOT NULL GENERATED ALWAYS AS IDENTITY, GID VARCHAR(20) NOT NULL, SUBJECT VARCHAR(118) NOT NULL, PREDICATE VARCHAR(118) NOT NULL, CNT INTEGER)!
--2: ALTER TABLE  MULTIVALUE_PREDICATES ADD CONSTRAINT  PKMVP PRIMARY KEY( GID, SUBJECT,PREDICATE ) !
--2: CREATE UNIQUE INDEX MVP_LID ON MULTIVALUE_PREDICATES(lidid)!
--2: ALTER TABLE MULTIVALUE_PREDICATES ALTER LIDID RESTART WITH 1!

CREATE OR REPLACE VIEW MULTIVALUE_PREDICATES
 AS 
  ---- this is to remember which subject is allocate which  MVP
 --2: INSERT INTO  MULTIVALUE_PREDICATES(GID, SUBJECT,PREDICATE, CNT )
   SELECT ROW_NUMBER() OVER () LIDID, V_GRAPH GID,SUBJECT,PREDICATE,COUNT( PREDICATE) CNT
     FROM THINTABLE 
    GROUP BY SUBJECT,PREDICATE HAVING COUNT(PREDICATE) >1
!

  ---NOW store this into the RDFSTORE1_DS -----
 INSERT INTO RDFSTORE1_DS (GID, LIST_ID, ELEM, TYP) 
   SELECT V_GRAPH,'lid:'||CHAR(lidid), OBJECT, 20 as TYP 
     FROM MULTIVALUE_PREDICATES MVP, THINTABLE TT
    WHERE MVP.SUBJECT=TT.SUBJECT AND MVP.PREDICATE=TT.PREDICATE
!

---just display  what was inserted into MULTIVALUE_PREDICATES ----
SELECT LIDID,GID,substr(subject,1,20) subject,substr(predicate,1,20) predicate,cnt FROM MULTIVALUE_PREDICATES ORDER BY LIDID
!

------------------------------------------------------------------
------------  SUBJECT having to spills ---------------------------
------------------------------------------------------------------
DROP TABLE SUBJECT_TO_SPILL!
--2: CREATE TABLE SUBJECT_TO_SPILL(SUBJECT VARCHAR(118) NOT NULL PRIMARY KEY, NB_DISTINCT_PREDICATES INT NOT NULL, NEED_TO_SPILL_DUE_TO_MAPPING INT  NOT NULL)!
--2: CREATE INDEX SS_CNT ON SUBJECT_TO_SPILL(SUBJECT,NB_DISTINCT_PREDICATES,NEED_TO_SPILL_DUE_TO_MAPPING)!

--INSERT INTO  SUBJECT_TO_SPILL 
--    SELECT SUBJECT ,COUNT( DISTINCT PREDICATE)
--      FROM THINTABLE 
--     GROUP BY SUBJECT HAVING COUNT( DISTINCT PREDICATE ) > V_NBCOLS ! 

--2: INSERT INTO  SUBJECT_TO_SPILL 

CREATE OR REPLACE VIEW SUBJECT_TO_SPILL
 AS
 SELECT T.subject,--V_NBCOLS max_columns_possible, 
        -- CASE WHEN (nb_distinct_predicates > V_NBCOLS) OR
        --           ( map1_col0 >= V_NBCOLS OR map1_col1 >= V_NBCOLS OR map1_col2 >= V_NBCOLS) AND 
        --            (map2_col0 >= V_NBCOLS OR map2_col1 >= V_NBCOLS OR map2_col2 >= V_NBCOLS) THEN 1 else 0 END need_to_spill,
         nb_distinct_predicates,
          CASE WHEN ( map1_col0 >= V_NBCOLS OR map1_col1 >= V_NBCOLS OR map1_col2 >= V_NBCOLS) AND 
                    (map2_col0 >= V_NBCOLS OR map2_col1 >= V_NBCOLS OR map2_col2 >= V_NBCOLS) THEN 1 else 0 END need_to_spill_due_to_mapping         
    FROM (
   SELECT T.SUBJECT ,
          COUNT( DISTINCT COALESCE('lid:'||CHAR(MVP.LIDID),T.predicate)) nb_distinct_predicates,
          count(distinct case when P.map1=0 then COALESCE('lid:'||CHAR(MVP.LIDID),T.predicate) else null end) as map1_col0, 
          count(distinct case when P.map1=1 then COALESCE('lid:'||CHAR(MVP.LIDID),T.predicate) else null end) as map1_col1, 
          count(distinct case when P.map1=2 then COALESCE('lid:'||CHAR(MVP.LIDID),T.predicate) else null end) as map1_col2,
          count(distinct case when P.map2=0 then COALESCE('lid:'||CHAR(MVP.LIDID),T.predicate) else null end) as map2_col0, 
          count(distinct case when P.map2=1 then COALESCE('lid:'||CHAR(MVP.LIDID),T.predicate) else null end) as map2_col1, 
          count(distinct case when P.map2=2 then COALESCE('lid:'||CHAR(MVP.LIDID),T.predicate) else null end) as map2_col2

     FROM PREDICATE_MAPPING P,
          THINTABLE T  LEFT OUTER JOIN  MULTIVALUE_PREDICATES MVP ON (T.subject= MVP.subject ANd MVP.PREDICATE = T.PREDICATE) 
    WHERE T.predicate = P.predicate
     GROUP BY T.SUBJECT 
    ) T 
 WHERE (CASE WHEN (nb_distinct_predicates > V_NBCOLS) OR
                   ( map1_col0 >= V_NBCOLS OR map1_col1 >= V_NBCOLS OR map1_col2 >= V_NBCOLS) AND 
                    (map2_col0 >= V_NBCOLS OR map2_col1 >= V_NBCOLS OR map2_col2 >= V_NBCOLS) THEN 1 else 0 END) = 1
!

SELECT substr(subject,1,20) subject,NB_DISTINCT_PREDICATES,NEED_TO_SPILL_DUE_TO_MAPPING FROM SUBJECT_TO_SPILL
!


-----------------------------------------------------------------------------------
-----------  INSERT All subjects with PREDICATE on first  Mapping  value ----------
-----------------------------------------------------------------------------------
CREATE OR REPLACE VIEW ALL_MAPPING AS 
 SELECT rn,entry,GID,spill,prop0,val0,typ0,prop1,val1,typ1,prop2,val2,typ2
  FROM ( 
      SELECT  row_number() over(partition by T.subject ) rn, T.subject as entry,V_GRAPH as GID, CASE WHEN SS.subject IS NOT NULL THEN 1 ELSE 0 END spill, 
                 CASE WHEN P.MAP1=0 THEN T.predicate ELSE NULL END as prop0,
                 CASE WHEN P.MAP1=0 THEN COALESCE('lid:'||CHAR(MVP.LIDID),T.object)    ELSE NULL END as val0,
                 CASE WHEN P.MAP1=0 THEN COALESCE(DT.DATATYPE_ID, V_DEFAULT_TYPE) ELSE NULL END as  typ0,
                 CASE WHEN P.MAP1=1 THEN T.predicate ELSE NULL END as prop1,
                 CASE WHEN P.MAP1=1 THEN COALESCE('lid:'||CHAR(MVP.LIDID),T.object)    ELSE NULL END as val1,
                 CASE WHEN P.MAP1=1 THEN COALESCE(DT.DATATYPE_ID, V_DEFAULT_TYPE) ELSE NULL END as  typ1,
                 CASE WHEN P.MAP1=2 THEN T.predicate ELSE NULL END as prop2,
                 CASE WHEN P.MAP1=2 THEN COALESCE('lid:'||CHAR(MVP.LIDID),T.object)    ELSE NULL END as val2,
                 CASE WHEN P.MAP1=2 THEN COALESCE(DT.DATATYPE_ID, V_DEFAULT_TYPE) ELSE NULL END as  typ2,
                 MVP.LIDID
       FROM predicate_mapping P,ThinTable T
        LEFT OUTER JOIN  SUBJECT_TO_SPILL SS       ON (SS.subject=T.subject)
        LEFT OUTER JOIN  RDFSTORE1_DT DT           ON (T.predicate = DT.datatype_name )
        LEFT OUTER JOIN  MULTIVALUE_PREDICATES MVP ON (T.subject= MVP.subject ANd MVP.PREDICATE = T.PREDICATE) 
       WHERE P.predicate=T.predicate
   ) T
!


 
  TRUNCATE TABLE RDFSTORE1_DPH
  !
  --Try to do as much as possible in first step. therefore only select the first 3 records per subject,  and coalesce them into one record as we 
  -- know that there will be only one value per column (propx,valx,typx) rest being NULL 
INSERT INTO RDFSTORE1_DPH(ENTRY, GID,SPILL, PROP0, VAL0, TYP0,PROP1, VAL1, TYP1,PROP2, VAL2, TYP2)
      SELECT
        ENTRY, GID,SPILL,
        MIN(PROP0) PROP0, MIN(VAL0) VAL0, MIN(TYP0) TYP0,
        MIN(PROP1) PROP1, MIN(VAL1) VAL1, MIN(TYP1) TYP1,
        MIN(PROP2) PROP2, MIN(VAL2) VAL2, MIN(TYP2) TYP2
    FROM ALL_MAPPING
    where RN < 4 
    GROUP BY  ENTRY, GID,SPILL
union all
    --INSERT INTO RDFSTORE1_DPH(ENTRY, GID,SPILL, PROP0, VAL0, TYP0,PROP1, VAL1, TYP1,PROP2, VAL2, TYP2)
     SELECT entry,A.GID,A.spill,
           MIN(PROP0) PROP0, MIN(VAL0) VAL0, MIN(TYP0) TYP0,
           MIN(PROP1) PROP1, MIN(VAL1) VAL1, MIN(TYP1) TYP1,
           MIN(PROP2) PROP2, MIN(VAL2) VAL2, MIN(TYP2) TYP2
       FROM ALL_MAPPING A
      WHERE rn between 4 and 7 and spill=1    
      GROUP BY  ENTRY, GID,SPILL
   union all
    --INSERT INTO RDFSTORE1_DPH(ENTRY, GID,SPILL, PROP0, VAL0, TYP0,PROP1, VAL1, TYP1,PROP2, VAL2, TYP2)
     SELECT entry,A.GID,A.spill,
           MIN(PROP0) PROP0, MIN(VAL0) VAL0, MIN(TYP0) TYP0,
           MIN(PROP1) PROP1, MIN(VAL1) VAL1, MIN(TYP1) TYP1,
           MIN(PROP2) PROP2, MIN(VAL2) VAL2, MIN(TYP2) TYP2
       FROM ALL_MAPPING A
      WHERE rn between 8 and 10 and spill=1    
      GROUP BY  ENTRY, GID,SPILL
 --and more like these as max(rn) for SPILLDE rows are >  7...
!
----now list DPH  and DS 
!
SELECT VARCHAR(ENTRY,20) ENTRY, VARCHAR(GID,20) GID,  SPILL, 
     VARCHAR(PROP0,20) PROP0,  VARCHAR(VAL0,20) VAL0, TYP0 TYP0,
     VARCHAR(PROP1,20) PROP1,  VARCHAR(VAL1,20) VAL1, TYP1 TYP1,
     VARCHAR(PROP2,20) PROP2,  VARCHAR(VAL2,20) VAL2, TYP2 TYP2
FROM RDFSTORE1_DPH
!

select substr(gid,1,20) GID,LIST_ID,substr(elem,1,20) elem, typ from RDFSTORE1_DS 
!

SELECT substr(subject,1,20) subject,NB_DISTINCT_PREDICATES,NEED_TO_SPILL_DUE_TO_MAPPING FROM SUBJECT_TO_SPILL
!

--terminate quit for now ....
quit
!

insert... 
  FROM ( 
           SELECT  row_number() over(partition by T.subject ) rn, T.subject as entry,V_GRAPH as GID, CASE WHEN SS.subject IS NOT NULL THEN 1 ELSE 0 END spill, 
                 CASE WHEN P.MAP2=0 THEN T.predicate ELSE NULL END as prop0,
                 CASE WHEN P.MAP2=0 THEN COALESCE('lid:'||CHAR(MVP.LIDID),T.object)    ELSE NULL END as val0,
                 CASE WHEN P.MAP2=0 THEN COALESCE(DT.DATATYPE_ID, V_DEFAULT_TYPE) ELSE NULL END as  typ0,
                 CASE WHEN P.MAP2=1 THEN T.predicate ELSE NULL END as prop1,
                 CASE WHEN P.MAP2=1 THEN COALESCE('lid:'||CHAR(MVP.LIDID),T.object)    ELSE NULL END as val1,
                 CASE WHEN P.MAP2=1 THEN COALESCE(DT.DATATYPE_ID, V_DEFAULT_TYPE) ELSE NULL END as  typ1,
                 CASE WHEN P.MAP2=2 THEN T.predicate ELSE NULL END as prop2,
                 CASE WHEN P.MAP2=2 THEN COALESCE('lid:'||CHAR(MVP.LIDID),T.object)    ELSE NULL END as val2,
                 CASE WHEN P.MAP2=2 THEN COALESCE(DT.DATATYPE_ID, V_DEFAULT_TYPE) ELSE NULL END as  typ2
                --, MVP.LIDID, T.object,T.predicate --, DPH.prop0,dph.prop1,dph.prop2
       FROM predicate_mapping P,ThinTable T
        LEFT OUTER JOIN  SUBJECT_TO_SPILL SS       ON (SS.subject=T.subject)
        LEFT OUTER JOIN  RDFSTORE1_DT DT           ON (T.predicate = DT.datatype_name )
        LEFT OUTER JOIN  MULTIVALUE_PREDICATES MVP ON (T.subject= MVP.subject ANd MVP.PREDICATE = T.PREDICATE)
       WHERE P.predicate=T.predicate 
       AND (T.subject,T.predicate,COALESCE('lid:'||CHAR(MVP.LIDID),T.object))
            not in (           select entry,prop0,val0 FROM RDFSTORE1_DPH DPH where entry=T.subject and T.predicate=prop0 
                     union all select entry,prop1,val1 FROM RDFSTORE1_DPH DPH where entry=T.subject and T.predicate=prop1 
                     union all select entry,prop2,val2 FROM RDFSTORE1_DPH DPH where entry=T.subject and T.predicate=prop2 ) 
      --  and  NOT EXISTS ( SELECT 1 FROM RDFSTORE1_DPH DPH   WHERE DPH.entry= T.subject and DPH.GID=V_GRAPH and ( T.predicate =DPH.prop0 OR T.predicate =DPH.prop1 OR T.predicate =DPH.prop2) )
   ) T 
!



             
-- MERGE INTO RDFSTORE1_DPH AS DPH
--  USING (SELECT entry,A.GID,A.spill,A.prop0,A.val0,A.typ0,A.prop1,A.val1,A.typ1,A.prop2,A.val2,A.typ2
--           FROM ALL_MAPPING A
--          WHERE rn>= 4  ) AC
--  ON DPH.entry = AC.entry AND AC.GID=DPH.GID 
--  WHEN MATCHED and DPH.prop0 is null and ac.val0 is not null THEN UPDATE SET (prop0,val0,typ0) = (ac.prop0, ac.val0,ac.typ0)
--  WHEN MATCHED and DPH.prop1 is null and ac.val1 is not null THEN UPDATE SET (prop1,val1,typ1) = (ac.prop1, ac.val1,ac.typ1)
--  WHEN MATCHED and DPH.prop2 is null and ac.val2 is not null THEN UPDATE SET (prop2,val2,typ2) = (ac.prop2, ac.val2,ac.typ2)
--  WHEN NOT MATCHED THEN
--     INSERT  (ENTRY,GID,SPILL,PROP0,VAL0,TYP0,PROP1,VAL1,TYP1,PROP2,VAL2,TYP2)
--        VALUES ( AC.entry  , V_GRAPH , spill ,
--               (CASE WHEN AC.prop0 IS NOT NULL THEN AC.prop0 ELSE NULL END) ,
--               (CASE WHEN AC.prop0 IS NOT NULL THEN AC.val0  ELSE NULL END), 
--               (CASE WHEN AC.prop0 IS NOT NULL THEN AC.typ0  ELSE NULL  END),
               
--               (CASE WHEN AC.prop1 IS NOT NULL THEN AC.prop1 ELSE NULL END),
--               (CASE WHEN AC.prop1 IS NOT NULL THEN AC.val1 ELSE NULL END), 
--               (CASE WHEN AC.prop1 IS NOT NULL THEN AC.typ1 ELSE NULL END),
               
--               (CASE WHEN AC.prop2 IS NOT NULL THEN AC.prop2 ELSE NULL END),
--               (CASE WHEN AC.prop2 IS NOT NULL THEN AC.val2  ELSE NULL END), 
--               (CASE WHEN AC.prop2 IS NOT NULL THEN AC.typ2  ELSE NULL END)
--               )  
-- !




-----------------------------------------------------------------------------------
-----------  NOW INSERT ALL PREDICATE with Mapping = 1   ----------------------------------------
-----------------------------------------------------------------------------------
--INSERT  INTO RDFSTORE1_DPH(ENTRY,GID,SPILL,PROP1,VAL1,TYP1 )
--  SELECT  T.subject as entry,V_GRAPH as GID, CASE WHEN SS.subject IS NOT NULL THEN 1 ELSE 0 END spill, T.predicate prop1,T.object val1,COALESCE(DT.DATATYPE_ID, V_DEFAULT_TYPE) typ1 
--                 FROM predicate_mapping P,ThinTable T
--                LEFT OUTER JOIN  SUBJECT_TO_SPILL SS  ON (SS.subject=T.subject)
--                LEFT OUTER JOIN  RDFSTORE1_DT DT  ON (T.predicate = DT.datatype_name )
--         Where P.map1=1 and P.predicate=T.predicate
--!

--INSERT  INTO RDFSTORE1_RPH(ENTRY,NUMENTRY,DTENTRY,TYP,GID,SPILL,PROP0,VAL0,PROP1,VAL1,PROP2,VAL2)
--               VALUES ( v1.predicate, 0, null, 716, V_GRAPH , 
--                      COALESCE(vfound, 0), -- spill,
--                      (CASE WHEN V1.map1=0 THEN V1.predicate ELSE NULL END),-- prop0,
--                      (CASE WHEN V1.map1=0 THEN V1.subject    ELSE NULL END),-- val0, 
--                      (CASE WHEN V1.map1=1 THEN V1.predicate ELSE NULL END),-- prop1,
--                      (CASE WHEN V1.map1=1 THEN V1.subject    ELSE NULL END),-- val1, 
--                      (CASE WHEN V1.map1=2 THEN V1.predicate ELSE NULL END),-- prop2,
--                      (CASE WHEN V1.map1=2 THEN V1.subject    ELSE NULL END) --val2 
--                      )
--!
  

 
