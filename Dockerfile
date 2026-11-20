# Etapa 1: Construcción
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# Etapa 2: Ejecución
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiamos específicamente el fat-jar generado por Spring Boot
COPY --from=build /app/target/WebSock_SB-1.0-SNAPSHOT.jar app.jar

EXPOSE 8862

ENTRYPOINT ["java", "-jar", "app.jar"]
