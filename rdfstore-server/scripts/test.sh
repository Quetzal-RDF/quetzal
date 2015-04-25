#!/bin/bash

DIR=`basename $0`

TEST_DIR=`realpath $1`

. ${TEST_DIR}/config.sh

for test_query in ${TEST_DIR}/*.sparql; do
    test_name=`basename ${test_query} .sparql`

    expected_answer=`dirname $test_query`/`basename $test_query .sparql`.csv

    actual_answer=/tmp/`basename $test_query .sparql`.csv
    rm -f $actual_answer

    echo -n "${test_name} time: "
    (time (s-query --service "http://${TEST_HOST}:${TEST_PORT}/quetzal/query" --output csv --query ${test_query} > $actual_answer) 2>&1) | grep real

    if diff $expected_answer $actual_answer; then
	echo "${test_name} succeeded"
    else
	echo "${test_name} FAILED!"
    fi
    
done

