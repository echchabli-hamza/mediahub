#!/bin/bash
set -e

echo "Starting subscription-service in development mode..."
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx512m"
