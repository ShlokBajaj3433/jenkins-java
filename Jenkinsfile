pipeline {
    agent any

    options {
        timestamps()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean package'
                sh 'java -cp target/sample-java-1.0-SNAPSHOT.jar Main'
            }
        }
    }
}
