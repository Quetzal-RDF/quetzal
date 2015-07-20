DIR=`dirname $0`
. $DIR/../dockerEnvt.sh
. $DIR/db2Envt.sh
cd $DIR/../../scripts
sh ./run-dir.sh $1