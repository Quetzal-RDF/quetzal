create or replace function typecode(lit varchar(200))
returns SMALLINT 
language sql contains sql no external action deterministic
begin
 return case
  when lit='http://www.w3.org/2001/XMLSchema#string' then 5003
  when lit='http://www.w3.org/2001/XMLSchema#boolean' then 5004
  when lit='http://www.w3.org/2001/XMLSchema#date' then 5005
  when lit='http://www.w3.org/2001/XMLSchema#dateTime' then 5006
  when lit='http://www.w3.org/2001/XMLSchema#positiveInteger' then 5007
  when lit='http://www.w3.org/2001/XMLSchema#nonPositiveInteger' then 5008
  when lit='http://www.w3.org/2001/XMLSchema#negativeInteger' then 5009
  when lit='http://www.w3.org/2001/XMLSchema#long' then 5010
  when lit='http://www.w3.org/2001/XMLSchema#int' then 5011
  when lit='http://www.w3.org/2001/XMLSchema#short' then 5012
  when lit='http://www.w3.org/2001/XMLSchema#byte' then 5013
  when lit='http://www.w3.org/2001/XMLSchema#nonNegativeInteger' then 5014
  when lit='http://www.w3.org/2001/XMLSchema#unsignedLong' then 5015
  when lit='http://www.w3.org/2001/XMLSchema#unsignedInt' then 5016
  when lit='http://www.w3.org/2001/XMLSchema#unsignedShort' then 5017
  when lit='http://www.w3.org/2001/XMLSchema#unsignedByte' then 5018
  when lit='http://www.w3.org/2001/XMLSchema#integer' then 5019
  when lit='http://www.w3.org/2001/XMLSchema#decimal' then 5020
  when lit='http://www.w3.org/2001/XMLSchema#float' then 5021
  when lit='http://www.w3.org/2001/XMLSchema#double' then 5022
  else 5001 end;
END
@
