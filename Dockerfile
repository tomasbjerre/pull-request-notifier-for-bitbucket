FROM java:8-jdk

EXPOSE 7990

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

ADD src /usr/src/app/src
ADD README.md /usr/src/app
ADD pom.xml /usr/src/app
ADD setup-atlassian-sdk.sh /usr/src/app

RUN ./setup-atlassian-sdk.sh
ENV PATH opt/atlassian-plugin-sdk/bin:opt/atlassian-plugin-sdk/apache-maven-*/bin:$PATH
RUN atlas-version

ENTRYPOINT exec atlas-run
