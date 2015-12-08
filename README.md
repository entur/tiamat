# Tiamat

Module also known as the backend for "Holdeplassregisteret"

# Build
 ```mvn clean install```

# Local run
 ```mvn spring-boot:run```

# Run with external properties file
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
```

To start Tiamat with this configuration, specify **spring.config.location**:

```mvn spring-boot:run -Dspring.config.location=/path/to/application-production.properties```

There is a PostgreSQL docker container in vagrant. It can be provisioned by using the tag **rb**:

```ONLY_TAGS=rb vagrant provision```

# Run with some bootstrapped data from GTFS stops.txt
```mvn spring-boot:run -Dspring.profiles.active=bootstrap```

# Run with external config **and** bootstrapped data:
```mvn spring-boot:run -Dspring.profiles.active=bootstrap -Dspring.config.location=/path/to/application-production.properties```

# Docker image
 ```mvn -Pf8-build```

# Run the docker image in, eh, docker
choose **one** of:
     * `mvn docker:start`
     * `docker run -it rutebanken/tiamat:0.0.1-SNAPSHOT`
* For more docker plugin goals, see: http://ro14nd.de/docker-maven-plugin/goals.html


# See also
https://rutebanken.atlassian.net/wiki/display/REIS/Holdeplassregister