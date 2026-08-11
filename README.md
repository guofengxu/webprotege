WebProtégé
==========

PLEASE NOTE
===========

**This repository is in the process of being superceded** with a collection of other, more fine-grained, [repositories](https://github.com/search?q=topic%3Awebprotege+org%3Aprotegeproject&type=Repositories). We are moving WebProtégé to a microservice architecture and each microservice and each common library now has its own repository.  While this repository still serves as the repository for the current WebProtégé release, all active development is now taking place in these repositories.  You can read more about this on the [WebProtégé Next Gen Wiki](https://github.com/protegeproject/webprotege-next-gen/wiki/WebProtégé-Next-Generation-Overview).


What is WebProtégé?
-------------------

WebProtégé is a free, open source collaborative ontology development environment.

It provides the following features:
- Support for editing OWL 2 ontologies
- A default simple editing interface, which provides access to commonly used OWL constructs
- Full change tracking and revision history
- Collaboration tools such as, sharing and permissions, threaded notes and discussions, watches and email notifications
- Customizable user interface
- Support for editing OBO ontologies
- Multiple file formats for upload and download of ontologies (supported formats: RDF/XML, Turtle, OWL/XML, OBO, and others)

WebProtégé runs as a Web application. End users access it through their Web browsers.
They do not need to download or install any software. We encourage end-users to use

https://webprotege.stanford.edu

If you have downloaded the webprotege war file from GitHub, and would like to deploy it on your own server,
please follow the instructions at:

https://github.com/protegeproject/webprotege/wiki/WebProtégé-4.0.0-beta-x-Installation

Building
--------

To build WebProtégé from source

1) Clone the github repository
   ```
   git clone https://github.com/protegeproject/webprotege.git
   ```
2) Open a terminal in the directory where you clone the repository to
3) Use maven to package WebProtégé
   ```
   mvn clean package
   ```
5) The WebProtege .war file will be built into the webprotege-server directory

Running from Maven
------------------

To run WebProtégé in SuperDev Mode using maven

1) Start the GWT code server in one terminal window
    ```
    mvn gwt:codeserver
    ```
2) In a different terminal window start the tomcat server
    ```
    mvn -Denv=dev tomcat7:run
    ```
3) Browse to WebProtégé in a Web browser by navigating to [http://localhost:8080](http://localhost:8080)

Running from Docker
-------------------

To run WebProtégé using Docker containers:

1. Enter this following command in the Terminal to start the docker container in the background

   ```bash
   docker-compose up -d
   ```

2. Create the admin user (follow the questions prompted to provider username, email and password)

   ```bash
   docker exec -it webprotege java -jar /webprotege-cli.jar create-admin-account
   ```

3. Browse to WebProtégé Settings page in a Web browser by navigating to [http://localhost:5000/#application/settings](http://localhost:5000/#application/settings)
   1. Define the `System notification email address` and `application host URL`
   2. Enable `User creation`, `Project creation` and `Project import`

To stop WebProtégé and MongoDB:

   ```bash
   docker-compose down
   ```

Sharing the volumes used by the WebProtégé app and MongoDB allow to keep persistent data, even when the containers stop. Default shared data storage:

* WebProtégé will store its data in the source code folder at `./.protegedata/protege` where you run `docker-compose`
* MongoDB will store its data in the source code folder at `./.protegedata/mongodb` where you run `docker-compose`

> Path to the shared volumes can be changed in the `docker-compose.yml` file.

Building and Deploying Your Own Docker Image
--------------------------------------------

The steps above use the pre-built WebProtégé image from Docker Hub. If you would rather build your own image from the source code (for example, from the latest code on the `master` branch), follow the steps below.

The only thing you need installed on your machine is [Docker](https://docs.docker.com/get-docker/). You do not need Java or Maven — the whole application is compiled inside Docker.

1. Get the source code and go into the project folder

   ```bash
   git clone https://github.com/protegeproject/webprotege.git
   cd webprotege
   ```

2. Build the Docker image

   Run the command below. It builds the image and names it with the `latest-dev` tag, so you can easily tell it apart from the official image on Docker Hub:

   ```bash
   docker build -t protegeproject/webprotege:latest-dev --build-arg WEBPROTEGE_VERSION=5.0.0-SNAPSHOT .
   ```

   Be patient — this compiles all of WebProtégé inside Docker and can take 20 minutes or more the first time you run it.

   > The `WEBPROTEGE_VERSION` value must match the `<version>` number near the top of the `pom.xml` file. On the current `master` branch that version is `5.0.0-SNAPSHOT`.

3. Point Docker Compose at your `latest-dev` image

   Create a file named `docker-compose.override.yml` next to `docker-compose.yml`, with this content:

   ```yaml
   version: "3"

   services:
     webprotege:
       image: protegeproject/webprotege:latest-dev
   ```

   Docker Compose reads this file automatically and will use your locally built image instead of downloading the official one.

4. Start WebProtégé and MongoDB

   ```bash
   docker-compose up -d
   ```

5. Finish the setup

   Follow steps 2 and 3 in the [Running from Docker](#running-from-docker) section above to create the admin user and configure the application settings. When you are done, WebProtégé is available at [http://localhost:5000](http://localhost:5000).

If you later change the source code, repeat steps 2 and 4 to rebuild the image and restart the containers with the new version.
