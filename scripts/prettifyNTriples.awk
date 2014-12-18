{
    printf("%s||%s||", $1, $2);

    for (i=3; i < NF; i++) {
	printf("%s ", $i);
    }
    printf("%s\n", "");
}
