import rpy2.robjects as robjects

#require XML package installed in R (install.packages('XML'))

rUtils = ('options(warn=-1)',
					'options(verbose=F)',
					'options(stringsAsFactors=F)',
					'library(stringi)',
					'library(XML)', 
					'''autoTypeConversion <- function(df)
							{
							  for(i in 1:length(df))
							  {
							    tmp_col = df[[i]][!is.na(df[[i]])]
							    if(length(tmp_col) > 0)
							    {
							      #try numeric
							      num_col = try(suppressWarnings(as.numeric(as.character(tmp_col))), silent=T)
							      if(class(num_col) != "try-error" && all(!is.na(num_col)))
							      { #numeric column
							        df[[i]] = try(suppressWarnings(as.numeric(as.character(df[[i]]))), silent=T)
							      }
							      else
							      { #try datetime
							        dt_col = try(suppressWarnings(as.POSIXct(as.character(tmp_col))), silent=T)
							        if(class(dt_col) != "try-error" && all(!is.na(dt_col)))
							        { #datatime column
							          df[[i]] = try(suppressWarnings(as.POSIXct(as.character(df[[i]]))), silent=T)
							        }
							      }
							    }
							  }
							  df
							}''',
					'''convertDataFrameToXML <- function(df,name)
													{
													  xml <- xmlNode(name, namespaceDefinitions = c("s" = "http://www.w3.org/2005/sparql-results#"))
													  for (i in 1:nrow(df)) {
													    t0 <- xmlNode("s:result")
													    for (j in names(df)) {
													      t0 <- addChildren(t0, xmlNode("s:binding", attrs=c("name" = j), df[i,j]))
													    }
													    xml <- addChildren(xml, t0)
													  }
													  return(toString(xml))
													}''', 
					'''convertDataFrameToXMLwithType <- function(df,name)
													{
													  xml <- xmlNode(name, namespaceDefinitions = c("s" = "http://www.w3.org/2005/sparql-results#", "xsi"="http://www.w3.org/2001/XMLSchema-instance", "xs"="http://www.w3.org/2001/XMLSchema"))
													  for (i in 1:nrow(df)) {
													    t0 <- xmlNode("s:result")
													    for (j in names(df)) {
													      t0 <- addChildren(t0, xmlNode("s:binding", attrs=c("name" = j, "xsi:type" = get_xml_datatype(df[i,j])), df[i,j]))
													    }
													    xml <- addChildren(xml, t0)
													  }
													  return(toString(xml))
													}''', 
					'''get_xml_datatype <- function(val)
													{
													  ret = 'xs:unknown'
													  tp = typeof(val)
													  tc = class(val)
													  if(length(val) > 1)
													  {
													    ret = 'xs:unknownArray'
													  }
													  if(tc == 'Date')
													  {
													    ret = 'xs:date'
													  }
													  else if('POSIXct' %in% tc || 'POSIXt' %in% tc)
													  {
													    ret = 'xs:dateTime'
													  }
													  else if(tp == 'numeric' || tp == 'double')
													  {
													    ret = 'xs:decimal'
													  }
													  else if(tp == 'integer')
													  {
													    ret = 'xs:integer'
													  }
													  else if(tp == 'character')
													  {
													    ret = 'xs:string'
													  }
													  return(ret)
													}''', 
					'''parseXML <- function(inData, root="data")
						{
						  doc=parseXMLAndAdd(inData, top=root)
						  df=xmlToDataFrame(nodes=getNodeSet(doc,paste("/",root,"/row",sep="")), stringsAsFactors=F)
						  df
						}''',
					'''parseXMLDoc <- function(inDoc, root="data")
						{
						  doc = xmlParse(inDoc, asText = T)
						  df=xmlToDataFrame(nodes=getNodeSet(doc,paste("/",root,"/row",sep="")), stringsAsFactors=F)
						  df
						}''')

def initR():
	#robjects.r('library(XML)')
	#robjects.r('convertDataFrameToXML <- function(df,name){ xml <- xmlNode(name); for (i in 1:nrow(df)) { t0 <- xmlNode("row"); for (j in names(df)) { t0 <- addChildren(t0, xmlNode(j, df[i,j])) }; xml <- addChildren(xml, t0); }; return(toString(xml)) }')
	#robjects.r('parseXML <- function(inData, root="data"){doc=parseXMLAndAdd(inData, top=root); df=xmlToDataFrame(nodes=getNodeSet(doc,paste("/",root,"/row",sep="")), stringsAsFactors=F); df}')
#	robjects.r['options'](warn=-1)
	for i in rUtils:
		robjects.r(i)
	
def registerRFunction(func):
	robjects.r(func)
	
def callRFunction(func, inData):
	print('got here 0')
	robjects.r('`__input` <- autoTypeConversion(parseXML("'+inData+'"))')
	print('got here 1')
	ret = robjects.r('convertDataFrameToXMLwithType('+func+'(__input)'+', "data")')
	print('got here 2')
	return ret
	#print(robjects.r('convertDataFrameToXMLwithType('+func+'()'+', "data")'))

def callRFunctionWithXMLDoc(func, inData):
	print('in call R function with xml doc')
	robjects.r("`__input` <- autoTypeConversion(parseXMLDoc('"+inData+"'))")
	print('parsed XML doc')
	ret = robjects.r('convertDataFrameToXMLwithType('+func+'(`__input`)'+', "data")')
	print('converted data frame back to xml')
	return ret
	#print(robjects.r('convertDataFrameToXMLwithType('+func+'()'+', "data")'))

def callRFunction1(func, inData):
	robjects.r('`__input` <- parseXML("'+inData+'")')
	ret = robjects.r(func+'()')
	return ret
	
def printRFuncCall(func):
	print(robjects.r(func))
	
