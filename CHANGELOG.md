# Pull Request Notifier for Bitbucket Changelog

Changelog of Pull Request Notifier for Bitbucket.

## Unreleased
### No issue
  Latest changelog plugin
  
  [a2ae249fec5b6f1](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/a2ae249fec5b6f1) Tomas Bjerre *2016-06-24 09:38:01*

## 2.26
### GitHub [#124](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/124) PR Updated not triggering a Job
  Using ApplicationUser from PR event 

 * Was taking it from AuthenticationContext when a RESCOPED event occured. At that time the getCurrentUser() returns null.
  
  [1964ba412ccbf84](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/1964ba412ccbf84) Tomas Bjerre *2016-06-23 19:35:41*

### No issue
  Building with BBS 4.7.1
  
  [3e9d67a80f506cc](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/3e9d67a80f506cc) Tomas Bjerre *2016-06-22 19:29:28*

## 2.25
### GitHub [#123](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/123) Question on the configuration permissions.
  Only showing repo settings on repo admin page 

 * Was also showing global settings, with "any" repo/project. Changing it to make implementation simpler.
  
  [2d290b7b9599ee1](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/2d290b7b9599ee1) Tomas Bjerre *2016-06-22 19:07:27*

## 2.24
### GitHub [#123](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/123) Question on the configuration permissions.
  Checking for admin permission on the repo/project 

 * If a user is only admin in one repo, the user should not be able to administrate the plugin in another repo.
 * Hiding admin restriction levels, in buttons config, that the user does not have access to. So that the user cannot create buttons that the user cannot see.
 * Sorting notifications and buttons by name in REST API.
  
  [15d2bd2ed9f8d6b](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/15d2bd2ed9f8d6b) Tomas Bjerre *2016-06-20 17:37:21*

## 2.23
### GitHub [#122](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/122) Preserving configs when upgrading from stash 3.x to bitbucket 4.x
  Loading legacy settings correctly 

 * Did not save loaded legacy settings in new format when found. Got new UUID:s on every load.
  
  [56827de4eb8310d](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/56827de4eb8310d) Tomas Bjerre *2016-06-04 21:25:42*

## 2.22
### GitHub [#119](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/119) You are not permitted to access this resource
  Getting clone URL:s with admin permission
  
  [c520e3654fb7608](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/c520e3654fb7608) Tomas Bjerre *2016-05-28 16:33:40*

### No issue
  Show buttons only on PR where the button belongs to
  
  [2cfaea05e0312d4](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/2cfaea05e0312d4) Stefan Anzinger *2016-05-27 09:00:34*

  Updating screenshots
  
  [eb2b5922f7473ce](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/eb2b5922f7473ce) Tomas Bjerre *2016-05-15 18:25:55*

  Docker
  
  [76aa6838a73a556](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/76aa6838a73a556) Tomas Bjerre *2016-05-15 10:24:55*

## 2.21
### GitHub [#117](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/117) PULL_REQUEST_TO_HASH resolved to old commit
  Adding missing consumes annotation on REST resources 

 * Adding integration test for REST API.
  
  [843a1d6cb7cc3d6](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/843a1d6cb7cc3d6) Tomas Bjerre *2016-05-14 22:04:48*

### No issue
  Avoiding looking for legacy settings if no such keys

 * Also adding Curl examples to README.
 * Documenting REST API.

Logging legacy settings
  
  [b8600f3a7d972c4](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/b8600f3a7d972c4) Tomas Bjerre *2016-05-14 15:35:56*

  Reusing Podam factory, to use caching
  
  [53eff487fbacd3b](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/53eff487fbacd3b) Tomas Bjerre *2016-05-12 17:18:11*

## 2.20
### GitHub [#116](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/116) Not saving configuration
  Linting with JSHint

 * Found JS that may crashes in IE9.
  
  [807d483c0c9b9f9](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/807d483c0c9b9f9) Tomas Bjerre *2016-05-12 16:04:16*

