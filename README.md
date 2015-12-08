# Tiamat

Module also known as "Holdeplassregister"

# Build
 ```mvn clean install```

# Local run
 ```mvn spring-boot:run```

# Run with production profile
Run with profile **production** (which means using a postgresql instance configured in application-production.properties):

```mvn spring-boot:run -Dspring.profiles.active=production```

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