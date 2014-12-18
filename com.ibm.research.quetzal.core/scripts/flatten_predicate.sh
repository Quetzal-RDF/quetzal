DIR=`dirname $0`

ORIG_NT=$1
SENSE=$2
PARALLEL=$3

shift; shift; shift

PREDICATES="$@"

FILES=$ORIG_NT.sorted_${SENSE}*primary.load*

function make_command() {
    cmd="gawk -F '\t' $AWK_OPTS -v predicate=$1 -v addedCols=$2 -f $DIR/types.awk -f $DIR/strings.awk -f $DIR/flatten_predicate.awk $f"
    shift; shift

    while [[ $# > 0 ]]; do
	cmd="$cmd | gawk -F '\t' $AWK_OPTS -v predicate=$1 -v addedCols=$2 -f $DIR/types.awk -f $DIR/strings.awk -f $DIR/flatten_predicate.awk"
	shift; shift
    done
}

(for f in $FILES; do
    AWK_OPTS="-v direction=${SENSE}ect"

    if [[ -f $ORIG_NT.types ]]; then
	AWK_OPTS="$AWK_OPTS -v typeCodeFile=$ORIG_NT.types"
    fi

    SF=`echo $f | sed s/primary/secondary/`
    if [[ -f $SF ]]; then
	AWK_OPTS="$AWK_OPTS -v secondaryFile=$SF"
	make_command $PREDICATES

	echo "$cmd > $f.flatten"
    fi

done) | xargs -d '\n' -n 1 -P $PARALLEL sh -c

