# CRM - Lâm Hùng

Ứng dụng quản lý nhân viên, dự án và công việc bằng Servlet/JSP/JDBC.

**Website demo: https://crm-lam-hung.onrender.com/crm/**

## Công nghệ

Java 17, Tomcat 9, Maven, MySQL. Source dùng package com.crm. Bản demo cloud hiện dùng MySQL 5.7 của Filess.io Free; bản local có thể dùng MySQL 8.

## Chạy trên hosting

Repository này có Dockerfile và render.yaml cho Render Free.
GitHub lưu source; GitHub Pages không chạy Servlet, JSP hoặc MySQL.
Tạo Web Service từ repository, runtime Docker, plan Free, cổng container 8080.
Health check: /crm/login. Link nộp bài là URL của service thêm /crm/.

Tạo một database MySQL từ dịch vụ hosting riêng và import lần lượt:

1. database/01-schema.sql
2. database/02-demo.sql

Chọn database rỗng do hosting cung cấp trong DBeaver trước khi chạy script.
Không chạy CREATE DATABASE CRM hoặc USE CRM trên hosting nếu tài khoản chỉ có quyền
trên database được cấp sẵn có tên khác. Ứng dụng dùng database trong DB_URL.

Trong Environment của Render, đặt DB_URL, DB_USER, DB_PASSWORD theo MySQL cloud.
Không đưa mật khẩu DB thật vào source hoặc commit file .env.

Ví dụ URL JDBC (thay HOST, PORT, DATABASE bằng thông tin của hosting):

```text
jdbc:mysql://HOST:PORT/DATABASE?sslMode=VERIFY_IDENTITY&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh
```

Khi nhà cung cấp dùng CA riêng, cấu hình truststore theo hướng dẫn của họ để giữ xác minh TLS.

## Tài khoản demo

- admin@crm.local: ADMIN
- leader@crm.local: LEADER
- member@crm.local: MEMBER
- Mật khẩu web: 111112113

Đây là dữ liệu mẫu để kiểm tra bài, không chứa dữ liệu thật của khách hàng.

## Build

```text
mvn clean package
```

Kết quả: target/crm.war. Có 8 kiểm thử Java/H2.

Giao diện có UTF-8 và chân trang Created by Lâm Hùng.
Bản demo đã deploy trên Render Free và kiểm tra thành công 54 tình huống HTTP. Render có thể ngủ khi không hoạt động; lần mở lại có thể chờ khoảng một phút.

Kết nối MySQL của bản demo dùng TLS sslMode=REQUIRED theo lựa chọn của chủ dự án, không xác minh chứng chỉ máy chủ. Chỉ dùng dữ liệu mẫu; khi dùng dữ liệu thật cần cấu hình chứng chỉ tin cậy. MySQL 5.7 không cưỡng chế CHECK; Servlet vẫn kiểm tra ngày và trạng thái.
