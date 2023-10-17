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
                script {
                  allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'allure-results']]
                  ])
                }
            }
        }

        // Add a stage to run 'allure serve'
        stage('Serve Allure Report') {
            steps {
                sh 'allure serve allure-results'
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
