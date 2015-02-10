useradd quetzal -p quetzalcoatl

mkdir /data/tmp

chown -R quetzal /data

/etc/bootstrap.sh

/usr/local/spark/sbin/start-all.sh 
/usr/local/spark/sbin/start-thriftserver.sh

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
export DB2_PORT=10000
export DB2_DB=default
export DB2_USER=quetzal
export DB2_PASSWORD=quetzalcoatl
export DB2_SCHEMA=quetzal
export KNOWLEDGE_BASE=kb

echo $FILETYPE
echo $DATAFILE

bash $DIR/../../scripts/build-load-files.sh --db-engine shark $PARALLEL --sort-options "--buffer-size=25%" --db2-config /dev/null --tmpdir /data/tmp $FILETYPE $DATAFILE

bash $DIR/../../scripts/load-load-files.sh --db-engine shark $PARALLEL --sort-options "--buffer-size=25%" --db2-config /dev/null --tmpdir /data/tmp $FILETYPE $DATAFILE

rm -rf /data/tmp
