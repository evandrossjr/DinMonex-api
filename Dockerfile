# ======================================================================
# ESTÁGIO 1: build da aplicação Java
# ======================================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copia pom.xml e baixa dependências primeiro
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o restante do código-fonte
COPY src ./src

# Compila e empacota (sem rodar testes dentro do container)
RUN mvn clean package -DskipTests

# ======================================================================
# ESTÁGIO 2: execução da aplicação
# ======================================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia o JAR gerado do estágio anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
