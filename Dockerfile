# Multi-stage build to optimize the final image size
FROM maven:3.9-eclipse-temurin-23 AS build
WORKDIR /app

COPY pom.xml .
COPY .mvn/settings.xml /usr/share/maven/ref/settings-docker.xml
ENV MAVEN_CONFIG=/root/.m2

RUN --mount=type=cache,target=/root/.m2/repository \
    mvn dependency:go-offline --settings /usr/share/maven/ref/settings-docker.xml

COPY src ./src
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn clean package -DskipTests --settings /usr/share/maven/ref/settings-docker.xml

# Final stage
FROM eclipse-temurin:23-jre
WORKDIR /app

RUN groupadd -r appuser && useradd -r -g appuser appuser
COPY --from=build /app/target/*.jar app.jar

RUN chown -R appuser:appuser /app
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]