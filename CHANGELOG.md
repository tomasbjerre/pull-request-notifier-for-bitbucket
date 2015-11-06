# Pull Request Notifier for Bitbucket Changelog

Changelog of Pull Request Notifier for Bitbucket.

## 2.11
* Bugfix: Replacing spaces with dashes in ${PULL_REQUEST_URL}. Was evaluating to wrong URL if repo name included spaces.

## 2.10
* Processing events on Bitbucket Server's event threads.

## 2.9
* Url encoding evaluated values when they are used in URL invocations.

## 2.8
* New variables
 * ${PULL_REQUEST_MERGE_COMMIT} Hash of merged commit (only available for merged-event).

## 2.7
* New variables
 * ${PULL_REQUEST_REVIEWERS_APPROVED_COUNT} Number of reviewers that approved the PR.
 * ${PULL_REQUEST_PARTICIPANTS_APPROVED_COUNT} Number of participants that approved the PR.
These can be used to, for example, show a trigger button only if there are non-zero number of approvals.

## 2.6
* Bugfix: Saving with checked checkboxes RESCOPED_FROM/TO and BUTTON_TRIGGER was not reflected in GUI.

## 2.5
* Bugfix: Avoiding admin page to crash if entering quote as value of a field.

## 2.4
* The storage key was accidently changed when migrating to Bitbucket 4. This includes a fix that will load 1.x settings if they exist, and if no 2.x settings are available.
* Adding "Triggers do not apply to" option with values DECLINED and MERGED.
* Changing wording of trigger conditions in admin GUI.

## 2.3
* Bugfix: Loading CSS and JS in admin-page

## 2.2
* Building against Bitbucket 4.0.0
 * Was using EAP

## 2.1
* Bugfix: Adding Basic Auth headers to injection url request.

## 2.0
* Migrated from Stash 3 to Bitbucket 4.
 * The release of Bitbucket 4.0 (2015-09-22) broke all backwards compatibility and made it more ore less impossible to maintain a version that is compatible with both Stash 3.x and Bitbucket 4.x. That is why this plugin changed name and started over with a 1.0 release.
 * Changed name from Pull Request Notifier for Stash to Pull Request Notifier for Bitbucket

## 1.28
* Can enable trigger
 * If PR has, or has no, conflicts
 * Only if PR has conflicts
 * Only if PR has no conflicts
* New variable ${PULL_REQUEST_TITLE}

## 1.27
* Adding an optional regular expression, that can be evaluated in the response from injection URL, to populate ${INJECTION_URL_VALUE}.

## 1.26
* Removing XPath alternative, introduced in version 1.22. It may not work in all installations. And is is not needed for Jenkins, which was the original use case.

## 1.25
* Bugfix: Sending post content in POST and PUT notifications.

## 1.24
* Removing JsonPath alternative, introduced in version 1.22. It causes classpath issues for some users.

## 1.23
* Removing SLF4J usage to deal with classpath issues.

## 1.22
* Adding feature that can inject variable evaluated in content of a URL. Raw content or value of an xPath or JsonPath.
* Bugfix: Closing inputstream after invocation. This may have caused "IOException: Too many open files".

## 1.21
* Hiding buttons in pull request view, if no notification will be fired when it is clicked
* Using label without ID:s in admin GUI
 * To avoid using same ID:s multiple times

## 1.20
* Optionally allow users and admins to configure the plugin.
 * A common user, will have to browse to http://domain/stash/plugins/servlet/prnfs/admin to do configuration.

## 1.19
* Bugfix: Only ignore events on closed pull requests if its a COMMENT-event.

## 1.18
* Avoiding endless loop if user not 'System Admin' when editing configuration
* Triggers can be named. To make it easier to keep track of them in large installations.
* Trigger Notification Buttons on Pull Request View
 * And ${BUTTON_TRIGGER_TITLE} variable resolving to title of pressed button
* Building against latest Stash version (3.11.1) using latest Atlassian Maven Plugin Suite version (6.0.3)
* Adding ${PULL_REQUEST_URL} variable. Points to the pull request view in Stash.

## 1.17
* Ignoring events if pull request is closed

## 1.16
* Triggering also on comment replies if COMMENTED event is checked.

## 1.15
* Removing RESCOPED event, its confusing when to use it together with _FROM and _TO. RESCOPED was triggered when both _FROM and _TO changed at the exact same time. Now, just check _FROM if you only want to trigger when source branch changes, _TO if only target and both if you want to trigger for both.
* Adding logging to make it easier to debug what events are triggered.

## 1.14
* New variables with information about the user who issued the event
 * ${PULL_REQUEST_USER_DISPLAY_NAME} Example: Some User
 * ${PULL_REQUEST_USER_EMAIL_ADDRESS} Example: some.user@stash.domain
 * ${PULL_REQUEST_USER_ID} Example: 1
 * ${PULL_REQUEST_USER_NAME} Example: user.name
 * ${PULL_REQUEST_USER_SLUG} Example: user.name

## 1.13
* Bugfix
 * Letting PULL_REQUEST_FROM_BRANCH, and PULL_REQUEST_TO_BRANCH, evaluate to branch display name. A branch with slashes in name, like "feature/branchmodmerge", will no longer be evaluated to "branchmodmerge" but keep the name "feature/branchmodmerge".

## 1.12
* Adding clone URL variables
 * ${PULL_REQUEST_FROM_HTTP_CLONE_URL} Example: http://admin@localhost:7990/stash/scm/project_1/rep_1.git
 * ${PULL_REQUEST_FROM_SSH_CLONE_URL} Example: ssh://git@localhost:7999/project_1/rep_1

## 1.11
* Custom HTTP headers
* Proxy support
* Stash Data Center compatibility
* Adding PULL_REQUEST_COMMENT_TEXT and PULL_REQUEST_VERSION variables

## 1.10
* Adding PULL_REQUEST_FROM_BRANCH and PULL_REQUEST_TO_BRANCH variables to make branch names available

## 1.9
* Adding support for PUT and DELETE

## 1.8
* Support for HTTP POST requests, with content that is rendered with variables
* Letting variable ${PULL_REQUEST_ACTION} return RESCOPED_FROM or RESCOPED_TO instead of just RESCOPED

## 1.7
* Not sending authentication headers when user and/or password is not set
* Adding RESCOPED_FROM and RESCOPED_TO event types

## 1.6
* Correcting design with CSS for password field in admin view
* Removing accidently added text from admin view

## 1.5
* Using password type on password-field in admin GUI
* Some new variables added
 * ${PULL_REQUEST_ACTION} Example: OPENED
 * ${PULL_REQUEST_AUTHOR_DISPLAY_NAME} Example: Administrator
 * ${PULL_REQUEST_AUTHOR_EMAIL} Example: admin@example.com
 * ${PULL_REQUEST_AUTHOR_ID} Example: 1
 * ${PULL_REQUEST_AUTHOR_NAME} Example: admin
 * ${PULL_REQUEST_AUTHOR_SLUG} Example: admin

## 1.4
* Bugfix: Avoiding multiple notifications being sent from same event.

## 1.3
Same as version 1.2 but with different version number. When version 1.2 was initially rejected I fixed the issue and created a new 1.2. But a new version number was needed for a resubmission to Atlassian Marketplace.

## 1.2
* Compatible with Java 7, was compatible with Java 8 since java.util.Base64 was accidently used

## 1.1
* Adding support for filters
* Adding support for PULL_REQUEST_TO_HASH and PULL_REQUEST_FROM_HASH variables
* Fixing authentication bug
* Making it compatible with Stash 2.12.0

## 1.0
Initial Release

