{
    str = $0;
    res = "";
    while (match(str, /(^|[^\\])\\([^tnr\"uU\\]|u([0-9a-fA-F]{0,3})([^0-9a-fA-F])|U([0-9a-fA-F]{0,7})([^0-9a-fA-F]))/, a) > 0)  {
	escape = substr(str, RSTART, RLENGTH);
	if (a[4, "length"] > 0 || a[6, "length"] > 0) {
	    adjust=-1;
	} else {
	    adjust=0;
	}
	if (a[1, "length"] == 0) {
	    res = res substr(str, 1, RSTART-1) "\\" substr(str, RSTART, RLENGTH+adjust);	   
	    str = substr(str, RSTART+RLENGTH+adjust);
	} else {
	    res = res substr(str, 1, RSTART) "\\" substr(str, RSTART+1, RLENGTH-1+adjust);
	    str = substr(str, RSTART+RLENGTH+adjust);
	}
    }

    print res str;
}
