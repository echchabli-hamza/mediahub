# ---- Stage 1: Build the artifact ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper and POM first for dependency caching
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source and build the JAR (skip tests for faster build)
COPY src ./src
RUN ./mvnw package -DskipTests -B

# ---- Stage 2: Run the application ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/gateaway-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 7001
ENTRYPOINT ["java", "-jar", "app.jar"]
