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

        stage('Test') {
             steps {
                sh 'mvn clean test'
             }
        }

        stage("Generate Allure Report") {
            steps {
                sh 'cd allure-results'
                sh 'allure serve'
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
