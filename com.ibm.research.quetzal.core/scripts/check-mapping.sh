#!/bin/bash

DB=$1
KB=$2
BASE=$3

function check() {
  table=$1
  hashes_file=$2

  max_column=`awk 'BEGIN {max=0;} { for(i = 2; i <= NF; i++) { if ($i > max) { max=$i; } } } END { print max; }' $hashes_file`

  i=1
  sql="select 0 as col, prop0 as prop from $table"
  while [[ $i -le $max_column ]]; do
    sql="$sql union select $i as col, prop$i as prop from $table"
    i=`expr $i + 1`
  done
  sql="select count(col) as count, prop from ($sql) where prop is not null group by prop"

  db2 <<EOF
connect to $DB
$sql
EOF
}

check ${KB}_dph ${BASE}.sorted_subj.edge_sets1_3.hashes
check ${KB}_rph ${BASE}.sorted_obj.edge_sets1_3.hashes
