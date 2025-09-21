# Etapa 1: Build da aplicação Java
FROM maven:3.9.9-amazoncorretto-21 AS builder

WORKDIR /app

# Copia apenas arquivos necessários para o build
COPY pom.xml .
COPY src ./src

# Executa o build do projeto, gerando o JAR já com o nome desejado
RUN mvn clean package -DskipTests --no-transfer-progress -DfinalName=empresa

# Renomeia o JAR gerado
RUN mv target/*.jar /app/empresa.jar

# Etapa 2: Imagem final enxuta para execução
FROM amazoncorretto:21

WORKDIR /app

# Copia apenas o JAR gerado na etapa de build
COPY --from=builder /app/target/empresa.jar /app/empresa.jar

EXPOSE 8083

CMD ["java", "-jar", "/app/empresa.jar"]
