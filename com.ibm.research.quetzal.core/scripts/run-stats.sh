#!/bin/bash

while [[ "--" = `expr substr $1 1 2` ]]; do
    if [[ $1 == "--schemaName" ]]; then
	shift
	SCHEMA_NAME=$1
	shift
    elif [[ $1 == "--storeName" ]]; then
	shift
	STORE_NAME=$1
	shift
    elif [[ $1 == "--indexes" ]]; then
	shift
	DETAILED_INDEXES=$1
	shift
    elif [[ $1 == "--freq" ]]; then
	shift
	FREQ=$1
	shift
    elif [[ $1 == "--quantiles" ]]; then
	shift
	QUANTILES=$1
	shift
    elif [[ $1 == "--all" ]]; then
	shift
	ALL=$1
	shift
    elif [[ $1 == "--db" ]]; then
	shift
	DB=$1
	shift
    elif [[ $1 == "--like" ]]; then
	shift
	LIKE=$1
	shift
    elif [[ $1 == "--directCount" ]]; then
	shift
	direct_count=$1
	shift
    elif [[ $1 == "--reverseCount" ]]; then
	shift
	reverse_count=$1
	shift
    else
	echo "unexpected option $1"
	exit -1
    fi
done


declare -a TABLES=('_DPH' '_DS' '_RPH' '_RS' '_LSTR');

declare -a SECONDARY_TABLES=('_DS' '_RS');

declare -a SUBSET_TABLES=('_LSTR');

db2 connect to $DB;
if [ "$ALL" == "true" ]; then
    for TABLE in "${TABLES[@]}"
    do
	db2 RUNSTATS ON TABLE $SCHEMA_NAME.$STORE_NAME$TABLE ON ALL COLUMNS WITH DISTRIBUTION ON ALL COLUMNS DEFAULT NUM_FREQVALUES $FREQ NUM_QUANTILES $QUANTILES AND DETAILED INDEXES ALL ALLOW WRITE ACCESS set profile;
	db2 commit work;
    done
else
    i=0;
    echo "direct count" $direct_count;
    echo "reverse_count" $reverse_count;

    direct_str='';
    prop_str=' prop';
    value_str=' val';
    delim_str=',';

    while [[ $i -lt $direct_count ]]
    do
	direct_str=$direct_str$prop_str$i$delim_str;
	i=$((i+1));
    done
    direct_str=$direct_str$prop_str$i;

 

    db2 RUNSTATS ON TABLE $SCHEMA_NAME.$STORE_NAME"_DPH" ON ALL COLUMNS WITH DISTRIBUTION ON COLUMNS \( $direct_str \) DEFAULT NUM_FREQVALUES $FREQ NUM_QUANTILES $QUANTILES AND DETAILED INDEXES ALL ALLOW WRITE ACCESS ;
    db2 commit work;

    i=0;

    reverse_str='';
    while [[ $i -lt $reverse_count ]]
    do
	reverse_str=$reverse_str$prop_str$i$delim_str;
	i=$((i+1));
    done
    reverse_str=$reverse_str$prop_str$i;


    db2 RUNSTATS ON TABLE $SCHEMA_NAME.$STORE_NAME"_RPH" ON ALL COLUMNS WITH DISTRIBUTION ON COLUMNS \( $reverse_str \) DEFAULT NUM_FREQVALUES $FREQ NUM_QUANTILES $QUANTILES AND DETAILED INDEXES ALL ALLOW WRITE ACCESS ;
    db2 commit work;

    for TABLE in "${SECONDARY_TABLES[@]}"
    do
	db2 RUNSTATS ON TABLE $SCHEMA_NAME.$STORE_NAME$TABLE ON ALL COLUMNS WITH DISTRIBUTION ON COLUMNS '(' list_id ')' DEFAULT NUM_FREQVALUES $FREQ NUM_QUANTILES $QUANTILES AND DETAILED INDEXES ALL ALLOW WRITE ACCESS;
	db2 commit work;

    done

    for TABLE in "${SUBSET_TABLES[@]}"
    do
	db2 RUNSTATS ON TABLE $SCHEMA_NAME.$STORE_NAME$TABLE ON ALL COLUMNS WITH DISTRIBUTION ON ALL COLUMNS DEFAULT NUM_FREQVALUES $FREQ NUM_QUANTILES $QUANTILES AND DETAILED INDEXES ALL ALLOW WRITE ACCESS;
	db2 commit work;

    done
fi

db2 disconnect $DB;
