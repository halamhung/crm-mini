package com.crm.controller;

import com.crm.dao.ProjectDAO;
import com.crm.dao.UserDAO;
import com.crm.model.Project;
import com.crm.model.User;
import com.crm.util.PermissionUtil;
import com.crm.util.WebUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/projects")
public class ProjectServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private ProjectDAO projectDAO = new ProjectDAO();
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            User user = currentUser(request);
            request.setAttribute("projects", projectDAO.findVisible(user));
            if (user.isManager()) {
                Project editProject = new Project();
                editProject.setOwnerId(user.getId());
                int id = WebUtil.number(request, "id");
           
                if (id > 0) {
                    editProject = projectDAO.findById(id);
                    PermissionUtil.requireProject(user, editProject);
                    request.setAttribute("members", projectDAO.findMembers(id));
                }
                request.setAttribute("editProject", editProject);
                request.setAttribute("users", userDAO.findAll());
            }
            forward(request, response, "projects");
        } catch (Exception e) {
            showError(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = 0;
        String action = "";
        try {
            User user = currentUser(request);
            PermissionUtil.requireManager(user);
            id = WebUtil.number(request, "id");
            action = WebUtil.text(request, "action");
            if (id > 0) {
                PermissionUtil.requireProject(user, projectDAO.findById(id));
            }
            if (action.equals("save")) {
                saveProject(request, user, id);
            } else if (action.equals("delete")) {
                WebUtil.check(id > 0, "Vui lòng chọn dự án.");
                projectDAO.delete(id);
            } else if (action.equals("addMember")) {
                WebUtil.check(id > 0, "Vui lòng chọn dự án.");
                int userId = WebUtil.number(request, "userId");
                User member = userDAO.findById(userId);
                WebUtil.check(member != null && member.getRoleId() == 3, "Chỉ thêm nhân viên có vai trò MEMBER.");
                WebUtil.check(!projectDAO.isMember(id, userId), "Nhân viên đã thuộc dự án.");
                projectDAO.addMember(id, userId);
            } else if (action.equals("removeMember")) {
                WebUtil.check(id > 0, "Vui lòng chọn dự án.");
                projectDAO.removeMember(id, WebUtil.number(request, "userId"));
            } else {
                throw new IllegalArgumentException("Thao tác không hợp lệ.");
            }
        } catch (Exception e) {
            saveError(request, e);
        }
        if (action.equals("addMember") || action.equals("removeMember")) {
            redirect(request, response, "/projects?id=" + id);
        } else {
            redirect(request, response, "/projects");
        }
    }

    private void saveProject(HttpServletRequest request, User user, int id) throws Exception {
        Project project = new Project();
        project.setId(id);
        project.setName(WebUtil.text(request, "name"));
        project.setDescription(WebUtil.text(request, "description"));
        project.setStartDate(LocalDate.parse(WebUtil.text(request, "startDate")));
        project.setEndDate(LocalDate.parse(WebUtil.text(request, "endDate")));
        project.setOwnerId(user.getId());
        WebUtil.check(!project.getName().isEmpty(), "Vui lòng nhập tên dự án.");
        WebUtil.checkDates(project.getStartDate(), project.getEndDate());
        if (user.isAdmin()) {
            User owner = userDAO.findById(WebUtil.number(request, "ownerId"));
            WebUtil.check(owner != null && owner.isManager(), "Người quản lý phải là ADMIN hoặc LEADER.");
            project.setOwnerId(owner.getId());
        }
        if (id == 0) {
            projectDAO.insert(project);
        } else {
            projectDAO.update(project);
        }
    }
}
