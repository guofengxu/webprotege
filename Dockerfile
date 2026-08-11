FROM maven:3.9-eclipse-temurin-11 AS build

# MongoDB is needed to run the test suite during the Maven build. Ubuntu no
# longer ships a mongodb package, so it comes from MongoDB's official repo.
RUN apt-get update && \
    apt-get install -y --no-install-recommends git ca-certificates curl gnupg && \
    curl -fsSL https://www.mongodb.org/static/pgp/server-8.0.asc | gpg --dearmor -o /usr/share/keyrings/mongodb-server-8.0.gpg && \
    echo "deb [signed-by=/usr/share/keyrings/mongodb-server-8.0.gpg] https://repo.mongodb.org/apt/ubuntu noble/mongodb-org/8.0 multiverse" > /etc/apt/sources.list.d/mongodb-org-8.0.list && \
    apt-get update && \
    apt-get install -y --no-install-recommends mongodb-org-server

COPY . /webprotege

WORKDIR /webprotege

RUN mkdir -p /data/db \
    && mongod --fork --logpath /var/log/mongod.log \
    && mvn clean package

# The runtime image has no unzip, but this stage has a full JDK, so the war
# is exploded here and copied into the runtime image as a directory.
ARG WEBPROTEGE_VERSION
RUN mkdir -p /webprotege-exploded \
    && cd /webprotege-exploded \
    && jar -xf /webprotege/webprotege-server/target/webprotege-server-${WEBPROTEGE_VERSION}.war

# Tomcat 9, not 10+: WebProtégé uses the javax.servlet API, which Tomcat 10
# replaced with jakarta.servlet.
FROM tomcat:9-jre11-temurin-jammy

RUN rm -rf /usr/local/tomcat/webapps/* \
    && mkdir -p /srv/webprotege

# Here WEBPROTEGE_VERSION is coming from the custom build args WEBPROTEGE_VERSION=$DOCKER_TAG hooks/build script.
# Ref: https://docs.docker.com/docker-hub/builds/advanced/
ARG WEBPROTEGE_VERSION
ENV WEBPROTEGE_VERSION=${WEBPROTEGE_VERSION}

COPY --from=build /webprotege/webprotege-cli/target/webprotege-cli-${WEBPROTEGE_VERSION}.jar /webprotege-cli.jar
COPY --from=build /webprotege-exploded /usr/local/tomcat/webapps/ROOT

WORKDIR /usr/local/tomcat/webapps/ROOT
