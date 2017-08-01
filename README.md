# Tiamat

Module also known as the backend for "Stoppestedsregisteret"

## Build
`mvn clean install`

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

peliasReverseLookupEndpoint=https://beta.rutebanken.org/apiman-gateway/rutebanken/geocoder/1.0/reverse
logging.level.no.rutebanken.tiamat.pelias=TRACE

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
docker run -p 5435:5432 -e POSTGRES_USER=tiamat -e POSTGRES_PASSWORD=<insertpasswordhere>" -e POSTGRES_INITDB_ARGS="-d" mdillon/postgis:9.4
```

#### Database creation in google cloud / kubernetes

Before starting tiamat, you need to run the following commands:

```
kubectl exec -it tiamatdb-HASH psql -- --username=postgres tiamat
CREATE DATABASE template_postgis;
UPDATE pg_database SET datistemplate = TRUE WHERE datname = 'template_postgis';
CREATE DATABASE tiamat WITH encoding 'UTF8' template=template0;
\c tiamat
CREATE EXTENSION postgis;
CREATE EXTENSION postgis_topology;
```


#### Postgres docker container in vagrant
There is a PostgreSQL docker container in vagrant. It can be provisioned by using the tag **rb**:

```
ONLY_TAGS=rb PLAY=build vagrant provision
ONLY_TAGS=rb PLAY=run vagrant provision
```

## Run Keycloak

Bot Tiamat and Abzu are set up to be used with Keycloak. Currently, Keycloak is not running in vagrant so we have to run it standalone. *Currently disabled, see NRP-16*

* Download Keycloak version 1.7.0.CR1 (or newer)
* Change the port in standalone/configuration/standalone.xml** to 18080 : ```{jboss.http.port:18080}```
* ```git pull``` devsetup.
* run:```bin/standalone.sh -Dkeycloak.migration.action=import -Dkeycloak.migration.provider=dir -Dkeycloak.migration.dir=/path/to/git/devsetup/vagrant/provisioning/roles/keycloak/files/ -Dkeycloak.migration.strategy=OVERWRITE_EXISTING```

## Docker image

```
mvn -Pf8-build
```

## Run the docker image in, eh, docker

choose **one** of:

* `mvn docker:start`
* `docker run -it rutebanken/tiamat:0.0.1-SNAPSHOT`

For more docker plugin goals, see: http://ro14nd.de/docker-maven-plugin/goals.html


## Validation for incoming and outgoing NeTEx publication delivery

It is possible to configure if tiamat should validate incoming and outgoing NeTEx xml when unmarshalling or marshalling publication deliveries.
Default values are true. Can be deactivated with setting properties to false.
```
publicationDeliveryStreamingOutput.validateAgainstSchema=false
publicationDeliveryUnmarshaller.validateAgainstSchema=true
```

## Synchronous NeTEx export with query params
It is possible to export stop places and topographic places directly to NeTEx format. This is the endpoint:
https://api-test.entur.org/tiamat/1.0/publication_delivery

### Query by name example:
```
https://api-test.entur.org/tiamat/1.0/publication_delivery?q=Arne%20Garborgs%20vei
```

### Query by ids that contains the number 3115
 ```
 https://api-test.entur.org/tiamat/1.0/publication_delivery?q=3115
 ```

### Query by stop place type
```
https://api-test.entur.org/tiamat/1.0/publication_delivery?stopPlaceType=RAIL_STATION
```
It is also possible with multiple types.

### Query by municipality ID
```
https://api-test.entur.org/tiamat/1.0/publication_delivery?municipalityReference=KVE:TopographicPlace:1003
```

### Query by county ID 
```
https://api-test.entur.org/tiamat/1.0/publication_delivery?countyReference=KVE:TopographicPlace:11
```

### Limit size of results
```
https://api-test.entur.org/tiamat/1.0/publication_delivery?size=1000
```

### Page
```
https://api-test.entur.org/tiamat/1.0/publication_delivery?page=1
```

### ID list
You can specify a list of NSR stop place IDs to return
```
https://api-test.entur.org/tiamat/1.0/publication_delivery?idList=NSR:StopPlace:3378&idList=NSR:StopPlace:123
```

### All Versions
``allVersions```. Acceptable values are true or false. If set to true, all versions of matching stop places will be returned.
If set to false, the highest version by number will be returned for matching stop places. This parameter is not enabled when using the version valitity parmaeter.

