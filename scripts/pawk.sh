#!/bin/bash

DIR=`dirname $0`

AWK_FILE=`basename $0 sh`awk

DEGREE=$1
CHUNKS=$2
SPLIT=$3
FILE=$4

shift; shift; shift; shift

SIZE=`ls -l $FILE | awk '{ print $5; }'`
CHUNK=`expr '(' $SIZE + $CHUNKS ')' / $CHUNKS`

i=$SIZE
j=0
(while expr $i '>' 0 > /dev/null; do
  i=`expr $i - $CHUNK`
  if expr $i '<' 0 > /dev/null; then
      echo cat $FILE "|" gawk -v splitFor=$SPLIT -v pawk=yes -v last=yes -v part=$j -v chunk=`expr $i + $CHUNK` -f $DIR/strings.awk -f $DIR/parse.awk -f $DIR/$AWK_FILE "$@"
  else
      echo tail --bytes=$i $FILE "|" gawk -v splitFor=$SPLIT -v pawk=yes -v part=$j -v chunk=$CHUNK -f $DIR/strings.awk -f $DIR/parse.awk -f $DIR/$AWK_FILE "$@" 
  fi
  j=`expr $j + 1`
done) | xargs -d '\n' -n 1 -P $DEGREE sh -c


