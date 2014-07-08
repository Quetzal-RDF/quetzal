#!/bin/bash

DIR=`dirname $0`
. $DIR/load-setup.sh

TEST_CLASS=$1
TEST_DIR=$2

. $DB2_CONFIG

java -Xmx1024M com.ibm.rdf.store.sparql11.$TEST_CLASS "jdbc:db2://$DB2_HOST:$DB2_PORT/$DB2_DB" $DB2_USER $DB2_PASSWORD $DB2_SCHEMA $TEST_DIR

