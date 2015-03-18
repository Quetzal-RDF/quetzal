--sqls used for add/delete triple testing in LUBM_100M_R store at 10.91.53.170

--clear
delete from DB2INST1.LUBM_100M_R_DPH T where T.entry like 'http://usersub%';
delete from DB2INST1.LUBM_100M_R_RPH T where T.entry like 'http://userobj%' OR T.entry like 'userobj%';
delete from DB2INST1.LUBM_100M_R_DS T where T.entity like 'http://usersub%';
delete from DB2INST1.LUBM_100M_R_RS T where T.elem like 'http://usersub%'; 

--add triples
call DB2INST1.ADDTRIPLE('DB2INST1','LUBM_100M_R','http://usersub1/', 'http://userobj1/', 'http://testpred1/', 10002, 1, 1, 'DEF', 1, ?);
call DB2INST1.ADDTRIPLE('DB2INST1','LUBM_100M_R','http://usersub1/', 'http://userobj2/', 'http://testpred2/', 10002, 2, 2, 'DEF', 1, ?);
call DB2INST1.ADDTRIPLE('DB2INST1','LUBM_100M_R','http://usersub1/', 'userobj3', 'http://testpred1/', 5001, 1, 1, 'DEF', 1, ?);
call DB2INST1.ADDTRIPLE('DB2INST1','LUBM_100M_R','http://usersub2/', 'http://userobj2/', 'http://testpred2/', 10002, 2, 2, 'DEF', 1, ?);
call DB2INST1.ADDTRIPLE('DB2INST1','LUBM_100M_R','http://usersub1/', 'userobj4', 'http://testpred3/', 5001, 1, 1, 'DEF', 1, ?);
call DB2INST1.ADDTRIPLE('DB2INST1','LUBM_100M_R','http://usersub1/', 'userobj4', 'http://testpred4/', 5001, 2, 2, 'DEF', 1, ?);
call DB2INST1.ADDTRIPLE('DB2INST1','LUBM_100M_R','http://usersub1/', 'userobj5', 'http://testpred5/', 5001, 2, 2, 'DEF', 1, ?);
call DB2INST1.ADDTRIPLE('DB2INST1','LUBM_100M_R','http://usersub2/', 'http://userobj2/', 'http://testpred6/', 10002, 2, 2, 'DEF', 1, ?);
call DB2INST1.ADDTRIPLE('DB2INST1','LUBM_100M_R','http://usersub2/', 'http://userobj2/', 'http://testpred7/', 10002, 1, 1, 'DEF', 1, ?);
call DB2INST1.ADDTRIPLE('DB2INST1','LUBM_100M_R','http://usersub2/', 'http://userobj2/', 'http://testpred8/', 10002, 1, 1, 'DEF', 1, ?);
call DB2INST1.ADDTRIPLE('DB2INST1','LUBM_100M_R','http://usersub2/', 'http://userobj2/', 'http://testpred9/', 10002, 1, 1, 'DEF', 1, ?);
call DB2INST1.ADDTRIPLE('DB2INST1','LUBM_100M_R','http://usersub3/', 'http://userobj2/', 'http://testpred2/', 10002, 2, 2, 'DEF', 1, ?);
call DB2INST1.ADDTRIPLE('DB2INST1','LUBM_100M_R','http://usersub1/', 'userobj4', 'http://testpred1/', 5001, 1, 1, 'DEF', 1, ?);
call DB2INST1.ADDTRIPLE('DB2INST1','LUBM_100M_R','http://usersub1/', 'userobj5', 'http://testpred1/', 5001, 1, 1, 'DEF', 1, ?);
call DB2INST1.ADDTRIPLE('DB2INST1','LUBM_100M_R','http://usersub1/', 'userobj5', 'http://testpred3/', 5001, 1, 1, 'DEF', 1, ?);
call DB2INST1.ADDTRIPLE('DB2INST1','LUBM_100M_R','http://usersub4/', 'http://userobj2/', 'http://testpred2/', 10002, 2, 2, 'DEF', 1, ?);
call DB2INST1.ADDTRIPLE('DB2INST1','LUBM_100M_R','http://usersub3/', 'userobj3', 'http://testpred1/', 5001, 1, 1, 'DEF', 1, ?);

