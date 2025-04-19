pipeline {
    agent any

    environment {
        DOCKERHUB_IMAGE = "keyssong/company"
        DEPLOYMENT_FILE = "k8s\\company-deployment.yaml"
        IMAGE_TAG = "latest"
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
                git credentialsId: 'Github',
                    url: 'https://github.com/KeyssonG/company.git',
                    branch: 'master'
            }
        }

        stage('Build da Imagem Docker') {
            steps {
                    bat "docker build -t %DOCKERHUB_IMAGE%:%IMAGE_TAG% ."
                bat "docker tag %DOCKERHUB_IMAGE%:%IMAGE_TAG% %DOCKERHUB_IMAGE%:latest"
            }
        }

        stage('Push da Imagem para Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    bat """
                        echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin
                        docker push %DOCKERHUB_IMAGE%:%IMAGE_TAG%
                        docker push %DOCKERHUB_IMAGE%:latest
                    """
                }
            }
        }

        stage('Atualizar deployment.yaml com nova tag') {
            steps {
                bat """
                    powershell -Command "(Get-Content %DEPLOYMENT_FILE%) -replace 'image: .*', 'image: %DOCKERHUB_IMAGE%:%IMAGE_TAG%' | Set-Content %DEPLOYMENT_FILE%"
                    git config user.email "jenkins@pipeline.com"
                    git config user.name "Jenkins"
                    git add %DEPLOYMENT_FILE%
                    git commit -m "Atualiza imagem Docker para latest" || echo "Nenhuma alteração para commitar."
                    git pull origin master --rebase || echo "Nenhuma atualização necessária no branch remoto."
                    git push origin master
                """
            }
        }
    }

    post {
        success {
            echo "Pipeline concluída com sucesso! A imagem 'keyssong/company:latest' foi atualizada e o ArgoCD aplicará as alterações automaticamente. 🚀"
        }
        failure {
            echo "Erro na pipeline. Confira os logs para detalhes."
        }
    }
}
