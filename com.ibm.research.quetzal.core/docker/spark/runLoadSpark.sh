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

# Unfortunately the way you add jars into Hive varies by Spark version.  This only works for 4.0. --jars works with 4.1 for fun and so on.
/usr/local/spark/sbin/start-thriftserver.sh --driver-class-path /data/quetzal/com.ibm.research.quetzal.core/target/quetzal-RDF-0.0.1-SNAPSHOT-jar-with-dependencies.jar

cd /data

mkdir /data/tmp


until netstat -anlp | grep 10000; do sleep 10; done

echo root:$PASSWD | chpasswd

bash $DIR/../../scripts/build-load-files.sh --db-engine shark $PARALLEL --sort-options "--buffer-size=25%" --db2-config /dev/null --tmpdir /data/tmp $FILETYPE $DATAFILE

bash $DIR/../../scripts/load-load-files.sh --db-engine shark $PARALLEL --sort-options "--buffer-size=25%" --db2-config /dev/null --tmpdir /data/tmp $FILETYPE $DATAFILE

rm -rf /data/tmp
