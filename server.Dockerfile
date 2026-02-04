FROM maven:3.8.1-openjdk-17-slim AS prebuild
RUN mkdir -p /workspace
WORKDIR /workspace
COPY ./kafkaChannel .
RUN mvn clean install -Dmaven.test.skip=true

FROM maven:3.8.1-openjdk-17-slim AS build
COPY --from=prebuild /root/.m2 /root/.m2
COPY ./productServer .
RUN mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:17-jdk-alpine
COPY --from=build ./target/*.jar app.jar

# Aiven SSL Configuration
COPY ./ca.pem /app/ca.pem
RUN keytool -import -file /app/ca.pem -alias aiven -keystore /app/truststore.jks -storepass changeit -noprompt

EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]