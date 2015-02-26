#!/bin/bash

git clone https://github.com/bryantsai/db2-docker.git

cp $1 db2-docker/

cd db2-docker

sudo ./build.sh $1 

cd ..

sudo docker build --rm -t "ibmresearch/quetzal-db2" .


