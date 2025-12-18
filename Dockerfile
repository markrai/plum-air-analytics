# Start with a base image containing JDK 17 LTS
FROM eclipse-temurin:17-jdk-jammy

# Add Maintainer Info
LABEL maintainer="markraidc@gmail.com"

# Install Java at /usr/local/openjdk-17 to match NAS setup
RUN mkdir -p /usr/local/openjdk-17 && \
    cp -r /opt/java/openjdk/* /usr/local/openjdk-17/ && \
    ln -s /usr/local/openjdk-17/bin/java /usr/local/bin/java

# Set JAVA_HOME to match NAS environment
ENV JAVA_HOME=/usr/local/openjdk-17
ENV PATH="/usr/local/openjdk-17/bin:${PATH}"

# Make port 8080 available to the world outside this container
EXPOSE 8080

# The application's jar file
ARG JAR_FILE=target/plumairanalytics-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar","--spring.profiles.active=prod"]