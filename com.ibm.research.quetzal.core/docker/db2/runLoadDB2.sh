DIR=`dirname $0`
. $DIR/../dockerEnvt.sh
. $DIR/db2Envt.sh

db2set DB2COMM=TCPIP
db2set DB2_COMPATIBILITY_VECTOR=MYS
db2start
db2 "CREATE DATABASE QUETZAL"

# uncomment the following if your database has over 100 properties
# and long strings for URIS
# db2 "CREATE DATABASE QUETZAL PAGESIZE 32 K"

cd /data

mkdir -p tmp


bash $DIR/../../scripts/build-load-files.sh --db-engine db2 $PARALLEL --sort-options "--buffer-size=25%" --db2-config /dev/null --tmpdir /data/tmp $FILETYPE $DATAFILE

bash $DIR/../../scripts/load-load-files.sh --drop --db-engine db2 $PARALLEL --sort-options "--buffer-size=25%" --db2-config /dev/null --tmpdir /data/tmp $FILETYPE $DATAFILE

rm -rf /data/tmp
