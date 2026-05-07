# Backend
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar codigo y compilar
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar jar generado
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Ejecutar aplicacion
ENTRYPOINT ["java", "-jar", "app.jar"]