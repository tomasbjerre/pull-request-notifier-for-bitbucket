# Pull Request Notifier for Stash [![Build Status](https://travis-ci.org/tomasbjerre/pull-request-notifier-for-stash.svg?branch=master)](https://travis-ci.org/tomasbjerre/pull-request-notifier-for-stash)
The original use case was to trigger Jenkins jobs to build pull requests that are created in Stash. The plugin can be configured to trigger different Jenkins jobs for different repositories. It can supply custom parameters to the jenkins job using the variables. It can authenticate with HTTP Basic.

It can, for example, trigger a build in Jenkins. Parameterized Jenkins jobs can be triggered remotely via:
```
http://server/job/theJob/buildWithParameters?token=TOKEN&PARAMETER=Value
```

The plugin can trigger any system, not only Jenkins. The plugin can notify any system that can be notified with a URL.

[Here](https://raw.githubusercontent.com/tomasbjerre/pull-request-notifier-for-stash/master/sandbox/all.png) is a screenshot of the admin GUI.

[Here](http://bjurr.se/building-atlassian-stash-pull-requests-in-jenkins/) is a blog post that includes the plugin.

Available in [Atlassian Marketplace](https://marketplace.atlassian.com/plugins/se.bjurr.prnfs.pull-request-notifier-for-stash).

## Features
The Pull Request Notifier for Stash can:

* Invoke any URL, or set of URL:s, when a pull request event happens.
 * With variables available to add necessary parameters.
 * HTTP POST, PUT, GET and DELETE. POST and PUT also supports rendered post content. 
* Be configured to trigger on any pull request event. Including source branch change (RESCOPED_FROM) and target branch change (RESCOPED_TO).
* Be configured to only trigger if the pull request mathches a filter. A filter text is constructed with any combination of the variables and then a regexp is constructed to match that text.
* Authenticate with HTTP basic authentication.
* Send custom HTTP headers
* Can optionally use proxy to connect

The filter text as well as the URL support variables. These are:

* ${PULL_REQUEST_ID} Example: 1
* ${PULL_REQUEST_VERSION} Example: 1
* ${PULL_REQUEST_COMMENT_TEXT} Example: A comment
* ${PULL_REQUEST_ACTION} Example: OPENED
* ${PULL_REQUEST_AUTHOR_DISPLAY_NAME} Example: Administrator
* ${PULL_REQUEST_AUTHOR_EMAIL} Example: admin@example.com
* ${PULL_REQUEST_AUTHOR_ID} Example: 1
* ${PULL_REQUEST_AUTHOR_NAME} Example: admin
* ${PULL_REQUEST_AUTHOR_SLUG} Example: admin
* ${PULL_REQUEST_FROM_HASH} Example: 6053a1eaa1c009dd11092d09a72f3c41af1b59ad
* ${PULL_REQUEST_FROM_ID} Example: refs/heads/branchmodmerge
* ${PULL_REQUEST_FROM_BRANCH} Example: branchmodmerge
* ${PULL_REQUEST_FROM_REPO_ID} Example: 1
* ${PULL_REQUEST_FROM_REPO_NAME} Example: rep_1
* ${PULL_REQUEST_FROM_REPO_PROJECT_ID} Example: 1
* ${PULL_REQUEST_FROM_REPO_PROJECT_KEY} Example: PROJECT_1
* ${PULL_REQUEST_FROM_REPO_SLUG} Example: rep_1
* And same variables for TO, like: ${PULL_REQUEST_TO_HASH}

## Developer instructions
Prerequisites:

* Atlas SDK [(installation instructions)](https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project).
* JDK 1.7 or newer

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
