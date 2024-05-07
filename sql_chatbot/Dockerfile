FROM openjdk:20-jdk

WORKDIR /app

# Copy the application JAR file into the image
COPY target/sql_chatbot-0.0.1-SNAPSHOT.jar /app/sql_chatbot-0.0.1-SNAPSHOT.jar

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "/app/sql_chatbot-0.0.1-SNAPSHOT.jar"]