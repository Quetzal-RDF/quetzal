#!/bin/bash
#
#  Script to build load files from a NTriples or NQuads file.  Following 
# any optional arguments, The first argument must always be the file 
# type, which is either "nt" for NTriples or "quad" for NQuads.  The 
# second argument is a single input triples or quads file.
#
# The script sorts that file twice to create the two sorted files needed 
# for generating edge sets.  Extra options to speed up sorting may be 
# given after --sort-options
#
DIR=`dirname $0`
. $DIR/load-setup.sh

if [[ $IS_CYGWIN == 1 ]]; then
    NT_FILE_CYG=`cygpath --path --unix $1`
    LOAD_DIR_CYG=`dirname $NT_FILE_CYG`
fi


if [[ $DB_ENGINE == "postgresql" ]]; then
    . $DB2_CONFIG
fi

if [[ $DB_ENGINE == "shark" ]]; then
    . $DB2_CONFIG
fi

SORTED_SUBJ_NT_FILE=${NT_FILE}.sorted_subj
SORTED_OBJ_NT_FILE=${NT_FILE}.sorted_obj
	
if [[ $IS_CYGWIN == 1 ]]; then
    SORTED_SUBJ_NT_FILE_1=`cygpath --path --unix $SORTED_SUBJ_NT_FILE`
    SORTED_OBJ_NT_FILE_1=`cygpath --path --unix $SORTED_OBJ_NT_FILE`
else
    SORTED_SUBJ_NT_FILE_1=$SORTED_SUBJ_NT_FILE
    SORTED_OBJ_NT_FILE_1=$SORTED_OBJ_NT_FILE
fi

if [[ $PARALLEL != 1 ]]; then
    if [[ $TABLES == "both" ]]; then
	PER_SORT_PARALLEL=`expr $PARALLEL / 2`
    else
	PER_SORT_PARALLEL=$PARALLEL
    fi
else
    PER_SORT_PARALLEL=1
fi

