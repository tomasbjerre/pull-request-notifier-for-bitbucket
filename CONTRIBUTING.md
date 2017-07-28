# Inspect DB

* Turn off BBS
* java -cp ./target/bitbucket/app/WEB-INF/lib/h2-1.3.176.jar org.h2.tools.Shell
* User: sa
* Driver Leave blank, just enter
* URL: jdbc:h2:file:///home/bjerre/workspace/pull-request-notifier-for-bitbucket/target/bitbucket/home/shared/data/db;DB_CLOSE_ON_EXIT=TRUE
* Password Leave blank, just enter
* maxwidth 9999
* SELECT * FROM PLUGIN_SETTING WHERE KEY_NAME LIKE '%pull%';
* SELECT KEY_VALUE FROM PLUGIN_SETTING WHERE KEY_NAME='se.bjurr.prnfb.pull-request-notifier-for-bitbucket-3'

# Developer instructions

The .travis.yml is setting up Atlas SDK and building the plugin. It may help you setup your environment.

Prerequisites:

* Atlas SDK [(installation instructions)](https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project).
* JDK 1.8 or newer

Generate Eclipse project:
```
atlas-mvn eclipse:eclipse
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
