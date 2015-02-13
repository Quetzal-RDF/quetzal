FROM sequenceiq/spark:1.1.1-ubuntu

MAINTAINER Kavitha Srinivas <ksrinivs@us.ibm.com>

RUN apt-get update && apt-get install -y default-jdk

RUN apt-get install -y maven

RUN apt-get install -y git

RUN apt-get install -y gawk

RUN apt-get install -y realpath

RUN mkdir /data

WORKDIR /data

RUN git clone https://github.com/Quetzal-RDF/quetzal

RUN cd /data/quetzal/com.ibm.research.quetzal.core/ &&  mvn verify -DskipTests
