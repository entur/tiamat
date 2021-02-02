FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /deployments
COPY target/tiamat-*-SNAPSHOT.jar tiamat.jar
RUN addgroup appuser && adduser --disabled-password appuser --ingroup appuser
USER appuser
EXPOSE 8777
RUN mkdir -p /deployments/data
CMD java $JAVA_OPTIONS -jar tiamat.jar