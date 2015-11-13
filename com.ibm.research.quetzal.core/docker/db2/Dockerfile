FROM bryantsai/db2-server:db2_inst_1

MAINTAINER Kavitha Srinivas <ksrinivs@us.ibm.com>

RUN apt-get update && apt-get install -y maven \
&& apt-get install -y git \
&& apt-get install -y gawk \
&& apt-get install -y realpath \
&&  mkdir /data \
&& cd /data \
&& git clone https://github.com/Quetzal-RDF/quetzal /data/quetzal \
&& cd /data/quetzal/com.ibm.research.quetzal.core/ && mvn verify -DskipTests \ 
&& mkdir /data/tmp  \
&& chown -R db2inst1:db2grp1 /data

# cant switch user here to db2inst1.  Mounting the volume for /home seems to depend 
# on logging in as root, and then db2 does not start 
