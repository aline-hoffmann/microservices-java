# Fase 1: Build e compilação do projeto
FROM maven:3-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copia arquivos de configuração e código fonte
COPY pom.xml .
COPY src ./src

# Executa o build do projeto (sem testes)
RUN mvn clean package -DskipTests

# Fase 2: Imagem de execução otimizada
FROM eclipse-temurin:21-jre-alpine

# Copia o artefato JAR gerado na fase anterior
COPY --from=build /app/target/*.jar /app/app.jar

# Inicia a aplicação
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
