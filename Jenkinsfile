pipeline {
    agent any
    tools {
          maven 'MAVEN_HOME'
    }
    stages {
        stage('Build') {
            steps {
                sh 'echo "Building the project"'
            }
        }

        stage('Deploy') {
            steps {
                sh 'echo "Deploying the project"'
            }
        }
    }

    post {
        success {
            echo 'The pipeline has succeeded!'
        }

        failure {
            echo 'The pipeline has failed!'
        }
    }
}
