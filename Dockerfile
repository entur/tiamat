FROM openjdk:11-jre
ADD target/tiamat-*-SNAPSHOT.jar tiamat.jar

EXPOSE 8777
CMD java $JAVA_OPTIONS -jar /tiamat.jar