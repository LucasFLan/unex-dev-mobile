# syntax=docker/dockerfile:1

# ---- Build: gera o .jar (os testes rodam fora daqui: precisam de Testcontainers) ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package -DskipTests && cp target/*.jar app.jar

# ---- Runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN groupadd --system app && useradd --system --gid app --home /app app \
    && mkdir -p /app/uploads/validacao /app/uploads/perfil /app/uploads/anuncios /app/uploads/propostas \
    && chown -R app:app /app
COPY --from=build --chown=app:app /build/app.jar app.jar
USER app

ENV VALIDACAO_DIRETORIO=/app/uploads/validacao \
    PERFIL_DIRETORIO=/app/uploads/perfil \
    ANUNCIOS_DIRETORIO=/app/uploads/anuncios \
    JAVA_OPTS="-XX:MaxRAMPercentage=75"
# Imagens enviadas (validação, perfil, anúncios): monte um volume aqui para não perder ao recriar o container.
VOLUME ["/app/uploads"]
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
