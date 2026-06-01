# Etapa de construcción
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa final
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copiar el jar construido y el script de entrada
COPY --from=build /app/target/Instahyre-0.0.1-SNAPSHOT.jar app.jar
COPY entrypoint.sh entrypoint.sh

# Dar permisos de ejecución al script
RUN chmod +x entrypoint.sh

# Exponer el puerto que usará la aplicación
EXPOSE 8080

# Usar el script de entrada
ENTRYPOINT ["./entrypoint.sh"]
