#!/bin/bash

git clone https://github.com/ksrinivs64/db2-docker.git

cp $1 db2-docker/

cd db2-docker

sudo ./build.sh $1 server 1.0

cd ..

sudo docker build --rm -t "ibmresearch/quetzal-db2" .


