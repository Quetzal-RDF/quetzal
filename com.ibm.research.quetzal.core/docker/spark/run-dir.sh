DIR=`dirname $0`
. $DIR/../dockerEnvt.sh
. $DIR/sparkEnvt.sh
cd $DIR/../../scripts
sh ./run-dir.sh $1