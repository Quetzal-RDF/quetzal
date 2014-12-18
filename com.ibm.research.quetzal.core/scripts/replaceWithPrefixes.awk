{
    if (NR == 1) {
	print "sed s@" $2 "@" NR ":@g $1\\";
    } else {
	print "| sed s@" $2 "@" NR ":@g\\";
    }
  
}