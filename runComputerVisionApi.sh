./gradlew clean assemble
docker rmi -f computervisionapi:latest
docker build -t computervisionapi:latest .
docker swarm leave --force
docker swarm init