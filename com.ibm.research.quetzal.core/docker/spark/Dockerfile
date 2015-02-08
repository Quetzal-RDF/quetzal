FROM sequenceiq/spark

MAINTAINER Kavitha Srinivas <ksrinivs@us.ibm.com>

RUN curl http://apache.cs.utah.edu/maven/maven-3/3.2.5/binaries/apache-maven-3.2.5-bin.tar.gz > apache-maven-3.2.5-bin.tar.gz

RUN tar xzf apache-maven-3.2.5-bin.tar.gz -C /usr/local

RUN cd /usr/local

RUN curl http://ftp.gnu.org/gnu/coreutils/coreutils-8.23.tar.xz > coreutils-8.23.tar.xz

RUN tar -xJf coreutils-8.23.tar.xz && cd coreutils-8.23 && FORCE_UNSAFE_CONFIGURE=1 ./configure && make && make install

RUN cd /

RUN yum install -y git

RUN yum install -y gawk

# these need to be catted to the spark_home/conf/spark-env.sh directory
#export SCALA_HOME=/opt/scala-2.10.3
#export SPARK_EXECUTOR_MEMORY=450G
#export SPARK_WORKER_MEMORY=450G
#export HADOOP_CONF_DIR=/opt/hadoop-2.4.1/etc/hadoop
#export HIVE_SERVER2_THRIFT_PORT=10000
#export MASTER=spark://tiresias-2.sl.cloud9.ibm.com:7077
#export HADOOP_HOME=/opt/hadoop-2.4.1

RUN mkdir /data

WORKDIR /data

RUN git clone https://github.com/Quetzal-RDF/quetzal

RUN cd /data/quetzal/com.ibm.research.quetzal.core/ &&  /usr/local/apache-maven-3.2.5/bin/mvn verify -DskipTests
