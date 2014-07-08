drop function RDF_REGEX;
create function RDF_REGEX(input varchar(2048), pattern varchar(2048), flags varchar(256)) 
	returns integer 
	external name 'com.ibm.rdf.store.sparql11.sqlwriter.udf.RDFUtil.regex' 
	language java parameter style java ;
/

/*
drop function RDF_DATATYPE;
create function RDF_DATATYPE(s varchar(2048)) 
	returns VARCHAR(2048)
	external name 'com.ibm.rdf.store.sparql11.sqlwriter.udf.RDFUtil.datatype' 
	language java parameter style java ;
/

drop function RDF_ISIRI;
create function RDF_ISIRI(s varchar(2048)) 
	returns integer 
	external name 'com.ibm.rdf.store.sparql11.sqlwriter.udf.RDFUtil.isIRI' 
	language java parameter style java ;
/

drop function RDF_ISURI;
create function RDF_ISURI(s varchar(2048)) 
	returns integer 
	external name 'com.ibm.rdf.store.sparql11.sqlwriter.udf.RDFUtil.isURI' 
	language java parameter style java ;
/

drop function RDF_ISLITERAl;
create function RDF_ISLITERAL(s varchar(2048)) 
	returns integer 
	external name 'com.ibm.rdf.store.sparql11.sqlwriter.udf.RDFUtil.isSameLang' 
	language java parameter style java ;
/
*/
