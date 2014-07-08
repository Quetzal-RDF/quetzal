BEGIN {
    stringOut = (pawk == "yes")? longStringFile "." part: longStringFile;
	cmd = "java com.ibm.rdf.store.loader.LongStringHasher " cutoff;
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
	    print result "\t" str "\t" type_code(entry) "\t" |& table;
	    return result;
	} else {
		return result;
	}
    }
}
