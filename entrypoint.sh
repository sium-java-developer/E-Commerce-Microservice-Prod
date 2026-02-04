#!/bin/sh

# Render provides the database connection string in the DATABASE_URL variable.
# It has the format: postgres://user:password@host:port/database
# Spring Boot's JDBC driver needs it in the format: jdbc:postgresql://host:port/database

# Check if DATABASE_URL is set from Render and transform it
if [ -n "$DATABASE_URL" ]; then
  export SPRING_DATASOURCE_URL=$(echo "$DATABASE_URL" | sed 's/^postgres:/jdbc:postgresql:/')
fi

# Execute the original command to run the Spring Boot application
exec java -jar app.jar