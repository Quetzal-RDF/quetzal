BEGIN {
    stringOut = (pawk == "yes")? longStringFile "." part: longStringFile;

    cmd = "java -Dfile.encoding=UTF-8 com.ibm.research.rdf.store.loader.LongStringHasher " cutoff;
#    PROCINFO[cmd, "pty"] = 1;

    table = "sort | uniq > " stringOut
}

function hash_string(str,entry,  result,stuff,str2) {
   if (length(str) < cutoff) {
	return str;
    } else {
	print str |& cmd;
	cmd |& getline result;
	if (str != result) {
		str = fixBrackets(str);
	    print result "\t" str "\t" type_code(entry) |& table;
	    return result;
	} else {
	    return result;
	}
    }
}
