# Use the official OpenJDK 21 slim image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy maven executable
COPY mvnw .
COPY .mvn .mvn

# Copy pom.xml
COPY pom.xml .

# Copy source code
COPY src src

# Install dependencies and build
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app/target/user-service-0.0.1-SNAPSHOT.jar"]