BEGIN {
    total = 0;
    delete totals;
    delete combinations;
}

{
    setCount = $2;
    total += setCount;
    n = split($0, preds, /\|/);
    for(i = 2; i <= n; i++) {
	totals[ preds[i] ] += setCount;
	for(j = 2; j <= n; j++) {
	    if (i != j) {
		combinations[ preds[i] "," preds[j] ] += setCount;
	    }
	}
    }
}

function good_proxy(p, q, scale, key) {
    key = p "," q;
    if (key in combinations) {
	if (combinations[key] == totals[q]) {
	    scale = combinations[key] / totals[p];
	    if (scale > .75) {
		return scale;
	    }
	}
    }

    return 0;
}

END {
    for(p in totals) {
	print p;
	for(q in totals) {
	    key = p "," q; 
	    if (key in combinations) {
		scale = good_proxy(p, q)
		if (scale > 0) {
		    has_proxy[q] = 1;
		    print p " " scale " " q;
		}
	    }
	}
    }

    proxied = 0;
    not_proxied = 0;
    for(p in totals) {
	if (! (p in has_proxy)) {
	    not_proxied++;
	} else {
	    proxied++;
	}
    }

    print "# " proxied " properties have proxies";
    print "# " not_proxied " do not";
}
