#!/bin/bash

# Load SONAR_TOKEN from .env file
export $(grep -E '^SONAR_TOKEN=' .env | xargs)

# Print out the token for verification
echo "SONAR_TOKEN is: $SONAR_TOKEN"

# Start SonarQube if not already running
docker-compose -f docker-compose-sonar.yml up -d

# Wait for SonarQube to start
echo "Waiting for SonarQube to start..."
while true; do
    STATUS=$(curl -s http://localhost:9000/api/system/status | grep -o '"status":"UP"')
    if [ "$STATUS" == '"status":"UP"' ]; then
        break
    fi
    echo "SonarQube is still starting..."
    sleep 5
done
echo "SonarQube is up and running"

# Run the analysis - Make sure SONAR_TOKEN is set in .env
cd user-service || exit
mvn clean verify sonar:sonar -Dsonar.login="$SONAR_TOKEN" -Dsonar.host.url=http://localhost:9000
