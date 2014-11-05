#!/bin/bash

DIR=`dirname $0`

TEST_CLASS=$1
DATASET=$2
ANSWERS=$3
TEST_DIR=$4

shift; shift; shift; shift

. $DIR/load-setup.sh
. $DB2_CONFIG

java -Xmx2048M com.ibm.rdf.store.sparql11.CommandLineDriver com.ibm.rdf.store.sparql11.$TEST_CLASS $DATASET $ANSWERS $TEST_DIR


