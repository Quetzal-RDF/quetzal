FROM ubuntu

MAINTAINER Kavitha Srinivas <ksrinivs@us.ibm.com>
 
RUN apt-get update && apt-get install -y python-software-properties software-properties-common postgresql postgresql-client postgresql-contrib

RUN apt-get install -y default-jdk

RUN apt-get install -y maven

RUN apt-get install -y git

RUN apt-get install -y gawk

RUN apt-get install -y realpath

RUN mkdir /data && chown -R postgres /data

USER postgres

# This image does nothing much other than set a volume where the data directory # is: i.e., where the nt file sits.  For now the assumption is that
# its unzipped and it exists in the /data dir.  All temporary files get written 
# to that directory.

WORKDIR /data

RUN /etc/init.d/postgresql start

RUN git clone https://github.com/Quetzal-RDF/quetzal

RUN cd /data/quetzal/com.ibm.research.quetzal.core/ &&  mvn verify -DskipTests

