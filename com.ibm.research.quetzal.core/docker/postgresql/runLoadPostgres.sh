cd /data

mkdir /data/tmp

psql --command "CREATE USER quetzal WITH SUPERUSER PASSWORD 'quetzalcoatl';"
psql --command "CREATE DATABASE quetzal WITH OWNER=quetzal;"

if ls *.nt; then
    export DATAFILE=`ls /data/*.nt`
    export FILETYPE=nt
else
    export DATAFILE=`ls /data/*.nq`
    export FILETYPE=nq
fi

export DB2_HOST=localhost
export DB2_PORT=5432
export DB2_DB=quetzal
export DB2_USER=quetzal
export DB2_PASSWORD=quetzalcoatl
export DB2_SCHEMA=quetzal
export KNOWLEDGE_BASE=kb

echo $FILETYPE
echo $DATAFILE

bash /data/quetzal/com.ibm.research.quetzal.core/scripts/build-load-files.sh --db-engine postgresql --parallel $PROCESSOR --sort-options "--buffer-size=25%" --db2-config /dev/null --tmpdir /data/tmp $FILETYPE $DATAFILE

bash /data/quetzal/com.ibm.research.quetzal.core/scripts/load-load-files.sh --db-engine postgresql --parallel $PROCESSOR --sort-options "--buffer-size=25%" --db2-config /dev/null --tmpdir /data/tmp $FILETYPE $DATAFILE

rm -rf /data/tmp