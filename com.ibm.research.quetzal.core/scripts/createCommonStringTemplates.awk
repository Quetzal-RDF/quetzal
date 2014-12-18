BEGIN { 
    FS="::=[ ]*<<"; 
    RS=">>[\n]*"; 
    delete ruleToBody;
    delete fileNames;
    fileNameIndex = 1;
    commonFile = "/tmp/common.stg";
} 
{
    if ($1 in ruleToBody) {
	ruleToBody[$1] = ruleToBody[$1] ">>" $2; 
    } else {
	ruleToBody[$1] = $2;
    } 
}

ENDFILE {
    n = split(FILENAME, arr, "/");
    fileNames[fileNameIndex] = "/tmp/" arr[n];
    fileNameIndex++;
}

function printToFile(rule, body, file) {
    print rule, "::=", "<<", body ">>" > file;
    print "" > file;
}

END {

    count = 0;
    total = 0;

    for (i in ruleToBody) {
	if (ruleToBody[i]=="") {
	    i > commonFile;
	}
    }

    for (i in ruleToBody) {
	total++;

	n = split(ruleToBody[i], arr, ">>");
	if (n == 0) {
	    print ruleToBody[i];
	}
	if (n == 1) {
	    printToFile(i, ruleToBody[i], commonFile);
	    continue;
	}
	str1 = gensub(/[\t \n]*/,"", "g", arr[1]);

	for (j = 2; j <= n; j++) {
	    str2 = gensub(/[\t \n]*/,"", "g", arr[j]);
	    if (str1 == str2 ) {
		same = 1;
	    } else {
		same = 0;
	    }
	}
	if (same == 1) {
	    printToFile(i, arr[1], "/tmp/common.stg");
	    count++;
	} else {
	    for (j = 1; j <= n; j++) {
		printToFile(i, arr[j], fileNames[j]);
	    }
	}
    }
    print "total:", total, "same:", count;
}