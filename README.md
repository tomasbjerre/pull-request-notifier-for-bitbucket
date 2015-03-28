# Pull Request Notifier for Stash [![Build Status](https://travis-ci.org/tomasbjerre/pull-request-notifier-for-stash.svg?branch=master)](https://travis-ci.org/tomasbjerre/pull-request-notifier-for-stash)
A plugin for Atlassian Stash that can notfy other systems on events regarding pull requests.

It can, for example, trigger a build in Jenkins. Parameterized Jenkins jobs can be triggered remotely via:
```
http://server/job/theJob/buildWithParameters?token=TOKEN&PARAMETER=Value
```

## Features
The Pull Request Notifier for Stash can

* Invoke any URL when a pull request event happens.
 * With variables available to add necessary parameters.
* Be configured to trigger on any pull request event.
* Authenticate with HTTP BASIC authentication.

## Developer instructions
You will need Atlas SDK to compile the code.

https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project

You can generate Eclipse project:
```
atlas-compile eclipse:eclipse
```

Package the plugin:
```
atlas-package
```

Run Stash, with the plugin, on localhost:
```
export MAVEN_OPTS=-Dplugin.resource.directories=`pwd`/src/main/resources
atlas-run
```

Make a release:

https://developer.atlassian.com/docs/common-coding-tasks/development-cycle/packaging-and-releasing-your-plugin
```
mvn release:prepare
mvn release:perform
```
