FROM eclipse-temurin:17.0.1_12-jdk-alpine
WORKDIR /deployments
COPY target/tiamat-*-SNAPSHOT.jar tiamat.jar
RUN addgroup appuser && adduser --disabled-password appuser --ingroup appuser
RUN mkdir -p /deployments/data && chown -R appuser:appuser /deployments/data
USER appuser
EXPOSE 8777
CMD java $JAVA_OPTIONS -jar tiamat.jar