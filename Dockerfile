FROM maven:3-openjdk-11-slim AS builder

# set up workdir
WORKDIR /build

# download dependencies
COPY ./pom.xml /build
RUN mvn de.qaware.maven:go-offline-maven-plugin:resolve-dependencies

# copy sources
COPY ./src /build/src
# package using "prod" profile
# COPY ./profiles/prod /build/profiles/prod
# RUN mvn -Pprod clean package spring-boot:repackage
RUN mvn clean package spring-boot:repackage

FROM amazoncorretto:11-al2-full

# expose server port
EXPOSE 8777

# download script for reading Docker secrets
RUN curl -o /tmp/read-secrets.sh "https://raw.githubusercontent.com/HSLdevcom/jore4-tools/main/docker/read-secrets.sh"

# copy compiled jar from builder stage
COPY --from=builder /build/target/*.jar /usr/src/jore4-tiamat/jore4-tiamat.jar

# read Docker secrets into environment variables and run application
CMD /bin/sh -c "source /tmp/read-secrets.sh && java -jar /usr/src/jore4-tiamat/jore4-tiamat.jar"

HEALTHCHECK --interval=1m --timeout=5s \
    CMD curl --fail http://localhost:8080/actuator/health
