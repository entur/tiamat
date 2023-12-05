FROM maven:3-openjdk-11-slim AS builder

# set up workdir
WORKDIR /build

# download dependencies
COPY ./pom.xml /build
RUN mvn de.qaware.maven:go-offline-maven-plugin:resolve-dependencies

# copy sources
COPY ./src /build/src

# manually install local dependency jar files
RUN mvn install:install-file \
-Dfile=src/main/resources/helper-jars/oauth2-1.89-SNAPSHOT.jar \
-DgroupId=org.entur.helpers \
-DartifactId=oauth2 \
-Dversion=1.89-SNAPSHOT \
-Dpackaging=jar \
-DgeneratePom=true

RUN mvn install:install-file \
-Dfile=src/main/resources/helper-jars/organisation-1.89-SNAPSHOT.jar \
-DgroupId=org.entur.helpers \
-DartifactId=organisation \
-Dversion=1.89-SNAPSHOT \
-Dpackaging=jar \
-DgeneratePom=true

RUN mvn install:install-file \
-Dfile=src/main/resources/helper-jars/hazelcast4-helper-1.89-SNAPSHOT.jar \
-DgroupId=org.entur.helpers \
-DartifactId=hazelcast4-helper \
-Dversion=1.89-SNAPSHOT \
-Dpackaging=jar \
-DgeneratePom=true

RUN mvn clean package spring-boot:repackage -DskipTests

FROM amazoncorretto:11-al2-full

# expose server port
EXPOSE 8080

# download script for reading Docker secrets
RUN curl -o /tmp/read-secrets.sh "https://raw.githubusercontent.com/HSLdevcom/jore4-tools/main/docker/read-secrets.sh"

# copy over helper scripts
COPY ./script/build-jdbc-urls.sh /tmp/

# copy compiled jar from builder stage
COPY --from=builder /build/target/*.jar /usr/src/jore4-tiamat/jore4-tiamat.jar

# read Docker secrets into environment variables and run application
CMD /bin/sh -c "source /tmp/read-secrets.sh && source /tmp/build-jdbc-urls.sh && java -jar /usr/src/jore4-tiamat/jore4-tiamat.jar"

HEALTHCHECK --interval=1m --timeout=5s \
    CMD curl --fail http://localhost:8080/actuator/health
