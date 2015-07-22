DIR=`dirname $0`
. $DIR/../dockerEnvt.sh
. $DIR/sparkEnvt.sh
cd $DIR/../../scripts
bash ./run-dir.sh $1