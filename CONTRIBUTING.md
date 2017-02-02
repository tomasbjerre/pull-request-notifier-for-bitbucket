# Developer instructions
There are some scripts to help working with the plugin.

 * `./setup-atlassian-sdk.sh` Setup Atlassian SDK.
 * `./docker-build.sh` Build Docker container.
 * `./docker-run.sh` Run the Docker container.
 * `./integration-test-local.sh` Run integration tests against localhost.
 * `./integration-test.sh` Start Docker container and then runs integration tests against it.

The .travis.yml is setting up Atlas SDK and building the plugin. It may help you setup your environment.

Prerequisites:

* Atlas SDK [(installation instructions)](https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project).
* JDK 1.8 or newer

Generate Eclipse project:
```
atlas-compile eclipse:eclipse
```

Package the plugin:
```
atlas-package
```

Run Bitbucket, with the plugin, on localhost:
```
export MAVEN_OPTS=-Dplugin.resource.directories=`pwd`/src/main/resources
atlas-run
```

You can also remote debug on port 5005 with:
```
atlas-debug
```

Make a release [(detailed instructions)](https://developer.atlassian.com/docs/common-coding-tasks/development-cycle/packaging-and-releasing-your-plugin):
```
mvn -B release:prepare -DperformRelease=true release:perform
```
