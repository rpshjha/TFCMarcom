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
                REPORTS.each {
                    dir(it) {
                        unstash name: it
                    }
                }
                def resultList = REPORTS.collect { [path:"${it}/target/allure-results"] }
                allure commandline: "Allure 2.18.1", includeProperties: false, results: resultList, reportBuildPolicy: "ALWAYS"
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
