#!/bin/bash

DIR=`dirname $0`

if [[ -z $KNOWLEDGE_BASE ]]; then
    export KNOWLEDGE_BASE=$1
    shift
fi

export TEST_DIR=$1
shift

. $DIR/load-setup.sh
if [[ -f $DB2_CONFIG ]]; then
    . $DB2_CONFIG
fi

java -Xmx2048M junit.textui.TestRunner com.ibm.rdf.store.sparql11.DirectoryDriver