### Topographic export mode
The parameter ```topopgraphicPlaceExportMode``` can be set to *RELEVANT* or *ALL*
Relevant topographic places will be found from the exported list of stop places.

### Tariff Zone export mode
The parameter ```tariffZoneExportMode``` can be set to *RELEVANT* or *ALL*
Relevant tariff zones with be found from the exported list of stop places. Because stop places can have a list of tariff zone refs.

### Version validity
This parameter controls what stop places to return.
* ALL: returns all stops in any version (See allVersions attribute), regardless of version validity
* CURRENT: returns only stop place versions valid at the current time
* FUTURE_CURRENT: returns only stop place versions valid at the current time, as well as versions valid in the future.

### Example
```
https://api-test.entur.org/tiamat/1.0/publication_delivery?tariffZoneExportMode=RELEVANT&topographicPlaceExportMode=RELEVANT&q=Nesbru&versionValidity=CURRENT&municipalityReference=KVE:TopographicPlace:0220
```

Returns stop places with current version validity now, matching the query 'Nesbru' and exists in municipality 0220. Fetches relevant tariff zones and topographic places.

## Async NeTEx export from Tiamat
Asynchronous export uploads exported data to google cloud storage. When initiated, you will get a job ID back.
When the job is finished, you can download the exported data.

*Most of the parameters from synchronous export works with asynchronous export as well!*

### Start async export:
```
curl https://api-test.entur.org/tiamat/1.0/publication_delivery/async | xmllint --format -
```

### Check job status:
```
curl https://api-test.entur.org/tiamat/1.0/publication_delivery/async/job | xmllint --format -
```

### When job is done. Download it:
```
curl https://api-test.entur.org/tiamat/1.0/publication_delivery/async/job/130116/content | zcat | xmllint --format - > export.xml
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

If you are running this from `spring:run`, then you need to make sure that you have enough memory available for the java process:
```
export MAVEN_OPTS='-Xms256m -Xmx1712m -Xss256m -XX:NewSize=64m -XX:MaxNewSize=128m -Dfile.encoding=UTF-8'
```

### Import previously exported NeTEx file into emtpy Tiamat
This NeTEx file contains stop places with IDs starting with *NSR*. Tiamat will bypass the ID sequence and insert these IDs as primary keys into the database.
```
curl  -XPOST -H"Content-Type: application/xml" -d@tiamat-export-130117-20170109-094137.xml http://localhost:1997/jersey/publication_delivery/restoring_import
```

### Initial import from previously exported tiamat data with kubernetes
```
pod=`kc get pods  |grep tiamat | awk '{print $1}' | head -n1`
kc exec -i $pod -- bash -c 'cat > /tmp/import' < tiamat-export-124268-20170313-160049.xml
kc exec -it $pod bash
cd /tmp
curl -XPOST -H "Content-type: application/xml" -d@import http://localhost:8777/jersey/publication_delivery/restoring_import
```
See https://github.com/rutebanken/devsetup/blob/master/docs/stolon.md#stolon-tiamat-setup


### Import NeTEx file without *NSR* IDs
This NeTEx file should not contain NSR ID.
* Tiamat will match existing stops based on name and coordinates.
* Tiamat will merge Quays inside stops that are close, have the same original ID and does not have too different compass bearing.

Tiamat will return the modified NeTEx structure with it's own NSR IDs. Original IDs will be present in key value list on each object.

```
curl  -XPOST -H"Content-Type: application/xml" -d@chouette-netex.xml http://localhost:1997/jersey/publication_delivery
```


## See also
https://rutebanken.atlassian.net/wiki/display/REIS/Holdeplassregister


# GraphQL
GraphQL endpoint is available on
```
https://api-test.entur.org/tiamat/1.0/graphql
```

Tip: GraphiQL UI available on https://www-test.entur.org/admin/shamash-nsr/
(Use e.g. `Modify Headers` for Chrome to add bearer-token for mutations)
