# Tiamat

Tiamat is the Stop Place Register.
It is used nationally in Norway, and other places.
Tiamat is created with technologies like Spring Boot, Hibernate, Postgis, Jersey and Jackson.

[![CircleCI](https://circleci.com/gh/entur/tiamat.svg?style=svg)](https://circleci.com/gh/entur/tiamat)

## Core functionality
### NeTEx imports
* Supports different pre steps and merging options for stop places, handling bad data quality.
* Assigns unique IDs to stop places (if desired).
* Validates incoming data against the XML schema.

### NeTEx exports
Supports exporting stop places and other entities to the http://netex-cen.eu/ format.
There are many options for exports:
* Asynchronous exports to google cloud storage. Asynchronous exports handles large amount of data, even if exporting thousands of stop places.
* Synchronous exports directly returned
* Several export parameters and filtering (ex: query or administrative polygons filtering)
* Exports can be validated against the NeTEx schema, ensuring quality.

### GraphQL API
Tiamat provides a rich GraphQL API for stop places, topographic places, path links, tariff zones and so on, support the same parameters as the NeTEx export API.
It also supports mutations. So you can update or create entities.
There are also graphql processes (named functions) which allows functionality like merging quays or stop places.

### A ReactJS Frontend
A frontend for Tiamat is available. It's name is Abzu.
See https://github.com/entur/abzu

### Supports running multiple instances
Tiamat uses Hazelcast memory grid to communicate with other instances in kubernetes.
This means that you can run multiple instances.

### Mapping of IDs
After import stop places and assigning new IDs to stop places, tiamat keeps olds IDs in a mapping table.
The mapping table between old and new IDs is available through the GraphQL API and a REST endpoint.

### Automatic topographic place and tariff zone lookup
Tiamat supports looking up and populating references to tariff zones and topographic places from polygon matches when saving a stop place.

### Versioning
Stop places and other entities are versioned. This means that you have full version history of stop places and what person that made those changes.
Tiamat also includes a diff tool. This is used to compare and show the difference between two versions of a stop place (or other entity).


## Build
`mvn clean install`

You need the directory `/deployments/data` with rights for the user who
performs the build.

Tiamat currently depends on snapshot dependencies. These are open source as well.
You might have to build those first (or fallback to latest release in the pom.xml file)

## Run with in-memory GeoDB (H2)
```
mvn spring-boot:run -Dspring.config.location=src/test/resources/application.properties
```
(default profiles are set in application.properties)

## Run with external properties file and PostgreSQL
To run with PostgreSQL you ned an external application.properties.
Below is an example of application.properties:
```
spring.jpa.database=POSTGRESQL
spring.datasource.platform=postgres
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=create
spring.database.driverClassName=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5435/tiamat
spring.datasource.username=tiamat
spring.datasource.password=<PASSWORD>
spring.datasource.initializationFailFast=false
spring.profiles.active=default
spring.jpa.properties.hibernate.dialect=org.hibernate.spatial.dialect.postgis.PostgisDialect
security.basic.enabled=false

keycloak.realm=rutebanken
keycloak.auth-server-url=https://beta.rutebanken.org/admin/neti/api
keycloak.resource=Tiamat

```

To start Tiamat with this configuration, specify **spring.config.location**:

`mvn spring-boot:run -Dspring.config.location=/path/to/tiamat.properties`

## Database

### HikariCP
Tiamat is using HikariCP. Most properties should be be possible to be specified in in application.properties, like `spring.datasource.initializationFailFast=false`. More information here. https://github.com/brettwooldridge/HikariCP/wiki/Configuration
See also http://stackoverflow.com/a/26514779

### Postgres

#### Run postgres/gis for tiamat in docker for development
```
docker run -p 5435:5432 -e POSTGRES_USER=tiamat -e POSTGRES_PASSWORD=<insertpasswordhere>" -e POSTGRES_INITDB_ARGS="-d" mdillon/postgis:9.6
```

## ID Generation
### Background
During the implementation of Tiamat was desirable to produce NeTEx IDs for stop places more or less gap less.
The reason for this implementation was legacy systems with restrictions of maximum number of digits.

### Configure ID generation
It is possible to control wether IDs should be generated outside Tiamat or not. See the class ValidPrefixList.
Setting the property `netex.validPrefix` tells Tiamat to generate IDs for new entities.
Please note that it is not possible to do an initial import (see ImportType) multiple times with the same IDs.

### How its all connected
It's all initiated by an entity listener annotated with `PrePersist` on the class `IdentifiedEntity` called `IdentifiedEntityListener`.
`NetexIdAssigner` determines if the entity already has an ID or not. `NetexIdProvider` either return a new ID or handles explicity claimed IDs if the configured prefix matches. See `ValidPrefixList` for the configuration of valid prefixes, and prefixes for IDs generated elsewhere. The `GaplessIdGeneratorService` uses Hazelcast to sync state between instances and avoid conflicts.


## Keycloak
Both Tiamat and Abzu are set up to be used with Keycloak.
### Keycloak configuraiton
- Create realm e.g. Entur
- Create client for frontend e.g. abzu
- Under client setting  configure Mapper e.g. entur-roles
- Create Roles e.g. deleteStop, editStop, viewStop
- Add User and assign roles
- Add User attribute roles `{"r":"editStops","o":"NSB","e":{"StopPlaceType":["*"]}}##{"r":"editStops","o":"RB","e":{"EntityType":["*"]}}##{"r":"deleteStops","o":"RB","e":{"EntityType":["StopPlace"]}}##{"r":"deleteStops","o":"RB"}##{"r":"editRouteData","o":"RUT"}`

## Docker image
Tiamat has the fabric8 docker plugin configured in the pom.file. It is optional to use.

## Validation for incoming and outgoing NeTEx publication delivery

It is possible to configure if tiamat should validate incoming and outgoing NeTEx xml when unmarshalling or marshalling publication deliveries.
Default values are true. Can be deactivated with setting properties to false.
```
publicationDeliveryStreamingOutput.validateAgainstSchema=false
publicationDeliveryUnmarshaller.validateAgainstSchema=true
```

## Synchronous NeTEx export with query params
It is possible to export stop places and topographic places directly to NeTEx format. This is the endpoint:
https://api.dev.entur.io/stop-places/v1/netex

### Query by name example:
```
https://api.dev.entur.io/stop-places/v1/netex?q=Arne%20Garborgs%20vei
```

### Query by ids that contains the number 3115
 ```
 https://api.dev.entur.io/stop-places/v1/netex?q=3115
 ```

### Query by stop place type
```
https://api.dev.entur.io/stop-places/v1/netex?stopPlaceType=RAIL_STATION
```
It is also possible with multiple types.

### Query by municipality ID
```
https://api.dev.entur.io/stop-places/v1/netex?municipalityReference=KVE:TopographicPlace:1003
```

### Query by county ID
```
https://api.dev.entur.io/stop-places/v1/netex?countyReference=KVE:TopographicPlace:11
```

### Limit size of results
```
https://api.dev.entur.io/stop-places/v1/netex?size=1000
```

### Page
```
https://api.dev.entur.io/stop-places/v1/netex?page=1
```

### ID list
You can specify a list of NSR stop place IDs to return
```
https://api.dev.entur.io/stop-places/v1/netex?idList=NSR:StopPlace:3378&idList=NSR:StopPlace:123
```

### All Versions
```allVersions```. Acceptable values are true or false. If set to true, all versions of matching stop places will be returned.
If set to false, the highest version by number will be returned for matching stop places. This parameter is not enabled when using the version valitity parmaeter.

### Stop places without location
Match only stop places without location
Use the parameter: ```withoutLocationOnly=true```

### Topographic export mode
The parameter ```topographicPlaceExportMode``` can be set to *NONE*, *RELEVANT* or *ALL*
Relevant topographic places will be found from the exported list of stop places.

### Tariff Zone export mode
The parameter ```tariffZoneExportMode``` can be set to *NONE*, *RELEVANT* or *ALL*
Relevant tariff zones with be found from the exported list of stop places. Because stop places can have a list of tariff zone refs.

### Group of stop places export mode
The parameter ```groupOfStopPlacesExportMode``` can be set to *NONE*, *RELEVANT* or *ALL*
Relevant group of stop places can be found from the exported list of stop places.

### Version validity
The ```versionValidity``` parameter controls what stop places to return.
* ALL: returns all stops in any version (See allVersions attribute), regardless of version validity
* CURRENT: returns only stop place versions valid at the current time
* FUTURE_CURRENT: returns only stop place versions valid at the current time, as well as versions valid in the future.

### Example
```
https://api.dev.entur.io/stop-places/v1/netex?tariffZoneExportMode=RELEVANT&topographicPlaceExportMode=RELEVANT&groupOfStopPlacesExportMode=NONE&q=Nesbru&versionValidity=CURRENT&municipalityReference=KVE:TopographicPlace:0220
```

Returns stop places with current version validity now, matching the query 'Nesbru' and exists in municipality 0220. Fetches relevant tariff zones and topographic places.

## Async NeTEx export from Tiamat
Asynchronous export uploads exported data to google cloud storage. When initiated, you will get a job ID back.
When the job is finished, you can download the exported data.

*Most of the parameters from synchronous export works with asynchronous export as well!*

### Start async export:
```
curl https://api.dev.entur.io/stop-places/v1/netex/export/initiate
```
Pro tip: Pipe the output from curl to xmllint to format the output:
```
curl https://api.dev.entur.io/stop-places/v1/netex/export/initiate | xmllint --format -
```

### Check job status:
```
curl https://api.dev.entur.io/stop-places/v1/netex/export
```

### When job is done. Download it:
```
curl https://api.dev.entur.io/stop-places/v1/netex/export/130116/content | zcat | xmllint --format - > export.xml
```

See also https://rutebanken.atlassian.net/browse/NRP-924

## Truncate data in tiamat database
Clean existing data in postgresql (streamline if frequently used):
```
TRUNCATE stop_place CASCADE;
TRUNCATE quay CASCADE;
TRUNCATE topographic_place CASCADE;
```

## Import data into Tiamat

If you are running this from `spring:run`, then you need to make sure that you have enough memory available for the java process (in case of large data sets).
Another issue is thread stack size, which might need to be increased when coping with really large NeTEx imports.
Example:
```
export MAVEN_OPTS='-Xms256m -Xmx1712m -Xss256m -XX:NewSize=64m -XX:MaxNewSize=128m -Dfile.encoding=UTF-8'
```

### Import NeTEx file without *NSR* IDs
This NeTEx file should not contain NSR ID. (The NSR prefix is configurable in the class ValidPrefixList)
* Tiamat will match existing stops based on name and coordinates.
* Tiamat will merge Quays inside stops that are close, have the same original ID and does not have too different compass bearing.

Tiamat will return the modified NeTEx structure with it's own NSR IDs. Original IDs will be present in key value list on each object.

```
curl  -XPOST -H"Content-Type: application/xml" -d@my-nice-netex-file.xml http://localhost:1997/services/stop_places/netex
```

### Importing with importType=INITIAL

When importing with _importType=INITIAL_, a parallel stream will be created, spawning the original process. During import, user authorizations is checked, thus accessing SecurityContextHolder.
By default, SecurityContextHolder use DEFAULT\_LOCAL\_STRATEGY. When using INITIAL importType, you should tell Spring to use MODE\_INHERITABLETHREADLOCAL for SecurityContextHolder, allowing Spring to duplicate Security Context in spawned threads.
This can be done setting env variable :

    -Dspring.security.strategy=MODE_INHERITABLETHREADLOCAL

If not, the application may complain about user not being authenticated if Spring tries to check authorization in a spawned process

## GraphQL
GraphQL endpoint is available on
```
https://api-test.entur.org/stop_places/1.0/graphql
```

Tip: GraphiQL UI available on https://www-test.entur.org/admin/shamash-nsr/ using *GraphiQL*:
https://github.com/graphql/graphiql
(Use e.g. `Modify Headers` for Chrome to add bearer-token for mutations)

## Flyway
To create the database for tiamat, download and use the flyway command line tool:
https://flywaydb.org/documentation/commandline/

### Migrations
Migrations are executed when tiamat is started.

### Schema changes
Create a new file according to the flyway documentation in the folder `resources/db/migrations`.
Commit the migration together with code changes that requires this schema change. Follow the naming convention.


## Tiamat scripts
Various queries and scripts related to tiamat, has been collected here:
https://github.com/entur/tiamat-scripts


## CircleCI
Tiamat is built using CircleCI. See the .circleci folder.


