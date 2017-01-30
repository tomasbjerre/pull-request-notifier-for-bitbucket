# Pull Request Notifier for Bitbucket Server [![Build Status](https://travis-ci.org/tomasbjerre/pull-request-notifier-for-bitbucket.svg?branch=master)](https://travis-ci.org/tomasbjerre/pull-request-notifier-for-bitbucket)
The original use case was to trigger Jenkins jobs to build pull requests that are created in Bitbucket Server. The plugin can be configured to trigger different Jenkins jobs for different repositories. It can supply custom parameters to the jenkins job using the variables. It can authenticate with HTTP Basic.

It can, for example, trigger a build in Jenkins. Parameterized Jenkins jobs can be triggered remotely via:
```
http://server/job/theJob/buildWithParameters?token=TOKEN&PARAMETER=Value
```

The plugin can trigger any system, not only Jenkins. The plugin can notify any system that can be notified with a URL.

[Here](https://raw.githubusercontent.com/tomasbjerre/pull-request-notifier-for-bitbucket/master/sandbox/all.png) is a screenshot of the admin GUI on global level. And [here](https://raw.githubusercontent.com/tomasbjerre/pull-request-notifier-for-bitbucket/master/sandbox/repo.png) is a screenshot of the admin GUI on repository level.


[Here](http://bjurr.com/continuous-integration-with-bitbucket-server-and-jenkins/) is a blog post that includes the plugin.

Available in [Atlassian Marketplace](https://marketplace.atlassian.com/plugins/se.bjurr.prnfs.pull-request-notifier-for-stash).

## Features
The Pull Request Notifier for Bitbucket Server can:

* Invoke any URL, or set of URL:s, when a pull request event happens.
 * With variables available to add necessary parameters.
 * HTTP POST, PUT, GET and DELETE. POST and PUT also supports rendered post content. 
* Be configured to trigger on any [pull request event](https://developer.atlassian.com/static/javadoc/stash.old-perms-pre-feb4/2.0.1/api/reference/com/atlassian/stash/event/pull/package-summary.html). Including extended events:
 * RESCOPED_FROM, when source branch change
 * RESCOPED_TO, when target branch change
 * BUTTON_TRIGGER, when trigger button in pull request view is pressed
* Can invoke CSRF protected systems, using the ${INJECTION_URL_VALUE} variable. How to to that with Jenkins is described below.
* Be configured to only trigger if the pull request mathches a filter. A filter text is constructed with any combination of the variables and then a regexp is constructed to match that text.
* Add buttons to pull request view in Bitbucket Server. And map those buttons to URL invocations. This can be done by setting the filter string to ${BUTTON_TRIGGER_TITLE} and the filter regexp to title of button.
 * Buttons can have forms associated with them, and then submit the form data using the ${BUTTON_FORM_DATA} variable.
* Authenticate with HTTP basic authentication.
* Optionally allow any SSL certificate.
* Use custom SSL key store, type and password.
* Send custom HTTP headers
* Can optionally use proxy to connect
* Can let users and/or admins do configuration. Or restrict configuration to just system admins. A user will have to browse to the configuration page at `http://domain/bitbucket/plugins/servlet/prnfb/admin`.
* Can enable trigger
 * If PR has, or has no, conflicts
 * Only if PR has conflicts
 * Only if PR has no conflicts
* Nice configuration GUI.
 * Global at */bitbucket/plugins/servlet/prnfb/admin*
 * Repo level at */bitbucket/plugins/servlet/prnfb/admin/PROJECT_1/rep_1*

The plugin has its own implementation to create the RESCOPED_FROM and RESCOPED_TO events. RESCOPED is transformed to RESCOPED_TO if target branch changed, RESCOPED_FROM if source branch, or both, changed.

The filter text as well as the URL support variables. These are:

| Variable | Description |
| :------- | :---------- | 
| `${EVERYTHING_URL}` | This variable is resolved to all available variables. The name of each parameter is the name of that variable. <br /> Example: `PULL_REQUEST_ID=1&PULL_REQUEST_TITLE=some%20thing...` |
| `${PULL_REQUEST_ID}` | Example: `1` |
| `${PULL_REQUEST_TITLE}` | Example: `Anything` |
| `${PULL_REQUEST_DESCRIPTION}` | The `${EVERYTHING_URL}` does not include this because it makes the URL very big. | Example: Anything |
| `${PULL_REQUEST_VERSION}` | Example: `1` |
| `${PULL_REQUEST_COMMENT_TEXT}` | Example: `A comment` |
| `${PULL_REQUEST_COMMENT_ACTION}` | Example: `ADDED`, `DELETED`, `EDITED`, `REPLIED` |
| `${PULL_REQUEST_ACTION}` | Example: `OPENED` |
| `${PULL_REQUEST_STATE}` | Example: `DECLINED`, `MERGED`, `OPEN` |
| `${BUTTON_TRIGGER_TITLE}` | Example: `Trigger Notification` |
| `${BUTTON_FORM_DATA}` | The form data that was submitted |
| `${INJECTION_URL_VALUE}` | Value retrieved from any URL |
| `${PULL_REQUEST_URL}` | Example: `http://localhost:7990/projects/PROJECT_1/repos/rep_1/pull-requests/1` |
| `${PULL_REQUEST_USER_DISPLAY_NAME}` | Example: `Some User` |
| `${PULL_REQUEST_USER_EMAIL_ADDRESS}` | Example: `some.user@bitbucket.domain` |
| `${PULL_REQUEST_USER_ID}` | Example: `1` |
| `${PULL_REQUEST_USER_NAME}` | Example: `user.name` |
| `${PULL_REQUEST_USER_SLUG}` | Example: `user.name` |
| `${PULL_REQUEST_AUTHOR_DISPLAY_NAME}` | Example: `Administrator` |
| `${PULL_REQUEST_AUTHOR_EMAIL}` | Example: `admin@example.com` |
| `${PULL_REQUEST_AUTHOR_ID}` | Example: `1` |
| `${PULL_REQUEST_AUTHOR_NAME}` | Example: `admin` |
| `${PULL_REQUEST_AUTHOR_SLUG}` | Example: `admin` |
| `${PULL_REQUEST_REVIEWERS}` | Example: `Administrator,User` |
| `${PULL_REQUEST_REVIEWERS_ID}` | Example: `1,2` |
| `${PULL_REQUEST_REVIEWERS_SLUG}` | Example: `admin,user` |
| `${PULL_REQUEST_REVIEWERS_EMAIL}` | Example: `admin@example.com,user@example.com` |
| `${PULL_REQUEST_REVIEWERS_APPROVED_COUNT}` | Number of reviewers that approved the PR. |
| `${PULL_REQUEST_REVIEWERS_UNAPPROVED_COUNT}` | Number of reviewers that unapproved the PR. |
| `${PULL_REQUEST_REVIEWERS_NEEDS_WORK_COUNT}` | Number of reviewers that says the PR needs work. |
| `${PULL_REQUEST_PARTICIPANTS_APPROVED_COUNT}` | Number of participants that approved the PR. |
| `${PULL_REQUEST_PARTICIPANTS_EMAIL}` | Example: `admin@example.com,user@example.com` |
| `${PULL_REQUEST_MERGE_COMMIT}` | Hash of merged commit (only available for merged-event). |
| `${PULL_REQUEST_FROM_SSH_CLONE_URL}` | Example: `ssh://git@localhost:7999/project_1/rep_1` |
| `${PULL_REQUEST_FROM_HTTP_CLONE_URL}` | Example: `http://admin@localhost:7990/bitbucket/scm/project_1/rep_1.git` |
| `${PULL_REQUEST_FROM_HASH}` | Example: `6053a1eaa1c009dd11092d09a72f3c41af1b59ad` |
| `${PULL_REQUEST_FROM_ID}` | Example: `refs/heads/branchmodmerge` |
| `${PULL_REQUEST_FROM_BRANCH}` | Example: `branchmodmerge` |
| `${PULL_REQUEST_FROM_REPO_ID}` | Example: `1` |
| `${PULL_REQUEST_FROM_REPO_NAME}` | Example: `rep_1` |
| `${PULL_REQUEST_FROM_REPO_PROJECT_ID}` | Example: `1` |
| `${PULL_REQUEST_FROM_REPO_PROJECT_KEY}` | Example: `PROJECT_1` |
| `${PULL_REQUEST_FROM_REPO_SLUG}` | Example: `rep_1` |
| `${PULL_REQUEST_TO_SSH_CLONE_URL}` | Example: `ssh://git@localhost:7999/project_1/rep_1` |
| `${PULL_REQUEST_TO_HTTP_CLONE_URL}` | Example: `http://admin@localhost:7990/bitbucket/scm/project_1/rep_1.git` |
| `${PULL_REQUEST_TO_HASH}` | Example: `6053a1eaa1c009dd11092d09a72f3c41af1b59ad` |
| `${PULL_REQUEST_TO_ID}` | Example: `refs/heads/branchmodmerge` |
| `${PULL_REQUEST_TO_BRANCH}` | Example: `branchmodmerge` |
| `${PULL_REQUEST_TO_REPO_ID}` | Example: `1` |
| `${PULL_REQUEST_TO_REPO_NAME}` | Example: `rep_1` |
| `${PULL_REQUEST_TO_REPO_PROJECT_ID}` | Example: `1` |
| `${PULL_REQUEST_TO_REPO_PROJECT_KEY}` | Example: `PROJECT_1` |
| `${PULL_REQUEST_TO_REPO_SLUG}` | Example: `rep_1` |

The ${PULL_REQUEST_USER...} contains information about the user who issued the event. Who commented it, who rejected it, who approved it...

You may want to use [Violation Comments to Bitbucket Server plugin](https://wiki.jenkins-ci.org/display/JENKINS/Violation+Comments+to+Bitbucket+Server+Plugin) and/or [StashNotifier plugin](https://wiki.jenkins-ci.org/display/JENKINS/StashNotifier+Plugin) to report results back to Bitbucket.

#### Button Forms

For each button you can specify a form that will show up when the button is pressed. That form data will then be submitted and will be available in the ${BUTTON_FORM_DATA} variable. Additionally, the form itself can reference other variables (with the exception of the ${BUTTON_...} ones) and will have those resolved prior to rendering.

A form is defined as a JSON array. Here is an example that shows all possibilities:

```
[
        {   "name": "var1",
            "label": "var1 label",
            "defaultValue": "you can put a variable like this: ${PULL_REQUEST_AUTHOR_NAME}",
            "type": "input", 
            "required": false,
            "description": "var1 description"
        },
        {   "name": "var2",
            "label": "var2 label",
            "defaultValue": "any string can go here",
            "type": "textarea", 
            "required": false,
            "description": "var2 description"
        },
        {   "name": "var3",
            "label": "var3 label",
            "defaultValue": "option2_name",
            "buttonFormElementOptionList": [
                {"label": "option1 label", "name": "option1_name"},
                {"label": "option2 label", "name": "option2_name"},
                {"label": "option3 label", "name": "option3_name"}
            ],
            "type": "radio", 
            "required": true,
            "description": "var3 description"
        },
        {   "name": "var4",
            "label": "var4 label",
            "type": "checkbox", 
            "required": true,
            "buttonFormElementOptionList": [
                {"label": "option1 label", "name": "option1_name", "defaultValue": true}, 
                {"label": "option2 label", "name": "option2_name", "defaultValue": true}
            ],
            "description": "var4 description"
        }
]
```

You can see a screenshot [here](https://raw.githubusercontent.com/tomasbjerre/pull-request-notifier-for-bitbucket/master/sandbox/rendered_form.png) when rendered.

When submitted with the default values, it will look like this:

```
{
   "var1":"you can put a variable like this: admin",
   "var2":"any string can go here",
   "var3":"option2_name",
   "var4":[
      "option1_name",
      "option2_name"
   ]
}
```

### REST
Some rest resources are available. You can figure out the JSON structure by looking at the [DTO:s](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/tree/master/src/main/java/se/bjurr/prnfb/presentation/dto).

* `/bitbucket/rest/prnfb-admin/1.0/settings`
 * `GET` Get all global settings.
 * `POST` Store all global settings.

* `/bitbucket/rest/prnfb-admin/1.0/settings/notifications`
 * `DELETE /{uuid}` Deletes notification with *uuid*.
 * `GET` Get all notifications.
 * `GET /{uuid}` Get notification with *uuid*.
 * `GET /projectKey/{projectKey}` Get all notifications for the project.
 * `GET /projectKey/{projectKey}/repositorySlug/{repositorySlug}` Get all notifications for the project and repository.
 * `POST` Save a notification.

* `/bitbucket/rest/prnfb-admin/1.0/settings/buttons`
 * `DELETE /{uuid}` Deletes button with *uuid*.
 * `GET` Get all buttons that the current user is allowed to use.
 * `GET /{uuid}` Get button with *uuid*.
 * `GET /repository/{repositoryId}/pullrequest/{pullRequestId}` Get all buttons for repository that the current user is allowed to use.
 * `GET /projectKey/{projectKey}` Get all buttons for the project.
 * `GET /projectKey/{projectKey}/repositorySlug/{repositorySlug}` Get all buttons for the project and repository.
 * `POST` Save a button.
 * `POST {uuid}/press/repository/{repositoryId}/pullrequest/{pullRequestId}` Press the button.

A new notification to trigger on *COMMENTED* can be added like this.
```
curl -u admin:admin 'http://localhost:7990/bitbucket/rest/prnfb-admin/1.0/settings/notifications' -H 'Content-Type: application/json; charset=UTF-8' -H 'Accept: application/json, text/javascript, */*; q=0.01' --data-binary '{"uuid":"","name":"","projectKey":"","repositorySlug":"","filterString":"","filterRegexp":"","triggers":["COMMENTED"],"injectionUrl":"","injectionUrlRegexp":"","user":"","password":"","proxyUser":"","proxyPassword":"","proxyServer":"","proxyPort":"","url":"http://localhost:80/?abc","method":"GET","postContent":"","headers":[{"name":"","value":""}]}'
```

It will respond with something like this.
```
{"headers":[],"method":"GET","name":"Notification","triggerIfCanMerge":"ALWAYS","triggerIgnoreStateList":[],"triggers":["COMMENTED"],"url":"http://localhost:80/?abc","uuid":"b1306a3a-5a87-4145-80b7-660bc986dd25"}
```

It can then be changed to trigger on *RESCOPED_FROM* and *RESCOPED_TO* like this.
```
curl -u admin:admin 'http://localhost:7990/bitbucket/rest/prnfb-admin/1.0/settings/notifications' -H 'Content-Type: application/json; charset=UTF-8' -H 'Accept: application/json, text/javascript, */*; q=0.01' --data-binary '{"uuid":"b1306a3a-5a87-4145-80b7-660bc986dd25","name":"Notification","projectKey":"","repositorySlug":"","filterString":"","filterRegexp":"","triggerIfCanMerge":"ALWAYS","triggers":["RESCOPED_FROM","RESCOPED_TO"],"injectionUrl":"","injectionUrlRegexp":"","user":"","password":"","proxyUser":"","proxyPassword":"","proxyServer":"","proxyPort":"","url":"http://localhost:80/?abc","method":"GET","postContent":"","headers":[{"name":"","value":""}]}' --compressed
```

It will respond with something like this.
```
{"headers":[],"method":"GET","name":"Notification","triggerIfCanMerge":"ALWAYS","triggerIgnoreStateList":[],"triggers":["RESCOPED_FROM","RESCOPED_TO"],"url":"http://localhost:80/?abc","uuid":"b1306a3a-5a87-4145-80b7-660bc986dd25"}
```

You may use Chrome and Developer Tools (press F12) to view rest calls while editing in GUI to find more examples.

## Integration guides

Generally, when working fiddling with this plugin, you may want to use something like [RequestBin](https://requestb.in/). Let the notification URL point to it and you can inspect what the invoked URL looks like.

Here are some guides on how to use the plugin with different systems. Feel free to add guides through pull requests to this repo!

 * [Jenkins](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/blob/master/README_jenkins.md)
 * [HipChat](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/blob/master/README_hipchat.md)

## Developer instructions
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