###########################################################################
# build edge sets to be used for creating optimized store layout
#
function process_edge_sets() {

    # setup
    nt_file=$2

    if [[ $IS_CYGWIN == 1 ]]; then
	nt_file_cygw=`cygpath --path --unix $nt_file`
	nt_file_1=$nt_file_cygw
    else
	nt_file_1=$nt_file
    fi

    if [[ $1 == "subject" ]]; then
		direct="true"
    else
		direct="false"
    fi


    # sort data file
    if expr $PER_SORT_PARALLEL '>' 1 > /dev/null; then
	DEGREE=`expr $PER_SORT_PARALLEL '*' $CHUNKS`
	SORT_OPTS="$SORT_OPTIONS --parallel $PER_SORT_PARALLEL"
    else
	SORT_OPTS="$SORT_OPTIONS" 
    fi

    if test $REUSE == 0 -o ! -f $nt_file_1; then
	if [[ $1 == "subject" ]]; then
	    sort $SORT_OPTS --unique $NT_FILE > $nt_file_1
	else
	    sort $SORT_OPTS -k 3 -k 1 --unique $NT_FILE > $nt_file_1
	fi
    fi

    # build edge sets
    GAWK_OPTS="-b -O -v setsFor=$1 -v fileType=$FILE_TYPE -v typeCodeFile=$NT_FILE.types -v typeTable=$nt_file_1.datatype -v localeTable=$nt_file_1.locale -v predicateTypeFile=$nt_file_1.pred_types -v EdgeSetsFile=$nt_file_1.edge_sets"
    if [[ $SCHEMA_EXPERIMENTS == 1 ]]; then
	GAWK_OPTS="$GAWK_OPTS -v multiValuePredicatesFile=$nt_file_1.multi"
    fi

    if test $REUSE == 0 -o ! -f $nt_file_1.edge_sets; then
	bash $DIR/triple-pawk.sh $PER_SORT_PARALLEL $CHUNKS $1 $nt_file_1 $GAWK_OPTS -f $DIR/types.awk -f $DIR/findAllPredOccurrences.awk

	gawk -b -O '{ counts[$3] += $2; sizes[$3] = $1; } END { for (key in counts) { print sizes[key] " " counts[key] " " key; } }' $nt_file_1.edge_sets.[0-9]* | sort $SORT_OPTIONS -k1,1n -k2,2nr > $nt_file_1.edge_sets
    fi

    # assemble other predicate info files
    if test $REUSE == 0 -o ! -f $nt_file_1.multi; then
	if ls $nt_file_1.multi.[0-9]* > /dev/null 2>&1; then
	    sort $SORT_OPTIONS --merge --unique $nt_file_1.multi.[0-9]* > $nt_file_1.multi
	else
	    touch $nt_file_1.multi
	fi
    fi

    if test $REUSE == 0 -o ! -f $nt_file_1.pred_types; then
	cat $nt_file_1.pred_types.[0-9]* | gawk -b -O 'BEGIN { delete pred_types; } { if ($1 in pred_types) { if ($2!=pred_types[$1]) { pred_types[$1] = "mixed"; } } else { pred_types[$1] = $2; } } END { for (p in pred_types) { print p "\t" pred_types[p]; } }' > $nt_file_1.pred_types
    fi

    if test $REUSE == 0 -o ! -f $nt_file_1.1_${HASHES}.hashes; then
	# graph coloring for predicate-to-column assignment
	if [[ -d $WORKLOAD_DIR ]]; then
	    
	    if [[ $CORRELATIONS == 1 ]]; then
		gawk -b -O -f $DIR/conditional-probabilities.awk $nt_file_1.edge_sets > $nt_file_1.correlations
		java $JAVA_OPTS -cp $CLASSPATH com.ibm.research.rdf.store.hashing.FindWorkloadProxies $WORKLOAD_DIR $nt_file_1.correlations > $nt_file_1.proxies
		gawk -b -O '{ print $2; }' $nt_file_1.proxies | sort $SORT_OPTIONS | uniq > $nt_file_1.predicates_to_index		
	    else
		java $JAVA_OPTS -cp $CLASSPATH com.ibm.research.rdf.store.hashing.GetPredicates $WORKLOAD_DIR > $nt_file_1.predicates_to_index
	    fi
	    
	    if [[ -f $SYSTEM_PREDICATES ]]; then
		
		cat $SYSTEM_PREDICATES $nt_file_1.predicates_to_index > $nt_file_1.priority_predicates
		
		java $JAVA_OPTS -cp $CLASSPATH com.ibm.research.rdf.store.hashing.AssignHashesToPredicates $nt_file.edge_sets $HASHES true $direct $nt_file.priority_predicates
		
	    else  
		java $JAVA_OPTS -cp $CLASSPATH com.ibm.research.rdf.store.hashing.AssignHashesToPredicates $nt_file.edge_sets $HASHES true $direct $nt_file.predicates_to_index
		
	    fi
	else
	    if [[ -f $SYSTEM_PREDICATES ]]; then
		java $JAVA_OPTS -cp $CLASSPATH com.ibm.research.rdf.store.hashing.AssignHashesToPredicates $nt_file.edge_sets $HASHES true $direct $SYSTEM_PREDICATES
		
	    else  
		java $JAVA_OPTS -cp $CLASSPATH com.ibm.research.rdf.store.hashing.AssignHashesToPredicates $nt_file.edge_sets $HASHES true $direct
	    fi
	fi    
    fi
}

