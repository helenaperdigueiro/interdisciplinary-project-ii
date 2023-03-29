FROM openjdk:17
ENV APP_PROFILE='prod'
ENV ACCOUNTS_DATABASE_HOST='pi2t1g1-backend-accounts.cy6nd2qhdavj.sa-east-1.rds.amazonaws.com'
ENV ACCOUNTS_DATABASE_USER='pi2t1g1_backend'
ENV ACCOUNTS_DATABASE_PASSWORD='pi2t1g1-backend'
ENV IAM_BASE_URL='http://ec2-15-228-99-140.sa-east-1.compute.amazonaws.com:8080/'
WORKDIR /docker
EXPOSE 8081
COPY target/account-service-0.0.1-SNAPSHOT.jar /docker/account-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "account-service-0.0.1-SNAPSHOT.jar"]