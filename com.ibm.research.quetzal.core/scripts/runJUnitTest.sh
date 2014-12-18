#!/bin/bash

export JAVA_HOME=/rdf_stores/db2/V10.1/java/jdk64
export PATH=$JAVA_HOME/bin:$PATH

if [[ $1 == "SESAME" ]]; then
	export CLASSPATH=./sesametest.jar:./lib/junit-4.10.jar:./lib/wala.jar:./lib/hash.jar:./lib/antlr-3.3-complete.jar:./lib/jena-2.6.3-patched.jar:./lib/slf4j-api-1.5.8.jar:./lib/slf4j-simple-1.5.8.jar:./lib/xercesImpl-2.7.1.jar:./lib/arq-2.8.5-patched.jar:./lib/iri-0.8.jar:./lib/icu4j-3.4.4.jar:./lib/commons-logging-1-0-3.jar:./lib/db2jcc4.jar:./lib/pdq.jar:./lib/openrdf-sesame-2.6.7-onejar.jar
	java -Xmx32G org.junit.runner.JUnitCore com.ibm.research.rdf.store.sesame.SesameSP2BTest
elif [[ $1 == "RDF3X" ]]; then
	export CLASSPATH=/rdf_stores/rdf3x-0.3.7/rdf3xdriver.jar:./rdf3xtest.jar:./lib/junit-4.10.jar:./lib/wala.jar:./lib/hash.jar:./lib/antlr-3.3-complete.jar:./lib/jena-2.6.3-patched.jar:./lib/slf4j-api-1.5.8.jar:./lib/slf4j-simple-1.5.8.jar:./lib/xercesImpl-2.7.1.jar:./lib/arq-2.8.5-patched.jar:./lib/iri-0.8.jar:./lib/icu4j-3.4.4.jar:./lib/commons-logging-1-0-3.jar:./lib/db2jcc4.jar:./lib/pdq.jar:./lib/openrdf-sesame-2.6.6-onejar.jar
	java -Xmx32G -Djava.library.path=/rdf_stores/rdf3x-0.3.7/ org.junit.runner.JUnitCore com.ibm.rdf3x.RDF3XSP2BTest
elif [[ $1 == "JENA" ]]; then
	export TDBROOT=/rdf_stores/TDB-0.8.10
	export PATH=$TDBROOT/bin:$PATH
	export CLASSPATH=./jenatest.jar:/rdf_stores/TDB-0.8.10/lib/arq-2.8.8.jar:/rdf_stores/TDB-0.8.10/lib/arq-2.8.8-tests.jar:/rdf_stores/TDB-0.8.10/lib/icu4j-3.4.4.jar:/rdf_stores/TDB-0.8.10/lib/iri-0.8.jar:/rdf_stores/TDB-0.8.10/lib/jena-2.6.4.jar:/rdf_stores/TDB-0.8.10/lib/jena-2.6.4-tests.jar:/rdf_stores/TDB-0.8.10/lib/lucene-core-2.3.1.jar:/rdf_stores/TDB-0.8.10/lib/tdb-0.8.10.jar:/rdf_stores/TDB-0.8.10/lib/tdb-0.8.10-tests.jar:./lib/junit-4.10.jar:./lib/wala.jar:./lib/hash.jar:./lib/antlr-3.3-complete.jar:./lib/jena-2.6.3-patched.jar:./lib/slf4j-api-1.5.8.jar:./lib/slf4j-simple-1.5.8.jar:./lib/xercesImpl-2.7.1.jar:./lib/iri-0.8.jar:./lib/icu4j-3.4.4.jar:./lib/commons-logging-1-0-3.jar:./lib/db2jcc4.jar:./lib/pdq.jar:./lib/openrdf-sesame-2.6.6-onejar.jar
	java -Xmx32G org.junit.runner.JUnitCore com.ibm.research.rdf.store.jena.JenaSP2BTest
elif [[ $1 == "VIRTUOSO" ]]; then
	export CLASSPATH=./virtuosotest.jar:/rdf_stores/virtuoso-opensource-6.1.5/lib/jena/virt_jena.jar:/rdf_stores/virtuoso-opensource-6.1.5/lib/jdbc-3.0/virtjdbc3.jar:./lib/junit-4.10.jar:./lib/wala.jar:./lib/hash.jar:./lib/antlr-3.3-complete.jar:./lib/jena-2.6.3-patched.jar:./lib/slf4j-api-1.5.8.jar:./lib/slf4j-simple-1.5.8.jar:./lib/xercesImpl-2.7.1.jar:./lib/arq-2.8.5-patched.jar:./lib/iri-0.8.jar:./lib/icu4j-3.4.4.jar:./lib/commons-logging-1-0-3.jar:./lib/db2jcc4.jar:./lib/pdq.jar:./lib/openrdf-sesame-2.6.6-onejar.jar
	java -Xmx32G org.junit.runner.JUnitCore com.ibm.research.rdf.store.virtuoso.VirtuosoSP2BTest
fi
