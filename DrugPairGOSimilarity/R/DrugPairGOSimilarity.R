library(GOSemSim)
library(readr)
require(XML)

computeSimilarDrugPairs <- function(str) {
  # used to test the function locally
  # str <- read_file("/tmp/postedData.xml")
  
  # parse a dataframe from XML
  data <- xmlParse(str)
  drug_cat = xmlSApply(xmlRoot(data), function(x) xmlSApply(x, xmlValue))
  free(data)
  drug_df <- data.frame(t(drug_cat),row.names=NULL)
  
  # select just the drug column to compute all drug pairs
  m <- unique(subset(d, select = c("drug")))
  z <- merge(x = m, y = m, by = NULL)
  z <- z[z$drug.x!=z$drug.y,]
  
  # for each row containing a drug pair
  # fetch the go annotations for each drug, and issue the query to GOSemSim
  xml <- xmlTree()
  xml$addTag("document", close=FALSE)
  xml$addTag("data", close=FALSE)
  
  for(i in 1:dim(z)[1])  
  {
    xml$addTag("row", close=FALSE)
    drug1 <- z[i,1]
    drug2 <- z[i,2]
    xml$addTag("drug1", drug1)
    xml$addTag("drug2", drug2)
    d1 = d[d$drug==drug1,]
    d2 = d[d$drug==drug2,]
    go1 <- as.character(d1[, "GO"])
    go2 <- as.character(d2[,"GO"])
    sim = mgoSim(go1, go2, ont="MF", measure="Wang", combine="BMA")
    xml$addTag("sim", sim)
    xml$closeTag()
  }
  xml$closeTag()
  xml$closeTag()
  ret <- saveXML(xml)
}