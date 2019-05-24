if [ -z "$PASSWD" ]; then
    echo "Need to set PASSWD for docker container.  Use docker -e PASSWD=<password> to set"
    exit 1
fi 

DIR=`dirname $0`
. $DIR/../dockerEnvt.sh
. $DIR/sparkEnvt.sh

# In a Docker container, make sure that the hive metastore gets written to a data volume or all data will be 
# inaccessible.  The HDFS default is to write to /tmp, start thriftserver from there.
cd /tmp

/init.sh

cd /data

mkdir /data/tmp

echo root:$PASSWD | chpasswd

bash $DIR/../../scripts/build-load-files.sh --db-engine shark $PARALLEL --sort-options "--buffer-size=25%" --db2-config /dev/null --tmpdir /data/tmp $FILETYPE $DATAFILE

bash $DIR/../../scripts/load-load-files.sh --db-engine shark $PARALLEL --sort-options "--buffer-size=25%" --db2-config /dev/null --tmpdir /data/tmp $FILETYPE $DATAFILE

rm -rf /data/tmp
