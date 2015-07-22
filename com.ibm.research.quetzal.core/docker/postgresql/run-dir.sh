DIR=`dirname $0`
. $DIR/../dockerEnvt.sh
. $DIR/postgresEnvt.sh
cd $DIR/../../scripts
bash ./run-dir.sh $1
