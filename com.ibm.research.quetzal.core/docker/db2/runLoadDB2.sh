db2set DB2COMM=TCPIP
db2start
db2 "CREATE DATABASE QUETZAL"

# uncomment the following if your database has over 100 properties
# and long strings for URIS
# db2 "CREATE DATABASE QUETZAL PAGESIZE 32 K"

cd /data

mkdir -p tmp

export PROCESSOR=`cat /proc/cpuinfo | grep 'processor' | wc -l`
DIR=`dirname $0`


if ls *.nt; then
    export DATAFILE=`ls /data/*.nt`
    export FILETYPE=nt
else
    export DATAFILE=`ls /data/*.nq`
    export FILETYPE=nq
fi

f=`wc -l $DATAFILE`
if [[$f > 10000]]; then
	PARALLEL="--parallel $PROCESSOR"
fi

export DB2_HOST=localhost
export DB2_PORT=50000
export DB2_DB=quetzal
export DB2_USER=db2inst1
export DB2_PASSWORD=db2inst1
export DB2_SCHEMA=db2inst1
export KNOWLEDGE_BASE=kb

echo $FILETYPE
echo $DATAFILE

bash $DIR/../../scripts/build-load-files.sh --db-engine db2 $PARALLEL --sort-options "--buffer-size=25%" --db2-config /dev/null --tmpdir /data/tmp $FILETYPE $DATAFILE

bash $DIR/../../scripts/load-load-files.sh --db-engine db2 $PARALLEL --sort-options "--buffer-size=25%" --db2-config /dev/null --tmpdir /data/tmp $FILETYPE $DATAFILE

rm -rf /data/tmp
