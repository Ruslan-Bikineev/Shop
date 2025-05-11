FROM maven:3.9.9-amazoncorretto-17 AS builder
WORKDIR /app
COPY pom.xml /app/.
RUN mvn -B dependency:resolve
COPY . /app/.
RUN mvn package -Dmaven.test.skip=true

FROM amazoncorretto:17-al2-native-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/shop.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","shop.jar"]
