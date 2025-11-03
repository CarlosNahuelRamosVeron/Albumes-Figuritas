# ====== Build stage ======
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Compilar sin tests
RUN mvn -q -DskipTests package

# ====== Run stage ======
FROM eclipse-temurin:17-jre
WORKDIR /app
# Copiar el jar compilado
ARG JAR_FILE=target/*.jar
COPY --from=build /app/${JAR_FILE} /app/app.jar
# Directorio de uploads (mapeado a volumen)
RUN mkdir -p /app/uploads
VOLUME ["/app/uploads"]
EXPOSE 8080
# Permitir inyectar JAVA_OPTS
ENV JAVA_OPTS=""
ENTRYPOINT ["/bin/sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

