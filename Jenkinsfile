pipeline {
    agent any

    environment {
        DOCKERHUB_IMAGE = "keyssong/company"
        IMAGE_TAG = "build-${BUILD_NUMBER}"
        DEPLOYMENT_FILE = "k8s/deployment.yaml"
    }

    triggers {

        pollSCM('* * * * *')
    }

    stages {
        stage('Verificar Branch') {
            when {
                branch 'master'
            }
            steps {
                echo "Executando pipeline na branch master"
            }
        }

        stage('Checkout do Código') {
            steps {
                git credentialsId: 'git-credencial-id',
                    url: 'https://github.com/KeyssonG/company.git',
                    branch: 'master'
            }
        }

        stage('Build da Imagem Docker') {
            steps {
                sh "docker build -t ${DOCKERHUB_IMAGE}:${IMAGE_TAG} ."
                sh "docker tag ${DOCKERHUB_IMAGE}:${IMAGE_TAG} ${DOCKERHUB_IMAGE}:latest"
            }
        }

        stage('Push da Imagem para Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-cred', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh """
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push ${DOCKERHUB_IMAGE}:${IMAGE_TAG}
                        docker push ${DOCKERHUB_IMAGE}:latest
                    """
                }
            }
        }

        stage('Atualizar deployment.yaml com nova tag') {
            steps {
                sh """
                    sed -i 's|image: .*|image: ${DOCKERHUB_IMAGE}:${IMAGE_TAG}|' ${DEPLOYMENT_FILE}
                    git config user.email "jenkins@pipeline.com"
                    git config user.name "Jenkins"
                    git add ${DEPLOYMENT_FILE}
                    git commit -m "Atualiza imagem Docker para ${IMAGE_TAG}"
                    git push origin master
                """
            }
        }
    }

    post {
        success {
            echo "Deploy atualizado com sucesso! O ArgoCD aplicará as alterações automaticamente. 🚀"
        }
        failure {
            echo "Erro na pipeline. Confira os logs para detalhes."
        }
    }
}
