export MAVEN_OPTS=-Dplugin.resource.directories=`pwd`/src/main/resources
#atlas-mvn versions:update-properties
atlas-run || mvn bitbucket:run
