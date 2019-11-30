FROM openjdk:11-jre
COPY target/mqtt-exporter-0.0.1-SNAPSHOT.jar /opt/mqtt-exporter-0.0.1-SNAPSHOT.jar
CMD ["java","-XshowSettings:vm", "-XX:+PrintCommandLineFlags", "-jar","/opt/mqtt-exporter-0.0.1-SNAPSHOT.jar", "--spring.config.location=/opt/mqtt-exporter/conf/mqtt-exporter.properties"]
