#!/bin/bash
#
#  \ is used to specify escapes, but the set of valid escapes is limited,
# so remove illegal \ characters from NTriples data by replacing them with
# the escape sequence for \ itself, which is \\
#
#  see http://www.w3.org/TR/rdf-testcases/#ntriples, section 3.2 for a 
# precise specification of what escape sequences are allowed.
#
DIR=`dirname $0`
gawk -f $DIR/fix-escapes-for-ntriples.awk $1

