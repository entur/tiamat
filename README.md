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

## Integration tests
Tiamat uses testcontainers to run integration tests against a real database.  To run Testcontainers-based tests, you need a Docker-API compatible container runtime
for more detail see https://www.testcontainers.org/supported_docker_environment/

(default profiles are set in application.properties)

## Run with external properties file and PostgreSQL
To run with PostgreSQL you need an external application.properties.
Below is an example of application.properties:
```
spring.jpa.database=POSTGRESQL
spring.datasource.platform=postgres
spring.jpa.properties.hibernate.hbm2ddl.import_files_sql_extractor=org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor
spring.jpa.hibernate.hbm2ddl.import_files_sql_extractor=org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor
spring.jpa.hibernate.ddl-auto=none

spring.http.gzip.enabled=true

#spring.jpa.properties.hibernate.format_sql=true

spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.batch_versioned_data=true

spring.flyway.enabled=true
spring.flyway.table =schema_version


server.compression.mime-types=application/json,application/xml,text/html,text/xml,text/plain

spring.jpa.hibernate.id.new_generator_mappings=true
spring.jpa.hibernate.use-new-id-generator-mappings=true
spring.jpa.properties.hibernate.cache.use_second_level_cache=false
spring.jpa.properties.hibernate.cache.use_query_cache=false
spring.jpa.properties.hibernate.cache.use_minimal_puts=false
spring.jpa.properties.hibernate.cache.region.factory_class=org.rutebanken.tiamat.hazelcast.TiamatHazelcastCacheRegionFactory

netex.import.enabled.types=MERGE,INITIAL,ID_MATCH,MATCH

hazelcast.performance.monitoring.enabled=true
hazelcast.performance.monitoring.delay.seconds=2

management.endpoints.web.exposure.include=info,env,metrics
management.endpoints.prometheus.enabled=true
management.metrics.endpoint.export.prometheus.enabled=true

spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.default_batch_fetch_size=16
spring.jpa.properties.hibernate.generate_statistics=false

changelog.publish.enabled=false


jettyMaxThreads=10
jettyMinThreads=1

spring.datasource.hikari.maximumPoolSize=40
spring.datasource.hikari.leakDetectionThreshold=30000

tiamat.locals.language.default=eng

tariffZoneLookupService.resetReferences=true

debug=true 

# Disable feature detection by this undocumented parameter. Check the org.hibernate.engine.jdbc.internal.JdbcServiceImpl.configure method for more details.
spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults = false

# Because detection is disabled you have to set correct dialect by hand.
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL9Dialect


tariffzoneLookupService.resetReferences=true


spring.jpa.properties.hibernate.dialect=org.hibernate.spatial.dialect.postgis.PostgisDialect


spring.database.driverClassName=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5436/tiamat
spring.datasource.username=tiamat
spring.datasource.password=tiamat

aspect.enabled=true

netex.id.valid.prefix.list={TopographicPlace:{'KVE','WOF','OSM','ENT','LAN'},TariffZone:{'*'},FareZone:{'*'},GroupOfTariffZones:{'*'}}

server.port=1888

blobstore.gcs.blob.path=exports
blobstore.gcs.bucket.name=tiamat-test
blobstore.gcs.credential.path=gcloud-storage.json
blobstore.gcs.project.id=carbon-1287

security.basic.enabled=false
management.security.enabled=false
authorization.enabled = true
rutebanken.kubernetes.enabled=false

#OAuth2 Resource Server
tiamat.oauth2.resourceserver.auth0.ror.jwt.audience=notinuse
tiamat.oauth2.resourceserver.auth0.ror.claim.namespace=notinuse

#keycloak config
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/auth/realms/entur
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/realms/entur/protocol/openid-connect/certs


async.export.path=/tmp

publicationDeliveryUnmarshaller.validateAgainstSchema=false
publicationDeliveryStreamingOutput.validateAgainstSchema=false
netex.validPrefix=NSR
netex.profile.version=1.12:NO-NeTEx-stops:1.4
blobstore.local.folder=/tmp/local-gcs-storage/tiamat/export
spring.profiles.active=local-blobstore,activemq

```

