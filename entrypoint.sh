#!/bin/sh
# This script is the definitive solution to transform Render's database URL
# into the format required by Java's JDBC driver.

echo "--- Running entrypoint.sh ---"

# Render provides the database connection string in the DATABASE_URL variable.
# Format: postgres://user:password@host:port/database
echo "Initial DATABASE_URL from Render: $DATABASE_URL"

# Spring Boot needs the format: jdbc:postgresql://host:port/database
# We use 'sed' to replace the protocol at the beginning of the string.
export SPRING_DATASOURCE_URL=$(echo "$DATABASE_URL" | sed 's/^postgres:/jdbc:postgresql:/')

echo "Transformed SPRING_DATASOURCE_URL for Java: $SPRING_DATASOURCE_URL"
echo "--- Starting application ---"

# 'exec' replaces the shell process with the Java process, which is a best practice.
exec java -jar app.jar