FROM ubuntu

MAINTAINER Kavitha Srinivas <ksrinivs@us.ibm.com>
 
RUN apt-get update && apt-get install -y python-software-properties software-properties-common postgresql postgresql-client postgresql-contrib \
&& apt-get install -y default-jdk \
&& apt-get install -y maven \
&& apt-get install -y git \
&& apt-get install -y gawk \
&& apt-get install -y realpath \
&& mkdir /data && chown -R postgres /data

RUN echo debconf shared/accepted-oracle-license-v1-1 select true | \
  sudo debconf-set-selections && sudo apt-get install -y oracle-java8-installer


USER postgres

# This image does nothing much other than set a volume where the data directory # is: i.e., where the nt file sits.  For now the assumption is that
# its unzipped and it exists in the /data dir.  All temporary files get written 
# to that directory.

WORKDIR /data

RUN /etc/init.d/postgresql start \
&& git clone https://github.com/Quetzal-RDF/quetzal \
&& cd /data/quetzal/com.ibm.research.quetzal.core/ &&  mvn verify -DskipTests

