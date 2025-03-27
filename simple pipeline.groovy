pipeline {
    agent any  // This tells Jenkins to run the pipeline on any available agent (node)

    environment {
        // Define environment variables here
        MY_ENV_VAR = 'value'
    }
pipeline {
  agent any 
    stages {
        stage('Checkout') {
            steps {
                // Checkout your source code from a Git repository
                git 'https://github.com/example/repo.git'
            }
        }

        stage('Build') {
            steps {
                // Run build commands like compiling code, generating artifacts, etc.
                echo 'Building the application...'
                sh 'mvn clean install'
            }
        }

        stage('Test') {
            steps {
                // Run tests, for example unit tests
                echo 'Running tests...'
                sh 'mvn test'
            }
        }

        stage('Deploy') {
            steps {
                // Deploy the application or artifacts
                echo 'Deploying the application...'
                sh 'make deploy'
            }
        }
    }

    post {
        success {
            // Actions to take if the pipeline succeeds
            echo 'Pipeline succeeded!'
        }
        failure {
            // Actions to take if the pipeline fails
            echo 'Pipeline failed!'
        }
        always {
            // Actions to take regardless of the outcome
            echo 'Cleaning up...'
        }
    }
}