###########################################################################
# create load files for DB backend
#
function process_load_files() {    
    nt_file=$2

    if [[ $1 == "subject" ]]; then
		table_name="direct"
    else
		table_name="reverse"
    fi

    if [[ $IS_CYGWIN == 1 ]]; then
	
	nt_file_cygw=`cygpath --path --unix $nt_file`
		
	GAWK_OPTS="-v cutoff=$LONG_LENGTH  -v classPath=$ClassPath -v typeCodeFile=$NT_FILE_CYG.types -v setsFor=$1 -v fileType=$FILE_TYPE -v hashFile=$nt_file_cygw.edge_sets1_${HASHES}.hashes"
	
	nt_file_1=$nt_file_cygw
    else
	GAWK_OPTS="-v reversed=$REVERSED -v cutoff=$LONG_LENGTH -v classPath=$ClassPath -v typeCodeFile=$NT_FILE.types -v setsFor=$1 -v fileType=$FILE_TYPE -v hashFile=$nt_file.edge_sets1_${HASHES}.hashes"
	
	nt_file_1=$nt_file
    fi
	
    if [[ $NO_LIDS == 1 ]]; then
		GAWK_OPTS="$GAWK_OPTS -v dontUseLids=yes"
    fi
    if [[ $ENTITY_IN_SECONDARY == 1 ]]; then
		GAWK_OPTS="$GAWK_OPTS -v useEntityInSecondary=yes"
    fi
    if [[ $PROPERTY_IN_SECONDARY == 1 ]]; then
		GAWK_OPTS="$GAWK_OPTS -v usePropertyInSecondary=yes"
    fi

    if [[ $SCHEMA_EXPERIMENTS == 1 ]]; then
	GAWK_OPTS="$GAWK_OPTS -v multiValuePredicatesFile=$nt_file_1.multi"
    fi

    if [[ $REVERSED == 1 ]]; then
	GAWK_OPTS="$GAWK_OPTS -v reverseUris=yes"
    fi

    bash $DIR/triple-pawk.sh $PER_SORT_PARALLEL $CHUNKS $1 $nt_file_1 -b -O $GAWK_OPTS -v priorityPredicatesFile=$nt_file_1.predicates_to_index -v predicateInfoFile=$nt_file_1.pred_info -v longStringFile=$nt_file_1.long_strings  -v primaryFile=$nt_file_1.primary.load -v secondaryFile=$nt_file_1.secondary.load -v dbEngine=$DB_ENGINE -f $DIR/types.awk -f $DIR/long-strings.awk -f $DIR/createLoadFile.awk

    # create predicate info load file
    awk -f $DIR/strings.awk -f $DIR/types.awk -v cutoff=$LONG_LENGTH -f $DIR/long-strings.awk -v longStringFile=$nt_file_1.predicate-info.long_strings -v typeCodeFile=$NT_FILE.types -b -f $DIR/build_predicate_table.awk `ls $nt_file_1.pred_info.[0-9]* $nt_file_1.multi.[0-9]* $nt_file_1.edge_sets1_${HASHES}.hashes $nt_file_1.pred_types.[0-9]* 2>/dev/null` > $LOAD_DIR/${table_name}-predicate-info.load
}

###########################################################################
# wrapper to introduce parallelism
#
process() {
    callee=$1
    if [[ $PARALLEL != 1 && $TABLES == "both" ]]; then
	($callee "object" $SORTED_OBJ_NT_FILE) &
	$callee "subject" $SORTED_SUBJ_NT_FILE
	wait
    else
	if [[ $TABLES == "both" || $TABLES == "reverse" ]]; then
	    $callee "object" $SORTED_OBJ_NT_FILE
	fi
	if [[ $TABLES == "both" || $TABLES == "direct" ]]; then
	    $callee "subject" $SORTED_SUBJ_NT_FILE
	fi
    fi
}

###########################################################################
# top level control
#
if [[ $IS_CYGWIN == 1 ]]; then
	NT_FILE_1=$NT_FILE_CYG
	LOAD_DIR_1=$LOAD_DIR_CYG
else
	NT_FILE_1=$NT_FILE
	LOAD_DIR_1=$LOAD_DIR
fi

# intial pre-defined types file
java $JAVA_OPTS -ea -cp $CLASSPATH com.ibm.research.rdf.store.runtime.service.types.TypeMapForLoader > $NT_FILE_1.types    

# set length for long strings
if [[ $LONG_FRAC -gt 0 ]]; then
    sh $DIR/stringLengthDistribution.sh $FILE_TYPE $NT_FILE_1 > $NT_FILE_1.string_dist
    LONG_LENGTH=`gawk -b -O -v frac=$LONG_FRAC 'BEGIN { done=0; max=0; } $4>frac && done==0 { print $1; done=1; } { max=$1; } END { if (done==0) { print max; } }' $NT_FILE_1.string_dist`
else
    LONG_LENGTH=118
fi
    
# build edge sets
process process_edge_sets

