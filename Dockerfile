# Étape 1 : Build
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/facture-service-1.0-SNAPSHOT.jar app.jar
RUN mkdir -p /app/factures-generees
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]