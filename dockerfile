FROM maven:3.8.4-openjdk-11-slim AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

FROM tomcat:9.0-jdk11-openjdk-slim
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
