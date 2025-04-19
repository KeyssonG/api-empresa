# Etapa 1: Build da aplicação Java
FROM maven:3.9.9-amazoncorretto-21 AS builder

# Defina o diretório de trabalho
WORKDIR /app

# Copiar os arquivos do projeto
COPY company /app/company

# Rodar o build
RUN mvn -f /app/company/pom.xml clean package

# Renomear o arquivo JAR para cii-modas.jar
RUN mv /app/company/target/*.jar /app/sistema/target/company.jar

# Etapa 2: Imagem de execução
FROM amazoncorretto:21 AS runtime

# Defina o diretório de trabalho
WORKDIR /app

# Copiar o JAR gerado da etapa de build
COPY --from=builder /app/company/target/company.jar /app/company.jar

# Expor a porta da aplicação
EXPOSE 8083

# Comando de inicialização
CMD ["java", "-jar", "/app/company.jar"]
