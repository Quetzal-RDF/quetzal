library(GOSemSim)
library(XML)

parseInput <- function(str) {
  # parse a dataframe from XML
  data <- xmlParse(str, asText=TRUE)
  drug_cat = xmlSApply(xmlRoot(data), function(x) xmlSApply(x, xmlValue))
  free(data)
  d <- data.frame(t(drug_cat),row.names=NULL)  
}


# Compute similarity of SMILES fingerprints
computeFPPerPair <- function(d1, d2, d) {
  smiles1 <- as.character(d1[, "smiles"])
  smiles2 <- as.character(d2[,"smiles"])
  
  assert("length should be 1 for a chemical fp of a drug", length(smiles1) == 1)
  assert("length should be 1 for a chemical fp of a drug", length(smiles2) == 1) 
  sim = RxnSim::ms.compute (smiles1, smiles2, fp.depth=6, fpCached=T, sim.method =  tanimoto)
}

# Compute similarity of GO functions
computeGOSimilarityPerPair <- function(d1, d2, d) {
  go1 <- as.character(d1[, "GO"])
  go2 <- as.character(d2[,"GO"])
  sim = mgoSim(go1, go2, ont="MF", measure="Wang", combine="BMA")
}

computeOverSimilarDrugPairs <- function(str, f) {
  # used to test the function locally
  # str <- read_file("/tmp/postedData.xml")
  d <- parseInput(str) 
  
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
    sim = f(d1, d2, d)
    xml$addTag("sim", sim)
    xml$closeTag()
  }
  xml$closeTag()
  xml$closeTag()
  ret <- saveXML(xml)
}

computeChemicalFingerprintSimilarity <- function(str) {
  computeOverSimilarDrugPairs(str, computeFPPerPair)
}

computeGOSimilarity <- function(funcData) {
  # str
  library(GOSemSim)
  library(XML)
  computeOverSimilarDrugPairs(funcData, computeGOSimilarityPerPair)
}

#str <- read_file("/tmp/postedData.xml")
#print(computeGOSimilarity(str))
#str <- read_file("/tmp/SMILES")
#print(computeChemicalFingerprintSimilarity(str))
#print(parseInput(str))

