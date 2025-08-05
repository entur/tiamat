FROM eclipse-temurin:21.0.8_9-jdk-jammy
WORKDIR /deployments
COPY target/tiamat-*-SNAPSHOT.jar tiamat.jar
RUN addgroup appuser && adduser --disabled-password appuser --ingroup appuser
RUN mkdir -p /deployments/data && chown -R appuser:appuser /deployments/data
USER appuser
EXPOSE 8777
CMD  [ "java", "-jar", "tiamat.jar"]