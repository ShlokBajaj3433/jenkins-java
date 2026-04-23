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
            }
        }

        stage('Web Smoke Test') {
            steps {
                sh '''
                    PORT=8080 java -jar target/sample-java-1.0-SNAPSHOT.jar > java-server.log 2>&1 &
                    JAVA_PID=$!
                    trap "kill $JAVA_PID" EXIT
                    sleep 3
                    curl -f http://localhost:8080/health
                '''
            }
        }
    }
}
