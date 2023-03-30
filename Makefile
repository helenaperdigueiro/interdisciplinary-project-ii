iam-build:
	cd iam-service; mvn install -DskipTests

accounts-build:
	cd account-service; mvn install -DskipTests

iam-run:
	nohup java -jar iam-service/target/iam-service-0.0.1-SNAPSHOT.jar &

accounts-run:
	nohup java -jar account-service/target/account-service-0.0.1-SNAPSHOT.jar &

log:
	tail -f nohup.out

docker-iam-service:
	cd iam-service; mvn install -DskipTests; docker build -t iam-service-image .; docker-compose up

docker-accounts-service:
	cd account-service; mvn install -DskipTests; docker build -t account-service-image .; docker-compose up