--delete triples
call DB2INST1.DELETETRIPLE('DB2INST1','LUBM_100M_R','http://usersub3/', 'userobj3', 'http://testpred1/', 11, 4, 1, 1, 'DEF', 1, ?);
call DB2INST1.DELETETRIPLE('DB2INST1','LUBM_100M_R','http://usersub1/', 'userobj3', 'http://testpred1/', 11, 4, 1, 1, 'DEF', 1, ?);
call DB2INST1.DELETETRIPLE('DB2INST1','LUBM_100M_R','http://usersub1/', 'userobj4', 'http://testpred1/', 11, 4, 1, 1, 'DEF', 1, ?);
call DB2INST1.DELETETRIPLE('DB2INST1','LUBM_100M_R','http://usersub2/', 'http://userobj2/', 'http://testpred8/', 11, 4, 1, 1, 'DEF', 1, ?);
call DB2INST1.DELETETRIPLE('DB2INST1','LUBM_100M_R','http://usersub2/', 'http://userobj2/', 'http://testpred6/', 11, 4, 2, 2, 'DEF', 1, ?);
call DB2INST1.DELETETRIPLE('DB2INST1','LUBM_100M_R','http://usersub4/', 'http://userobj2/', 'http://testpred2/', 11, 4, 2, 2, 'DEF', 1, ?);
call DB2INST1.DELETETRIPLE('DB2INST1','LUBM_100M_R','http://usersub2/', 'http://userobj2/', 'http://testpred7/', 11, 4, 1, 1, 'DEF', 1, ?);
call DB2INST1.DELETETRIPLE('DB2INST1','LUBM_100M_R','http://usersub2/', 'http://userobj2/', 'http://testpred2/', 11, 4, 2, 2, 'DEF', 1, ?);
call DB2INST1.DELETETRIPLE('DB2INST1','LUBM_100M_R','http://usersub3/', 'http://userobj2/', 'http://testpred2/', 11, 4, 2, 2, 'DEF', 1, ?);
call DB2INST1.DELETETRIPLE('DB2INST1','LUBM_100M_R','http://usersub2/', 'http://userobj2/', 'http://testpred9/', 11, 4, 1, 1, 'DEF', 1, ?);
call DB2INST1.DELETETRIPLE('DB2INST1','LUBM_100M_R','http://usersub1/', 'userobj5', 'http://testpred1/', 11, 4, 1, 1, 'DEF', 1, ?);
call DB2INST1.DELETETRIPLE('DB2INST1','LUBM_100M_R','http://usersub1/', 'userobj5', 'http://testpred3/', 11, 4, 1, 1, 'DEF', 1, ?);
call DB2INST1.DELETETRIPLE('DB2INST1','LUBM_100M_R','http://usersub1/', 'userobj4', 'http://testpred3/', 11, 4, 1, 1, 'DEF', 1, ?);
call DB2INST1.DELETETRIPLE('DB2INST1','LUBM_100M_R','http://usersub1/', 'userobj4', 'http://testpred4/', 11, 4, 2, 2, 'DEF', 1, ?);
call DB2INST1.DELETETRIPLE('DB2INST1','LUBM_100M_R','http://usersub1/', 'userobj5', 'http://testpred5/', 11, 4, 2, 2, 'DEF', 1, ?);
call DB2INST1.DELETETRIPLE('DB2INST1','LUBM_100M_R','http://usersub1/', 'http://userobj1/', 'http://testpred1/', 11, 4, 1, 1, 'DEF', 1, ?);
call DB2INST1.DELETETRIPLE('DB2INST1','LUBM_100M_R','http://usersub1/', 'http://userobj2/', 'http://testpred2/', 11, 4, 2, 2, 'DEF', 1, ?);

