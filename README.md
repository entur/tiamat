# Tiamat

Module also known as the backend for "Holdeplassregisteret"

## Build

`mvn clean install`

## Local run

`mvn spring-boot:run`


# Run with external properties file and PostgreSQL
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
```

To start Tiamat with this configuration, specify **spring.config.location**:

`mvn spring-boot:run -Dspring.config.location=/path/to/application.properties`


## HikariCP
Tiamat is using HikariCP. Most properties shoul be be possible to be specified in in application.properties, like `spring.datasource.initializationFailFast=false`. More information here. https://github.com/brettwooldridge/HikariCP/wiki/Configuration
See also http://stackoverflow.com/a/26514779

# Postgres
There is a PostgreSQL docker container in vagrant. It can be provisioned by using the tag **rb**:

```
ONLY_TAGS=rb PLAY=build vagrant provision
ONLY_TAGS=rb PLAY=run vagrant provision
```

# Run with in-memory GeoDB and bootstrap generation of data from GTFS stops.txt

`mvn spring-boot:run -Dspring.profiles.active=geodb,bootstrap -Dspring.config.location=src/test/resources/application.properties`

# Run with in-memory GeoDB without bootstrapped data from GTFS stops.txt

```
mvn spring-boot:run -Dspring.config.location=src/test/resources/application.properties
```
(default profiles are set in internal application.properties)

# Run with external config **and** bootstrap data from GTFS:

Can be used with an empty PostgreSQL.
```
mvn spring-boot:run -Dspring.profiles.active=bootstrap -Dspring.config.location=/path/to/application.properties
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


# Run postgres/gis for tiamat in docker
```
docker run -p 5435:5432 -e POSTGRES_USER=tiamat -e POSTGRES_PASSWORD=<insertpasswordhere>" -e POSTGRES_INITDB_ARGS="-d" mdillon/postgis:9.4
```

# Export *ALL* data from Tiamat

Note that you need to run this with enough memory available, or else you might
run into **java.lang.OutOfMemoryError: GC overhead limit exceeded**. Exactly
how much memory should be tested. **Note** at the time of writing
(24.08.2016), this does not work due to GW timeout,

```
curl -H"Accept: application/xml" -H"Content-type: application/xml" -XGET https://nhr.rutebanken.org/jersey/site_frame > netex_site_frame_stop_places.xml
```

Alternative:

```
kubectl exec -i tiamat-HASH -- curl -H"Accept: application/xml" -H"Content-type: application/xml" -XGET http://localhost:8777/jersey/site_frame > netex_site_frame_stop_places.xml
```

## Database creation

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


## Validation for incoming and outgoing NeTEx publication delivery

It is possible to configure if tiamat should validate incoming and outgoing netex xml when unmarshalling or marshalling publication deliveries.
Default values are true. Can be deactivated with setting properties to false.
```
publicationDeliveryStreamingOutput.validateAgainstSchema
publicationDeliveryUnmarshaller.validateAgainstSchema
```

# Import data into Tiamat

If you are running this from `spring:run`, then you need to make sure that you
have enough memory available for the java process:
`export MAVEN_OPTS='-Xms256m -Xmx1712m -Xss256m -XX:NewSize=64m -XX:MaxNewSize=128m -Dfile.encoding=UTF-8'``

Clean existing data in postgresql (streamline if frequently used):
```
TRUNCATE stop_place CASCADE;
TRUNCATE quay CASCADE;
TRUNCATE topographic_place CASCADE;
```

TODO: Update URLs to publication_delivery
```
curl --max-time 60000 -H"Accept: application/xml" -H"Content-type: application/xml" -XPOST -d@netex_site_frame_stop_places.xml http://nhr.rutebanken.org/jersey/site_frame
```

Alternative:

```
kc exec -i tiamat-HASH -- bash -c 'cat > /tmp/netex.xml' < netex_site_frame_stop_places.xml
kc exec -i tiamat-HASH -- curl --max-time 60000 -H"Accept: application/xml" -H"Content-type: application/xml" -XPOST -d@/tmp/netex.xml http://localhost:8777/jersey/site_frame
```


Example site frame data can be found on the jump server (/var/www/...). There are several example files. Recent versions of Tiamat requires XML namespaces.


*Note that the import above is somewhat fragile. It is developed during the proof of concept. For instance, it does allow you to call the import multiple times. It also might happen that you loose the connection, but the import continues to run in Tiamat. Please monitor the logs of Tiamat while using the import.*


# See also
https://rutebanken.atlassian.net/wiki/display/REIS/Holdeplassregister


# GraphQL
GraphQL endpoint is available on
```
https://test.rutebanken.org/admin/nsr/jersey/graphql
```

## Available data
```
{
	stopPlace {
		id
		name
		shortName
		description
		allAreasWheelchairAccessible
		stopPlaceType
		county
		municipality
		centroid {
			location {
				latitude
				longitude
			}
		}
		quays {
			id
			name
			shortName
			description
			allAreasWheelchairAccessible
			quayType
			compassBearing
			centroid {
			location {
				latitude
				longitude
			}
		}
		}
	}
}
```

## Limit response 
_page_ and _count_ parameters may be used together with all other parameters. Defaults will be used if not provided.
```
{
	stopPlace (page:0 count:20) {
		id 
		name
	}
}
```

## Search

Available search parameters.

### id
```
{
	stopPlace (id:1) {
		id 
		name
	}
}

{
	stopPlace (id:[1,2,3,4,5]) {
		id 
		name
	}
}
```

### stopPlaceType
```
{
	stopPlace (stopPlaceType:onstreetBus) {
		id 
		name
	}
}

{
	stopPlace (stopPlaceType:[onstreetBus, onstreetTram]) {
		id 
		name
	}
}
```

### countyReference
```
{
	stopPlace (countyReference:1) {
		id 
		name
	}
}

{
	stopPlace (countyReference:[1,2,3,4,5]) {
		id 
		name
	}
}
```

### municipalityReference
```
{
	stopPlace (municipalityReference:1) {
		id 
		name
	}
}

{
	stopPlace (municipalityReference:[1,2,3,4,5]) {
		id 
		name
	}
}
```

### query
```
{
	stopPlace (query:"asker") {
		id 
		name
	}
}
```

### Combinations
All query-parameters above may be combined
```
{
	stopPlace (stopPlaceType:onstreetBus municipalityReference:2 query:"stad") {
		name
	}
}
```

## BoundingBox
```
{
	stopPlace (xMin:5.2 yMin:59.9 xMax:12.7 yMax:63.0 ignoreStopPlaceId:1234) {
		id 
		name
		stopPlaceType
		centroid {
			location {
				latitude
				longitude
			}
		}
	}
}
```
Parameter _ignoreStopPlaceId_ is optional 
