# Etapa 1: Construcción usando una imagen que ya trae Maven y Java 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el pom.xml y descargamos las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiamos el código fuente y compilamos el .jar
COPY src ./src
RUN mvn clean package -DskipTests -B

# Etapa 2: Ejecución ligera (usamos jammy para evitar problemas de certificados SSL con Upstash)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiamos el .jar compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto por defecto
EXPOSE 8862

# Arrancamos la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
