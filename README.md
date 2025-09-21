# api-empresa

API REST para cadastro, consulta e gerenciamento de empresas, desenvolvida em Java com Spring Boot. Possui autenticação JWT, integração com RabbitMQ, documentação Swagger e está pronta para deploy via Docker/Kubernetes.

## O que a aplicação faz

- Permite cadastrar novas empresas e consultar empresas existentes via endpoints REST.
- Realiza autenticação e autorização de usuários utilizando JWT.
- Publica eventos de empresas registradas em uma fila RabbitMQ para integração com outros sistemas.
- Disponibiliza endpoints para contagem de usuários por empresa.
- Realiza tratamento centralizado de exceções, retornando mensagens padronizadas de erro.
- Oferece documentação interativa dos endpoints via Swagger/OpenAPI.
- Pronta para execução em ambientes Docker e Kubernetes, facilitando o deploy e a escalabilidade.

## Estrutura técnica do projeto

Abaixo, um resumo das principais pastas e arquivos do projeto, com suas responsabilidades:

```
api-empresa/
├── src/
│   ├── main/
│   │   ├── java/keysson/apis/empresa/
│   │   │   ├── controller/         # Controllers REST: definem os endpoints da API
│   │   │   ├── service/            # Serviços: lógica de negócio e regras da aplicação
│   │   │   ├── repository/         # Repositórios: acesso a dados e integração com banco
│   │   │   ├── dto/                # DTOs: objetos de transferência de dados
│   │   │   ├── exception/          # Exceções e tratamento de erros
│   │   │   ├── config/             # Configurações: JWT, Swagger, RabbitMQ, segurança
│   │   │   ├── mapper/             # Conversão entre entidades e DTOs
│   │   │   ├── Utils/              # Utilitários diversos (ex: JWT)
│   │   │   └── CompanyApplication.java # Classe principal (main)
│   │   └── resources/              # application.properties e outros recursos
│   └── test/java/keysson/apis/empresa/ # Testes automatizados
├── k8s/                            # Arquivos de deployment e service para Kubernetes
├── Dockerfile                      # Build da imagem Docker
├── Jenkinsfile                     # Pipeline de CI/CD
├── pom.xml                         # Gerenciamento de dependências Maven
└── README.md                       # Documentação do projeto
