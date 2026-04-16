pipeline {
    agent any

    environment {
        HAS_DOCKER = 'false'
        HAS_MVN = 'false'
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
                    echo "Docker available: ${env.HAS_DOCKER}"
                    echo "Maven available: ${env.HAS_MVN}"
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
  mkdir -p .maven
  ARCHIVE="apache-maven-${MAVEN_VERSION}-bin.tar.gz"
  URL="https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/${ARCHIVE}"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$URL" -o ".maven/${ARCHIVE}"
  elif command -v wget >/dev/null 2>&1; then
    wget -q -O ".maven/${ARCHIVE}" "$URL"
  else
    echo "ERROR: Neither curl nor wget is available to download Maven."
    exit 1
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
                expression { env.HAS_DOCKER != 'true' }
            }
            steps {
                sh 'java -cp target/sample-java-1.0-SNAPSHOT.jar Main'
            }
        }

    }
}