### No issue
  Defaulting proxy port to null
  
  [4761aa53d58cf06](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/4761aa53d58cf06) Tomas Bjerre *2016-05-12 16:37:39*

  doc
  
  [6e92abeebfaae7b](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/6e92abeebfaae7b) Tomas Bjerre *2016-05-11 15:36:41*

## 2.19
### No issue
  Using password type for keystore password field
  
  [d24afe75aa36f0d](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/d24afe75aa36f0d) Tomas Bjerre *2016-05-10 15:57:49*

  doc
  
  [934453f12768a51](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/934453f12768a51) Tomas Bjerre *2016-05-10 15:51:17*

  Change proxy url/port fields to text
  
  [27750702101f275](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/27750702101f275) Stefan Anzinger *2016-05-10 15:22:41*

## 2.18
### GitHub [#109](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/109) Refactor admin pages
  Adding buttons after refactoring
  
  [5a16a6941de3a53](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/5a16a6941de3a53) Tomas Bjerre *2016-05-08 20:23:13*

  Adding migration code for old settings format
  
  [d937d3cc18bdfd0](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/d937d3cc18bdfd0) Tomas Bjerre *2016-05-08 13:08:07*

  Admin GUI
  
  [319f5de5c123e06](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/319f5de5c123e06) Tomas Bjerre *2016-05-08 13:07:55*

  Refactoring
  
  [3e1fddaecb4aab8](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/3e1fddaecb4aab8) Tomas Bjerre *2016-05-03 16:01:52*

### GitHub [#25](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/25) Enable  configuration in per-repository hook screen
  Admin GUI
  
  [319f5de5c123e06](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/319f5de5c123e06) Tomas Bjerre *2016-05-08 13:07:55*

  Adding repo and global admin pages
  
  [8904d03202979bd](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/8904d03202979bd) Tomas Bjerre *2016-05-03 18:30:39*

  Adding project and repo filter to notification
  
  [1fc12a72100afe8](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/1fc12a72100afe8) Tomas Bjerre *2016-05-03 16:44:25*

### GitHub [#55](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/55) remove width limit
  Admin GUI
  
  [319f5de5c123e06](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/319f5de5c123e06) Tomas Bjerre *2016-05-08 13:07:55*

### No issue
  Using entire injection URL regexp as group when no grouping
  
  [63d51f7cff69fbc](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/63d51f7cff69fbc) Tomas Bjerre *2016-05-09 16:03:27*

  changelog maven plugin 1.29
  
  [25ccd05b465bd4a](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/25ccd05b465bd4a) Tomas Bjerre *2016-04-14 16:36:16*

## 2.17
### GitHub [#107](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/107) java.lang.NullPointerException: null at PrnfbRenderer.java:367
  Finding current user correctly 

 * Was looking up currently logged in user with the users username. That caused NullPointerException  if username not same as user slug.
  
  [296f894974dd831](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/296f894974dd831) Tomas Bjerre *2016-04-02 07:17:39*

### No issue
  Using git changelog plugin 1.20
  
  [97dc36d24773408](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/97dc36d24773408) Tomas Bjerre *2016-03-15 21:15:55*

## 2.16
### GitHub [#103](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/103) Support Bitbucket Server 4.4
  Build with 4.4.0
  
  [a94e4e4ce65df7d](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/a94e4e4ce65df7d) Tomas Bjerre *2016-03-02 17:09:53*

### GitHub [#106](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/106) Don&#39;t log Authorization header value
  Not logging authorization header value
  
  [40f8652541336a8](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/40f8652541336a8) Tomas Bjerre *2016-03-14 16:03:54*

### No issue
  Using git-changelog-maven-plugin
  
  [75146ae73ad1151](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/75146ae73ad1151) Tomas Bjerre *2016-03-14 16:18:12*

  Correctin link to pull request events
  
  [620b9287a424852](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/620b9287a424852) Tomas Bjerre *2016-02-24 18:16:13*

## 2.15
### GitHub [#91](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/91) Change multiple buttons to be in a dropdown
  Sorting buttons
  
  [c253934c0cc7569](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/c253934c0cc7569) Tomas Bjerre *2016-01-30 07:12:19*

