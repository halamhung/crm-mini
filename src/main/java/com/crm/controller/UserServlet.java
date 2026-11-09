package com.crm.controller;

import com.crm.dao.RoleDAO;
import com.crm.dao.UserDAO;
import com.crm.model.User;
import com.crm.util.PermissionUtil;
import com.crm.util.WebUtil;
import org.mindrot.jbcrypt.BCrypt;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/users")
public class UserServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private UserDAO userDAO = new UserDAO();
    private RoleDAO roleDAO = new RoleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            User currentUser = currentUser(request);
            PermissionUtil.requireManager(currentUser);
            request.setAttribute("users", userDAO.findAll());
            // Mặc định là thêm mới. Có id trên URL thì lấy thông tin để sửa.
            User editUser = new User();
            editUser.setRoleId(3);
            int id = WebUtil.number(request, "id");
            if (id > 0) {
                PermissionUtil.requireAdmin(currentUser);
                editUser = userDAO.findById(id);
                WebUtil.check(editUser != null, "Không tìm thấy nhân viên.");
            }
            request.setAttribute("editUser", editUser);
            request.setAttribute("roles", roleDAO.findAll());
            forward(request, response, "users");
        } catch (Exception e) {
            showError(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User currentUser = currentUser(request);
            PermissionUtil.requireAdmin(currentUser);
            String action = WebUtil.text(request, "action");
            int id = WebUtil.number(request, "id");
            if (action.equals("delete")) {
                WebUtil.check(id != currentUser.getId(), "Không được tự xóa tài khoản đang đăng nhập.");
                userDAO.delete(id);
            } else if (action.equals("save")) {
                saveUser(request, currentUser, id);
            } else {
                throw new IllegalArgumentException("Thao tác không hợp lệ.");
            }
        } catch (Exception e) {
            saveError(request, e);
        }
        redirect(request, response, "/users");
    }

    private void saveUser(HttpServletRequest request, User currentUser, int id) throws Exception {
        // Đọc các ô trong form rồi gán vào đối tượng User.
        User user = new User();
        user.setId(id);
        user.setName(WebUtil.text(request, "name"));
        user.setEmail(WebUtil.text(request, "email"));
        user.setAddress(WebUtil.text(request, "address"));
        user.setPhone(WebUtil.text(request, "phone"));
        user.setRoleId(WebUtil.number(request, "roleId"));
        WebUtil.check(!user.getName().isEmpty(), "Vui lòng nhập họ tên.");
        WebUtil.check(user.getEmail().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+"), "Email không hợp lệ.");
        WebUtil.check(user.getRoleId() >= 1 && user.getRoleId() <= 3, "Vai trò không hợp lệ.");

        User oldUser = null;
        if (id > 0) {
            oldUser = userDAO.findById(id);
            WebUtil.check(oldUser != null, "Không tìm thấy nhân viên.");
            WebUtil.check(id != currentUser.getId() || user.getRoleId() == 1,
                    "Không được tự hạ quyền ADMIN đang đăng nhập.");
            if (oldUser.getRoleId() != user.getRoleId()) {
                WebUtil.check(!userDAO.hasProjectRelations(id),
                        "Hãy chuyển dự án và bỏ thành viên dự án trước khi đổi vai trò.");
            }
        }
        String password = WebUtil.text(request, "password");
        if (id == 0 || !password.isEmpty()) {
            WebUtil.checkPassword(password);
            user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        } else {
            user.setPassword(oldUser.getPassword());
        }
        // Form thêm mới gửi id = 0; form sửa gửi id của nhân viên.
        if (id == 0) {
            userDAO.insert(user);
        } else {
            userDAO.update(user);
        }
    }
}
