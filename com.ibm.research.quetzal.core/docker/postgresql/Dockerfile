FROM ibmresearch/quetzal

MAINTAINER Kavitha Srinivas <ksrinivs@us.ibm.com>

RUN apt-get update && apt-get install -y python-software-properties software-properties-common postgresql postgresql-client postgresql-contrib

# This image does nothing much other than set a volume where the data directory # is: i.e., where the nt file sits.  For now the assumption is that
# its unzipped and it exists in the /data dir.  All temporary files get written 
# to that directory.

VOLUME /data

RUN    /etc/init.d/postgresql start
 
# Adjust PostgreSQL configuration so that local connections to the
# database don't need a password
RUN echo "local all  all    0.0.0.0/0  trust" >> /etc/postgresql/9.3/main/pg_hba.conf

