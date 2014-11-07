#!/bin/bash

KNOWLEDGE_BASE=`echo $1 | awk '{ print toupper($0); }'`
DB2_CONFIG=$2
SQL_FILE=$3
DROP=$4
FILE_TYPE=$5
DB_ENGINE=$6

IS_CYGWIN=0
case "`uname`" in
	CYGWIN*) IS_CYGWIN=1;
esac

if [[ $IS_CYGWIN == 1 ]]; then
    DB2_CONFIG_CYG=`cygpath --path --unix $DB2_CONFIG`
    . $DB2_CONFIG_CYG
else
    . $DB2_CONFIG
fi

function run_command() {
    db2_cmd=$1
    data=`(db2 | awk '/----------/ { start=1; next } /[0-9]* record\(s\) selected/ { start=0; } /^$/ { next; } start==1 { print $0; }') <<EOF 
connect to $DB2_DB
$db2_cmd
EOF`

    echo $data
}

function count_properties() {
    table_suffix=$1
    run_command "select count(*) from sysibm.syscolumns where tbname='${KNOWLEDGE_BASE}_${table_suffix}' and name like 'PROP%'"
}

function list_secondary_props() {
    table_suffix=$1
    run_command "select distinct prop from ${KNOWLEDGE_BASE}_${table_suffix}"
}

function secondary_prop_in_list() {
    second=$1

    second_many_properties=`list_secondary_props $second`
    
    in_list=""
    for prop in $second_many_properties; do
	clause="'$prop'"
	if [[ x$in_list = x ]]; then
	    in_list=$clause
	else
	    in_list="$in_list, $clause"
	fi
    done

    echo $in_list
}

function for_each_main_prop() {
    main=$1
    fun=$2
    shift; shift

    main_property_count=`count_properties $main`

    result=""
    i=0
    while [[ $i -lt $main_property_count ]]; do
	result=`$fun $i "$result" "$@"`
	i=`expr $i + 1`
    done
    
    echo $result
}

# create indexes
if [[ $FILE_TYPE == "quad" ]]; then
    if [[ $DB_ENGINE == "db2" ]]; then
		GRAPH=", \"GID\" ASC"
    else
    	if [[ $DB_ENGINE == "shark" ]]; then
			GRAPH=", GID"
		else 
			GRAPH=", GID ASC"
		fi
    fi
else
 GRAPH=
fi

if [[ $DB_ENGINE == "db2" ]]; then
    echo "create unique index prop_elem_entity_${KNOWLEDGE_BASE}_DS on ${KNOWLEDGE_BASE}_DS (\"PROP\" ASC, \"ELEM\" ASC, \"ENTITY\" ASC, \"TYP\" ASC${GRAPH}) compress yes allow reverse scans" >> $SQL_FILE
    echo "create unique index prop_entity_elem_${KNOWLEDGE_BASE}_DS on ${KNOWLEDGE_BASE}_DS (\"PROP\" ASC, \"ENTITY\" ASC, \"ELEM\" ASC, \"TYP\" ASC${GRAPH}) compress yes allow reverse scans" >> $SQL_FILE
    echo "create unique index prop_elem_entity_${KNOWLEDGE_BASE}_RS on ${KNOWLEDGE_BASE}_RS (\"PROP\" ASC, \"ELEM\" ASC, \"ENTITY\" ASC, \"TYP\" ASC${GRAPH}) compress yes allow reverse scans" >> $SQL_FILE
    echo "create unique index prop_entity_elem_${KNOWLEDGE_BASE}_RS on ${KNOWLEDGE_BASE}_RS (\"PROP\" ASC, \"ENTITY\" ASC, \"ELEM\" ASC, \"TYP\" ASC${GRAPH}) compress yes allow reverse scans" >> $SQL_FILE
else
    if [[ $DB_ENGINE == "shark" ]]; then
    	echo "create  index prop_elem_entity_${KNOWLEDGE_BASE}_DS on table ${KNOWLEDGE_BASE}_DS(PROP, ELEM, ENTITY , TYP ${GRAPH}) AS 'COMPACT' WITH DEFERRED REBUILD;" >> $SQL_FILE
