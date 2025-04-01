FROM maven:3.9.9-amazoncorretto-21-al2023 AS build


# Thiết lập thư mục làm việc
WORKDIR /app

# Sao chép file pom.xml và các thư mục nguồn vào thư mục làm việc
COPY pom.xml .
COPY src ./src

# Sao chép file .env vào thư mục làm việc /app
COPY .env .

# Kiểm tra các file trong thư mục /app
RUN ls -l /app


RUN mvn package -DskipTests



FROM amazoncorretto:21.0.6
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/.env .

ENTRYPOINT ["java", "-jar", "app.jar"]