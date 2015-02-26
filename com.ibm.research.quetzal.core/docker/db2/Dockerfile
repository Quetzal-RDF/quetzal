FROM bryantsai/db2-server:db2_inst_1

MAINTAINER Kavitha Srinivas <ksrinivs@us.ibm.com>

RUN apt-get update && apt-get install -y default-jdk \
&& apt-get install -y maven \
&& apt-get install -y git \
&& apt-get install -y gawk \
&& apt-get install -y realpath \
&& mkdir /data && chown db2inst1 /data 

WORKDIR /data

RUN git clone https://github.com/Quetzal-RDF/quetzal \
&& cd /data/quetzal/com.ibm.research.quetzal.core/ &&  mvn verify -DskipTests
