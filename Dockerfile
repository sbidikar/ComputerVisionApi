FROM openjdk:8-jdk-alpine
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} ComputerVisionApi-0.0.1.jar
ENTRYPOINT ["java","-DsubscriptionKey=467d3d353dfd4fffbf74b1c1931a658d","-jar","/ComputerVisionApi-0.0.1.jar"]