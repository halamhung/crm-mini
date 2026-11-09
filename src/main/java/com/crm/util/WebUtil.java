package com.crm.util;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.nio.charset.StandardCharsets;

public class WebUtil {
    public static String text(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    public static int number(HttpServletRequest request, String name) {
        String value = text(request, name);
        if (value.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Mã hoặc trạng thái không hợp lệ.");
        }
    }

    public static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkDates(LocalDate startDate, LocalDate endDate) {
        check(startDate != null && endDate != null, "Vui lòng nhập đủ ngày.");
        check(!endDate.isBefore(startDate), "Ngày kết thúc không được trước ngày bắt đầu.");
    }

    public static void checkPassword(String password) {
        check(password.length() >= 8, "Mật khẩu phải có ít nhất 8 ký tự.");
        check(password.getBytes(StandardCharsets.UTF_8).length <= 72,
                "Mật khẩu quá dài (tối đa 72 byte UTF-8).");
    }

    // Escape du lieu truoc khi hien thi tren JSP de tranh chen HTML.
    public static String escape(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString().replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}
