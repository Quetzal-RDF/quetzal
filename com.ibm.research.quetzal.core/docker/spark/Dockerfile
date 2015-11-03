FROM sequenceiq/spark

MAINTAINER Kavitha Srinivas <ksrinivs@us.ibm.com>

RUN curl -O http://gnu.mirrorcatalogs.com/coreutils/coreutils-8.24.tar.xz \
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
	&& yum -y install wget
	&& wget --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/$JAVA_VERSION-$BUILD_VERSION/jdk-$JAVA_VERSION-linux-x64.rpm" -O /tmp/jdk-8-linux-x64.rpm
	&& yum -y install /tmp/jdk-8-linux-x64.rpm
	&& RUN alternatives --install /usr/bin/java jar /usr/java/latest/bin/java 200000
	&& RUN alternatives --install /usr/bin/javaws javaws /usr/java/latest/bin/javaws 200000
	&& RUN alternatives --install /usr/bin/javac javac /usr/java/latest/bin/javac 200000
	&& ENV JAVA_HOME /usr/java/latest
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
	&&  /apache-maven-3.3.3/bin/mvn verify -DskipTests

EXPOSE 10000