# build type map, including types and locale
if ls $NT_FILE_1.*.datatype.* > /dev/null 2>&1; then
    sort $SORT_OPTIONS --merge --unique $NT_FILE_1.*.datatype.* > $NT_FILE_1.datatype
    DATATYPE_FILE=$NT_FILE_1.datatype
elif [[ -e $NT_FILE_1.datatype ]]; then
    DATATYPE_FILE=$NT_FILE_1.datatype
else
    DATATYPE_FILE=/dev/null
fi

if ls $NT_FILE_1.*.locale.* > /dev/null 2>&1; then
    sort $SORT_OPTIONS --merge --unique $NT_FILE_1.*.locale.* > $NT_FILE_1.locale
    LOCALE_FILE=$NT_FILE_1.locale
elif [[ -e $NT_FILE_1.locale ]]; then
    LOCALE_FILE=$NT_FILE_1.locale
else
    LOCALE_FILE=/dev/null
fi

java $JAVA_OPTS -ea -cp $CLASSPATH com.ibm.research.rdf.store.runtime.service.types.TypeMapForLoader $DATATYPE_FILE $LOCALE_FILE > $NT_FILE.types
    
# build load files
process process_load_files

# single long strings table; set maximum long string length
if ls $NT_FILE_1.*long_strings* > /dev/null 2>&1; then
    sort $SORT_OPTIONS --parallel $PARALLEL --merge --unique $NT_FILE_1.*long_strings* > $NT_FILE_1.long_strings

    rm -f $LOAD_DIR_1/long-strings.load
    ln -sf $NT_FILE_1.long_strings $LOAD_DIR_1/long-strings.load
fi

# assemble predicate mapping data
if [[ -e $SORTED_SUBJ_NT_FILE.edge_sets1_${HASHES}.load ]]; then
  cat $SORTED_SUBJ_NT_FILE.edge_sets1_${HASHES}.load $SORTED_OBJ_NT_FILE.edge_sets1_${HASHES}.load | sort $SORT_OPTIONS > $LOAD_DIR/predicate_mappings.load
else
  touch $LOAD_DIR/predicate_mappings.load
fi

# load file for type/local info
awk -b '{ match($0, /^(.*[^[:blank:]])[[:blank:]]+([0-9]+)$/, a); print substr($0, a[2, "start"], a[2, "length"]) "\t" substr($0, a[1, "start"], a[1, "length"]); }' $NT_FILE_1.types > $LOAD_DIR_1/datatypes.load

# create DB2 command file
function load_parallel_tables() {
    if ls -1 $1.$2.load.[0-9]* > /dev/null 2>&1; then
	LAST=`ls -1 $1.$2.load.[0-9]* | tail -n 1`
	for f in `ls -1 $1.$2.load.[0-9]*`; do
	    if [[ $f = $LAST ]]; then
		MODE="REBUILD"
	    else
		MODE="DEFERRED"
	    fi
	    if [[ $IS_CYGWIN == 1 ]]; then
		loadFile=`cygpath --path --windows $f`
	    else
		loadFile=$f
	    fi

	    LOAD_PATH=`realpath $loadFile`
	
	    if [[ $4 == "postgresql" ]]; then
		cat >> $NT_FILE.db2_cmds <<EOF
\COPY ${DB2_SCHEMA}.${KNOWLEDGE_BASE}_$3 FROM '$LOAD_PATH' WITH (FORMAT CSV, DELIMITER '	', QUOTE '');
EOF
	    elif  [[ $4 == "shark" ]]; then
		cat >> $NT_FILE.db2_cmds <<EOF
LOAD DATA LOCAL INPATH '$LOAD_PATH' INTO TABLE ${DB2_SCHEMA}.${KNOWLEDGE_BASE}_$3  ;
EOF
 	    elif [[ $3 == "DS" ]]; then
		fields="gID list_id elem typ"
		if [[ $ENTITY_IN_SECONDARY == 1 ]]; then
		    fields="$fields ENTITY" 
		fi
		if [[ $PROPERTY_IN_SECONDARY == 1 ]]; then
		    fields="$fields PROP"
		fi
		
		format_str=""
		fields_str=""
		for f in $fields; do
		    if [[ $f == "typ" ]]; then
			field_str="\$$f smallint external"
		    else
			field_str="\$$f character notrim optionally enclosed by X'07'"
		    fi
		    if [[ $format_str != "" ]]; then
			format_str="$format_str, $field_str"
			fields_str="$fields_str, $f"
		    else
			format_str=$field_str
			fields_str=$f
		    fi
		done
		
		cat >> $NT_FILE.db2_cmds <<EOF
INGEST DATA FROM FILE $loadFile FORMAT DELIMITED BY X'09'($format_str) RESTART OFF INSERT INTO ${KNOWLEDGE_BASE}_$3($fields_str)
EOF
	    else
		cat >> $NT_FILE.db2_cmds <<EOF
LOAD FROM $loadFile OF DEL MODIFIED BY fastparse keepblanks chardel0x07 coldel0x09 INSERT INTO ${KNOWLEDGE_BASE}_$3 NONRECOVERABLE INDEXING MODE $MODE
EOF
		fi
	done
	if  [[ $4 == "shark" ]]; then
	    cat >> $NT_FILE.db2_cmds <<EOF
CACHE TABLE ${KNOWLEDGE_BASE}_$3 ;
EOF
	fi
    fi
}
    
