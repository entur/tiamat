# Tiamat ![Build](https://github.com/entur/tiamat/actions/workflows/entur-push.yml/badge.svg)

Tiamat is the Stop Place Register.
It is used nationally in Norway, and other places.
Tiamat is created with technologies like Spring Boot, Hibernate, Postgis, Jersey and Jackson.

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

```shell
mvn clean install
```

You need the directory `/deployments/data` with rights for the user who
performs the build.

## Integration tests
Tiamat uses testcontainers to run integration tests against a real database.  To run Testcontainers-based tests, you need a Docker-API compatible container runtime
for more detail see https://www.testcontainers.org/supported_docker_environment/

(default profiles are set in application.properties)

## Running the service

There are several options for running the service depending on what you need.

 - [Run locally for development](#run-locally-for-development) is for people intending to maintain, modify and improve 
   tiamat's source code
 - [Run tiamat with Docker compose](#run-tiamat-with-docker-compose) if you just need to get the service running
 - [Run with external properties file and PostgreSQL](#run-with-external-properties-file-and-postgresql) for low 
   level debugging

> **Note!** Each of these configurations use unique port numbers and such, be sure to read the provided documentation 
> and configuration files for more details.

## Run locally for development

Local development is a combination of using Docker Compose based configuration for starting up the supporting 
services and running Spring Boot with at least `local` profile enabled.

When running,

 - tiamat will be available at `http://localhost:37888`
 - PostGIS will be available at `localhost:37432`

### 1. Start Local Environment through Docker Compose

Tiamat has [docker-compose.yml](./docker-compose.yml) which contains all necessary dependent services for running tiamat in
various configurations. It is assumed this environment is always running when the service is being run locally
(see below).

> **Note!** This uses the compose version included with modern versions of Docker, not the separately installable
> `docker-compose` command.

All Docker Compose commands run in relation to the `docker-compose.yml` file located in the same directory in which the
command is executed.

```shell
# run with defaults - use ^C to shutdown containers
docker compose up
# run with additional profiles, e.g. with LocalStack based AWS simulator
docker compose --profile aws up
# run in background
docker compose up -d # or --detach
# shutdown containers
docker compose down
# shutdown containers included in specific profile
docker compose --profile aws down
```

#### Supported Docker Compose profiles

Docker Compose has its own profiles which start up additional supporting services to e.g. make specific feature 
development easier. You may include any number of additional profiles when working with Docker Compose by listing 
them in the commands with the `--profile {profile name}` argument. Multiple profiles are activated by providing the 
same attribute multiple times, for example starting Compose environment with profiles a and b would be
```shell
docker compose --profile a --profile b up
```

The provided profiles for Tiamat development are


| profile | description                                                                                       |
|:--------|---------------------------------------------------------------------------------------------------|
| `aws`   | Starts up [LocalStack](https://www.localstack.cloud/) meant for developing AWS specific features. |


See [Docker Compose reference](https://docs.docker.com/compose/reference/) for more details.

See [Supported Docker Compose Profiles](#supported-docker-compose-profiles) for more information on provided profiles.

### 2. Run the Service

#### Available Spring Boot Profiles

> **Note!** You must choose at least one of the options from each category below!

> **Note!** `local` profile must always be included!

##### Storage

| profile                | description                                                                                                                                                     |
|:-----------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `gcs-blobstore`        | GCP GCS implementation of tiamat's blob storage                                                                                                                 |
| `local-blobstore`      | Use local directory as backing storage location.                                                                                                                |
| `rutebanken-blobstore` | Use [`rutebanken-helpers/storage`][rutebanken-storage] based implementation for storage. Must be combined with one of the supported extra profiles (see below). |

[rutebanken-storage]: https://github.com/entur/rutebanken-helpers/tree/master/storage

###### Supported `rutebanken-blobstore` extra profiles

If this profile is chosen, an additional implementation must be chosen to activate the underlying actual implementation.
Supported extra profiles are

| extra profile          | description                              |
|:-----------------------|------------------------------------------|
| `local-disk-blobstore` | Similar to `local-blobstore`.            |
| `in-memory-blobstore`  | Entirely in-memory based implementation. |
| `s3-blobstore`         | AWS S3 implementation.                   |

**Example: Activating `in-memory-blobstore` for local development**
```properties
spring.profiles.active=local,rutebanken-blobstore,in-memory-blobstore,local-changelog
```

See the [`RutebankenBlobStoreServiceConfiguration`](./src/main/java/org/rutebanken/tiamat/config/RutebankenBlobStoreConfiguration.java)
class for configuration keys and additional information.

##### Changelog

| profile           | description                                                        |
|:------------------|--------------------------------------------------------------------|
| `local-changelog` | Simple local implementation which logs the sent events to `stdout` |
| `activemq`        | JMS based ActiveMQ implementation.                                 |
| `google-pubsub`   | GCP PubSub implementation for publishing tiamat entity changes.    |

#### Supported Docker Compose Profiles

Tiamat's [`docker-compose.yml`](./docker-compose.yml) comes with built-in profiles for various use cases. The profiles 
are mostly optional, default profile contains all mandatory configuration while the named profiles add features on 
top of that. You can always activate zero or more profiles at the same time, e.g.

```shell
docker compose --profile first --profile second up
# or
COMPOSE_PROFILES=first,second docker compose up
```

### Default profile (no activation key)

Starts up PostGIS server with settings matching the ones in [`application-local.properties`](./src/main/resources/application-local.properties).

### `aws` profile

Starts up [LocalStack](https://www.localstack.cloud/) meant for developing AWS specific features.

See also [Disable AWS S3 Autoconfiguration](#disable-aws-s3-autoconfiguration), [NeTEx Export](#netex-export).

#### Run It!

**IntelliJ**: Right-click on `TiamatApplication.java` and choose Run (or Cmd+Shift+F10). Open Run -> Edit 
configurations, choose the correct configuration (Spring Boot -> App), and add a comma separated list of desired 
profiles (e.g. `local,local-blobstore,activemq`) to Active profiles. Save the configuration.

**Command line**: `mvn spring-boot:run`

## Run tiamat with Docker compose
To run Tiamat with Docker compose, you need to have a docker-compose.yml file. In docker-compose folder you will find a compose.yml file.:

```shell
docker compose up
```

This will start Tiamat with PostgreSQL and Hazelcast. and you can access Tiamat on http://localhost:1888 and the database on http://localhost:5433 
and graphiql on http://localhost:8777/services/stop_places/graphql , At start up tiamat copy empty schema to the database. Spring properties are set in application.properties.
Security is disabled in this setup.

## Run with external properties file and PostgreSQL
To run with PostgreSQL you need an external `application.properties`. Below is an example of `application.properties`:

```properties
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
spring.flyway.table=schema_version

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

#OAuth2 Resource Server
spring.security.oauth2.resourceserver.jwt.issuer-uri=https:http://localhost:8082/realms/entur

spring.cloud.gcp.pubsub.enabled=false

aspect.enabled=true

netex.id.valid.prefix.list={TopographicPlace:{'KVE','WOF','OSM','ENT','LAN'},TariffZone:{'*'},FareZone:{'*'},GroupOfTariffZones:{'*'}}

server.port=1888

blobstore.gcs.blob.path=exports
blobstore.gcs.bucket.name=tiamat-test
blobstore.gcs.project.id=carbon-1287

security.basic.enabled=false
management.security.enabled=false
authorization.enabled = true
rutebanken.kubernetes.enabled=false

async.export.path=/tmp

publicationDeliveryUnmarshaller.validateAgainstSchema=false
publicationDeliveryStreamingOutput.validateAgainstSchema=false
netex.validPrefix=NSR
netex.profile.version=1.12:NO-NeTEx-stops:1.4
blobstore.local.folder=/tmp/local-gcs-storage/tiamat/export
spring.profiles.active=local-blobstore,activemq
```

To start Tiamat with this configuration, specify **spring.config.location**:

`java -jar -Dspring.config.location=/path/to/tiamat.properties --add-opens java.base/java.lang=ALL-UNNAMED -Denv=dev tiamat-0.0.2-SNAPSHOT.jar`

## Database

### HikariCP
Tiamat is using HikariCP. Most properties should be be possible to be specified in in application.properties, like `spring.datasource.initializationFailFast=false`. More information here. https://github.com/brettwooldridge/HikariCP/wiki/Configuration
See also http://stackoverflow.com/a/26514779

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
A detailed guide on how to setup Keycloak can be found [here](./Keycloak_Setup_Guide.md).

## Validation for incoming and outgoing NeTEx publication delivery

It is possible to configure if tiamat should validate incoming and outgoing NeTEx xml when unmarshalling or marshalling publication deliveries.
Default values are true. Can be deactivated with setting properties to false.
```properties
publicationDeliveryStreamingOutput.validateAgainstSchema=false
publicationDeliveryUnmarshaller.validateAgainstSchema=true
```

## Synchronous NeTEx export with query params
It is possible to export stop places and topographic places directly to NeTEx format. This is the endpoint:
https://api.dev.entur.io/stop-places/v1/netex

### Query by name example:
```http request
GET https://api.dev.entur.io/stop-places/v1/netex?q=Arne%20Garborgs%20vei
```

### Query by ids that contains the number 3115
```http request
GET https://api.dev.entur.io/stop-places/v1/netex?q=3115
```

### Query by stop place type
```http request
GET https://api.dev.entur.io/stop-places/v1/netex?stopPlaceType=RAIL_STATION
```
It is also possible with multiple types.

### Query by municipality ID
```http request
GET https://api.dev.entur.io/stop-places/v1/netex?municipalityReference=KVE:TopographicPlace:1003
```

### Query by county ID
```http request
GET https://api.dev.entur.io/stop-places/v1/netex?countyReference=KVE:TopographicPlace:11
```

### Limit size of results
```http request
GET https://api.dev.entur.io/stop-places/v1/netex?size=1000
```

### Page
```http request
GET https://api.dev.entur.io/stop-places/v1/netex?page=1
```

### ID list
You can specify a list of NSR stop place IDs to return
```http request
GET https://api.dev.entur.io/stop-places/v1/netex?idList=NSR:StopPlace:3378&idList=NSR:StopPlace:123
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

```shell
curl -XPOST -H"Content-Type: application/xml" -d@my-nice-netex-file.xml http://localhost:1997/services/stop_places/netex
```

### Importing with importType=INITIAL

When importing with _importType=INITIAL_, a parallel stream will be created, spawning the original process. During import, user authorizations is checked, thus accessing SecurityContextHolder.
By default, SecurityContextHolder use DEFAULT\_LOCAL\_STRATEGY. When using INITIAL importType, you should tell Spring to use MODE\_INHERITABLETHREADLOCAL for SecurityContextHolder, allowing Spring to duplicate Security Context in spawned threads.
This can be done setting env variable :
```shell
-Dspring.security.strategy=MODE_INHERITABLETHREADLOCAL
```

If not, the application may complain about user not being authenticated if Spring tries to check authorization in a spawned process

### Importing Fare Zones from FareFrame

Tiamat supports importing fare zones from both SiteFrame (legacy) and FareFrame (proper NeTEx structure). Use the `fareZoneFrameSource` parameter to control the import source:

**Available modes:**
- `SITE_FRAME` (default) - Import fare zones from SiteFrame/tariffZones (backward compatible)
- `FARE_FRAME` - Import fare zones from FareFrame/fareZones only
- `BOTH` - Import from both SiteFrame and FareFrame simultaneously

**Examples:**

```shell
# Default mode (SITE_FRAME) - backward compatible
curl -XPOST -H"Content-Type: application/xml" \
  -d@my-netex-file.xml \
  http://localhost:1888/services/stop_places/netex

# Import from FareFrame only
curl -XPOST -H"Content-Type: application/xml" \
  -d@fareframe-data.xml \
  "http://localhost:1888/services/stop_places/netex?fareZoneFrameSource=FARE_FRAME"

# Import from both SiteFrame and FareFrame
curl -XPOST -H"Content-Type: application/xml" \
  -d@both-frames-data.xml \
  "http://localhost:1888/services/stop_places/netex?fareZoneFrameSource=BOTH"
```

**Response structure:**
- `SITE_FRAME` mode returns SiteFrame with tariffZones (existing behavior)
- `FARE_FRAME` mode returns FareFrame with fareZones only
- `BOTH` mode returns both SiteFrame and FareFrame in the response

**Note:** When using `FARE_FRAME` or `BOTH` modes, ensure your input XML has the correct NeTEx structure with FrameDefaults before ValidBetween elements.

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

