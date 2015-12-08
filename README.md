# Tiamat

Module also known as the backend for "Holdeplassregisteret"

# Build
 ```mvn clean install```

# Local run
 ```mvn spring-boot:run```

# Run with production profile
Run with profile **production** (which means using a postgresql instance configured in application-production.properties). This configuration is separate from the application:
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

```mvn spring-boot:run -Dspring.config.location=/path/to/application-production.properties -Dspring.profiles.active=production```

Later, we will look into using *etcd*

# Run with some bootstrapped data from GTFS stops.txt
```mvn spring-boot:run -Dspring.profiles.active=bootstrap```

# Combine profiles
For instance, run with postgresql (production profile) and load data with the bootstrap profile:
```mvn spring-boot:run -Dspring.profiles.active=production,bootstrap```


# Docker image
 ```mvn -Pf8-build```

# Run the docker image in, eh, docker
choose **one** of:
     * `mvn docker:start`
     * `docker run -it rutebanken/tiamat:0.0.1-SNAPSHOT`
* For more docker plugin goals, see: http://ro14nd.de/docker-maven-plugin/goals.html


# See also
https://rutebanken.atlassian.net/wiki/display/REIS/Holdeplassregister