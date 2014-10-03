FROM ibmresearch/quetzal

MAINTAINER Kavitha Srinivas <ksrinivs@us.ibm.com>

WORKDIR /sparqltosqlbase

ENV KB kb
ENV DB_USER quetzal
ENV DB_PASSWORD quetzalcoatl
ENV DB_SCHEMA quetzal

ENTRYPOINT ["/bin/bash", "/sparqltosqlbase/docker/queryImage/runQuery.sh"] 
CMD ["--help"]

