DIR=`dirname $0`
. $DIR/../dockerEnvt.sh
. $DIR/postgresEnvt.sh
cd $DIR/../../scripts
sh ./run-dir.sh $1