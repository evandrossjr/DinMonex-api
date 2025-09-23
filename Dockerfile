# ESTÁGIO 1: Build com Maven
# Usamos uma imagem oficial do Maven com Java 21 para compilar nossa aplicação.
# O nome "builder" é um apelido para este estágio, que usaremos depois.
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Define o diretório de trabalho dentro do contêiner.
WORKDIR /app

# Copia apenas o pom.xml primeiro. Isso aproveita o cache do Docker.
# As dependências só serão baixadas novamente se o pom.xml mudar.
COPY pom.xml .
RUN mvn dependency:go-offline

# Agora, copia o resto do código fonte do projeto.
COPY src ./src

# Executa o build do projeto para gerar o arquivo .jar.
# -DskipTests pula a execução dos testes, que já devem ter rodado no pipeline de CI.
RUN mvn package -DskipTests

# ---

# ESTÁGIO 2: Imagem Final de Execução
# Usamos uma imagem JRE (Java Runtime Environment) enxuta com Java 21, que é menor
# e mais segura por não conter as ferramentas de compilação.
FROM eclipse-temurin:21-jre-focal

# Define o diretório de trabalho.
WORKDIR /app

# Copia APENAS o arquivo .jar gerado no estágio anterior (builder) para a imagem final.
# Isso resulta em uma imagem muito menor.
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta 8080, que é a porta padrão do Spring Boot.
EXPOSE 8080

# Define o comando que será executado quando o contêiner iniciar.
ENTRYPOINT ["java", "-jar", "app.jar"]

