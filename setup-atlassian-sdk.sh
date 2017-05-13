#!/bin/bash

if [ "$1" -gt "-1" ]
  then echo Give param, like /home/username
fi

installdir=$1
cd $1
wget https://marketplace.atlassian.com/download/plugins/atlassian-plugin-sdk-tgz
mkdir opt
tar -xvzf *plugin-sdk* -C opt
mv opt/*plugin-sdk* opt/atlassian-plugin-sdk
chmod a+x opt/atlassian-plugin-sdk/bin/*
chmod a+x opt/atlassian-plugin-sdk/apache-maven-*/bin/*
echo "export PATH=$installdir/opt/atlassian-plugin-sdk/bin:$installdir/opt/atlassian-plugin-sdk/apache-maven-*/bin:$PATH" >> ~/.bashrc
source ~/.bashrc
