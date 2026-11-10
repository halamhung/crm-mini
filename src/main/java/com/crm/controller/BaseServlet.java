package com.crm.controller;

import com.crm.model.User;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BaseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected User currentUser(HttpServletRequest request) {
        return (User) request.getAttribute("currentUser");
    }

    protected void forward(HttpServletRequest request, HttpServletResponse response, String page)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/" + page + ".jsp").forward(request, response);
    }

    protected void redirect(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }

    protected void saveError(HttpServletRequest request, Exception exception) {
        String message;
        if (exception instanceof IllegalArgumentException) {
            message = exception.getMessage();
        } else {
            log("Loi xu ly du lieu CRM", exception);
            message = "Không lưu được dữ liệu. Kiểm tra email trùng, quan hệ đang sử dụng hoặc kết nối MySQL.";
        }
        request.getSession().setAttribute("error", message);
    }

    protected void showError(HttpServletRequest request, HttpServletResponse response, Exception exception)
            throws ServletException, IOException {
        if (exception instanceof IllegalArgumentException) {
            response.setStatus(403);
            request.setAttribute("error", exception.getMessage());
        } else {
            log("Loi tai trang CRM", exception);
            response.setStatus(500);
            request.setAttribute("error", "Không tải được dữ liệu. Vui lòng kiểm tra kết nối MySQL.");
        }
        forward(request, response, "error");
    }
}
