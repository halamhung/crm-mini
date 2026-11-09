<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="common/header.jspf" %>
<%
    List<Task> tasks = (List<Task>) request.getAttribute("tasks");
%>
<h2>Danh sách công việc</h2>
<div class="table-wrap">
<table>
    <tr><th>ID</th><th>Công việc</th><th>Mô tả</th><th>Dự án</th><th>Người thực hiện</th><th>Bắt đầu</th><th>Kết thúc</th><th>Trạng thái</th><th>Thao tác</th></tr>
    <% for (Task task : tasks) { %>
    <tr>
        <td><%= task.getId() %></td>
        <td><%= WebUtil.escape(task.getName()) %></td>
        <td><%= WebUtil.escape(task.getDescription()) %></td>
        <td><%= WebUtil.escape(task.getProjectName()) %></td>
        <td><%= WebUtil.escape(task.getAssigneeName()) %></td>
        <td><%= task.getStartDate() %></td>
        <td><%= task.getEndDate() %></td>
        <td><span class="status status-<%= task.getStatus() %>"><%= task.getStatusName() %></span></td>
        <td>
            <% if (currentUser.isManager()) { %>
            <a href="<%= contextPath %>/tasks?id=<%= task.getId() %>">Sửa</a>
            <form method="post" action="<%= contextPath %>/tasks" class="inline-form" onsubmit="return confirm('Xóa công việc này?');">
                <%@ include file="common/csrf.jspf" %>
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="id" value="<%= task.getId() %>">
                <button type="submit" class="delete">Xóa</button>
            </form>
            <% } %>
            <% if (task.getAssigneeId() == currentUser.getId()) { %>
            <form method="post" action="<%= contextPath %>/tasks" class="inline-form">
                <%@ include file="common/csrf.jspf" %>
                <input type="hidden" name="action" value="status">
                <input type="hidden" name="id" value="<%= task.getId() %>">
                <select name="status">
                    <option value="0" <%= task.getStatus() == 0 ? "selected" : "" %>>Chưa bắt đầu</option>
                    <option value="1" <%= task.getStatus() == 1 ? "selected" : "" %>>Đang thực hiện</option>
                    <option value="2" <%= task.getStatus() == 2 ? "selected" : "" %>>Đã hoàn thành</option>
                </select>
                <button type="submit">Cập nhật</button>
            </form>
            <% } %>
        </td>
    </tr>
    <% } %>
</table>
</div>
<% if (tasks.isEmpty()) { %>
    <% if (currentUser.isManager()) { %>
        <p>Chưa có công việc.</p>
    <% } else { %>
        <p>Bạn chưa được giao công việc. Các dự án đã tham gia nằm ở mục Dự án.</p>
    <% } %>
<% } %>
<% if (currentUser.isManager()) {
    List<Project> projects = (List<Project>) request.getAttribute("projects");
    List<ProjectMember> members = (List<ProjectMember>) request.getAttribute("members");
    Task editTask = (Task) request.getAttribute("editTask");
%>
<h3>Thêm / sửa công việc</h3>
<form method="get" action="<%= contextPath %>/tasks" class="edit-form">
    <label>Chọn dự án để phân công công việc mới</label>
    <select name="projectId" required>
        <option value="">-- Chọn dự án --</option>
        <% for (Project project : projects) { %>
        <option value="<%= project.getId() %>" <%= project.getId() == editTask.getProjectId() ? "selected" : "" %>><%= WebUtil.escape(project.getName()) %></option>
        <% } %>
    </select>
    <button type="submit">Chọn dự án</button>
</form>
<% if (editTask.getProjectId() > 0) { %>
<form method="post" action="<%= contextPath %>/tasks" class="edit-form">
    <%@ include file="common/csrf.jspf" %>
    <input type="hidden" name="action" value="save">
    <input type="hidden" name="id" value="<%= editTask.getId() %>">
    <input type="hidden" name="projectId" value="<%= editTask.getProjectId() %>">
    <label>Tên công việc</label>
    <input name="name" value="<%= WebUtil.escape(editTask.getName()) %>" required maxlength="150">
    <label>Mô tả</label>
    <textarea name="description" rows="3"><%= WebUtil.escape(editTask.getDescription()) %></textarea>
    <label>Ngày bắt đầu</label>
    <input type="date" name="startDate" value="<%= WebUtil.escape(editTask.getStartDate()) %>" required>
    <label>Ngày kết thúc</label>
    <input type="date" name="endDate" value="<%= WebUtil.escape(editTask.getEndDate()) %>" required>
    <label>Người thực hiện (thành viên của dự án)</label>
    <select name="assigneeId" required>
        <% for (ProjectMember member : members) { %>
        <option value="<%= member.getUserId() %>" <%= member.getUserId() == editTask.getAssigneeId() ? "selected" : "" %>><%= WebUtil.escape(member.getUserName()) %> - <%= WebUtil.escape(member.getEmail()) %></option>
        <% } %>
    </select>
    <label>Trạng thái</label>
    <select name="status">
        <option value="0" <%= editTask.getStatus() == 0 ? "selected" : "" %>>Chưa bắt đầu</option>
        <option value="1" <%= editTask.getStatus() == 1 ? "selected" : "" %>>Đang thực hiện</option>
        <option value="2" <%= editTask.getStatus() == 2 ? "selected" : "" %>>Đã hoàn thành</option>
    </select>
    <button type="submit">Lưu công việc</button>
    <a href="<%= contextPath %>/tasks?projectId=<%= editTask.getProjectId() %>">Nhập mới</a>
</form>
<% if (members.isEmpty()) { %><p>Hãy thêm thành viên vào dự án trước khi phân công.</p><% } %>
<% } } %>
<%@ include file="common/footer.jspf" %>
