docker rmi -f computervisionapi:v1
./gradlew clean assemble
docker build -t computervisionapi:v1 .
docker run -p 8080:8080 computervisionapi:v1 > log.txt &
