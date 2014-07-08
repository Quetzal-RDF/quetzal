BEGIN {
    total = 0;
    nl=length("\n");
}

{
    total += (length($0) + nl);

    if (FNR==1 && (last != "yes")) {
	next;
    }

    if (next_is_last == 1) {
	exit(0);
    }

    if (total > chunk) {
	next_is_last = 1;
    }
}

