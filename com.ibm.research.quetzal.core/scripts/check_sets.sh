#!/bin/bash

SETS=$1
DATA=$2
ENTITY=$3

(while read line; do
    sh ~/check_line.sh "$line" $DATA $ENTITY
done) < $SETS

