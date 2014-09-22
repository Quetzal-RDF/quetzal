
export PROCESSOR=`cat /proc/cpuinfo | grep 'processor' | wc -l`

ls -l .

if [[ x$CREATE_DB == "xtrue" ]]; then
    psql -h $POSTGRES_PORT_5432_TCP_ADDR -p $POSTGRES_PORT_5432_TCP_PORT --command "CREATE USER quetzal WITH SUPERUSER PASSWORD 'quetzalcoatl';"
    psql -h $POSTGRES_PORT_5432_TCP_ADDR -p $POSTGRES_PORT_5432_TCP_PORT --command "CREATE DATABASE quetzal WITH OWNER=quetzal;"
fi

if ls *.nt; then
    export DATAFILE=`ls *.nt`
    export FILETYPE=nt
else
    export DATAFILE=`ls *.nq`
    export FILETYPE=nq
fi

export DB2_HOST=$POSTGRES_PORT_5432_TCP_ADDR
export DB2_PORT=$POSTGRES_PORT_5432_TCP_PORT
export DB2_DB=quetzal
export DB2_USER=quetzal
export DB2_PASSWORD=quetzalcoatl
export DB2_SCHEMA=default
export KNOWLEDGE_BASE=kb

mkdir /data/tmp

bash /sparqltosqlbase/scripts/build-load-files.sh --db-engine postgresql --parallel $PROCESSOR --sort-options "buffer-size=25%" --tmpdir /data/tmp $FILETYPE $DATAFILE

bash /sparqltosqlbase/scripts/load-load-files.sh --db-engine postgresql --parallel $PROCESSOR --sort-options "buffer-size=25%" --tmpdir /data/tmp $FILETYPE $DATAFILE

rm -rf /data/tmp