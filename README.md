# Jenkins Java Project

Simple Java Maven project with Docker and Jenkins pipeline support.

## Project Overview

This project contains a single Java entry point:

- `src/main/java/Main.java`

It prints:

- `Hello from Java Maven project!`

The project also includes:

- `pom.xml` for Maven build configuration
- `Dockerfile` for containerized build/run
- `Jenkinsfile` for CI pipeline execution

## Project Structure

```text
project1-java/
  Dockerfile
  Jenkinsfile
  pom.xml
  README.md
  src/
    main/
      java/
        Main.java
```

## Prerequisites

Install:

- Java 17+
- Maven 3.9+
- Docker (optional, for container run)
- Jenkins (optional, for CI)

## Run Locally (Recommended)

From the `project1-java` folder:

```bash
mvn clean package
java -cp target/sample-java-1.0-SNAPSHOT.jar Main
```

Expected output:

```text
Hello from Java Maven project!
```

## Run with Docker

### 1. Build image

```bash
docker build --no-cache -t my-app .
```

### 2. Run container

```bash
docker run --rm my-app
```

## Run Jenkins Inside Docker

This is the recommended setup for this project. Jenkins runs in Docker, then Jenkins uses Maven inside the container to build and run the Java app.

### 1. Start Jenkins container

```powershell
docker volume create jenkins_home

docker run -d --name jenkins `
  -p 8080:8080 -p 50000:50000 `
  -v jenkins_home:/var/jenkins_home `
  -v c:/codes/Jenkins:/workspace `
  jenkins/jenkins:lts-jdk17
```

### 2. Install Maven inside Jenkins container

```powershell
docker exec -u 0 jenkins sh -c "apt-get update && apt-get install -y maven"
```

### 3. Open Jenkins and unlock

- URL: http://localhost:8080
- Get admin password:

```powershell
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Install suggested plugins and complete the first admin user setup.

## Jenkins Pipeline

The pipeline uses Maven inside Jenkins:

1. Check out the repo
2. Build the Java app
3. Run the Java class

### Stages in Jenkinsfile

- Checkout
- Build

### Jenkins setup requirements

- Jenkins agent with shell support
- Maven installed in the Jenkins container
- Java 17 available in the Jenkins container

### Create Jenkins job for this project

1. Create a new Pipeline job.
2. Choose Pipeline script from SCM.
3. SCM: Git
4. Repository URL: https://github.com/ShlokBajaj3433/jenkins-java.git
5. Branch: main
6. Script Path: Jenkinsfile
7. Click Build Now.

The pipeline then checks out the repo, builds the app with Maven, and runs the main class directly inside Jenkins.

## Troubleshooting

- `mvn: command not found`:
  - Install Maven inside the Jenkins container
- `java: command not found`:
  - Use the Jenkins image with JDK 17 or install Java 17 in the container

## Keep It Simple and Standard

- Build locally with Maven when needed
- Build and run with Maven inside Jenkins
