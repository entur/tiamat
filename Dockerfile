FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /deployments
COPY target/tiamat-*-SNAPSHOT.jar tiamat.jar
RUN addgroup appuser && adduser --disabled-password appuser --ingroup appuser
RUN chown -R appuser:appuser /deployments
RUN chmod 770 /deployments
USER appuser
EXPOSE 8777
CMD java $JAVA_OPTIONS -jar tiamat.jar