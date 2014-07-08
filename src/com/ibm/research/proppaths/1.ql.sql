connect to uobm @
CREATE USER TEMPORARY TABLESPACE tmpspace MANAGED BY DATABASE USING (FILE '~/usertmpspace' 10000) PREFETCHSIZE 10M @
DECLARE GLOBAL TEMPORARY TABLE SESSION.OLDT(X VARCHAR(100)) ON COMMIT PRESERVE ROWS  NOT LOGGED IN tmpspace @
DECLARE GLOBAL TEMPORARY TABLE SESSION.T(X VARCHAR(100)) ON COMMIT PRESERVE ROWS  NOT LOGGED IN tmpspace @
DECLARE GLOBAL TEMPORARY TABLE SESSION.DELTAT(X VARCHAR(100)) ON COMMIT PRESERVE ROWS  NOT LOGGED IN tmpspace @

CREATE OR REPLACE FUNCTION LeftR(property  VarChar(100), startingPoint VarChar(100))  RETURNS TABLE (result VarChar(100) )   LANGUAGE SQL  MODIFIES SQL DATA
BEGIN ATOMIC
   
   DELETE FROM session.oldt;
   DELETE FROM session.t;
   DELETE FROM session.deltat;
   
   RETURN select entity as result from db2inst2.uobm30_RS fetch first 3 rows only;                  
END @

select * from  TABLE( LeftR('http://semantics.crl.ibm.com/univ-bench-dl.owl#subOrganizationOf', 'http://www.University0.edu' )) @
 
drop FUNCTION LeftR @
disconnect uobm @



   -- DECLARE GLOBAL TEMPORARY TABLE SESSION.OLDT(X VARCHAR(100)) ON COMMIT PRESERVE ROWS  NOT LOGGED IN tmpspace @
   DECLARE GLOBAL TEMPORARY TABLE SESSION.T(X VARCHAR(100)) ON COMMIT PRESERVE ROWS  NOT LOGGED IN tmpspace @
   DECLARE GLOBAL TEMPORARY TABLE SESSION.DELTAT(X VARCHAR(100)) ON COMMIT PRESERVE ROWS  NOT LOGGED IN tmpspace @
   DECLARE GLOBAL TEMPORARY TABLE SESSION.OLDDELTAT(X VARCHAR(100)) ON COMMIT PRESERVE ROWS  NOT LOGGED IN tmpspace @
   
   DECLARE GLOBAL TEMPORARY TABLE SESSION.SUP(X VARCHAR(100), Y VARCHAR(100)) ON COMMIT PRESERVE ROWS  NOT LOGGED IN tmpspace @
   DECLARE GLOBAL TEMPORARY TABLE SESSION.DELTASUP(X VARCHAR(100),  Y VARCHAR(100) ) ON COMMIT PRESERVE ROWS  NOT LOGGED IN tmpspace @
   DECLARE GLOBAL TEMPORARY TABLE SESSION.OLDDELTASUP(X VARCHAR(100),  Y VARCHAR(100)) ON COMMIT PRESERVE ROWS  NOT LOGGED IN tmpspace @
   
   DECLARE GLOBAL TEMPORARY TABLE SESSION.ANC(X VARCHAR(100), Y VARCHAR(100)) ON COMMIT PRESERVE ROWS  NOT LOGGED IN tmpspace @
   DECLARE GLOBAL TEMPORARY TABLE SESSION.DELTAANC(X VARCHAR(100),  Y VARCHAR(100) ) ON COMMIT PRESERVE ROWS  NOT LOGGED IN tmpspace @
   DECLARE GLOBAL TEMPORARY TABLE SESSION.OLDDELTAANC(X VARCHAR(100),  Y VARCHAR(100)) ON COMMIT PRESERVE ROWS  NOT LOGGED IN tmpspace @
   
   DECLARE GLOBAL TEMPORARY TABLE SESSION.INPUTSUB(X VARCHAR(100)) ON COMMIT PRESERVE ROWS  NOT LOGGED IN tmpspace @
   DECLARE GLOBAL TEMPORARY TABLE SESSION.INPUTOBJ(X VARCHAR(100)) ON COMMIT PRESERVE ROWS  NOT LOGGED IN tmpspace @
  
  
   DECLARE GLOBAL TEMPORARY TABLE SESSION.M(X VARCHAR(100)) ON COMMIT PRESERVE ROWS  NOT LOGGED IN tmpspace @
   -- DECLARE GLOBAL TEMPORARY TABLE SESSION.DELTAM(X VARCHAR(100)) ON COMMIT PRESERVE ROWS  NOT LOGGED IN tmpspace @
   -- DECLARE GLOBAL TEMPORARY TABLE SESSION.OLDDELTAM(X VARCHAR(100)) ON COMMIT PRESERVE ROWS  NOT LOGGED IN tmpspace @
   
 CREATE OR REPLACE PROCEDURE notEmpty(IN c CURSOR,  OUT result BOOLEAN ) 
 LANGUAGE SQL
 -- The row type of the cursor must be ROW ( x INT) and the cursor must be open. 
 -- example c =  CURSOR FOR SELECT 1 FROM table;
 BEGIN
    DECLARE rowV INT;
 	FETCH c  INTO rowV;
   	IF (c IS FOUND) THEN
   		SET result =  true;
   	ELSE 
   		SET result = false;
 	END IF;
 END @

