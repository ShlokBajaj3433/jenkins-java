FROM maven:3.8.6-openjdk-17

WORKDIR /app

COPY . .

RUN mvn clean package

CMD ["java", "-cp", "target/sample-java-1.0-SNAPSHOT.jar", "Main"]