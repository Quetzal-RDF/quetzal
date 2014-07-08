#!/bin/bash
# replace with file to read
NUM=$1
FILE=$2

if [[$FILE =~ .*bz2]]
bzcat | shuf -n ${NUM} | awk '{print $1 > "/tmp/subject"; 
for (i = 3; i <= NF; i++) {printf("%s", $i) > "/tmp/object";} printf("%s\n", "") > "/tmp/object";}'  
else
cat | shuf -n ${NUM} | awk '{print $1 > "/tmp/subject"; 
for (i = 3; i <= NF; i++) {printf("%s ", $i) > "/tmp/object";} printf("%s\n", "") > "/tmp/object";}'  
fi

fgrep -f "/tmp/subject" $FILE | sort | uniq | statistics.awk
fgrep -f "/tmp/object" $FILE |awk '{for (i = 3; i <= NF; i++) {printf("%s ", $i);} printf("%s", "|"); print $2 "|" $1;}' | sort -t "|" | uniq | statistics.awk 

cut -f2 -d " " $FILE | awk '{predicate[$0]=1;} END {for f in predicate) {print f}}' > /tmp/predicates

# we need to call java with the set of predicates and the set of statistics
# so we can compute the 'optimal schema' and output the .ddl, and the RDF 
# store parameters


 