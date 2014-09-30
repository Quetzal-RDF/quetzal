FROM ibmresearch/quetzal

MAINTAINER Kavitha Srinivas <ksrinivs@us.ibm.com>

WORKDIR /sparqltosqlbase

ENV JDBC_URL $POSTGRES_PORT_5432_TCP_ADDR
ENV KB kb
ENV DB_USER quetzal
ENV DB_PASSWORD quetzalcoatl
ENV DB_SCHEMA quetzal
# enter the size of the kb to check answers.  Predefined sizes are:
# for LUBM: 100k, 10m, 100m, 1B

ENV KB_SIZE 100k

ENV QUERY_BASE /sparqltosqlbase/test/lubm/

ENTRYPOINT ["ant", "DockerPostgresqlLUBM"]

