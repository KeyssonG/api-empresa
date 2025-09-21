# Etapa 1: Build da aplicação Java
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copia apenas arquivos necessários para o build
COPY pom.xml .
COPY src ./src

# Executa o build do projeto, gerando o JAR já com o nome desejado
RUN mvn clean package -DskipTests --no-transfer-progress -DfinalName=empresa

# Etapa 2: Imagem final enxuta para execução
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia apenas o JAR gerado na etapa de build
COPY --from=builder /app/target/empresa.jar /app/empresa.jar

EXPOSE 8083

CMD ["java", "-jar", "/app/empresa.jar"]
