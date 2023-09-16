FROM maven:latest

MAINTAINER Grandench1k

COPY . .

RUN mvn clean package -DskipTests

ENTRYPOINT ["java", "-jar", "target/online-gallery-0.0.1-SNAPSHOT.jar"]
