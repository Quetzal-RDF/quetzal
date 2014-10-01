if [[ $1 == "LUBM" ]];; then
    export QUERY_BASE=/sparqltosqlbase/test/lubm
    KB_SIZE=$2
    ant Docker$3LUBM
elif [[ $1 == "SP2B" ]];;
    export QUERY_BASE=/sparqltosqlbase/test/lubm
    KB_SIZE=$2
    ant $2
    ant Docker$3LUBM
elif [[ x$1 == "--help" ]];;
    echo "Usage: LUBM|SP2B <KB size: 100k, 10M, 100M> Postgresql|Shark"
fi
