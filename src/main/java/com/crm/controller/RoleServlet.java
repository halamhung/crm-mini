package com.crm.controller;

import com.crm.dao.RoleDAO;
import com.crm.util.PermissionUtil;
import com.crm.util.WebUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/roles")
public class RoleServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private RoleDAO roleDAO = new RoleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            PermissionUtil.requireAdmin(currentUser(request));
            request.setAttribute("roles", roleDAO.findAll());
            forward(request, response, "roles");
        } catch (Exception e) {
            showError(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            PermissionUtil.requireAdmin(currentUser(request));
            int id = WebUtil.number(request, "id");
            String description = WebUtil.text(request, "description");
            WebUtil.check(id >= 1 && id <= 3 && !description.isEmpty(), "Vui lòng nhập mô tả quyền hợp lệ.");
            roleDAO.updateDescription(id, description);
        } catch (Exception e) {
            saveError(request, e);
        }
        redirect(request, response, "/roles");
    }
}
