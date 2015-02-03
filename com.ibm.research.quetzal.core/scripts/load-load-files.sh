#!/bin/bash

DIR=`dirname $0`
. $DIR/load-setup.sh

echo "load-setup.sh done!"

if [[ $IS_CYGWIN == 1 ]]; then
    DB2_CONFIG_CYG=`cygpath --path --unix $DB2_CONFIG`
    . $DB2_CONFIG_CYG
else
    . $DB2_CONFIG
fi

echo "config done!"

OPTS="-db $DB2_DB -host $DB2_HOST -port $DB2_PORT -user $DB2_USER -password $DB2_PASSWORD -schema $DB2_SCHEMA -backend $DB_ENGINE"

CREATE_OPTS="-predicateMappings $LOAD_DIR/predicate_mappings.load $OPTS"
if [[ -f $SYSTEM_PREDICATES ]]; then
    CREATE_OPTS="$CREATE_OPTS -systempredicates $SYSTEM_PREDICATES"
fi

if [[ $DROP == 1 ]]; then
    java $JAVA_OPTS -cp $CLASSPATH com.ibm.research.rdf.store.cmd.DropRdfStore $KNOWLEDGE_BASE $OPTS
fi

if [[ -e $LOAD_DIR/long-strings.load ]]; then
    LONG_STRINGS_LEN=`awk -F'\t' 'BEGIN {len=0;} { if (length($2) > len) { len=length($2); } } END { print len; }' $LOAD_DIR/long-strings.load`
    if [[ $LONG_STRINGS_LEN -gt 5000 ]]; then
	LONG_STRINGS_LEN=5000

	mv $LOAD_DIR/long-strings.load $LOAD_DIR/long-strings-orig.load

	awk -F'\t' '{ if (length($2) > 15000) { print $1 "\t" substr($2, 1, 5000) "\t" $3 "\t" substr($2, 5001, 10000); } else if (length($2) > 5000) {  print $1 "\t" substr($2, 1, 5000) "\t" $3 "\t" substr($2, 5001); } else {  print $1 "\t" substr($2, 1, 5000) "\t" $3; } }' $LOAD_DIR/long-strings-orig.load > $LOAD_DIR/long-strings.load

    fi
fi

if [[ $DB_ENGINE == "db2" ]]; then
cat > $NT_FILE.db2_cmds_tmp <<EOF
    CONNECT TO $DB2_DB
EOF
fi

rm -f $NT_FILE.db2_create_cmds
java $JAVA_OPTS -Dcom.ibm.rdf.longStringsLength=$LONG_STRINGS_LEN -Dcom.ibm.rdf.createStore="true" -Dcom.ibm.rdf.createIndex="false" -Dcom.ibm.rdf.generateStoreSQL="$NT_FILE.db2_create_cmds" -cp $CLASSPATH com.ibm.research.rdf.store.cmd.CreateRdfStore $KNOWLEDGE_BASE $CREATE_OPTS

rm -f $NT_FILE.db2_index_cmds
java $JAVA_OPTS -Dcom.ibm.rdf.createStore="false" -Dcom.ibm.rdf.createIndex="true" -Dcom.ibm.research.rdf.store.no_lids=$NO_LIDS -Dcom.ibm.rdf.generateStoreSQL="$NT_FILE.db2_index_cmds" -cp $CLASSPATH com.ibm.research.rdf.store.cmd.CreateRdfStore $KNOWLEDGE_BASE $CREATE_OPTS

if [[ $SCHEMA_EXPERIMENTS == 1 ]]; then
    rm -f $NT_FILE.exp_sql
    bash $DIR/setup-schema-experiments.sh $KNOWLEDGE_BASE $DB2_CONFIG $NT_FILE.exp_sql $DROP $FILE_TYPE $DB_ENGINE
else
    touch $NT_FILE.exp_sql
fi


if [[ $DB_ENGINE == "db2" ]]; then
    cat $NT_FILE.db2_cmds_tmp $NT_FILE.db2_create_cmds $NT_FILE.db2_cmds $NT_FILE.db2_index_cmds $NT_FILE.exp_sql > $NT_FILE.db2_script
    cat >> $NT_FILE.db2_script <<EOF
COMMIT WORK
EOF
elif [[ $DB_ENGINE == "postgresql" ]]; then
    cat $NT_FILE.db2_create_cmds $NT_FILE.db2_cmds $NT_FILE.db2_index_cmds $NT_FILE.exp_sql > $NT_FILE.db2_script
fi

if [[ $DB_ENGINE == "shark" ]]; then
    cat $NT_FILE.db2_create_cmds $NT_FILE.db2_cmds $NT_FILE.db2_index_cmds $NT_FILE.exp_sql > $NT_FILE.db2_script
fi

if [[ $CREATE_ONLY == 0 ]]; then
    if [[ $DB_ENGINE == "db2" ]]; then
	if which db2 > /dev/null 2>&1; then
	    cat $NT_FILE.db2_script | db2 -c-
     	fi
    elif [[ $DB_ENGINE == "postgresql" ]]; then
	psql --dbname=$DB2_DB --host=$DB2_HOST --port=$DB2_PORT --username=$DB2_USER --file=$NT_FILE.db2_script --no-password --single-transaction 
    fi	 
    if [[ $DB_ENGINE == "shark" ]]; then
	$SPARK_HOME/bin/beeline -u "jdbc:hive2://"$DB2_HOST":"$DB2_PORT"/"$DB2_DB -n $DB2_USER -p $DB2_PASSWORD < $NT_FILE.db2_script
    fi	    
    java $JAVA_OPTS -cp $CLASSPATH com.ibm.research.rdf.store.cmd.UpdateRdfStoreStats $KNOWLEDGE_BASE $OPTS
fi


