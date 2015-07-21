if [ -z "$PASSWD" ]; then
    echo "Need to set PASSWD for docker container.  Use docker -e PASSWD=<password> to set"
    exit 1
fi 

DIR=`dirname $0`
. $DIR/../dockerEnvt.sh
. $DIR/sparkEnvt.sh


mkdir /data/tmp

/usr/local/spark/sbin/start-thriftserver.sh

until netstat -anlp | grep 10000; do sleep 10; done

echo root:$PASSWD | chpasswd

bash $DIR/../../scripts/build-load-files.sh --db-engine shark $PARALLEL --sort-options "--buffer-size=25%" --db2-config /dev/null --tmpdir /data/tmp $FILETYPE $DATAFILE

bash $DIR/../../scripts/load-load-files.sh --db-engine shark $PARALLEL --sort-options "--buffer-size=25%" --db2-config /dev/null --tmpdir /data/tmp $FILETYPE $DATAFILE

rm -rf /data/tmp