To start Tiamat with specific `application.properties` set the **spring.config.location** system property like:

`java -jar -Dspring.config.location=/path/to/application.properties --add-opens java.base/java.lang=ALL-UNNAMED -Denv=dev tiamat-0.0.2-SNAPSHOT.jar`

*Note: For VSCode users you can set this as a launch config in .vscode/launch.json file (example below)*  
```
{
  "configurations": [
    {
      "type": "java",
      "name": "Spring Boot-TiamatApplication<tiamat>",
      "request": "launch",
      "cwd": "${workspaceFolder}",
      "mainClass": "org.rutebanken.tiamat.TiamatApplication",
      "projectName": "tiamat",
      "args": "",
      "envFile": "${workspaceFolder}/.env",
      "vmArgs": "-Denv=dev -Dspring.config.location=/path/to/application.properties --add-opens java.base/java.lang=ALL-UNNAMED"
    }
  ]
}

```

## Database

### HikariCP
Tiamat is using HikariCP. Most properties should be be possible to be specified in in application.properties, like `spring.datasource.initializationFailFast=false`. More information here. https://github.com/brettwooldridge/HikariCP/wiki/Configuration
See also http://stackoverflow.com/a/26514779

### Postgres

#### Run postgres/gis for tiamat in docker for development
```
docker run -it -d -p 5435:5432 --name postgress-13 -e POSTGRES_USER=tiamat -e POSTGRES_PASSWORD="tiamat" -e POSTGRES_INITDB_ARGS="-d" postgis/postgis:13-master
```

## ID Generation
### Background
During the implementation of Tiamat was desirable to produce NeTEx IDs for stop places more or less gap less.
The reason for this implementation was legacy systems with restrictions of maximum number of digits.

### Configure ID generation
It is possible to control whether IDs should be generated outside Tiamat or not. See the class ValidPrefixList.
Setting the property `netex.validPrefix` tells Tiamat to generate IDs for new entities.
Please note that it is not possible to do an initial import (see ImportType) multiple times with the same IDs.

### How its all connected
It's all initiated by an entity listener annotated with `PrePersist` on the class `IdentifiedEntity` called `IdentifiedEntityListener`.
`NetexIdAssigner` determines if the entity already has an ID or not. `NetexIdProvider` either return a new ID or handles explicity claimed IDs if the configured prefix matches. See `ValidPrefixList` for the configuration of valid prefixes, and prefixes for IDs generated elsewhere. The `GaplessIdGeneratorService` uses Hazelcast to sync state between instances and avoid conflicts.


## Keycloak/Auth0
Both Tiamat and Abzu are set up to be used with Keycloak or Auth0.

Run a docker container with Keycloak:
```
docker run -p 8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:23.0.1 start-dev
```


### Keycloak configuraiton
- Go to http://localhost:8080/admin/master/console/
- From top left create a new realm 
    - Set the realm name to "entur"
- Go to "Clients" 
    - create a new client with id "entur-abzu"
    - leave everything else as default/blank
- Under client Roles:
- Create the 3 roles with names:
    -  "deleteStop", "editStop", "viewStop"
- Go to "Users" 
    - Crete new user called "abzu-user" (this will be used by the frontent app (abzu))
    - Go to "Role mapping" and click "Assign role":
        - From the filters select "Filter by clienst" 
        - assign the 3 roles from the client "entur-abzu"
        - click "Assign"
    - Go to "Attributes" and add the following attribute:
        - key: "roles" with value: `{"r":"editStops","o":"NSB","e":{"StopPlaceType":["*"]}}##{"r":"editStops","o":"RB","e":{"EntityType":["*"]}}##{"r":"deleteStops","o":"RB","e":{"EntityType":["StopPlace"]}}##{"r":"deleteStops","o":"RB"}##{"r":"editRouteData","o":"RUT"}`


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
https://api.dev.entur.io/stop-places/v1/graphql
```

Tip: GraphiQL UI available on https://api.dev.entur.io/graphql-explorer/stop-places using *GraphiQL*:
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


