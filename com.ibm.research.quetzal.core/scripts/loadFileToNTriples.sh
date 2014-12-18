DIR=`dirname $0`

ORIG_NT=$1
SENSE=$2

shift; shift

if [[ -e $ORIG_NT.sorted_$SENSE.primary.load.* ]]; then
  FILES=$ORIG_NT.sorted_$SENSE.primary.load.*
else
  FILES="$@"
fi

(for f in $FILES; do
    AWK_OPTS="-v fileFor=$SENSE"

    if [[ -f $ORIG_NT.long_strings ]]; then
	AWK_OPTS="$AWK_OPTS -v longStringFile=$ORIG_NT.long_strings"
    fi

    SF=`echo $f | sed s/primary/secondary/`
    if [[ -f $SF ]]; then
	AWK_OPTS="$AWK_OPTS -v secondaryFile=$SF"
    fi

    if [[ -f $ORIG_NT.types ]]; then
	AWK_OPTS="$AWK_OPTS -v typeFile=$ORIG_NT.types"
    fi

    gawk $AWK_OPTS -f $DIR/strings.awk -f $DIR/loadFileToNTriples.awk $f
done) | LC_ALL=C sort

