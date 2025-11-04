# Arquivo: Dockerfile

# ======================================================================
# ESTÁGIO 1: O "AMBIENTE DE CONSTRUÇÃO" (Build Stage)
# Usamos uma imagem Docker que já vem com o Maven e o JDK instalados.
# Chamamos este estágio de "build" para poder nos referir a ele depois.
# ======================================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Define o diretório de trabalho dentro do container.
WORKDIR /app

# Copia primeiro o pom.xml para aproveitar o cache de camadas do Docker.
# Se as dependências não mudarem, o Docker não precisará baixá-las de novo.
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o resto do código-fonte da sua aplicação para dentro do container.
COPY src ./src

# Executa o comando do Maven para compilar o projeto, rodar os testes
# e empacotar tudo em um arquivo .jar.
RUN mvn clean package -DskipTests


# ======================================================================
# ESTÁGIO 2: O "AMBIENTE DE EXECUÇÃO" (Run Stage)
# Usamos uma imagem muito menor, que contém apenas o Java (JRE),
# o suficiente para RODAR a aplicação, mas não para compilá-la.
# ======================================================================
FROM eclipse-temurin:21-jre-alpine

# Define o diretório de trabalho para este novo estágio.
WORKDIR /app

# A MÁGICA DO MULTI-STAGE:
# Copia APENAS o arquivo .jar que foi gerado no estágio "build"
# para dentro do nosso ambiente de execução limpo.
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta 8080 do container para que o mundo exterior possa se conectar a ela.
EXPOSE 8080

# O comando que será executado quando o container iniciar.
# Ele simplesmente roda a nossa aplicação Spring Boot.
ENTRYPOINT ["java", "-jar", "app.jar"]