### No issue
  Move button actions into a dropdown rather than individual buttons

Previously, PRNFB would end up creating a separate button for each
action, which in turn would cause wrapping of the buttons and overall
not looking good, or being very usable if you had more than one or two
buttons.

This change moves them into a dropdown, making it a lot more compact
and a lot easier to add multiple actions.
  
  [17f50cb4c88aee0](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/17f50cb4c88aee0) Itay Neeman *2016-01-30 02:56:30*

  Updating CHANGELOG.md

 * And correcting test case assertion for reviewers variables.
  
  [f016b29c1665e15](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/f016b29c1665e15) Tomas Bjerre *2016-01-26 16:47:28*

## 2.14
### No issue
  Fixing IndexOutOfBoundsException if no reviewers and using reviewers variable

 * Adding tests to reviewers variables.
 * Also changing changelog.md to specify exact names of new variables in 2.13.
  
  [d84ea711e189fa8](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/d84ea711e189fa8) Tomas Bjerre *2016-01-25 20:43:25*

## 2.13
### GitHub [#90](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/90) Add an option to ignore certificates
  Allowing SSL certificates to be ignored 

 * Also making keystore configurable in admin GUI.
  
  [cd2321799656b26](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/cd2321799656b26) Tomas Bjerre *2016-01-24 18:12:02*

### GitHub [#93](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/93) Get reviewers of pr
  update: update changelog.md and readme.md.
  
  [60732c85dbda9a8](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/60732c85dbda9a8) 张盟 *2016-01-25 14:24:07*

### No issue
  update: support reviewers list
  
  [fcd612e0d9512a2](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/fcd612e0d9512a2) 张盟 *2016-01-25 09:15:49*

  Correcting link to blog post in README.md

 * And renaming package in test source
  
  [982d5db365e5ff5](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/982d5db365e5ff5) Tomas Bjerre *2016-01-22 17:03:09*

## 2.12
### GitHub [#82](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/82) Wrong PULL_REQUEST_URL
  Fixing PULL_REQUEST_URL-bug correctly with getSlug
  
  [c19f72f04d33d9e](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/c19f72f04d33d9e) Tomas Bjerre *2015-11-09 16:36:20*

### No issue
  Renaming application variable to lowercase
  
  [0cd2c14c0b1f1e3](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/0cd2c14c0b1f1e3) Tomas Bjerre *2015-11-09 16:28:05*

## 2.11
### GitHub [#82](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/82) Wrong PULL_REQUEST_URL
  Replacing spaces with dashes in PULL_REQUEST_URL 

 * Was evaluating to wrong URL if repo name included spaces.
  
  [1e2d237a8c565b9](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/1e2d237a8c565b9) Tomas Bjerre *2015-11-06 19:27:17*

## 2.10
### GitHub [#78](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/78) Processing on Bitbucket Server&#39;s event threads
  Processing events on Bitbucket Server's event threads
  
  [eef94cd53904b7e](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/eef94cd53904b7e) Tomas Bjerre *2015-10-16 15:48:32*

## 2.9
### GitHub [#76](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/76) Wrong url built when using &amp; in branch names
  Url encoding evaluated values when they are used in URL invocations
  
  [6925ea2649c3b8a](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/6925ea2649c3b8a) Tomas Bjerre *2015-10-15 14:55:40*

## 2.8
### GitHub [#75](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/75) Variable for merged pull request commit hash
  Hash of merged commit (only available for merged-event)
  
  [d04d4bbcb5b1a6e](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/d04d4bbcb5b1a6e) Tomas Bjerre *2015-10-14 19:06:05*

## 2.7
### No issue
  New variables
 * ${PULL_REQUEST_REVIEWERS_APPROVED_COUNT} Number of reviewers that approved the PR.
 * ${PULL_REQUEST_PARTICIPANTS_APPROVED_COUNT} Number of participants that approved the PR.
 * These can be used to, for example, show a trigger button only if there are non-zero number of approvals.
  
  [af17e040766546b](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/af17e040766546b) Tomas Bjerre *2015-10-13 18:49:22*

