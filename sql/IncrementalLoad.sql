-- KS: adapting Patrick's code to support an incremental load of triples/quads into the db
-- PDA: delimiter is  ! :  launch this script with  db2 -vtd! -f SetOrientedLoad.sql
-- PDA:  some management


create procedure loadTriples(in subjects stringArray, in predicates stringArray, in objects stringArray, in count integer, out counter integer)
modifies sql data
language sql

begin


-- these steps are done separately for direct and reverse tables
-- PDA: Assuming a thin table with 3 columns
create table ThinTable(subject varchar(118) not null, predicate varchar(118) not null , object varchar(118) not null, index smallInt) ;

create index ThinTableidx on ThinTable(subject) ;
create index ThinTableidx1 on ThinTable(predicate) ;
create index ThinTableidx2 on ThinTable(object) ;

-- New triples to be inserted
insert into ThinTable(subject, predicate, object) 
(select T.s, T.p, T.o from UNNEST(subjects, predicates, objects) as T(s, p, o));

-- Fetch the triples that already exist for the subject, drop it into ThinTable
-- Idea here is to get all the triples for the subject and drop it back in one shot
-- rather than 'updating' the records in place.  This is an un-pivot of the DPH table
-- to look like the triple table
SELECT S.subject, Q.predicate, Q.object
FROM RDFSTORE1_DPH AS S,
     TABLE (VALUES(S.PROP0, S.VAL0),
     	   	  (S.PROP1, S.VAL1))
            AS Q(predicate, object);


-- This code now tries to lay the set of triples for a subject out into the appropriate cols.  Loop:
-- for every distinct subject, gather all the predicate value pairs per subject, select the predicate col mappings
-- (assumption is we have 3) and insert the first unused col to that triple in the triple table (note the triple table 
-- has an extra column for the ID of the col where the predicate value will go into.
 
DECLARE
  CURSOR s IS select distinct subject from ThinTable
  CURSOR t (VARCHAR subj) IS select predicate, object from ThinTable where subject=subj
  CURSOR m (VARCHAR pred) IS select LT.v from PredicateMap P, TABLE(VALUES(P.c1), VALUES(P.c2), VALUES(P.c3)) AS LT(v) WHERE P.predicate=pred
  DECFLOAT(34) used
BEGIN
  OPEN s
  LOOP
    FETCH s INTO subject
    EXIT WHEN s%NOTFOUND
    used = 0
    OPEN t(subject)
    LOOP
      FETCH t INTO predicate, object
      EXIT WHEN t%NOTFOUND
      OPEN m(predicate)
      LOOP
        FETCH m INTO col
        IF (BITTEST(used,col) = 0) THEN
          UPDATE t.index = col
          BITSET(used,col)
          EXIT
        END
      END
      CLOSE m
    END
    CLOSE t
  END
  CLOSE s
END

-- Now pivot the triple table so we get basically the DPH records we need to insert back into DPH
-- Note: final merge is yet to be done.
--Sample pivot code:
--SELECT Year,
--       MAX(DECODE(Quarter, 1, Results)) AS Q1,
--       MAX(DECODE(Quarter, 2, Results)) AS Q2,            
--       MAX(DECODE(Quarter, 3, Results)) AS Q3,
--       MAX(DECODE(Quarter, 4, Results)) AS Q4
--  FROM Sales
--  GROUP BY Year;



DROP TABLE ThinTable ;


end !


create procedure main(in directColumns integer, in reverseColumns integer, out total integer)
begin
declare subjects stringArray;
declare predicates stringArray;
declare objects stringArray;

set subjects = ARRAY['a', 'b', 'c'];
set predicates = ARRAY['p', 'q', 'r'];
set objects = ARRAY['m', 'n', 'o'];


call loadTriples(subjects, predicates, objects, 3, total);

end !

