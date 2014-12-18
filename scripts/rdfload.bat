@ECHO OFF
IF NOT DEFINED %RDFSTORE_HOME% THEN GOTO HOME_NOT_DEFINED

SET RDF_CLASSPATH=%RDFSTORE_HOME%\lib\antlr-2.7.5.jar;%RDFSTORE_HOME%\lib\openrdf-sesame-2.2.3-onejar.jar;%RDFSTORE_HOME%\lib\parser.jar;%RDFSTORE_HOME%\lib\log4j.jar;%RDFSTORE_HOME%\lib\db2jcc4.jar;%RDFSTORE_HOME%\lib\rdfstore.jar
SET CLASSPATH=%CLASSPATH%;%RDF_CLASSPATH%

java com.ibm.research.rdf.store.utilities.DataLoader %*

GOTO END

:HOME_NOT_DEFINED
	echo Variable RDFSTORE_HOME is not defined. Please set it and try again.
	GOTO END

:END