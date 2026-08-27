// Walking skeleton pipeline: checkout -> build+test -> image -> local registry -> local deploy.
// No quality gates yet (JaCoCo/Sonar) on purpose - those come once there's real code/tests
// to make them meaningful. See ../../architecture/ for why this project is sequenced this way.
pipeline {
    agent any

    environment {
        REGISTRY      = 'localhost:5000'
        IMAGE_NAME    = 'catalog'
        CONTAINER_NAME = 'catalog-service'
        HOST_PORT     = '8081'
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
