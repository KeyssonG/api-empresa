pipeline {
    agent any

    environment {
        DOCKER_HUB_USER = 'keyssong'
        IMAGE_NAME = 'company'
        IMAGE_TAG = "build-${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'homol', url: 'https://github.com/KeyssonG/company.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    bat "docker build -t ${DOCKER_HUB_USER}/${IMAGE_NAME}:${IMAGE_TAG} ."
                    bat "docker tag ${DOCKER_HUB_USER}/${IMAGE_NAME}:${IMAGE_TAG} ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    script {
                        bat "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"
                        bat "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:${IMAGE_TAG}"
                        bat "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
                    }
                }
            }
        }

        stage('Atualizar deployment.yaml com nova tag') {
            steps {
                script {
                    def content = readFile('k8s/deployment.yaml')
                    content = content.replaceAll(/image:\s+keyssong\/company:.*/, "image: keyssong/company:${IMAGE_TAG}")
                    writeFile file: 'k8s/deployment.yaml', text: content
                }
            }
        }

        stage('Commit e Push do Manifesto') {
            steps {
                script {
                    bat 'git config user.email "jenkins@localhost"'
                    bat 'git config user.name "Jenkins"'
                    bat 'git add k8s/deployment.yaml'
                    bat "git commit -m \"Atualiza imagem para tag ${IMAGE_TAG}\" || exit 0"
                    bat 'git push origin homol'
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline concluída com sucesso! Imagem '${DOCKER_HUB_USER}/${IMAGE_NAME}:${IMAGE_TAG}' publicada e manifesto atualizado."
        }
        failure {
            echo " Erro na pipeline. Confira os logs para detalhes."
        }
    }
}
