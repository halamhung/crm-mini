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

@WebServlet("/profile")
public class ProfileServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forward(request, response, "profile");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = currentUser(request);
            String name = WebUtil.text(request, "name");
            WebUtil.check(!name.isEmpty(), "Vui lòng nhập họ tên.");
            String password = WebUtil.text(request, "password");
            if (!password.isEmpty()) {
                WebUtil.checkPassword(password);
                String oldPassword = WebUtil.text(request, "currentPassword");
                WebUtil.check(BCrypt.checkpw(oldPassword, user.getPassword()), "Mật khẩu hiện tại không đúng.");
                user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
            }
            user.setName(name);
            user.setAddress(WebUtil.text(request, "address"));
            user.setPhone(WebUtil.text(request, "phone"));
            userDAO.update(user);
        } catch (Exception e) {
            saveError(request, e);
        }
        redirect(request, response, "/profile");
    }
}
