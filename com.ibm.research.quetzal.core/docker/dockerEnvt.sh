export DB2_HOST=localhost
export DB2_DB=quetzal
if [ -z "$KNOWLEDGE_BASE" ]; then
	export KNOWLEDGE_BASE=kb
fi 

export PROCESSOR=`cat /proc/cpuinfo | grep 'processor' | wc -l`

if ls /data/*.nt; then
    export DATAFILE=`ls /data/*.nt`
    export FILETYPE=nt
else
    export DATAFILE=`ls /data/*.nq`
    export FILETYPE=quad
fi

echo "filetype:" $FILETYPE
echo "datafile:" $DATAFILE

f=`cat $DATAFILE | wc -l | awk '{print $1}'`
echo $f
if [ "$f" -gt 100 ] ; then
	PARALLEL="--parallel $PROCESSOR"
fi

