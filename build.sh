#!/bin/bash
npm install
grunt
#atlas-mvn versions:update-properties
atlas-mvn package verify
