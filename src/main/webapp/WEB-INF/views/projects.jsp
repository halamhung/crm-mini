<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="common/header.jspf" %>
<%
    List<Project> projects = (List<Project>) request.getAttribute("projects");
%>
<h2>Danh sách dự án</h2>
<% if (!currentUser.isManager()) { %>
<p class="page-description">Các dự án bạn đã được thêm vào. Công việc được giao nằm ở mục Công việc.</p>
<% } %>
<div class="table-wrap">
<table>
    <tr><th>ID</th><th>Tên dự án</th><th>Mô tả</th><th>Bắt đầu</th><th>Kết thúc</th><th>Quản lý</th>
        <% if (currentUser.isManager()) { %><th>Thao tác</th><% } %>
    </tr>
    <% for (Project project : projects) { %>
    <tr>
        <td><%= project.getId() %></td>
        <td><%= WebUtil.escape(project.getName()) %></td>
        <td><%= WebUtil.escape(project.getDescription()) %></td>
        <td><%= project.getStartDate() %></td>
        <td><%= project.getEndDate() %></td>
        <td><%= WebUtil.escape(project.getOwnerName()) %></td>
        <% if (currentUser.isManager()) { %>
        <td>
            <a href="<%= contextPath %>/projects?id=<%= project.getId() %>">Sửa / Thành viên</a>
            <form method="post" action="<%= contextPath %>/projects" class="inline-form" onsubmit="return confirm('Xóa dự án sẽ xóa cả công việc. Bạn đồng ý?');">
                <%@ include file="common/csrf.jspf" %>
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="id" value="<%= project.getId() %>">
                <button type="submit" class="delete">Xóa</button>
            </form>
        </td>
        <% } %>
    </tr>
    <% } %>
</table>
</div>
<% if (projects.isEmpty()) { %><p>Chưa có dự án.</p><% } %>
<% if (currentUser.isManager()) {
    Project editProject = (Project) request.getAttribute("editProject");
    List<User> users = (List<User>) request.getAttribute("users");
%>
<h3><%= editProject.getId() == 0 ? "Thêm dự án" : "Sửa dự án" %></h3>
<form method="post" action="<%= contextPath %>/projects" class="edit-form">
    <%@ include file="common/csrf.jspf" %>
    <input type="hidden" name="action" value="save">
    <input type="hidden" name="id" value="<%= editProject.getId() %>">
    <label>Tên dự án</label>
    <input name="name" value="<%= WebUtil.escape(editProject.getName()) %>" required maxlength="150">
    <label>Mô tả</label>
    <textarea name="description" rows="3"><%= WebUtil.escape(editProject.getDescription()) %></textarea>
    <label>Ngày bắt đầu</label>
    <input type="date" name="startDate" value="<%= WebUtil.escape(editProject.getStartDate()) %>" required>
    <label>Ngày kết thúc</label>
    <input type="date" name="endDate" value="<%= WebUtil.escape(editProject.getEndDate()) %>" required>
    <% if (currentUser.isAdmin()) { %>
    <label>Người quản lý</label>
    <select name="ownerId">
        <% for (User user : users) { if (user.isManager()) { %>
        <option value="<%= user.getId() %>" <%= user.getId() == editProject.getOwnerId() ? "selected" : "" %>><%= WebUtil.escape(user.getName()) %></option>
        <% } } %>
    </select>
    <% } %>
    <button type="submit">Lưu dự án</button>
    <a href="<%= contextPath %>/projects">Nhập mới</a>
</form>
<% if (editProject.getId() > 0) {
    List<ProjectMember> members = (List<ProjectMember>) request.getAttribute("members");
%>
<h3>Thành viên: <%= WebUtil.escape(editProject.getName()) %></h3>
<div class="table-wrap">
<table>
    <tr><th>Họ tên</th><th>Email</th><th>Thao tác</th></tr>
    <% for (ProjectMember member : members) { %>
    <tr>
        <td><%= WebUtil.escape(member.getUserName()) %></td>
        <td><%= WebUtil.escape(member.getEmail()) %></td>
        <td>
            <form method="post" action="<%= contextPath %>/projects" class="inline-form" onsubmit="return confirm('Loại thành viên khỏi dự án?');">
                <%@ include file="common/csrf.jspf" %>
                <input type="hidden" name="action" value="removeMember">
                <input type="hidden" name="id" value="<%= editProject.getId() %>">
                <input type="hidden" name="userId" value="<%= member.getUserId() %>">
                <button type="submit" class="delete">Loại khỏi dự án</button>
            </form>
        </td>
    </tr>
    <% } %>
</table>
</div>
<form method="post" action="<%= contextPath %>/projects" class="edit-form">
    <%@ include file="common/csrf.jspf" %>
    <input type="hidden" name="action" value="addMember">
    <input type="hidden" name="id" value="<%= editProject.getId() %>">
    <label>Chọn nhân viên để thêm vào dự án</label>
    <select name="userId" required>
        <% for (User user : users) { if (user.getRoleId() == 3) { %>
        <option value="<%= user.getId() %>"><%= WebUtil.escape(user.getName()) %> - <%= WebUtil.escape(user.getEmail()) %></option>
        <% } } %>
    </select>
    <button type="submit">Thêm thành viên</button>
</form>
<p>Muốn loại thành viên, hãy chuyển hoặc xóa công việc của người đó trước.</p>
<% } } %>
<%@ include file="common/footer.jspf" %>
