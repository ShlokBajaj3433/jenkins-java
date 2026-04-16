pipeline {
    agent any

    environment {
        HAS_DOCKER = 'false'
    }

    stages {

        stage('Check Tooling') {
            steps {
                script {
                    env.HAS_DOCKER = sh(
                        script: 'if command -v docker >/dev/null 2>&1; then echo true; else echo false; fi',
                        returnStdout: true
                    ).trim()
                    echo "Docker available: ${env.HAS_DOCKER}"
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
                sh 'mvn -B clean package'
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