## 2.6
### GitHub [#73](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/73) In Bitbucket, no forms are visible in the plugin&#39;s configuration screen
  Saving with checked checkboxes RESCOPED_FROM/TO and BUTTON_TRIGGER was not reflected in GUI
  
  [8884fb08982f2d0](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/8884fb08982f2d0) Tomas Bjerre *2015-10-09 20:54:28*

### GitHub [#74](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/74) In configuration on a save, not saving all info (e.g. checkbox checked).
  Saving with checked checkboxes RESCOPED_FROM/TO and BUTTON_TRIGGER was not reflected in GUI
  
  [8884fb08982f2d0](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/8884fb08982f2d0) Tomas Bjerre *2015-10-09 20:54:28*

## 2.5
### GitHub [#73](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/73) In Bitbucket, no forms are visible in the plugin&#39;s configuration screen
  Avoiding admin page to crash if entering quote as value of a field
  
  [b3b7d01fcdfbd4f](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/b3b7d01fcdfbd4f) Tomas Bjerre *2015-10-08 16:09:32*

### No issue
  Migrating Travis CI to container-based infrastructure
  
  [7913ee7538b8a34](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/7913ee7538b8a34) Tomas Bjerre *2015-09-26 17:47:16*

## 2.4
### GitHub [#68](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/68) wording of new &quot;trigger conditions&quot;
  Wording of new trigger conditions
  
  [737d040fe33511a](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/737d040fe33511a) Tomas Bjerre *2015-09-26 06:55:08*

### GitHub [#71](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/71) buttons should be disabled (or just plain hidden) when the PR is closed (declined/merged)
  Adding 'Triggers do not apply to' option with values DECLINED and MERGED
  
  [7e956068fee19be](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/7e956068fee19be) Tomas Bjerre *2015-09-26 09:35:01*

### No issue
  Falling back on 1.x settings if no 2.x settings
  
  [e8ecb22d8fe68b5](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/e8ecb22d8fe68b5) Tomas Bjerre *2015-09-26 10:42:43*

## 2.3
### GitHub
  Bugfix: Loading CSS and JS in admin-page
  
  [ef9fcc167f1daf5](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/ef9fcc167f1daf5) Tomas Bjerre *2015-09-25 16:58:04*

## 2.2
### No issue
  * Building against Bitbucket 4.0.0
 * Was using EAP
  
  [a65efc689746dd0](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/a65efc689746dd0) Tomas Bjerre *2015-09-22 19:25:44*

## 2.1
### GitHub [#69](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/69) No authentication in the injection url
  Adding authentication in the injection url
  
  [ac0b07f4647d520](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/ac0b07f4647d520) Tomas Bjerre *2015-09-22 19:02:17*

## 2.0
### No issue
  Bitbucket 4.0 Compatible

 * Adding banner image used in Marketplace
 * Downloading Atlassian Plugin SDK from tar.gz archive in Travis
  * The APT repo is sometimes unavailable
  
  [5403e2ab255c083](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/5403e2ab255c083) Tomas Bjerre *2015-09-17 01:36:43*

  Cleaning
  
  [8515ee50aa7da3a](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/8515ee50aa7da3a) Tomas Bjerre *2015-09-10 17:12:32*

## 1.28
### GitHub [#64](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/64) [feature] disable a trigger if there are merge conflicts
  Disable a trigger if there are merge conflicts
  
  [26977070d5f8b53](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/26977070d5f8b53) Tomas Bjerre *2015-09-09 20:09:16*

### GitHub [#65](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/65) [feature] new parameter PULL_REQUEST_TITLE
  New variable PULL_REQUEST_TITLE
  
  [693476d0c3cac7e](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/693476d0c3cac7e) Tomas Bjerre *2015-09-09 18:00:46*

