# Bước 1: Dùng JDK nhẹ chỉ để CHẠY, không cần build nữa
FROM eclipse-temurin:21-jre
WORKDIR /app

# Bước 2: Copy file .jar ĐÃ BUILD XONG từ máy thật vào Docker
# (Nhớ kiểm tra tên file trong thư mục target của mày có đúng là demo-0.0.1-SNAPSHOT.jar không nhé)
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Bước 3: Mở cổng và chạy
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]