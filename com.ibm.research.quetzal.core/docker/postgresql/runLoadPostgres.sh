/etc/init.d/postgresql start

DIR=`dirname $0`
. $DIR/../dockerEnvt.sh
. $DIR/postgresEnvt.sh

cd /data

mkdir /data/tmp

psql --command "CREATE USER quetzal WITH SUPERUSER PASSWORD 'quetzalcoatl';"
psql --command "CREATE DATABASE quetzal WITH OWNER=quetzal;"


chmod 600 $PGPASSFILE


bash $DIR/../../scripts/build-load-files.sh --db-engine postgresql $PARALLEL --sort-options "--buffer-size=25%" --db2-config /dev/null --tmpdir /data/tmp $FILETYPE $DATAFILE

bash $DIR/../../scripts/load-load-files.sh --drop --db-engine postgresql $PARALLEL --sort-options "--buffer-size=25%" --db2-config /dev/null --tmpdir /data/tmp $FILETYPE $DATAFILE

rm -rf /data/tmp
