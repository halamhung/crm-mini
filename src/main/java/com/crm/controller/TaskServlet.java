package com.crm.controller;

import com.crm.dao.ProjectDAO;
import com.crm.dao.TaskDAO;
import com.crm.model.Task;
import com.crm.model.User;
import com.crm.util.PermissionUtil;
import com.crm.util.WebUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

@WebServlet("/tasks")
public class TaskServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private TaskDAO taskDAO = new TaskDAO();
    private ProjectDAO projectDAO = new ProjectDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            User user = currentUser(request);
            request.setAttribute("tasks", taskDAO.findVisible(user));
            if (user.isManager()) {
                Task editTask = new Task();
                int projectId = WebUtil.number(request, "projectId");
                int id = WebUtil.number(request, "id");
                if (id > 0) {
                    editTask = taskDAO.findById(id);
                    WebUtil.check(editTask != null, "Không tìm thấy công việc.");
                    projectId = editTask.getProjectId();
                }
                if (projectId > 0) {
                    PermissionUtil.requireProject(user, projectDAO.findById(projectId));
                    request.setAttribute("members", projectDAO.findMembers(projectId));
                } else {
                    request.setAttribute("members", new ArrayList<>());
                }
                editTask.setProjectId(projectId);
                request.setAttribute("editTask", editTask);
                request.setAttribute("projects", projectDAO.findVisible(user));
            }
            forward(request, response, "tasks");
        } catch (Exception e) {
            showError(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = currentUser(request);
            int id = WebUtil.number(request, "id");
            String action = WebUtil.text(request, "action");
            if (action.equals("status")) {
                int status = WebUtil.number(request, "status");
                WebUtil.check(status >= 0 && status <= 2, "Trạng thái không hợp lệ.");
                int updated = taskDAO.updateStatus(id, user.getId(), status);
                WebUtil.check(updated == 1, "Bạn chỉ được cập nhật công việc được giao cho mình.");
            } else {
                PermissionUtil.requireManager(user);
                if (id > 0) {
                    Task oldTask = taskDAO.findById(id);
                    WebUtil.check(oldTask != null, "Không tìm thấy công việc.");
                    PermissionUtil.requireProject(user, projectDAO.findById(oldTask.getProjectId()));
                }
                if (action.equals("delete")) {
                    WebUtil.check(id > 0, "Vui lòng chọn công việc.");
                    taskDAO.delete(id);
                } else if (action.equals("save")) {
                    saveTask(request, user, id);
                } else {
                    throw new IllegalArgumentException("Thao tác không hợp lệ.");
                }
            }
        } catch (Exception e) {
            saveError(request, e);
        }
        redirect(request, response, "/tasks");
    }

    private void saveTask(HttpServletRequest request, User user, int id) throws Exception {
        Task task = new Task();
        task.setId(id);
        task.setName(WebUtil.text(request, "name"));
        task.setDescription(WebUtil.text(request, "description"));
        task.setStartDate(LocalDate.parse(WebUtil.text(request, "startDate")));
        task.setEndDate(LocalDate.parse(WebUtil.text(request, "endDate")));
        task.setProjectId(WebUtil.number(request, "projectId"));
        task.setAssigneeId(WebUtil.number(request, "assigneeId"));
        task.setStatus(WebUtil.number(request, "status"));
        PermissionUtil.requireProject(user, projectDAO.findById(task.getProjectId()));
        WebUtil.check(!task.getName().isEmpty(), "Vui lòng nhập tên công việc.");
        WebUtil.checkDates(task.getStartDate(), task.getEndDate());
        WebUtil.check(task.getStatus() >= 0 && task.getStatus() <= 2, "Trạng thái không hợp lệ.");
        WebUtil.check(projectDAO.isMember(task.getProjectId(), task.getAssigneeId()),
                "Người được giao phải là thành viên của dự án.");
        if (id == 0) {
            taskDAO.insert(task);
        } else {
            taskDAO.update(task);
        }
    }
}
