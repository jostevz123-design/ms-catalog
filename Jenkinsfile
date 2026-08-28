// Walking skeleton pipeline: checkout -> build+test -> quality gate -> image -> local registry -> local deploy.
pipeline {
    agent any

    environment {
        REGISTRY      = 'localhost:5000'
        IMAGE_NAME    = 'catalog'
        CONTAINER_NAME = 'catalog-service'
        HOST_PORT     = '8081'
        SONAR_HOST_URL = 'http://sonarqube:9000'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh './mvnw -B clean verify'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh './mvnw -B sonar:sonar -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.token=${SONAR_TOKEN}'
                }
            }
        }

        stage('Compute Image Tag') {
            steps {
                script {
                    env.SHA_SHORT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                }
            }
        }

        stage('Build Image') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${env.SHA_SHORT} ."
            }
        }

        stage('Push to Local Registry') {
            steps {
                sh """
                    docker tag ${IMAGE_NAME}:${env.SHA_SHORT} ${REGISTRY}/${IMAGE_NAME}:${env.SHA_SHORT}
                    docker push ${REGISTRY}/${IMAGE_NAME}:${env.SHA_SHORT}
                """
            }
        }

        stage('Deploy Locally') {
            steps {
                sh """
                    docker stop ${CONTAINER_NAME} || true
                    docker rm ${CONTAINER_NAME} || true
                    docker run -d --name ${CONTAINER_NAME} -p ${HOST_PORT}:8080 ${REGISTRY}/${IMAGE_NAME}:${env.SHA_SHORT}
                """
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
        }
    }
}
