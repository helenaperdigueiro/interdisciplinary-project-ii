FROM openjdk:17

ENV APP_PROFILE='prod'
ENV IAM_DATABASE_HOST='pi2t1g1-backend-iam.cy6nd2qhdavj.sa-east-1.rds.amazonaws.com'
ENV IAM_DATABASE_USER='pi2t1g1_backend'
ENV IAM_DATABASE_PASSWORD='pi2t1g1-backend'

ENV EMAIL_HOST='sandbox.smtp.mailtrap.io'
ENV EMAIL_PORT='2525'
ENV EMAIL_USER='71348defe629b2'
ENV EMAIL_PASSWORD='9e992a2118a5c6'

ENV ACCOUNTS_BASE_URL='http://ec2-18-229-118-235.sa-east-1.compute.amazonaws.com:8081'

WORKDIR /docker
EXPOSE 8080
COPY target/iam-service-0.0.1-SNAPSHOT.jar /docker/iam-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "iam-service-0.0.1-SNAPSHOT.jar"]