function loadIntoDB2() {
    rm -f $NT_FILE.db2_cmds

    REORG=0
    if [[ $NO_LIDS == 1 ]];  then
	REORG=1
	cat >> $NT_FILE.db2_cmds <<EOF
alter table ${KNOWLEDGE_BASE}_DS drop column LIST_ID cascade
alter table ${KNOWLEDGE_BASE}_RS drop column LIST_ID cascade
EOF
    fi

    if [[ $REORG == 1 ]]; then
	cat >> $NT_FILE.db2_cmds <<EOF
reorg table ${KNOWLEDGE_BASE}_DS
reorg table ${KNOWLEDGE_BASE}_RS
EOF
    fi

    load_parallel_tables $SORTED_SUBJ_NT_FILE_1 "primary" "DPH" "db2"
    load_parallel_tables $SORTED_SUBJ_NT_FILE_1 "secondary" "DS" "db2"
    load_parallel_tables $SORTED_OBJ_NT_FILE_1 "primary" "RPH" "db2"
    load_parallel_tables $SORTED_OBJ_NT_FILE_1 "secondary" "RS" "db2"
    
    if [[ -e $LOAD_DIR/long-strings.load ]]; then
	cat >> $NT_FILE.db2_cmds <<EOF
LOAD FROM $LOAD_DIR/long-strings.load OF DEL MODIFIED BY fastparse keepblanks nochardel coldel0x09 INSERT INTO ${KNOWLEDGE_BASE}_LSTR NONRECOVERABLE INDEXING MODE REBUILD
EOF
    fi

    cat >> $NT_FILE.db2_cmds <<EOF
LOAD FROM $LOAD_DIR/datatypes.load OF DEL MODIFIED BY fastparse keepblanks nochardel coldel0x09 INSERT INTO ${KNOWLEDGE_BASE}_DT NONRECOVERABLE INDEXING MODE REBUILD
EOF

    cat >> $NT_FILE.db2_cmds <<EOF
LOAD FROM $LOAD_DIR/direct-predicate-info.load OF DEL MODIFIED BY fastparse keepblanks nochardel coldel0x09 INSERT INTO ${KNOWLEDGE_BASE}_DIRECT_PREDS NONRECOVERABLE INDEXING MODE REBUILD	
EOF

    cat >> $NT_FILE.db2_cmds <<EOF
LOAD FROM $LOAD_DIR/reverse-predicate-info.load OF DEL MODIFIED BY fastparse keepblanks nochardel coldel0x09 INSERT INTO ${KNOWLEDGE_BASE}_REVERSE_PREDS NONRECOVERABLE INDEXING MODE REBUILD	
EOF
}