#    	echo "alter  index prop_elem_entity_${KNOWLEDGE_BASE}_DS on  ${KNOWLEDGE_BASE}_DS  REBUILD;" >> $SQL_FILE
    	
		echo "create  index prop_entity_elem_${KNOWLEDGE_BASE}_DS on table ${KNOWLEDGE_BASE}_DS(PROP , ENTITY , ELEM , TYP ${GRAPH}) AS 'COMPACT' WITH DEFERRED REBUILD;" >> $SQL_FILE
#		echo "alter  index prop_entity_elem_${KNOWLEDGE_BASE}_DS on  ${KNOWLEDGE_BASE}_DS  REBUILD;" >> $SQL_FILE
		
		echo "create  index prop_elem_entity_${KNOWLEDGE_BASE}_RS on table ${KNOWLEDGE_BASE}_RS(PROP , ELEM , ENTITY , TYP ${GRAPH}) AS 'COMPACT' WITH DEFERRED REBUILD;" >> $SQL_FILE
#		echo "alter  index prop_elem_entity_${KNOWLEDGE_BASE}_RS on  ${KNOWLEDGE_BASE}_RS REBUILD;" >> $SQL_FILE
		
		echo "create  index prop_entity_elem_${KNOWLEDGE_BASE}_RS on table ${KNOWLEDGE_BASE}_RS(PROP , ENTITY , ELEM , TYP ${GRAPH}) AS 'COMPACT' WITH DEFERRED REBUILD;" >> $SQL_FILE
#		echo "alter  index prop_entity_elem_${KNOWLEDGE_BASE}_RS on ${KNOWLEDGE_BASE}_RS REBUILD;" >> $SQL_FILE
	
    else 
    	echo "create unique index prop_elem_entity_${KNOWLEDGE_BASE}_DS on ${KNOWLEDGE_BASE}_DS (PROP ASC, ELEM ASC, ENTITY ASC, TYP ASC${GRAPH});" >> $SQL_FILE
	    echo "create unique index prop_entity_elem_${KNOWLEDGE_BASE}_DS on ${KNOWLEDGE_BASE}_DS (PROP ASC, ENTITY ASC, ELEM ASC, TYP ASC${GRAPH});" >> $SQL_FILE
	    echo "create unique index prop_elem_entity_${KNOWLEDGE_BASE}_RS on ${KNOWLEDGE_BASE}_RS (PROP ASC, ELEM ASC, ENTITY ASC, TYP ASC${GRAPH});" >> $SQL_FILE
	    echo "create unique index prop_entity_elem_${KNOWLEDGE_BASE}_RS on ${KNOWLEDGE_BASE}_RS (PROP ASC, ENTITY ASC, ELEM ASC, TYP ASC${GRAPH});" >> $SQL_FILE
	    echo "ALTER TABLE ${KNOWLEDGE_BASE}_DS ALTER PROP SET STATISTICS 5000;" >> $SQL_FILE
	    echo "ALTER TABLE ${KNOWLEDGE_BASE}_DS ALTER ELEM SET STATISTICS 5000;" >> $SQL_FILE
	    echo "ALTER TABLE ${KNOWLEDGE_BASE}_DS ALTER ENTITY SET STATISTICS 5000;" >> $SQL_FILE
	    echo "ALTER TABLE ${KNOWLEDGE_BASE}_DS ALTER TYP SET STATISTICS 5000;" >> $SQL_FILE
	    echo "ALTER TABLE ${KNOWLEDGE_BASE}_RS ALTER PROP SET STATISTICS 5000;" >> $SQL_FILE
	    echo "ALTER TABLE ${KNOWLEDGE_BASE}_RS ALTER ELEM SET STATISTICS 5000;" >> $SQL_FILE
	    echo "ALTER TABLE ${KNOWLEDGE_BASE}_RS ALTER ENTITY SET STATISTICS 5000;" >> $SQL_FILE
	    echo "ALTER TABLE ${KNOWLEDGE_BASE}_RS ALTER TYP SET STATISTICS 5000;" >> $SQL_FILE
	
		if [[ $FILE_TYPE == "quad" ]]; then
	    	echo "ALTER TABLE ${KNOWLEDGE_BASE}_DS ALTER GID SET STATISTICS 5000;" >> $SQL_FILE
	    	echo "ALTER TABLE ${KNOWLEDGE_BASE}_RS ALTER GID SET STATISTICS 5000;" >> $SQL_FILE
		fi
	fi

fi
