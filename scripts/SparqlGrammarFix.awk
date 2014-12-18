BEGIN {
    RS=";\n";
}

/[[:space:]]*[A-Z_]+[[:space:]]*:[[:space:]]*'[a-z_]+'[[:space:]]*$/ {
    split($0, a, /'/);
    word=a[2];
    newword="";
    for(i = 1; i <= length(word); i++) {
	char=substr(word,i,1);
	if (char != "_") {
	    newword = newword " ('" char "'|'" toupper(char) "')";
	} else {
	    newword = newword " '" char "'";
	}
    } 
    print a[0], a[1], newword, "\n  ;";
    next;
}

{
    print $0, ";";
}