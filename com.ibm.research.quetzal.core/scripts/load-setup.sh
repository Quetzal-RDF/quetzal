#!/bin/bash

DIR=`dirname $0`
LONG_FRAC=0
PARALLEL=1
PER_SORT_PARALLEL=1
GRANULARITY=10000
HASHES=3
DROP=0
INVOKED_FROM_WRAPPER=0
IS_CYGWIN=0
REVERSED=0
LONG_STRINGS_LEN=2000
CORRELATIONS=0
COLOR="true"
CHUNKS=1
CREATE_ONLY=0
JAVA_OPTS="$_JAVA_OPTIONS -Dfile.encoding=UTF-8"
TABLES="both"
REUSE=0
ENTITY_IN_SECONDARY=1
PROPERTY_IN_SECONDARY=1
SCHEMA_EXPERIMENTS=1
IMPLICIT_CHUNKS=0

case "`uname`" in
	CYGWIN*) IS_CYGWIN=1;
esac

ClassPath=
if [[ $IS_CYGWIN == 1 ]]; then
	ClassPath=`cygpath --path --unix $CLASSPATH`
else
	ClassPath=$CLASSPATH
fi

while [[ $# > 0 && "--" = `expr substr $1 1 2` ]]; do
    if [[ $1 == "--hashing" ]]; then
	shift
	COLOR="false"
    elif [[ $1 == "--db-engine" ]]; then
	shift
	DB_ENGINE=$1
	shift
    elif [[ $1 == "--long-string-fraction" ]]; then
	shift
	LONG_FRAC=$1
	shift
    elif [[ $1 == "--tables" ]]; then
	shift
	TABLES=$1
	shift
    elif [[ $1 == "--tmpdir" ]]; then
	shift
	export TMPDIR=$1
	shift
    elif [[ $1 == "--java-opts" ]]; then
	shift
	export JAVA_OPTS=$1
	shift
    elif [[ $1 == "--sort-options" ]]; then
	shift
	SORT_OPTIONS=$1
	shift
    elif [[ $1 == "--db2-config" ]]; then
	shift
	DB2_CONFIG=$1
	shift
    elif [[ $1 == "--knowledge-base" ]]; then
	shift
	KNOWLEDGE_BASE=$1
	shift
    elif [[ $1 == "--workload-query-dir" ]]; then
	shift
	WORKLOAD_DIR=$1
	shift
    elif [[ $1 == "--system-predicates" ]]; then
	shift
	SYSTEM_PREDICATES=$1
	shift
    elif [[ $1 == "--parallel" ]]; then
	shift
	PARALLEL=$1
	IMPLICIT_CHUNKS=`expr 3 '*' $1`
	shift
    elif [[ $1 == "--chunks" ]]; then
	shift
	CHUNKS=$1
	shift
    elif [[ $1 == "--granularity" ]]; then
	shift
	GRANULARITY=$1
	shift
    elif [[ $1 == "--hashes" ]]; then
	shift
	HASHES=$1
	shift
    elif [[ $1 == "--drop" ]]; then
	DROP=1
	shift
    elif [[ $1 == "--no-lids" ]]; then
	NO_LIDS=1
	shift
    elif [[ $1 == "--correlations-for-indexing" ]]; then
	CORRELATIONS=1
	shift
    elif [[ $1 == "--from-wrapper" ]]; then
        shift
        INVOKED_FROM_WRAPPER=$1
        shift
    elif [[ $1 == "--reversed" ]]; then
        shift
	REVERSED=1
    elif [[ $1 == "--create-load-files-only" ]]; then
        shift
	CREATE_ONLY=1
    elif [[ $1 == "--reuse-existing-files" ]]; then
        shift
	REUSE=1
    else
	echo "unexpected option $1"
	exit -1
    fi
done

sep=":"
for f in $DIR/../target/lib/*.jar; do lib+=$f$sep; done

echo "LIB=" $lib

if [[ $INVOKED_FROM_WRAPPER != 1 ]]; then
    export CLASSPATH=$DIR/../target/test-classes:$DIR/../target/classes:$DIR/../../com.ibm.wala.util/bin:$DIR/../lib/db2jcc4.jar:$lib
fi

#  it really sucks to have random distinct characters be 
# the same w.r.t. sort order, so use byte values and ignore
# any locale nonsense
export LC_ALL=C

# make sure we have enough chunks for parallel to be meaningful
if expr $IMPLICIT_CHUNKS '>' $CHUNKS > /dev/null; then
    CHUNKS=$IMPLICIT_CHUNKS
fi

if [[ $# > 1 ]]; then
  if [[ $1 == "quad" ]]; then
    FILE_TYPE=$1
  elif [[ $1 == "nt" ]]; then
    FILE_TYPE=$1
  else
    echo "invalid file type $1, expecting nt or quad"
    exit -1
  fi

  if [[ $# > 1 ]]; then
      NT_FILE=$2
      LOAD_DIR=`dirname $NT_FILE`
  fi
fi
