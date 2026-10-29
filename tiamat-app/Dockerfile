FROM bellsoft/liberica-openjre-debian:25.0.5.0.3-5
WORKDIR /deployments
COPY target/tiamat.jar tiamat.jar
RUN addgroup --gid 2000 appuser && adduser --uid 2000 --disabled-password --ingroup appuser appuser
RUN mkdir -p /deployments/data && chown -R appuser:appuser /deployments/data
USER appuser
EXPOSE 8777
CMD  [ "java", "-jar", "tiamat.jar"]