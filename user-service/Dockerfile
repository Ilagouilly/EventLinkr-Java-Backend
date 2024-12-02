# Use official OpenJDK image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Maven executable
COPY mvnw .
COPY .mvn .mvn

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Give execute permissions to mvnw
RUN chmod +x mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/target/user-service-0.0.1-SNAPSHOT.jar"]