CREATE OR REPLACE PROCEDURE  LeftR(IN property  VarChar(100), IN startingPoint VarChar(100), IN maxLevel INTEGER,  OUT resultTable VARCHAR(20), OUT level INTEGER)  
-- negative maxLevel indicate need to recurse until the fix point
-- computes select ?X where { ?X property+ startingPoint } and stores the result in resultTable
LANGUAGE SQL
BEGIN 
   DECLARE delta_notEmpty BOOLEAN;
   DECLARE delta_cursor CURSOR;
   -- initialization 
   DELETE FROM session.t;
   DELETE FROM session.deltat;
   DELETE FROM session.olddeltat;
  
   INSERT INTO session.deltat
    -- generated sql [
    SELECT elem AS X  FROM db2inst2.uobm30_RS AS T
    WHERE  entity = startingPoint AND   prop = property;
    -- ] 
       -- NOTE: the above select query corresponds to the sql generated for the following SPARQL query:
       -- select ?X where { ?X <property> <startingPoint> }
     
   INSERT INTO session.olddeltat 
   select * from session.deltat;
   
   SET level = 0;
   -- end initialization 
 
   -- iteration to fix point
   SET  delta_cursor = CURSOR FOR SELECT 1 FROM session.deltat;
   OPEN delta_cursor;
   call notEmpty(delta_cursor, delta_notEmpty);
   CLOSE delta_cursor;
   
   WHILE (delta_notEmpty = true  AND (maxLevel < 0 OR  level < maxLevel) ) DO 
   		
   		-- olddeltat = copy(deltat)
   		DELETE FROM session.olddeltat;
   		INSERT INTO session.olddeltat 
   		select * from session.deltat;
   		-- 
   		
   		INSERT INTO session.t 
  		select * from session.deltat;
         
   		DELETE FROM session.deltat;
   			 -- generated sql [
   			INSERT INTO session.deltat
   			(SELECT elem FROM session.olddeltat AS delta, db2inst2.uobm30_RS AS T
   			WHERE entity = delta.X AND prop = property )
   			-- ]
   			 -- NOTE: the above select query corresponds to the sql generated for the following SPARQL query:
     		 -- select ?X where { ?X <property> delta.X }
   			EXCEPT 
   			(SELECT X FROM session.t );
   		 
   		SET  delta_cursor = CURSOR FOR SELECT 1 FROM session.deltat;
   		OPEN delta_cursor;
   		call notEmpty(delta_cursor, delta_notEmpty);
   		CLOSE delta_cursor;
   		SET level = level + 1;
   		
   END WHILE; 
   -- end iteration to fix point
  
   SET resultTable = 'session.t';
END @


