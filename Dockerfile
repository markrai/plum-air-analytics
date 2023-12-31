# Start with a base image containing Oracle JDK 17 LTS
FROM openjdk:17-jdk-slim

# Add Maintainer Info
LABEL maintainer="markraidc@gmail.com"

# Make port 8080 available to the world outside this container
EXPOSE 8080

# The application's jar file
ARG JAR_FILE=target/plumairanalytics-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} app.jar

# Run the jar file 
ENTRYPOINT ["java","-jar","/app.jar","--spring.profiles.active=prod"]