FROM ubuntu

MAINTAINER Kavitha Srinivas <ksrinivs@us.ibm.com>

RUN apt-get update && apt-get install -y default-jdk

RUN apt-get update && apt-get install -y ant

RUN apt-get update && apt-get install -y git

RUN mkdir -p /root/.ssh

ADD id_rsa /root/.ssh/id_rsa

RUN chmod 700 /root/.ssh/id_rsa

RUN echo "Host github.rtp.raleigh.ibm.com\n\tStrictHostKeyChecking no\n" >> /root/.ssh/config

RUN git clone git@github.rtp.raleigh.ibm.com:sparqltosqlgp/sparqltosqlbase.git

RUN cd ./sparqltosqlbase && ant

