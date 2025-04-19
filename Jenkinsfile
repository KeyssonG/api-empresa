pipeline {
    agent any

    environment {
        IMAGE_NAME = 'keyssong/company'
        TAG = "build-${BUILD_NUMBER}"
    }

    stages {
        stage('Build Java') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                bat """
                docker build -t %IMAGE_NAME%:%TAG% -t %IMAGE_NAME%:latest .
                """
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    bat """
                    echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin
                    docker push %IMAGE_NAME%:%TAG%
                    docker push %IMAGE_NAME%:latest
                    """
                }
            }
        }

        stage('Atualizar deployment.yaml com nova tag') {
            steps {
                bat """
                powershell -Command "(Get-Content k8s\\deployment.yaml) -replace 'image: .*', 'image: %IMAGE_NAME%:%TAG%' | Set-Content k8s\\deployment.yaml"
                """
            }
        }

        stage('Commit e Push do Manifesto') {
            steps {
                bat """
                git config user.name "Jenkins"
                git config user.email "jenkins@example.com"
                git add k8s\\deployment.yaml
                git commit -m "Atualiza imagem para %IMAGE_NAME%:%TAG%"
                git push origin homol
                """
            }
        }
    }

    post {
        success {
            echo 'Pipeline executada com sucesso. Imagem Docker publicada e manifesto atualizado.'
        }
        failure {
            echo 'Erro na pipeline. Confira os logs para detalhes.'
        }
    }
}
