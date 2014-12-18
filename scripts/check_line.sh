#!/bin/bash

LINE=$1
DATA=$2
ENTITY=$3

SIZE=`echo $LINE | awk '{ print $1; }'`

rm -f /tmp/names_file.* /tmp/matches.txt

PREDS=`echo $LINE | sed 's/[0-9]*[ ]*|/ /g'`

i=0
for p in $PREDS; do
    awk -f ~/find_by_pred.awk -v entity=$ENTITY -v pred=$p $DATA | sort | uniq > /tmp/names_file.$i
    i=`expr $i + 1`
done

NAMES_FILES=`ls /tmp/names_file.* | awk '{ printf("%s ", $1); }'`

awk -f ~/find_by_names.awk -v entity=$ENTITY -v names_files="$NAMES_FILES" $DATA | sort | uniq | awk '{ $1=""; print $0; }' | sort | uniq -c | egrep "^[[:blank:]]*$i[[:blank:]]" > /tmp/matches.txt

if [[ `cat /tmp/matches.txt | wc -l` = $SIZE ]]; then
    echo $LINE matches
else
    echo $LINE does NOT match
fi
