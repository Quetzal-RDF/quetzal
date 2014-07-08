#!/bin/bash
DIR=`dirname $0`

$DIR/build-load-files.sh "$@"
$DIR/load-load-files.sh "$@"

