export DB2_HOST=localhost
export DB2_DB=quetzal
export KNOWLEDGE_BASE=kb

export PROCESSOR=`cat /proc/cpuinfo | grep 'processor' | wc -l`

if ls *.nt; then
    export DATAFILE=`ls /data/*.nt`
    export FILETYPE=nt
else
    export DATAFILE=`ls /data/*.nq`
    export FILETYPE=nq
fi

echo "filetype:" $FILETYPE
echo "datafile:" $DATAFILE

f=`cat $DATAFILE | wc -l | awk '{print $1}'`
echo $f
if [ "$f" -gt 100 ] ; then
	PARALLEL="--parallel $PROCESSOR"
fi

