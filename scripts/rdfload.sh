#!/bin/bash
if [ ! -n "${RDFSTORE_HOME+x}" ]; then
    echo "Environment variable RDFSTORE_HOME is not set. Please set it and try again."
    exit 1
fi

RDF_CLASSPATH=${RDFSTORE_HOME}/lib/antlr-2.7.5.jar:${RDFSTORE_HOME}/lib/openrdf-sesame-2.2.3-onejar.jar:${RDFSTORE_HOME}/lib/parser.jar:${RDFSTORE_HOME}/lib/log4j.jar:${RDFSTORE_HOME}/lib/db2jcc4.jar:${RDFSTORE_HOME}/lib/rdfstore.jar

java -classpath ${CLASSPATH}:${RDF_CLASSPATH} com.ibm.research.rdf.store.utilities.DataLoader "$@"