CREATE OR REPLACE PROCEDURE  RightR(IN property  VarChar(100), IN startingPoint VarChar(100), IN maxLevel INTEGER,  OUT resultTable VARCHAR(20), OUT level INTEGER)  
-- negative maxLevel indicate need to recurse until the fix point
-- computes select ?X where { startingPoint property+ ?X } and stores the result in resultTable
LANGUAGE SQL
BEGIN 
   DECLARE delta_notEmpty BOOLEAN;
   DECLARE delta_cursor CURSOR;
   -- initialization 
   DELETE FROM session.t;
   DELETE FROM session.deltat;
   DELETE FROM session.olddeltat;
  
   INSERT INTO session.deltat
     -- generated sql [
  	 WITH  QS0 AS (SELECT T.val0 AS X
 	 FROM db2inst2.uobm30_DPH AS T
     WHERE  entry = startingPoint
     AND   (T.prop0 = property)
	 )
	SELECT  DISTINCT  X AS X FROM QS0;
	
	 -- ]
     -- NOTE: the above select query corresponds to the sql generated for the following SPARQL query:
     -- select ?X where { <startingPoint> <property> ?X}
     
 
   
   SET level = 0;
   -- end initialization 
 
   -- iteration to fix point
   SET  delta_cursor = CURSOR FOR SELECT 1 FROM session.deltat;
   OPEN delta_cursor;
   call notEmpty(delta_cursor, delta_notEmpty);
   CLOSE delta_cursor;
   
   WHILE (delta_notEmpty = true  AND (maxLevel < 0 OR  level < maxLevel) ) DO 
   
   		-- olddeltat = copy(deltat)
   		DELETE FROM session.olddeltat;
   		INSERT INTO session.olddeltat 
   		select * from session.deltat;
   		-- 
   		
   		INSERT INTO session.t 
  		select * from session.deltat;
         
   		DELETE FROM session.deltat;
   		INSERT INTO session.deltat
   		 -- generated sql [
  			WITH  QS0 AS (SELECT T.val0 AS X
 			FROM   db2inst2.uobm30_DPH AS T,  session.olddeltat AS delta
   			WHERE  entry = delta.X
     		AND   (T.prop0 = property)
			 )
			SELECT  DISTINCT  X AS X FROM QS0
		 -- ] VERY IMPORTANT NOTE: does not work when parentheses are put around the "WITH .. select" clause
    	 -- NOTE: the above select query corresponds to the sql generated for the following SPARQL query:
    	 -- select ?X where { delta.X  <property> ?X}
   			EXCEPT 
   			(SELECT X FROM session.t );
   		 
   		SET  delta_cursor = CURSOR FOR SELECT 1 FROM session.deltat;
   		OPEN delta_cursor;
   		call notEmpty(delta_cursor, delta_notEmpty);
   		CLOSE delta_cursor;
   		SET level = level + 1;
   		
   END WHILE; 
   -- end iteration to fix point
   
   -- Delete self 
   DELETE FROM session.t
   WHERE X = startingPoint;
   --
  
  
   SET resultTable = 'session.t';
END @

CREATE OR REPLACE PROCEDURE  RightRFromMultipleSP(IN property  VarChar(100), IN maxLevel INTEGER,  OUT resultTable VARCHAR(20),  OUT relevantPropertyTable VARCHAR(20), OUT level INTEGER)  
-- negative maxLevel indicate need to recurse until the fix point
-- computes select ?X where { ?Y property* ?X } where ?Y is an element in the table session.inputsub
-- it stores the result in resultTable
-- it also stores in relevantPropertyTable(X, Y) the following table resultTable (X)  & ( X property Y). In other words, relevantPropertyTable(X, Y) containing only the relevant pairs X, Y.
--
LANGUAGE SQL
BEGIN 

	-- m_anc_bf(X) :- tableOfStartingPoints(X)     ( m_anc_bf = session.t  )
	-- sup_0_1(X, Z) :- m_anc_bf(X) ^ parent(X, Z);  ( sup_0_1 = session.sup)
	-- m_anc_bf(Z) :- sup_0_1(X, Z) ;
	
	
	
   DECLARE delta_notEmpty BOOLEAN; -- for deltasup 
   DECLARE delta_cursor CURSOR; -- for deltasup
   -- initialization 
   DELETE FROM session.t;
   DELETE FROM session.deltat;
   DELETE FROM session.olddeltat;
   
   DELETE FROM session.sup;
   DELETE FROM session.deltasup;
   DELETE FROM session.olddeltasup;
   
  
   INSERT INTO session.deltat
    SELECT * FROM session.inputsub;
  
   
   SET level = 0;
   -- end initialization 
 
   -- iteration to fix point
   SET  delta_cursor = CURSOR FOR SELECT 1 FROM session.deltat;
   OPEN delta_cursor;
   call notEmpty(delta_cursor, delta_notEmpty);
   CLOSE delta_cursor;
   
   WHILE (delta_notEmpty = true  AND (maxLevel < 0 OR  level < maxLevel) ) DO 
   
   		-- olddeltat = copy(deltat)
   		DELETE FROM session.olddeltat;
   		INSERT INTO session.olddeltat 
   		select * from session.deltat;
   		-- 
   		-- olddeltasup = copy(deltasup)
   		DELETE FROM session.olddeltasup;
   		INSERT INTO session.olddeltasup 
   		select * from session.deltasup;
        --
        
   		INSERT INTO session.t 
  		select * from session.deltat;
        
        INSERT INTO session.sup 
  		select * from session.deltasup; 
         
   		DELETE FROM session.deltasup;
   		INSERT INTO session.deltasup
   		 -- generated sql [
  			WITH  QS0 AS (SELECT T.entry AS X, T.val0 AS Y
 			FROM   db2inst2.uobm30_DPH AS T,  session.olddeltat AS deltat
   			WHERE  entry = deltat.X
     		AND   (T.prop0 = property)
			 )
			SELECT  DISTINCT  X AS X, Y AS Y FROM QS0
		 -- ] VERY IMPORTANT NOTE: does not work when parentheses are put around the "WITH .. select" clause
    	 -- NOTE: the above select query corresponds to the sql generated for the following SPARQL query:
    	 -- select ?X where { delta.X  <property> ?X}
   			EXCEPT 
   			(SELECT * FROM session.sup );
   		 
   		DELETE FROM session.deltat;
   		INSERT INTO session.deltat
   		  SELECT Y FROM session.olddeltasup
   		  EXCEPT 
   		  (SELECT * FROM session.t);
   		
   		-- update delta_notEmpty
   		SET  delta_cursor = CURSOR FOR SELECT 1 FROM session.deltasup;
   		OPEN delta_cursor;
   		call notEmpty(delta_cursor, delta_notEmpty);
   		CLOSE delta_cursor;
   		IF (delta_notEmpty = false)  THEN
   			SET  delta_cursor = CURSOR FOR SELECT 1 FROM session.deltat;
   			OPEN delta_cursor;
   			call notEmpty(delta_cursor, delta_notEmpty);
   			CLOSE delta_cursor;
   		END IF;
   		--
   		SET level = level + 1;
   		
   END WHILE; 
   -- end iteration to fix point
   
 
  
  
   SET resultTable = 'session.t';
   SET relevantPropertyTable = 'session.sup';
