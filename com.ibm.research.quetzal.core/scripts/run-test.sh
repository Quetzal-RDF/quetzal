#!/bin/bash

DIR=`dirname $0`

if [[ -z $KNOWLEDGE_BASE ]]; then
    export KNOWLEDGE_BASE=$1
    shift
fi
export TEST_DIR=$1
TEST_CLASS=$2
ANSWERS=$3
REPEAT=$4
RANDOM=$5

shift; shift; shift; shift; shift

. $DIR/load-setup.sh
. $DB2_CONFIG

java -Xmx2048M com.ibm.rdf.store.sparql11.CommandLineDriver com.ibm.rdf.store.sparql11.$TEST_CLASS $ANSWERS $REPEAT $RANDOM



