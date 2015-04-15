# Pull Request Notifier for Stash Changelog

Changelog of Pull Request Notifier for Stash.

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