END  @


CREATE OR REPLACE PROCEDURE  ANCFromMultipleSPSubject(IN property  VarChar(100), IN maxLevel INTEGER, IN hasObjectSP BOOLEAN,  OUT resultTable VARCHAR(20),   OUT level INTEGER)  
-- negative maxLevel indicate need to recurse until the fix point
-- computes select ?X, ?Y where { ?X property* ?Y } where ?X is an element in the table session.input
-- it stores the result in resultTable
--
LANGUAGE SQL
BEGIN
	-- anc_bf(X, Y) :- sup_0_1(X, Y);
	-- anc_bf(X, Y) :- sup_0_1(X, Z) ^ anc_bf(Z, Y) ;
	
   DECLARE delta_notEmpty BOOLEAN;
   DECLARE delta_cursor CURSOR;
   DECLARE tmpv1  VARCHAR(20);
   DECLARE tmpv2 VARCHAR(20);
   -- initialization 
   DELETE FROM session.t;
   DELETE FROM session.deltat;
   DELETE FROM session.olddeltat;
   
   DELETE FROM session.sup;
   DELETE FROM session.deltasup;
   DELETE FROM session.olddeltasup;
   
   DELETE FROM session.anc;
   DELETE FROM session.deltaanc;
   DELETE FROM session.olddeltaanc;
   
   call RightRFromMultipleSP(property, maxLevel, tmpv1, tmpv2, level);
   
   IF ( hasObjectSP = false) THEN
   	INSERT INTO session.deltaanc
   	SELECT * FROM session.sup;
   ELSE
   	INSERT INTO session.deltaanc
   	SELECT sup.X AS X, sup.Y AS Y FROM session.sup AS sup, session.inputobj AS obj
   	WHERE sup.Y = obj.X;
   END IF;
   	
   -- end initialization 
 
   -- iteration to fix point
   SET  delta_cursor = CURSOR FOR SELECT 1 FROM session.deltaanc;
   OPEN delta_cursor;
   call notEmpty(delta_cursor, delta_notEmpty);
   CLOSE delta_cursor;
   
   WHILE (delta_notEmpty = true  AND (maxLevel < 0 OR  level < maxLevel) ) DO 
   
   		-- olddeltaanc = copy(deltaanc)
   		DELETE FROM session.olddeltaanc;
   		INSERT INTO session.olddeltaanc 
   		select * from session.deltaanc;
   		-- 
   		
   		INSERT INTO session.anc
  		select * from session.deltaanc;
         
   		DELETE FROM session.deltaanc;
   		INSERT INTO session.deltaanc
   			SELECT sup.X AS X, delta.Y AS Y FROM session.sup AS sup, session.olddeltaanc AS delta  WHERE sup.Y = delta.X 
   			EXCEPT 
   			(SELECT * FROM session.anc);
   		 
   		SET  delta_cursor = CURSOR FOR SELECT 1 FROM session.deltaanc;
   		OPEN delta_cursor;
   		call notEmpty(delta_cursor, delta_notEmpty);
   		CLOSE delta_cursor;
   		SET level = level + 1;
   		
   END WHILE; 
   -- end iteration to fix point
   
    -- retain only anc(X, Y) where X is in session.inputsub(X)
  	DELETE FROM session.deltaanc;
  	INSERT INTO session.deltaanc
  	SELECT anc.X AS X, anc.Y AS Y FROM session.anc AS anc, session.inputsub AS sub
   	WHERE anc.X = sub.X;
   	DELETE FROM session.anc;
   	INSERT INTO session.anc 
   	select * from session.deltaanc;
    --
    
   SET resultTable = 'session.t';