## 1.27
### GitHub [#56](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/56) No valid crumb in POST request
  Adding optional regular expression to injection feature 

 * To be able to extract crumb from Jenkins even if primitive XPath result sets forbidden
  
  [bcd78419efe26e8](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/bcd78419efe26e8) Tomas Bjerre *2015-09-02 17:15:45*

## 1.26
### No issue
  Removing XPath alternative

 * Introduced in version 1.22. It may not work in all installations. And is is not needed for Jenkins, which was the original use case.
 * Also adjusting admin GUI
  
  [defa1014c12bb8c](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/defa1014c12bb8c) Tomas Bjerre *2015-09-01 19:42:36*

## 1.25
### GitHub [#62](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/62) bug: no POST content
  Bugfix: Sending post content in POST and PUT notifications
  
  [041c84213f9f48c](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/041c84213f9f48c) Tomas Bjerre *2015-09-01 16:07:09*

## 1.24
### GitHub [#60](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/60) NullPointerException after upgrading
  Removing JsonPath alternative, introduced in version 1.22

 * It causes classpath issues for some users
  
  [74efd45df49109d](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/74efd45df49109d) Tomas Bjerre *2015-09-01 15:40:24*

### GitHub [#61](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/61) NoClassDefFoundError JsonReader
  Removing JsonPath alternative, introduced in version 1.22

 * It causes classpath issues for some users
  
  [74efd45df49109d](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/74efd45df49109d) Tomas Bjerre *2015-09-01 15:40:24*

### GitHub [#62](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/62) bug: no POST content
  Removing JsonPath alternative, introduced in version 1.22

 * It causes classpath issues for some users
  
  [74efd45df49109d](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/74efd45df49109d) Tomas Bjerre *2015-09-01 15:40:24*

## 1.23
### GitHub [#60](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/60) NullPointerException after upgrading
  Removing SLF4J usage to deal with class path issues
  
  [f5d9426be0b8a72](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/f5d9426be0b8a72) Tomas Bjerre *2015-08-31 18:17:22*

### No issue
  Reformatting code

 * Also correcting spelling in CHANGELOG
  
  [fecf223f8a962c1](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/fecf223f8a962c1) Tomas Bjerre *2015-08-29 20:02:39*

## 1.22
### GitHub [#56](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/56) No valid crumb in POST request
  Adding INJECTION_URL_VALUE variable 

 * Evaluated in content of a URL. Raw content or value of a xPath or JsonPath
 * Bugfix: Closing inputstream after invokation. This may have caused "IOException: Too many open files"
  
  [e984b77686d1583](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/e984b77686d1583) Tomas Bjerre *2015-08-29 12:41:39*

## 1.21
### GitHub [#50](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/50) ids of HTML elements need to be unique
  Using label without ID:s in admin GUI 

* To avoid using same ID:s multiple times
  
  [abfd8128aec2784](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/abfd8128aec2784) Tomas Bjerre *2015-08-18 19:38:21*

### GitHub [#51](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/51) trigger button should be hidden if no rule with BUTTON_TRIGGER event matches the PR
  Hide buttons in PR if no notification configured
  
  [c67d1a7a8566245](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/c67d1a7a8566245) Tomas Bjerre *2015-08-18 20:13:40*

### No issue
  Validating only buttons as buttons

* Was validating also visibility config as button.
  
  [4914c81b382579d](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/4914c81b382579d) Tomas Bjerre *2015-08-18 18:58:58*

## 1.20
### GitHub [#25](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/25) Enable  configuration in per-repository hook screen
  Optionally allow users and admins to configure the plugin
  
  [325719d15073ec6](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/325719d15073ec6) Tomas Bjerre *2015-08-17 20:27:48*

### GitHub [#48](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/48) MERGED event is not risen
  Testing correct bug reported in
  
  [7e125b1d34d8e61](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/7e125b1d34d8e61) Tomas Bjerre *2015-08-13 09:55:42*

