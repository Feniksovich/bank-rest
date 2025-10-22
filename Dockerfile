# Build Stage
FROM maven:3.9-eclipse-temurin-23 AS build
WORKDIR /app

COPY pom.xml .
COPY .mvn/settings.xml /usr/share/maven/ref/settings-docker.xml
RUN mvn dependency:go-offline --settings /usr/share/maven/ref/settings-docker.xml

COPY src ./src
RUN mvn package -DskipTests

# Test Stage
FROM maven:3.9-eclipse-temurin-23 AS test
WORKDIR /app
COPY --from=build /app .

# Production Stage
FROM eclipse-temurin:23-jre
WORKDIR /app

RUN groupadd -r appuser && useradd -r -g appuser appuser
COPY --from=build /app/target/*.jar app.jar

RUN chown -R appuser:appuser /app
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]