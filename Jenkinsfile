pipeline {
    agent any

    environment {
        HAS_DOCKER = 'false'
        HAS_MVN = 'false'
        HAS_JAVA = 'false'
        HAS_TAR = 'false'
        HAS_CURL = 'false'
        HAS_WGET = 'false'
        MVN_CMD = 'mvn'
    }

    stages {

        stage('Check Tooling') {
            steps {
                script {
                    env.HAS_DOCKER = sh(
                        script: 'if command -v docker >/dev/null 2>&1; then echo true; else echo false; fi',
                        returnStdout: true
                    ).trim()
                    env.HAS_MVN = sh(
                        script: 'if command -v mvn >/dev/null 2>&1; then echo true; else echo false; fi',
                        returnStdout: true
                    ).trim()
                    env.HAS_JAVA = sh(
                        script: 'if command -v java >/dev/null 2>&1; then echo true; else echo false; fi',
                        returnStdout: true
                    ).trim()
                    env.HAS_TAR = sh(
                        script: 'if command -v tar >/dev/null 2>&1; then echo true; else echo false; fi',
                        returnStdout: true
                    ).trim()
                    env.HAS_CURL = sh(
                        script: 'if command -v curl >/dev/null 2>&1; then echo true; else echo false; fi',
                        returnStdout: true
                    ).trim()
                    env.HAS_WGET = sh(
                        script: 'if command -v wget >/dev/null 2>&1; then echo true; else echo false; fi',
                        returnStdout: true
                    ).trim()
                    echo "Docker available: ${env.HAS_DOCKER}"
                    echo "Maven available: ${env.HAS_MVN}"
                    echo "Java available: ${env.HAS_JAVA}"
                    echo "tar available: ${env.HAS_TAR}"
                    echo "curl available: ${env.HAS_CURL}"
                    echo "wget available: ${env.HAS_WGET}"
                }
            }
        }

        stage('Setup Maven (Fallback)') {
            when {
                expression { env.HAS_DOCKER != 'true' && env.HAS_MVN != 'true' }
            }
            steps {
                sh '''#!/bin/sh
set -eu
MAVEN_VERSION=3.9.9
MAVEN_DIR=".maven/apache-maven-${MAVEN_VERSION}"
if [ ! -x "${MAVEN_DIR}/bin/mvn" ]; then
    if [ "${HAS_TAR}" != "true" ]; then
        echo "ERROR: 'tar' is not available on the Jenkins agent. Install tar or Maven on the agent."
        exit 1
    fi
    if [ "${HAS_CURL}" != "true" ] && [ "${HAS_WGET}" != "true" ]; then
        echo "ERROR: Neither curl nor wget is available to download Maven."
        exit 1
    fi
  mkdir -p .maven
  ARCHIVE="apache-maven-${MAVEN_VERSION}-bin.tar.gz"
  URL="https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/${ARCHIVE}"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$URL" -o ".maven/${ARCHIVE}"
  elif command -v wget >/dev/null 2>&1; then
    wget -q -O ".maven/${ARCHIVE}" "$URL"
  fi
  tar -xzf ".maven/${ARCHIVE}" -C .maven
fi
'''
                script {
                    env.MVN_CMD = './.maven/apache-maven-3.9.9/bin/mvn'
                    echo "Using local Maven: ${env.MVN_CMD}"
                }
            }
        }

        stage('Docker Build') {
            when {
                expression { env.HAS_DOCKER == 'true' }
            }
            steps {
                sh 'docker build --no-cache -t my-app .'
            }
        }

        stage('Run Container') {
            when {
                expression { env.HAS_DOCKER == 'true' }
            }
            steps {
                sh 'docker run my-app'
            }
        }

        stage('Maven Build (Fallback)') {
            when {
                expression { env.HAS_DOCKER != 'true' }
            }
            steps {
                script {
                    if (env.HAS_MVN == 'true') {
                        env.MVN_CMD = 'mvn'
                    }
                }
                sh "${env.MVN_CMD} -B clean package"
            }
        }

        stage('Run Java App (Fallback)') {
            when {
                expression { env.HAS_DOCKER != 'true' && env.HAS_JAVA == 'true' }
            }
            steps {
                sh 'java -cp target/sample-java-1.0-SNAPSHOT.jar Main'
            }
        }

        stage('Validate Java Availability (Fallback)') {
            when {
                expression { env.HAS_DOCKER != 'true' && env.HAS_JAVA != 'true' }
            }
            steps {
                error('Java is not available on this Jenkins agent PATH. Install Java 17+ and rerun.')
            }
        }

    }
}
