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

        stage('Build Image') {
            steps {
                sh 'docker build --no-cache -t sample-java-app .'
            }
        }

        stage('Run Container') {
            steps {
                sh 'docker run --rm sample-java-app'
            }
        }
    }
}
