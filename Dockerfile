# Build Stage
FROM amazoncorretto:17-alpine as build
WORKDIR /workspace/app

# Install Maven in the build stage
RUN apk add --no-cache maven

# Copy the required files to build the application
COPY pom.xml .
RUN mvn dependency:go-offline  # Cache dependencies first to speed up later builds

COPY src/ src/
RUN mvn package -DskipTests  # Build the JAR without running tests

# Final Stage (Only copy the built JAR to the runtime image)
FROM amazoncorretto:17-alpine

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /workspace/app/target/*.jar /app/app.jar

# Use a non-root user (optional, but good for security)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
