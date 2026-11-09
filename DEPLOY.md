# CRM deployment package

Source lấy từ project Eclipse tại ổ D, gồm Servlet/JSP/JDBC, Java 17 và Tomcat 9.
Dockerfile build thành file crm.war. Đường dẫn ứng dụng là /crm/ và trang đăng nhập là /crm/login.
Tomcat lắng nghe cổng 8080 bên trong container; hosting ánh xạ cổng này ra HTTPS công khai.

## Cấu hình trên máy chủ

Tạo MySQL trên máy chủ và chạy lần lượt database/01-schema.sql, database/02-demo.sql
trong database CRM còn trống. Các file này không xuất dữ liệu riêng trên máy cá nhân.

Đặt ba biến môi trường trên service web:

- DB_URL: URL JDBC được cấu hình theo host, port và database của MySQL trên hosting.
- DB_USER: tài khoản database được cấp quyền cho ứng dụng.
- DB_PASSWORD: mật khẩu database; nhập trong trang quản lý secrets của hosting.

Không sử dụng localhost:3307 của máy cá nhân cho MySQL nằm ở service khác.
Nếu hosting yêu cầu TLS, dùng sslMode=VERIFY_IDENTITY cùng CA/cấu hình do nhà cung cấp chỉ dẫn.

## Tài khoản dữ liệu mẫu

admin@crm.local, leader@crm.local, member@crm.local.
Mật khẩu đăng nhập web: 111112113 (lưu bằng BCrypt).
Đây là mật khẩu của tài khoản demo, không phải mật khẩu MySQL.

## Kiểm tra sau khi triển khai

1. /crm/login tải được qua đường link công khai.
2. Đăng nhập ba tài khoản, dữ liệu tiếng Việt đúng.
3. ADMIN quản lý nhân viên; LEADER quản lý dự án của mình; MEMBER chỉ cập nhật việc được giao.
4. Dữ liệu MySQL vẫn còn sau khi web được khởi động lại.

Gói này đã chuẩn bị source và SQL; chưa có URL công khai cho đến khi kết nối tài khoản hosting/máy chủ.
