package com.crm.filter;

import com.crm.dao.UserDAO;
import com.crm.model.User;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

@WebFilter("/*")
public class AuthFilter implements Filter {
    private UserDAO userDAO = new UserDAO();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ServletException("Khong tim thay MySQL JDBC driver", e);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");

        String path = request.getServletPath();
        if (path.equals("/style.css")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession();
        if (session.getAttribute("csrf") == null) {
            session.setAttribute("csrf", UUID.randomUUID().toString());
        }

        User user = null;
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            try {
                user = userDAO.findById(userId);
            } catch (SQLException e) {
                throw new ServletException("Khong ket noi duoc MySQL", e);
            }
        }
        request.setAttribute("currentUser", user);
        if (user == null && !path.equals("/login")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

       
        if (request.getMethod().equals("POST")) {
            String csrf = request.getParameter("csrf");
            if (!session.getAttribute("csrf").equals(csrf)) {
                response.setStatus(403);
                request.setAttribute("error", "Yêu cầu không hợp lệ. Vui lòng tải lại trang rồi thử lại.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
