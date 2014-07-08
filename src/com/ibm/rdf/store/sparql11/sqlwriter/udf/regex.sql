CREATE FUNCTION %s_RDF_REGEX(input varchar(2048), pattern varchar(2048), flags varchar(256)) 
	returns integer 
	external name '%JAR_ID%db2rdf.RDFUtil.regex' 
	language java parameter style java 