FROM eclipse-temurin:17
LABEL authors="anieshbaratam"
WORKDIR /home
COPY ./target/app-0.0.1-SNAPSHOT.jar ims-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","ims-service.jar"]