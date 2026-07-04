FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
RUN useradd --system --create-home --home-dir /app mindmirror
COPY --from=build /workspace/target/mindmirror-backend-0.0.1-SNAPSHOT.jar app.jar
USER mindmirror
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
