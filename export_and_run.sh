export MAVEN_OPTS=-Dplugin.resource.directories=`pwd`/src/main/resources
atlas-run || mvn bitbucket:run
