#!/bin/bash
npm install
node_modules/.bin/grunt
#atlas-mvn versions:update-properties
atlas-mvn package verify
