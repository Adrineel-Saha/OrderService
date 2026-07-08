FROM eclipse-temurin:17
WORKDIR /app
COPY target/OrderService-0.0.1-SNAPSHOT.jar /app/OrderService.jar
ENTRYPOINT ["java","-jar","/app/OrderService.jar"]
EXPOSE 8082
