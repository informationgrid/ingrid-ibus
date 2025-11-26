# InGrid iBus

<img src="https://ingrid-oss.eu/8.2.0/assets/components/ingrid-plattform.png" alt="InformationGrid illustration" width="480" align="right">

This repository is part of **[InGrid](https://ingrid-oss.eu)**, an open-source solution for building, managing, and exposing metadata-driven information systems. 

**About InGrid iBus:**  
Orchestrates communication among InGrid components and delivers straightforward interfaces for unified data access across the InGrid data space.

Features
--------

- manages communication to other InGrid components
- provides several interfaces to access data in the InGrid data space
- provides message routing within the InGrid communication
- enables/disables connected iPlugs
- GUI for easy administration


Requirements
-------------

- a running InGrid Software System

Installation
------------

Download from https://distributions.informationgrid.eu/ingrid-ibus/
 
or

build from source with `mvn clean package`.

Execute

```
java -jar ingrid-ibus-x.x.x-installer.jar
```

and follow the install instructions.

Obtain further information at http://informationgrid.github.io/

# Apache-Configuration

ProxyPass /ibus-gui/ http://<ip-address>:<port>/
ProxyPassReverse /ibus-gui/ http://<ip-address>:<port>/

Contribute
----------

- Issue Tracker: https://github.com/informationgrid/ingrid-ibus/issues
- Source Code: https://github.com/informationgrid/ingrid-ibus

### General Setup

You need an external elasticsearch node you want to store your indexed documents. Make sure to have the following properties correctly configured:

- elastic.remoteHosts
- cluster.name (elasticsearch.properties)

You can use the pre-configured docker image by calling

> docker-compose up -d elastic

There's also an Elasticsearch image with indices and documents. You can use it by modifying the
docker-compose.yml file and setting the following image instead:

> image: docker-registry.wemove.com/ingrid-elasticsearch-with-decompound:6.4.2-numis

If you want to compare search results with an older iBus version, then you can define the old version
inside the docker-compose.yml and run the iBus container

> docker-compose up -d ibus

Afterwards you can access the defined iBus with http://localhost:8888. This one can run parallel
to your local development version.

### Setup IntelliJ Idea

* add project as Maven-module
* add "src/test/resources" to dependency class-directory
* mark directory "target/frontend" as Resources
* run IBusApplication.java

### Set up eclipse project

Import this project as Maven-Project and add the folder "src/test/resources" to the classpath for the run configuration.
The configuration can be done in the file application-default.properties under the test resources.


### Debug under eclipse

Run as Java 

> "src\main\java\de\ingrid\ibus\IBusApplication.java"

or with maven

> mvn spring-boot:run

- execute `mvn package` to build the frontend and let it copy to the resource directory
- add `src/test/resources`, `src/main/release/webapp`  to class path
- the admin gui starts in this sample on port 8100

### Frontend development

To start a local server for the angular application call:

> cd frontend
> npm install # only once
> npm start

Afterwards the server can be accessed through http://localhost:4200. Doing any change on the sources the browser will be automatically updated.

Support
-------

If you are having issues, please let us know: info@informationgrid.eu

License
-------

The project is licensed under the EUPL license.
