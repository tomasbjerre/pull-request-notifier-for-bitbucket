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
}
```
* Encode post content as JSON
* In **Headers** add "Content-Type" with value "application/json"

Additional data can be sent in the post. See [Azure API Docs](https://docs.microsoft.com/en-us/rest/api/azure/devops/build/Builds/Queue?view=azure-devops-rest-5.0) for more info. The build history will state 'Manual build for <owner of personal access token>' for each build triggered by this method.
