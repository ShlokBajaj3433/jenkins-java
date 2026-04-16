FROM maven:3.9.9-eclipse-temurin-17

WORKDIR /app

COPY . .

RUN mvn clean package

CMD ["java", "-cp", "target/project1-1.0.jar", "App"]