pipeline {
    agent any

    stages {

        stage('Docker Build') {
            steps {
                sh 'docker build --no-cache -t my-app .'
            }
        }

        stage('Run Container') {
            steps {
                sh 'docker run my-app'
            }
        }

    }
}
