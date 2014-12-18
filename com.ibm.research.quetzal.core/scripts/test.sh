#!/bin/bash

reverse_count=127;

i=0;

reverse_str='';
prop_str=' prop';
entry_str='entry';
delim_str=',';
group_str_o='(';
group_str_c=')';



while [[ $i -lt $reverse_count ]]
	do
		reverse_str=$reverse_str$group_str_o$entry_str$delim_str$prop_str$i$group_str_c$delim_str;
		i=$((i+1));
    done
reverse_str=$reverse_str$group_str_o$entry_str$delim_str$prop_str$i$group_str_c;


echo "db2 RUNSTATS ON TABLE $SCHEMA_NAME.$STORE_NAME"_RPH" ON ALL COLUMNS WITH DISTRIBUTION ON COLUMNS ( $reverse_str ) DEFAULT NUM_FREQVALUES $FREQ NUM_QUANTILES $QUANTILES AND DETAILED INDEXES ALL ALLOW WRITE ACCESS ";

