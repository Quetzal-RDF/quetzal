FROM sequenceiq/spark:1.6.0

MAINTAINER Kavitha Srinivas <ksrinivs@us.ibm.com>

ENV JAVA_VERSION 8u31 
ENV BUILD_VERSION b13 

RUN curl -O http://ftp.gnu.org/gnu/coreutils/coreutils-8.24.tar.xz \
    && tar -xJf coreutils-8.24.tar.xz \
	&& export FORCE_UNSAFE_CONFIGURE=1 \
	&& cd ./coreutils-8.24 \
	&& ./configure \
	&& make \
	&& make install \
	&& cd .. \
	&& curl -O http://ftp.gnu.org/gnu/gawk/gawk-4.1.3.tar.xz \
    && tar -xJf gawk-4.1.3.tar.xz \	
	&& cd ./gawk-4.1.3 \
	&& ./configure \
	&& yum -y install wget \
	&& wget --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/$JAVA_VERSION-$BUILD_VERSION/jdk-$JAVA_VERSION-linux-x64.rpm" -O /tmp/jdk-8-linux-x64.rpm \
	&& yum -y install /tmp/jdk-8-linux-x64.rpm \
	&& alternatives --install /usr/bin/java jar /usr/java/latest/bin/java 200000 \
	&& alternatives --install /usr/bin/javaws javaws /usr/java/latest/bin/javaws 200000 \
	&& alternatives --install /usr/bin/javac javac /usr/java/latest/bin/javac 200000 \
	&& export JAVA_HOME=/usr/java/latest \
	&& make \
	&& make install \
	&& export PATH=/usr/local/bin:$PATH && cd .. \
	&& curl -O http://mirror.sdunix.com/apache/maven/maven-3/3.3.3/binaries/apache-maven-3.3.3-bin.tar.gz \
	&& tar -xzf apache-maven-3.3.3-bin.tar.gz \
	&& yum -y install git	\
	&& mkdir /data

WORKDIR /data

RUN git clone https://github.com/Quetzal-RDF/quetzal \
	&& cd /data/quetzal/com.ibm.research.quetzal.core/ \ 
	&&  /apache-maven-3.3.3/bin/mvn assembly:assembly -DdescriptorId=jar-with-dependencies -DskipTests=true

EXPOSE 10000