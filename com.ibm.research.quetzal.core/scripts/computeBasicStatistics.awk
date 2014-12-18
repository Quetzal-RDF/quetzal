#
# Computes the statistics for 
# 1.  entity-predicate pairs
# 2.  entity-predicate-value pairs 
# 3.  values
# Statistics computed: average, min, max, std, frequency distribution
# Usage: 
# To compute statistics per subject type:
# sort <ntFile> | awk -f prettifyNTriples.awk | awk -f computeBasicStatistics.awk -v entity="subject"
#
# To compute statistics per object type:
# awk -f invertObjectsInTriples.awk <ntFile> | sort | awk -f computeBasicStatistics.awk -v entity="object"
#

function computeStatistics(triplesPerEntity, counter) {
    counter = 0;
    for (i in triplesPerEntity) {
	computeRunningStatistics(triplesPerEntity[i], statPerEntityPredicate);
	counter = counter + triplesPerEntity[i];
    }
    computeRunningStatistics(counter, statPerEntity);
}

function increment(arr, key) {
    if (key in arr) {
	arr[key] = arr[key] + 1;
    } else {
	arr[key] = 1;
    }
}
function arrlength(arr, count) {
    count = 0;
    for (i in arr) {
	count++;
    }
    return count;
}

function writeJSONObject(candidate, arr, writeTripleCount) {
    # We assume that arr has a set of name value pairs contained
    # by the object.  All values are assumed to be scalars
    print("{");
    if (writeTripleCount == "true") {
	print "\"tripleCount\":\"" tripleCount "\",";
    }

    print "\"statName\" : \"" candidate "\","; 
    print "\"" AVG "\" :\""  globalStatistics[candidate AVG] "\",";
    print "\"" STD "\" :\"" sqrt(globalStatistics[candidate SS]/globalStatistics[candidate SAMPLE_SIZE]) "\",";
    print "\"" MIN "\" :\"" globalStatistics[candidate MIN] "\",";
    print "\"" MAX "\" :\"" globalStatistics[candidate MAX] "\",";
   
    print "\"" HISTOGRAM "\":";
    print("{");
    count = arrlength(arr);
    k = 0;
    for (i in arr) {
	k++;
	s = "\"" i "\":\"" arr[i] "\"";
	printf("%s", s);
	if (k < count) {
	    printf("%s,", "");
	} else {
	    printf("%s\n", "");
	}
    }
    print("}");
    print("}");
}


function updateStatistics(candidate, count, hist) {
    sampleSize = candidate SAMPLE_SIZE;
    average = candidate AVG;
    sumOfSquares = candidate SS;
    min = candidate MIN;
    max = candidate MAX;
    increment(globalStatistics, sampleSize);
    globalStatistics[average] = globalStatistics[average] + ((count - globalStatistics[average]) / globalStatistics[sampleSize]);
    globalStatistics[sumOfSquares] = globalStatistics[sumOfSquares] + ((count - globalStatistics[average]) * (count - globalStatistics[average]));
    if (count > globalStatistics[max]) {
	globalStatistics[max] = count;
    } 
    if (count < globalStatistics[min] || globalStatistics[min] == -1) {
	globalStatistics[min] = count;
    }
    increment(hist, count);
}

function computeRunningStatistics(count, statType) {
    if (statType == statPerEntity) {
	updateStatistics(PREDICATES_PER_ENTITY, count, predicatesPerEntityHist);
    } else if (statType == statPerEntityPredicate) {
	updateStatistics(VALUES_PER_ENTITY, count, valuesPerEntityPredicateHist);
    } else if (statType == statString) {
	updateStatistics(STRING, count, stringHist);
    }
}

function computeStringStatistics(str) {
    if (maxStringLength < length(str)) {
	maxStringLength = length(str);
    }
    computeRunningStatistics(length(str), statString);
}

BEGIN {
   # declare a boolean effectively
   statPerEntity = 1;
   statPerEntityPredicate = 2;
   statString = 3;
   # this is a bit ugly in some ways since awk does not have 
   # data structures. 
   delete globalStatistics;
   # compute a running average
   PREDICATES_PER_ENTITY = "predicatesPer" entity;
   VALUES_PER_ENTITY = "valuesPer" entity "Predicate";
   STRING = "string";
   # statistics
   AVG = "arithmeticMean";
   SS = "sumsOfSquares";
   SAMPLE_SIZE = "sampleSize";
   STD = "standardDeviation";
   MIN = "minValue";
   MAX = "maxValue";
   HISTOGRAM = "histogram";

   globalStatistics[PREDICATES_PER_ENTITY AVG] = 0;
   globalStatistics[VALUES_PER_ENTITY AVG] = 0;
   globalStatistics[STRING AVG] = 0;

   globalStatistics[PREDICATES_PER_ENTITY SAMPLE_SIZE] = 0;
   globalStatistics[VALUES_PER_ENTITY SAMPLE_SIZE] = 0;
   globalStatistics[STRING SAMPLE_SIZE] = 0;

   globalStatistics[PREDICATES_PER_ENTITY SS] = 0;
   globalStatistics[VALUES_PER_ENTITY SS] = 0;
   globalStatistics[STRING SS] = 0;

   globalStatistics[PREDICATES_PER_ENTITY MAX] = 0;
   globalStatistics[VALUES_PER_ENTITY MAX] = 0;
   globalStatistics[STRING MAX] = 0;

   globalStatistics[PREDICATES_PER_ENTITY MIN] = -1;
   globalStatistics[VALUES_PER_ENTITY MIN] = -1;
   globalStatistics[STRING MIN] = -1;

   # histogram of counts and their frequencies
   delete predicatesPerEntityHist;
   delete valuesPerEntityPredicateHist;
   delete stringHist;

   maxStringLength = 0;
   tripleCount = 0;
   prevEntity = "";
   prevPredicate = "";
   # this data structure keeps track of all triples for a given entity
   delete triplesPerEntity;
 
   # note because of literals, the assumption in the code is that
   # "||" is used as a delimiter
   FS = "\\|\\|";
}

{
    tripleCount++;
    computeStringStatistics($1);
    computeStringStatistics($2);
    computeStringStatistics($3);
    # note this code assumes that $1 is always the entity of interest
    # switch fields around if this is not the case.
    if ($1 != prevEntity) {
	if (prevEntity != "") {
	    computeStatistics(triplesPerEntity);
	}
	delete triplesPerEntity;
	prevEntity = $1;
    }
    if ($2 in triplesPerEntity) {
	triplesPerEntity[$2] = triplesPerEntity[$2] + 1;
    } else {
	triplesPerEntity[$2] = 1;
    }
}

END {
    computeStatistics(triplesPerEntity);
    writeJSONObject(PREDICATES_PER_ENTITY, predicatesPerEntityHist, "true");
    writeJSONObject(VALUES_PER_ENTITY, valuesPerEntityHist, "false");
    writeJSONObject(STRING, stringHist, "false");
}