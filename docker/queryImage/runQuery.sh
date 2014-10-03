if [[ $1 == "LUBM" ]]; then
    export QUERY_BASE=/sparqltosqlbase/test/lubm/
    export KB_SIZE=$2
    ant Docker$3LUBM
elif [[ $1 == "SP2B" ]]; then
    export QUERY_BASE=/sparqltosqlbase/test/lubm/
    export KB_SIZE=$2
    ant $2
    ant Docker$3LUBM
elif [[ $1 == "--help" ]]; then
    echo "Usage: LUBM|SP2B <KB size: 100k, 10M, 100M> Postgresql|Shark"
fi
