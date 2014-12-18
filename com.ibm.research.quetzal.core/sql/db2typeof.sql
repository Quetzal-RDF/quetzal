create or replace function typeof(lit varchar(200))
returns CHAR(1) 
language sql contains sql no external action deterministic
begin
 declare v DECFLOAT;
 declare continue handler for sqlstate '22018'
 begin
  declare d TIMESTAMP;
  declare continue handler for sqlstate '22007'
  begin
   return 'V';
  end;
  set d = CAST(lit as TIMESTAMP);
  return 'T';
 end;
 declare continue handler for sqlstate '42820'
 begin
  return 'V';
 end;
 declare continue handler for sqlstate '22003'
 begin
  return 'V';
 end;
 set v = CAST(lit as DECFLOAT);
 return 'D';
END
@
