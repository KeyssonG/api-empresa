pipeline {
    agent any

    environment {
        DOCKERHUB_IMAGE = "keyssong/company"
        DEPLOYMENT_FILE = "k8s\\company-deployment.yaml"
        IMAGE_TAG = "latest"
    }

    options {
        disableConcurrentBuilds()
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
                git credentialsId: 'Github',
                    url: 'https://github.com/KeyssonG/company.git',
                    branch: 'master'
            }
        }

        stage('Build da Imagem Docker') {
            steps {
                bat "docker build -t ${DOCKERHUB_IMAGE}:${IMAGE_TAG} ."
                bat "docker tag ${DOCKERHUB_IMAGE}:${IMAGE_TAG} ${DOCKERHUB_IMAGE}:latest"
            }
        }

        stage('Push da Imagem para Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    bat """
                        echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin
                        docker push ${DOCKERHUB_IMAGE}:${IMAGE_TAG}
                        docker push ${DOCKERHUB_IMAGE}:latest
                    """
                }
            }
        }

        stage('Atualizar deployment.yaml') {
            steps {
                powershell """
                    \$content = Get-Content ${DEPLOYMENT_FILE}
                    \$newContent = \$content -replace 'image: .*', 'image: ${DOCKERHUB_IMAGE}:${IMAGE_TAG}'
                    if (-not (\$content -eq \$newContent)) {
                        \$newContent | Set-Content ${DEPLOYMENT_FILE}
                    }

                    git config user.email "jenkins@pipeline.com"
                    git config user.name "Jenkins"
                    git add ${DEPLOYMENT_FILE}

                    if (git diff --cached --quiet) {
                        Write-Host "Nenhuma alteração para commit."
                    } else {
                        git commit -m "Atualiza imagem Docker para latest"
                        try {
                            git pull origin master --rebase
                        } catch {
                            Write-Host "Falha ao sincronizar com o branch remoto."
                        }
                        git push origin master --quiet
                    }
                """
            }
        }


    }

    post {
        success {
            echo "Pipeline concluída com sucesso! A imagem 'keyssong/company:latest' foi atualizada e o ArgoCD aplicará as alterações automaticamente. 🚀"
        }
        failure {
            echo "Erro na pipeline. Confira os logs para mais detalhes."
        }
    }
}
