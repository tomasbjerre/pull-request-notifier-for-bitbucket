#!/bin/bash
#atlas-mvn versions:update-properties
atlas-mvn release:prepare release:perform -B || exit 1
./build.sh
git commit -a --amend --no-edit
git push -f
