FROM maven:3-openjdk-17-slim AS builder

# set up workdir
WORKDIR /build

# download dependencies
COPY ./pom.xml /build
RUN mvn de.qaware.maven:go-offline-maven-plugin:resolve-dependencies

# copy sources
COPY ./src /build/src
# package
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17.0.9_9-jre

# expose server port
EXPOSE 1888

# download script for reading Docker secrets
RUN curl -o /tmp/read-secrets.sh "https://raw.githubusercontent.com/HSLdevcom/jore4-tools/main/docker/read-secrets.sh"

# copy over helper scripts
COPY ./script/build-jdbc-urls.sh /tmp/

# copy compiled jar from builder stage
COPY --from=builder /build/target/*.jar /usr/src/jore4-tiamat/jore4-tiamat.jar

# read Docker secrets into environment variables and run application
CMD /bin/bash -c "source /tmp/read-secrets.sh && source /tmp/build-jdbc-urls.sh && java --add-opens java.base/java.lang=ALL-UNNAMED -jar /usr/src/jore4-tiamat/jore4-tiamat.jar"

HEALTHCHECK --interval=1m --timeout=5s \
    CMD curl --fail http://localhost:1888/actuator/health