function loadIntoPostgresql() {
    rm -f $NT_FILE.db2_cmds

    load_parallel_tables $SORTED_SUBJ_NT_FILE_1 "primary" "DPH" "postgresql"
    load_parallel_tables $SORTED_SUBJ_NT_FILE_1 "secondary" "DS" "postgresql"
    load_parallel_tables $SORTED_OBJ_NT_FILE_1 "primary" "RPH" "postgresql"
    load_parallel_tables $SORTED_OBJ_NT_FILE_1 "secondary" "RS" "postgresql"

    
    if [[ -e $LOAD_DIR/long-strings.load ]]; then
	LOAD_PATH=`realpath $LOAD_DIR/long-strings.load`
	cat >> $NT_FILE.db2_cmds <<EOF
\COPY ${DB2_SCHEMA}.${KNOWLEDGE_BASE}_LSTR FROM '$LOAD_PATH' WITH (FORMAT CSV, DELIMITER '	', QUOTE '');
EOF
    fi

    LOAD_PATH=`realpath $LOAD_DIR/datatypes.load`

    cat >> $NT_FILE.db2_cmds <<EOF

\COPY ${DB2_SCHEMA}.${KNOWLEDGE_BASE}_DT FROM '$LOAD_PATH' WITH (FORMAT CSV, DELIMITER '	', QUOTE '');
EOF

    LOAD_PATH=`realpath $LOAD_DIR/direct-predicate-info.load`

    cat >> $NT_FILE.db2_cmds <<EOF
\COPY ${DB2_SCHEMA}.${KNOWLEDGE_BASE}_DIRECT_PREDS FROM '$LOAD_PATH' WITH (FORMAT CSV, DELIMITER '	', QUOTE '');
EOF

    LOAD_PATH=`realpath $LOAD_DIR/reverse-predicate-info.load`

    cat >> $NT_FILE.db2_cmds <<EOF
\COPY ${DB2_SCHEMA}.${KNOWLEDGE_BASE}_REVERSE_PREDS FROM '$LOAD_PATH' WITH (FORMAT CSV, DELIMITER '	', QUOTE '');
EOF
}

function loadIntoShark() {
    rm -f $NT_FILE.db2_cmds

    load_parallel_tables $SORTED_SUBJ_NT_FILE_1 "primary" "DPH" "shark"
    load_parallel_tables $SORTED_SUBJ_NT_FILE_1 "secondary" "DS" "shark"
    load_parallel_tables $SORTED_OBJ_NT_FILE_1 "primary" "RPH" "shark"
    load_parallel_tables $SORTED_OBJ_NT_FILE_1 "secondary" "RS" "shark"

    
    if [[ -e $LOAD_DIR/long-strings.load ]]; then
	LOAD_PATH=`realpath $LOAD_DIR/long-strings.load`
	cat >> $NT_FILE.db2_cmds <<EOF
LOAD DATA LOCAL INPATH '$LOAD_PATH' INTO TABLE ${DB2_SCHEMA}.${KNOWLEDGE_BASE}_LSTR  ;
EOF
    fi

    LOAD_PATH=`realpath $LOAD_DIR/datatypes.load`

    cat >> $NT_FILE.db2_cmds <<EOF

LOAD DATA LOCAL INPATH '$LOAD_PATH' INTO TABLE  ${DB2_SCHEMA}.${KNOWLEDGE_BASE}_DT ;
EOF

    LOAD_PATH=`realpath $LOAD_DIR/direct-predicate-info.load`

    cat >> $NT_FILE.db2_cmds <<EOF
LOAD DATA LOCAL INPATH '$LOAD_PATH' INTO TABLE ${DB2_SCHEMA}.${KNOWLEDGE_BASE}_DIRECT_PREDS;
EOF

    LOAD_PATH=`realpath $LOAD_DIR/reverse-predicate-info.load`

    cat >> $NT_FILE.db2_cmds <<EOF
LOAD DATA LOCAL INPATH '$LOAD_PATH' INTO TABLE ${DB2_SCHEMA}.${KNOWLEDGE_BASE}_REVERSE_PREDS;
EOF
}

if [[ $DB_ENGINE == "db2" ]]; then
    loadIntoDB2
elif [[ $DB_ENGINE == "postgresql" ]]; then
    loadIntoPostgresql
elif [[ $DB_ENGINE == "shark" ]]; then
	loadIntoShark
fi
