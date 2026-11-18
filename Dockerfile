# Build
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw \
 && ./mvnw -B -q dependency:go-offline

COPY src src
RUN ./mvnw -B -DskipTests package \
 && cp target/contracts-api-*.jar app.jar

# Runtime
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

RUN groupadd --system spring \
 && useradd --system --gid spring spring
USER spring:spring

COPY --from=build /workspace/app.jar /app/app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
