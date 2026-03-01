FROM amazoncorretto:25-jdk
WORKDIR /app

COPY build/libs/ocjsys-0.0.1-SNAPSHOT.jar ocjsys.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "ocjsys.jar"]