## 1.19
### GitHub [#42](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/42) Comments on closed/merged issues trigger Commented event
  Only ignore events on closed pull requests if its a COMMENT-event
  
  [c1912f15db492c7](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/c1912f15db492c7) Tomas Bjerre *2015-08-13 09:44:16*

### GitHub [#48](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/48) MERGED event is not risen
  Only ignore events on closed pull requests if its a COMMENT-event
  
  [c1912f15db492c7](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/c1912f15db492c7) Tomas Bjerre *2015-08-13 09:44:16*

### No issue
  Cleaning

* Adding logo images
* Updating screenshots
* Minor refactoring
* Correcting CHANGELOG and README
  
  [4497d053f0e857c](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/4497d053f0e857c) Tomas Bjerre *2015-08-09 18:48:39*

## 1.18
### GitHub [#33](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/33) Add an Trigger Notification Button on Pull Request View
  Trigger Notification Button on Pull Request View
  
  [a3854c75c7af440](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/a3854c75c7af440) Tomas Bjerre *2015-08-09 11:10:10*

### GitHub [#39](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/39) Possible issue with isAdmin
  Avoiding endless loop if user not 'System Admin' when editing configuration 

* Plugin will now respond with error message saying user must be 'System Admin'
* Redirecting to login will just cause an endless loop
  
  [4c1c37c3abd3b68](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/4c1c37c3abd3b68) Tomas Bjerre *2015-08-07 20:08:48*

### GitHub [#43](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/43) Config page becomes rather long (hard to overview) in large installations
  Naming triggers
  
  [da1c1dde94e3443](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/da1c1dde94e3443) Tomas Bjerre *2015-08-07 14:18:38*

### GitHub [#45](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/45) Provide URL for Pull Request as parameter to be used in notification
  Adding test cases and formatting code after merge

* PR: 
* Issue
  
  [bc2820ea6e97a41](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/bc2820ea6e97a41) Tomas Bjerre *2015-08-08 07:14:29*

### GitHub [#47](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/pull/47) [Issue-45] Make URL for Pull Request as parameter ${PULL_REQUEST_URL} available
  Adding test cases and formatting code after merge

* PR: 
* Issue
  
  [bc2820ea6e97a41](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/bc2820ea6e97a41) Tomas Bjerre *2015-08-08 07:14:29*

### Jira
  [] Provide URL for Pull Request as parameter to be used in notification
  
  [788d1069899cd63](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/788d1069899cd63) Christian Galsterer *2015-08-08 06:05:47*

### No issue
  Building against latest Stash and AMPS versions

* Stash: 3.11.1
* Atlassian Maven Plugin Suite: 6.0.3
  
  [ced8eb759739ba4](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/ced8eb759739ba4) Tomas Bjerre *2015-08-06 19:11:22*

## 1.17
### GitHub [#42](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/42) Comments on closed/merged issues trigger Commented event
  Comments on closed/merged issues trigger Commented event
  
  [f43a38efc4de0bf](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/f43a38efc4de0bf) Tomas Bjerre *2015-08-04 14:52:47*

## 1.16
### GitHub [#40](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/40) Reply on Comment, doesn&#39;t trigger Commented event
  Reply on Comment, doesn't trigger Commented event 
* Solved by listening for PullRequestCommentRepliedEvent
  
  [c7065615c3fa241](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/c7065615c3fa241) Tomas Bjerre *2015-08-01 07:42:19*

### No issue
  Update README.md
  
  [30039a0e09718a2](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/30039a0e09718a2) Tomas Bjerre *2015-06-27 16:42:51*

## 1.15
### GitHub [#37](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/issues/37) Pull Request Trigger Firing Twice
  Removing RESCOPED event, will trigger _FROM and _TO instead 
* Also adding logging that shows event name together with from and to hashes.
  
  [440ec961e2ac0e5](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/440ec961e2ac0e5) Tomas Bjerre *2015-06-21 17:19:09*

  Including link to Stash events in README
  
  [76a9217621257e4](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/76a9217621257e4) Tomas Bjerre *2015-06-19 20:16:29*

