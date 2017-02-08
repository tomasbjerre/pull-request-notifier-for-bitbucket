# TeamCity

Here is how to integrate with TeamCity.

* URL should be set to `https://youserver/httpAuth/app/rest/buildQueue`
* Method should be **POST**
* Post content should be:
```
<build>
<buildType id="Ci_Build "/>
<properties>
<property name="PULL_REQUEST_FROM_BRANCH" value="${PULL_REQUEST_FROM_BRANCH}"/>
<property name="PULL_REQUEST_USER_DISPLAY_NAME" value="${PULL_REQUEST_USER_DISPLAY_NAME}"/>
<property name="test" value="reopen"/>
<property name="PULL_REQUEST_ID" value="${PULL_REQUEST_ID}"/>
<property name="PULL_REQUEST_VERSION" value="${PULL_REQUEST_VERSION}"/>
<property name="PULL_REQUEST_TO_REPO_SLUG" value="${PULL_REQUEST_TO_REPO_SLUG}"/>
<property name="PULL_REQUEST_TO_REPO_PROJECT_KEY" value="${PULL_REQUEST_TO_REPO_PROJECT_KEY}"/>
</properties>
</build>
```
* Don't encode post content
* In **Headers** add "Content-Type" with value "application/xml"