END @

CREATE OR REPLACE PROCEDURE GENPROC()
	DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
	
	DECLARE tmptable VARCHAR(100);
	DECLARE level INTEGER;
		
	DECLARE C1 CURSOR WITH RETURN FOR 
		WITH  QS0 AS (SELECT X AS Z FROM  session.t),
		
  QS1 AS (SELECT entity AS Z,elem AS Y
 FROM db2inst2.uobm30_RS AS T,QS0
 WHERE  entity = QS0.Z 
  AND   (prop = 'http://semantics.crl.ibm.com/univ-bench-dl.owl#isStudentOf')
 ),
 QS2 AS (SELECT entity AS Z,elem AS Y
 FROM db2inst2.uobm30_RS AS T,QS0
 WHERE  entity = QS0.Z 
  AND   (prop = 'http://semantics.crl.ibm.com/univ-bench-dl.owl#enrollIn')
 ),
QS3 AS ( SELECT QS1.Y AS Y FROM QS1 UNION ALL
SELECT QS2.Y AS Y FROM QS2),
 QS4 AS (SELECT entity AS Z,elem AS Y
 FROM db2inst2.uobm30_RS AS T,QS0
 WHERE  entity = QS0.Z 
  AND   (prop = 'http://semantics.crl.ibm.com/univ-bench-dl.owl#isHeadOf')
 ),
QS5 AS ( SELECT QS3.Y AS Y FROM QS3 UNION ALL
SELECT QS4.Y AS Y FROM QS4),
 QS6 AS (SELECT entity AS Z,elem AS Y
 FROM db2inst2.uobm30_RS AS T,QS0
 WHERE  entity = QS0.Z 
  AND   (prop = 'http://semantics.crl.ibm.com/univ-bench-dl.owl#isMemberOf')
 ),
QS7 AS ( SELECT QS5.Y AS Y FROM QS5 UNION ALL
SELECT QS6.Y AS Y FROM QS6),
 QS8 AS (SELECT entity AS Z,elem AS Y
 FROM db2inst2.uobm30_RS AS T,QS0
 WHERE  entity = QS0.Z 
  AND   (prop = 'http://semantics.crl.ibm.com/univ-bench-dl.owl#worksFor')
 ),
QS9 AS ( SELECT QS7.Y AS Y FROM QS7 UNION ALL
SELECT QS8.Y AS Y FROM QS8),
 QS10 AS (SELECT entity AS Y,elem AS X
 FROM db2inst2.uobm30_DS AS T,QS9
 WHERE  entity = QS9.Y 
  AND   (prop = 'http://semantics.crl.ibm.com/univ-bench-dl.owl#teacherOf')
 ),
 QS11 AS (SELECT entry AS Y,T.val0 AS X
 FROM QS9,db2inst2.uobm30_RPH AS T
 WHERE  entry = QS9.Y 
  AND   (T.prop0 = 'http://semantics.crl.ibm.com/univ-bench-dl.owl#isTaughtBy')
 ),
QS12 AS ( SELECT QS10.X AS X FROM QS10 UNION ALL
SELECT QS11.X AS X FROM QS11),
 QS13 AS (SELECT entity AS X
 FROM db2inst2.uobm30_DS AS T,QS12
 WHERE  entity = QS12.X 
  AND   (prop = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type')
  AND  elem = 'http://semantics.crl.ibm.com/univ-bench-dl.owl#Course' 
 )
SELECT  DISTINCT  X AS X FROM QS13 ; 
	
	call leftr('http://semantics.crl.ibm.com/univ-bench-dl.owl#subOrganizationOf', 'http://www.University0.edu', -1, tmptable, level);
	
	OPEN C1;	
END @