### No issue
  Correcting LICENSE
  
  [49e83c66078f8cf](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/49e83c66078f8cf) Tomas Bjerre *2015-06-13 07:16:15*

## 1.14
### No issue
  New variables with information about the user who issued the event
  
  [f18ab7e268f0013](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/f18ab7e268f0013) Tomas Bjerre *2015-05-13 17:24:16*

  Doc on RESCOPED, RESCOPED_FROM and RESCOPED_TO events
  
  [075534c181a7eeb](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/075534c181a7eeb) Tomas Bjerre *2015-05-01 08:36:11*

## 1.13
### Jira
  [] PULL_REQUEST_FROM_BRANCH and PULL_REQUEST_TO_BRANCH now contains fhe full branch name.
  
  [8f00c461d72c96e](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/8f00c461d72c96e) Christian Galsterer *2015-04-22 17:50:41*

### No issue
  Updating changelog with BRANCH name bug fix
  
  [c3c9c70e7ef6e48](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/c3c9c70e7ef6e48) Tomas Bjerre *2015-04-22 18:06:29*

## 1.12
### Jira
  [] Add new variables for SSH and HTTP clone URL source and target branch
  
  [001a9c7ce0d89b9](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/001a9c7ce0d89b9) Christian Galsterer *2015-04-18 20:36:11*

### No issue
  Testing clone URL variables
* Updating README and CHANGELOG
  
  [580f1cca1e2f0b6](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/580f1cca1e2f0b6) Tomas Bjerre *2015-04-19 18:54:17*

  Cleanup and more tests
  
  [ed70d2fe9f9ed50](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/ed70d2fe9f9ed50) Tomas Bjerre *2015-04-18 07:09:39*

## 1.11
### No issue
  Custom HTTP headers, proxy support, PULL_REQUEST_COMMENT_TEXT and PULL_REQUEST_VERSION variables
Also:
* Marking plugin as compatible with Stash Data Center
* Replacing spaces in URL with %20
  
  [a213fbd6426fd6f](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/a213fbd6426fd6f) Tomas Bjerre *2015-04-17 21:04:29*

## 1.10
### No issue
  Adding PULL_REQUEST_FROM_BRANCH and PULL_REQUEST_TO_BRANCH variables to make branch names available
  
  [2b6d1c07042c3b9](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/2b6d1c07042c3b9) Tomas Bjerre *2015-04-15 17:19:39*

## 1.9
### No issue
  Adding support for PUT and DELETE
  
  [0e92ec95e6dfd30](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/0e92ec95e6dfd30) Tomas Bjerre *2015-04-14 15:15:43*

## 1.8
### No issue
  Support for HTTP POST requests, with content that is rendered with variables
* Also letting variable ${PULL_REQUEST_ACTION} return RESCOPED_FROM or RESCOPED_TO instead of just RESCOPED
  
  [341c703cbbb0324](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/341c703cbbb0324) Tomas Bjerre *2015-04-13 23:12:16*

  Doc
  
  [69ef82ce0cd4ec6](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/69ef82ce0cd4ec6) Tomas Bjerre *2015-04-10 16:47:07*

## 1.7
### No issue
  Adding event types RESCOPED_FROM and RESCOPED_TO
* RESCOPED_FROM, when only source branch is changed
* RESCOPED_TO, when only target branch is changed
  
  [fd4e411b7d7240b](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/fd4e411b7d7240b) Tomas Bjerre *2015-04-10 16:17:58*

  Not sending authentication headers when user and/or password is not set
  
  [2537dec01f53114](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/2537dec01f53114) Tomas Bjerre *2015-04-10 05:32:29*

## 1.6
### No issue
  Updating changelog
  
  [d8c811576f60d13](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/d8c811576f60d13) Tomas Bjerre *2015-04-08 17:19:46*

  fix(admin): account for type password in css input fields
  
  [035b3f555610cc3](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/035b3f555610cc3) Steven Sojka *2015-04-08 15:55:21*

  Removing unnecessary catch try-catch
  
  [0e4e1f8bb2a857a](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/0e4e1f8bb2a857a) Tomas Bjerre *2015-04-08 15:06:24*

  Removing accidently added line from admin GUI
  
  [4a21a380f1bbe49](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/4a21a380f1bbe49) Tomas Bjerre *2015-04-07 20:49:49*

  fix(admin): change password input to type password
  
  [050296e9217f040](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/050296e9217f040) Steven Sojka *2015-04-07 17:33:45*

