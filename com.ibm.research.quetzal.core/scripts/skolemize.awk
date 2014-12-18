function skolemize(str, value) {
    if (match(str, /^_:/) > 0) {
	sub(/^_:/, "<http://", str); 
	value = str ">";
    } else {
	value = str;
    }
    return value;
}

{
    for (i = 1; i <= NF; i++) {
	value = skolemize($i);
	printf("%s ", value);
    }
    printf("%s\n", "");
}