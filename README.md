

InGrid iBus
========

The iBus is the central commmunication component in a InGrid System. It handles the communication to other InGrid components and provides several interfaces to access data in the InGrid data space.


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

Download from https://dev.informationgrid.eu/ingrid-distributions/ingrid-ibus/
 
or

build from source with `mvn package assembly:single`.

Execute

```
java -jar ingrid-ibus-x.x.x-installer.jar
```

and follow the install instructions.

Obtain further information at http://informationgrid.github.io/


Contribute
----------

- Issue Tracker: https://github.com/informationgrid/ingrid-ibus/issues
- Source Code: https://github.com/informationgrid/ingrid-ibus
 
### Set up eclipse project

Import this project as Maven-Project and add the folder "src/test/resources" to the classpath for the run configuration.
The configuration can be done in the file application-default.properties under the test resources.

You need an external elasticsearch node you want to store your indexed documents. Make sure to have the following properties correctly configured:

- elastic.remoteHosts
- cluster.name (elasticsearch.properties)



### Debug under eclipse
- Start up an Elasticsearch Cluster (docker)

> docker-compose up -d

- Run as Java "src\main\java\de\ingrid\ibus\IBusApplication.java"

- mvn spring-boot:run

- execute `mvn install` to expand the base web application
- set up a java application Run Configuration with start class `de.ingrid.ibus.BusServer`
- add the program arguments `--descriptor src/test/resources/communication.xml --busurl /ibus-test --adminport 8100 --adminpassword admin` to the Run Configuration
- as VM-argument the path to the webapp must be added "-DwebappDir=src/main/release"
- make sure the tools.jar (from JDK) is added to the classpath of the runtime configuration
- add `src/test/resources`, `src/main/release/webapp`  to class path
- the admin gui starts in this sample on port 8100

### Frontend development

To start a local server for the angular application call:

> npm install # only once
> npm start

Afterwards the server can be accessed through http://localhost:4200. Doing any change on the sources the browser will be automatically updated.

Support
-------

If you are having issues, please let us know: info@informationgrid.eu

License
-------

The project is licensed under the EUPL license.