## 1.5
### No issue
  Adding author related variables
  
  [475f25de338f7bc](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/475f25de338f7bc) Tomas Bjerre *2015-04-07 20:19:04*

## 1.4
### No issue
  Listening for each specific event, instead of all pull request events
* To avoid handling same event twice
  
  [608e3c714ef53fc](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/608e3c714ef53fc) Tomas Bjerre *2015-04-06 11:19:37*

  Doc updated
  
  [41db3f8eb13d07e](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/41db3f8eb13d07e) Tomas Bjerre *2015-04-03 08:08:30*

## 1.2
### No issue
  Compiling for Java 6 as Stash 2.12 may run on it
  
  [777950639f662ae](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/777950639f662ae) Tomas Bjerre *2015-04-02 19:53:32*

  Avoiding stack trace in log when running tests
  
  [28e1839c5575957](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/28e1839c5575957) Tomas Bjerre *2015-04-01 19:55:17*

  testThatDuplicateEventsFiredInStashAreIgnored was toggling
  
  [6990ef6f46879e7](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/6990ef6f46879e7) Tomas Bjerre *2015-04-01 19:41:52*

  Adding some tests on authentication request property
  
  [2faf842e6d328d4](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/2faf842e6d328d4) Tomas Bjerre *2015-04-01 19:34:43*

  Updating pom.xml, CHANGELOG.md and README.md on JDK 1.7 compatibility
  
  [102ff202abfca8d](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/102ff202abfca8d) Tomas Bjerre *2015-04-01 18:47:03*

  Using Oracle JDK 7 and 8 in Travis CI
  
  [2989ba09247340b](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/2989ba09247340b) Tomas Bjerre *2015-04-01 18:35:28*

  Correct typo and add required JDK version
  
  [68df953937ea401](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/68df953937ea401) Raimana *2015-04-01 10:06:31*

  Updates POM to reflect JDK requirements
  
  [0026e06ca543962](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/0026e06ca543962) Raimana *2015-04-01 09:50:28*

  Compatibility with JDK 1.7

If you ever consider backward compatibility to be relevant.

java.util.Base64 is available since 1.8 and javax.xml.bind.DatatypeConverter since 1.6.
  
  [36f99c11d31c763](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/36f99c11d31c763) Raimana *2015-04-01 09:24:20*

  Adding support for PULL_REQUEST_FROM/TO_HASH, filters and fixing authentication bug
* Making it compatible with Stash 2.12.0
* Some more documentation
* Validating regexp
* Log events
  
  [60bea78e853280f](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/60bea78e853280f) Tomas Bjerre *2015-03-30 15:41:44*

## 1.0
### No issue
  Fixing multiple events issue
  
  [04dd29b87664df9](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/04dd29b87664df9) Tomas Bjerre *2015-03-28 18:47:59*

  doc
  
  [3d93bb18e290f32](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/3d93bb18e290f32) Tomas Bjerre *2015-03-28 18:08:25*

  Invoking URL
  
  [c905ca3cd3b4205](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/c905ca3cd3b4205) Tomas Bjerre *2015-03-28 18:02:00*

  Implementing variables suport
  
  [b84e8c212e162da](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/b84e8c212e162da) Tomas Bjerre *2015-03-28 17:09:39*

  Admin GUI in place
  
  [9e5b6db9b1d0ef1](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/9e5b6db9b1d0ef1) Tomas Bjerre *2015-03-28 12:12:27*

  Initial commit
  
  [1ba6feddd199edc](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket/commit/1ba6feddd199edc) Tomas Bjerre *2015-03-27 06:06:24*

