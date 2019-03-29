# Azure Pipelines

Here is how to integrate with Azure Pipelines (VSTS).

* URL should be set to `https://<account name>.visualstudio.com/<project name>/_apis/build/builds?api-version=5.0` or `https://dev.azure.com/<account name>/<project name>/_apis/build/builds?api-version=5.0`
* Method should be **POST**
* Post content should be:
```
{
        "definition": {
            "id": <build definitionId from url>
        }
        "reason": "pullRequest",
        "sourceBranch": "${PULL_REQUEST_FROM_ID}",
        "sourceVersion": "${PULL_REQUEST_FROM_HASH}",
        "properties": {
            "pullRequest": {
                "url": "${PULL_REQUEST_URL}",
                "user": "${PULL_REQUEST_USER_EMAIL_ADDRESS}",
                "title": "${PULL_REQUEST_TITLE}",
                "version": "${PULL_REQUEST_VERSION}",
                "author": "${PULL_REQUEST_AUTHOR_EMAIL}",
                "reviewers": "${PULL_REQUEST_REVIEWERS_EMAIL}"
            }
        }
}
```
* Encode post content as JSON
* In **Headers** add "Content-Type" with value "application/json"

Additional data can be sent in the post. See [Azure API Docs](https://docs.microsoft.com/en-us/rest/api/azure/devops/build/Builds/Queue?view=azure-devops-rest-5.0) for more info. The properties collection is not shown in the VSTS UI, but is returned in API calls.

It's a good idea to use a personal access token from a service principal for authentication. The queued builds will all state that they were triggered by the owner of the token.
