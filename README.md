# Pull Request Notifier for Stash [![Build Status](https://travis-ci.org/tomasbjerre/pull-request-notifier-for-stash.svg?branch=master)](https://travis-ci.org/tomasbjerre/pull-request-notifier-for-stash)
A plugin for Atlassian Stash that can notify other systems on events regarding pull requests.

It can, for example, trigger a build in Jenkins. Parameterized Jenkins jobs can be triggered remotely via:
```
http://server/job/theJob/buildWithParameters?token=TOKEN&PARAMETER=Value
```

[Here](https://raw.githubusercontent.com/tomasbjerre/pull-request-notifier-for-stash/master/sandbox/all.png) is a screenshot of the admin GUI.

Available in [Atlassian Marketplace](https://marketplace.atlassian.com/plugins/se.bjurr.prnfs.pull-request-notifier-for-stash).

## Features
The Pull Request Notifier for Stash can

* Invoke any URL, or set of URL:s, when a pull request event happens.
 * With variables available to add necessary parameters.
* Be configured to trigger on any pull request event.
* Be configured to only trigger only if the pull request mathches a filter. Where a filter can target (among other things) branch, project or repo.
* Authenticate with HTTP BASIC authentication.

## Developer instructions
### Prerequisites
- Atlas SDK [(installation instructions)](https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project).
- JDK 1.7

Generate Eclipse project:
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

Make a release [(detailed instructions)](https://developer.atlassian.com/docs/common-coding-tasks/development-cycle/packaging-and-releasing-your-plugin):
```
mvn release:prepare
mvn release:perform
```
