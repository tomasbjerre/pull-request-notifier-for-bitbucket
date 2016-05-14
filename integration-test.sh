#!/bin/bash

#
# Cleanup
#

function build_clean {
 for KILLPID in `ps ax | grep 'Dbitbucket' | awk ' { print $1;}'`; do
  echo "Bitbucket Server is running at $KILLPID, killing it"
  kill $KILLPID || echo;
 done
 atlas-mvn clean
}

function on_exit {
 build_clean
}
trap on_exit EXI
build_clean

#
# Start Bitbucket Server
#
atlas-run -q  || exit 1 &

BITBUCKET_URL=http://localhost:7990/bitbucket
until $(curl --output /dev/null --silent --head --fail $BITBUCKET_URL); do
 printf '.'
 sleep 1
done
echo
echo
echo Bitbucket Server started at $BITBUCKET_URL
echo
echo

#
# Run tests
#
./integration-test-local.sh 
