# Jenkins
Parameterized Jenkins jobs can be triggered remotely by invoking a URL. How you trigger your Jennkins installation may vary depending on how it is configured. Here is, probably, the most complicated scenario where there is CSRF protection and authentication requirements.

The job that you want to trigger must have:
 * *This build is parameterized* checkbox checked.
 * *Trigger builds remotely* checkbox checked.
  * You may, or may not, use a token here.

There is a full job-dsl for this in [here](https://github.com/jenkinsci/violation-comments-to-stash-plugin).

I like to add an *Execute shell* build step and then just do `echo param: $paramName` to test that my parameter shows up in the build job log.

First, you may try to trigger Jenkins with [Curl](https://github.com/curl/curl) from command line and then, when you know how it should be done, configure the plugin.

If your Jenkins is CSRF protected, you need to get a crumb. It can be done like this.
```
curl -s 'http://JENKINS_HOSTNAME/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,":",//crumb)'
```

The response should be something like `Jenkins-Crumb:f122c77298b349b0116140265418ec7f`.

Now you can trigger a build like this (just remove `?token=YOUR_TOKEN` if you are not using a token).

```
curl -u USERNAME:PASSWORD -X POST --data "paramName=paramValue" -H "Jenkins-Crumb:f122c77298b349b0116140265418ec7f" http://JENKINS_HOSTNAME/job/JENKINS_JOB/buildWithParameters?token=YOUR_TOKEN
```

Now that job should have been triggered and you should be able to verify that Jenkins is setup correclty. You may want to SSH to the Bitbucket Server machine and do this, to also verify that firewalls are open.

Now to configure the plugin!

If you need ***authentication***, add your username and password in *Basic authentication*.

If you are using a ***CSRF*** protection in Jenkins, you can use the **Injection URL** feature.
 * Set **Injection URL** field to `http://JENKINS_HOSTNAME/crumbIssuer/api/xml?xpath=//crumb/text()`.
  * You may get an error like *primitive XPath result sets forbidden; implement jenkins.security.SecureRequester*. If so, you can set Injection URL to `http://JENKINS/crumbIssuer/api/xml?xpath=//crumb` in combination with regular expression `<crumb>([^<]*)</crumb>`.
  * A third option is to checkout [this](https://wiki.jenkins-ci.org/display/JENKINS/Secure+Requester+Whitelist+Plugin) Jenkins plugin.
 * In the headers section, set header **Jenkins-Crumb** with value **${INJECTION_URL_VALUE}**. The `Jenkins-Crumb` header name was previously just `.crumb`, use whatever the `curl` command responded with above.

You may trigger the build with `GET` or `POST`.

In ***URL*** add `http://JENKINS_HOSTNAME/job/JENKINS_JOB/buildWithParameters?token=YOUR_TOKEN&paramName=paramValue`.

Thats it! There are some common mistakes.
 * If using ${EVERYTHING_URL}, like `...?token=token&${EVERYTHING_URL}` then in your jenkins job you have to have parameters for each parameter, like `PULL_REQUEST_URL`.
 * Even when using `POST`, you should add the parameters to the `URL`.

#### Jenkins build step
To perform the merge and verify that the pull request builds in its target branch, I do something like this.

```
git clone $TO_REPO  
cd *  
git reset --hard $TO_HASH  
git status  
git remote add from $FROM_REPO  
git fetch --all  
git merge $FROM_HASH  
git --no-pager log --max-count=10 --graph --abbrev-commit

#compile command here ...
```
