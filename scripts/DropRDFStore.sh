#!/bin/bash
DIR=`dirname $0`
. $DIR/load-setup.sh
. $DB2_CONFIG

if [[ $DB_ENGINE == "db2" ]]; then
cat > $NT_FILE.db2_drop_cmds <<EOF
    CONNECT TO $DB2_DB
EOF
fi

if [[ $DB_ENGINE == "db2" ]]; then
    cat > $NT_FILE.db2_drop_cmds <<EOF
drop table $DB2_SCHEMA.testdb_DPH
drop table $DB2_SCHEMA.testdb_DS
drop table $DB2_SCHEMA..testdb_RPH
drop table $DB2_SCHEMA.testdb_RS
drop table $DB2_SCHEMA.testdb_LSTR
drop table $DB2_SCHEMA.testdb_BASESTATS
drop table $DB2_SCHEMA.testdb_TOPKSTATS
drop table $DB2_SCHEMA.testdb_DIRECT_PREDS
drop table $DB2_SCHEMA.testdb_REVERSE_PREDS
drop table $DB2_SCHEMA.testdb_SP
drop table $DB2_SCHEMA.testdb_DT
drop table $DB2_SCHEMA.testdb
drop sequence $DB2_SCHEMA.testdb_DT_type_seq
drop sequence $DB2_SCHEMA.testdb_DT_lang_seq
drop sequence $DB2_SCHEMA.testdb_entry_id_seq

COMMIT WORK
EOF

elif [[ $DB_ENGINE == "postgresql" ]]; then
    cat > $NT_FILE.db2_drop_cmds <<EOF
drop table $DB2_SCHEMA.testdb_DPH;
drop table $DB2_SCHEMA.testdb_DS;
drop table $DB2_SCHEMA.testdb_RPH;
drop table $DB2_SCHEMA.testdb_RS;
drop table $DB2_SCHEMA.testdb_LSTR;
drop table $DB2_SCHEMA.testdb_BASESTATS;
drop table $DB2_SCHEMA.testdb_TOPKSTATS;
drop table $DB2_SCHEMA.testdb_DIRECT_PREDS;
drop table $DB2_SCHEMA.testdb_REVERSE_PREDS;
drop table $DB2_SCHEMA.testdb_SP;
drop table $DB2_SCHEMA.testdb_DT;
drop table $DB2_SCHEMA.testdb;
drop sequence $DB2_SCHEMA.testdb_DT_type_seq;
drop sequence $DB2_SCHEMA.testdb_DT_lang_seq;
drop sequence $DB2_SCHEMA.testdb_entry_id_seq;

EOF
fi

echo $DB_ENGINE
echo $DB2_HOST

if [[ $DB_ENGINE == "db2" ]]; then
    if which db2 > /dev/null 2>&1; then
	cat $NT_FILE.db2_drop_cmds | db2 -c-
    fi
elif [[ $DB_ENGINE == "postgresql" ]]; then
    echo psql --dbname=$DB2_DB --host=$DB2_HOST --port=$DB2_PORT --username=$DB2_USER --file=$NT_FILE.db2_drop_cmds --no-password 
    psql --dbname=$DB2_DB --host=$DB2_HOST --port=$DB2_PORT --username=$DB2_USER --file=$NT_FILE.db2_drop_cmds --no-password 
fi
