FROM bryantsai/db2-server:db2_inst_1

MAINTAINER Kavitha Srinivas <ksrinivs@us.ibm.com>

RUN apt-get update 
RUN apt-get install -y software-properties-common
RUN sudo apt-add-repository -y ppa:webupd8team/java
RUN sudo apt-get update
RUN echo debconf shared/accepted-oracle-license-v1-1 select true | \
  sudo debconf-set-selections && sudo apt-get install -y oracle-java8-installer
# RUN apt-get install -y default-jdk 
RUN apt-get install -y maven 
RUN apt-get install -y git 
RUN apt-get install -y gawk 
RUN apt-get install -y realpath 
RUN mkdir /data 
# RUN cd /data 
RUN git clone https://github.com/Quetzal-RDF/quetzal /data/quetzal
RUN cd /data/quetzal/com.ibm.research.quetzal.core/ && mvn verify -DskipTests 
RUN mkdir /data/tmp  
RUN chown -R db2inst1:db2grp1 /data

# cant switch user here to db2inst1.  Mounting the volume for /home seems to depend 
# on logging in as root, and then db2 does not start 
