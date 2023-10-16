docker build -t todo-service .
docker rm -f todo-service
docker run -p 8080:8080 --name todo-service todo-service
