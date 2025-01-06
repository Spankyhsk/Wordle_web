# Stage 1: Build image
FROM sbtscala/scala-sbt:eclipse-temurin-jammy-22_36_1.10.0_3.4.2 AS builder

# Arbeitsverzeichnis im Container
WORKDIR /app

# Install curl and gnupg2 to handle SBT download
RUN apt-get update && apt-get install -y curl gnupg2

# Download sbt and install
RUN curl -sL https://dlcdn.apache.org/sbt/debian/sbt-1.9.4.deb -o sbt.deb

# Install sbt using dpkg and resolve missing dependencies
RUN dpkg -i sbt.deb || apt-get install -f -y

# Copy the project files and dependencies
COPY build.sbt /app/
COPY project /app/project
COPY lib /app/lib

# Download and cache sbt dependencies
RUN sbt update

# Copy the rest of the application code
COPY . /app

# Build and stage the application
RUN sbt stage

# Stage 2: Production image
FROM openjdk:22-jdk-slim

# Set working directory for the app
WORKDIR /app

# Copy the staged application from the builder image
COPY --from=builder /app/target/universal/stage /app

# Expose Play Framework Standard-Port
EXPOSE 9000

# Startbefehl, um die Play-Anwendung zu starten
CMD ["./bin/server", "-Dplay.port=9000"]