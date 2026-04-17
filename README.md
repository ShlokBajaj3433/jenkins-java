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

This is the recommended setup for this project. Jenkins runs in Docker, then the pipeline builds and runs this Java app from inside that Jenkins container.

### 1. Start Jenkins container

```powershell
docker volume create jenkins_home

docker run -d --name jenkins `
  -p 8081:8080 -p 50000:50000 `
  -v jenkins_home:/var/jenkins_home `
  -v //var/run/docker.sock:/var/run/docker.sock `
  -v c:/codes/Jenkins:/workspace `
  jenkins/jenkins:lts-jdk17
```

### 2. Install Docker CLI inside Jenkins container

```powershell
docker exec -u 0 jenkins sh -c "apt-get update && apt-get install -y docker.io"
```

### 3. Open Jenkins and unlock

- URL: http://localhost:8080
- Get admin password:

```powershell
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Install suggested plugins and complete the first admin user setup.

## Jenkins Pipeline

The pipeline uses Docker inside Jenkins:

1. Build the Java image
2. Run the container

### Stages in Jenkinsfile

- Checkout
- Build Image
- Run Container

### Jenkins setup requirements

- Jenkins agent with shell support
- Docker socket mounted and Docker CLI installed in the Jenkins container
- Docker access from the Jenkins container

### Create Jenkins job for this project

1. Create a new Pipeline job.
2. Choose Pipeline script from SCM.
3. SCM: Git
4. Repository URL: https://github.com/ShlokBajaj3433/jenkins-java.git
5. Branch: main
6. Script Path: Jenkinsfile
7. Click Build Now.

The pipeline then checks out the repo, builds the Docker image, and runs the Java app from the container.

## Troubleshooting

- `mvn: command not found`:
  - This pipeline no longer uses Maven directly in Jenkins
- `java: command not found`:
  - The app runs inside the Docker image, so Java must be available in the image, not on Jenkins
- Docker permission issues:
  - Ensure Jenkins user can run Docker commands

## Keep It Simple and Standard

- Build locally with Maven when needed
- Build and run with Docker inside Jenkins
