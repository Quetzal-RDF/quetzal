#!/bin/bash

DIR=`dirname $0`

TEST_CLASS=$1
DATA_FACTORY=$2
DATASET=$3
ANSWERS=$4
TEST_DIR=$5
REPEAT=$6
RANDOM=$7

shift; shift; shift; shift; shift; shift; shift

. $DIR/load-setup.sh
. $DB2_CONFIG

java -Xmx2048M com.ibm.research.rdf.store.sparql11.CommandLineDriver com.ibm.research.rdf.store.sparql11.$TEST_CLASS com.ibm.research.rdf.store.sparql11.TestRunner\$$DATA_FACTORY $DATASET $ANSWERS $TEST_DIR $REPEAT $RANDOM



