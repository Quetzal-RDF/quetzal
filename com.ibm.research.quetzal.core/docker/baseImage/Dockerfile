FROM ubuntu

MAINTAINER Kavitha Srinivas <ksrinivs@us.ibm.com>

RUN apt-get update && apt-get install -y default-jdk

RUN apt-get update && apt-get install -y maven

RUN apt-get update && apt-get install -y git

RUN git clone https://github.com/Quetzal-RDF/quetzal

RUN cd ./quetzal/com.ibm.research.quetzal.core/ &&  mvn verify -DskipTests