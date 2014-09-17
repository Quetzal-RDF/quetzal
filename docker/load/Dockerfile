FROM ibmresearch/quetzal

MAINTAINER Kavitha Srinivas <ksrinivs@us.ibm.com>

# This image does nothing much other than set a volume where the data directory # is: i.e., where the nt file sits.  For now the assumption is that
# its unzipped and it exists in the /data dir.  All temporary files get written 
# to that directory.
VOLUME /data

RUN ls ./sparqltosqlbase

WORKDIR /data

RUN ls /sparqltosqlbase/docker/

ENTRYPOINT ["/bin/bash ~/sparqltosqlbase/docker/runLoadPostgres.sh"] 

