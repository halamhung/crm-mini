package com.crm.controller;

import com.crm.dao.UserDAO;
import com.crm.model.User;
import com.crm.util.WebUtil;
import org.mindrot.jbcrypt.BCrypt;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (currentUser(request) != null) {
            redirect(request, response, "/");
            return;
        }
        forward(request, response, "login");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String email = WebUtil.text(request, "email");
            String password = WebUtil.text(request, "password");
            User user = userDAO.findByEmail(email);
            if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
                request.setAttribute("error", "Email hoặc mật khẩu không đúng.");
                forward(request, response, "login");
                return;
            }
            request.changeSessionId();
            request.getSession().setAttribute("userId", user.getId());
            request.getSession().setAttribute("csrf", UUID.randomUUID().toString());
            redirect(request, response, "/");
        } catch (Exception e) {
            showError(request, response, e);
        }
    }
}
