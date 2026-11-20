# Etapa 1: Construcción (Build)
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copiamos los archivos de Maven y descargamos dependencias (aprovecha la caché de Docker)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copiamos el código fuente y compilamos el .jar
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Etapa 2: Ejecución (Runtime)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiamos únicamente el .jar compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto configurado en tu application.yml
EXPOSE 8862

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
