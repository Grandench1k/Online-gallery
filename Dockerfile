FROM maven:3.9.6 AS build
WORKDIR /app
COPY pom.xml .

RUN mvn dependency:go-offline

COPY src src
RUN mvn clean package -DskipTests

FROM openjdk:21
WORKDIR /app

COPY --from=build /app/target/gallery-1.5.0.jar /app

ENTRYPOINT ["java", "-jar", "gallery-1.5.0.jar"]
