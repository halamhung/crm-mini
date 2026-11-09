<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="common/header.jspf" %>
<%
    List<User> users = (List<User>) request.getAttribute("users");
    User editUser = (User) request.getAttribute("editUser");
    List<Role> roles = (List<Role>) request.getAttribute("roles");
%>
<h2>Danh sách nhân viên</h2>
<div class="table-wrap">
<table>
    <tr><th>ID</th><th>Họ tên</th><th>Email</th><th>Địa chỉ</th><th>Điện thoại</th><th>Vai trò</th>
        <% if (currentUser.isAdmin()) { %><th>Thao tác</th><% } %>
    </tr>
    <% for (User user : users) { %>
    <tr>
        <td><%= user.getId() %></td>
        <td><%= WebUtil.escape(user.getName()) %></td>
        <td><%= WebUtil.escape(user.getEmail()) %></td>
        <td><%= WebUtil.escape(user.getAddress()) %></td>
        <td><%= WebUtil.escape(user.getPhone()) %></td>
        <td><span class="role-badge"><%= user.getRoleName() %></span></td>
        <% if (currentUser.isAdmin()) { %>
        <td>
            <a href="<%= contextPath %>/users?id=<%= user.getId() %>">Sửa</a>
            <form method="post" action="<%= contextPath %>/users" class="inline-form" onsubmit="return confirm('Bạn muốn xóa nhân viên này?');">
                <%@ include file="common/csrf.jspf" %>
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="id" value="<%= user.getId() %>">
                <button type="submit" class="delete">Xóa</button>
            </form>
        </td>
        <% } %>
    </tr>
    <% } %>
</table>
</div>
<% if (currentUser.isAdmin()) { %>
<h3><%= editUser.getId() == 0 ? "Thêm nhân viên" : "Sửa nhân viên" %></h3>
<form method="post" action="<%= contextPath %>/users" class="edit-form">
    <%@ include file="common/csrf.jspf" %>
    <input type="hidden" name="action" value="save">
    <input type="hidden" name="id" value="<%= editUser.getId() %>">
    <label>Họ tên</label>
    <input name="name" value="<%= WebUtil.escape(editUser.getName()) %>" required maxlength="150">
    <label>Email</label>
    <input type="email" name="email" value="<%= WebUtil.escape(editUser.getEmail()) %>" required maxlength="190">
    <label>Mật khẩu (khi sửa có thể để trống để giữ nguyên)</label>
    <input type="password" name="password" minlength="8" maxlength="72" <%= editUser.getId() == 0 ? "required" : "" %>>
    <label>Địa chỉ</label>
    <input name="address" value="<%= WebUtil.escape(editUser.getAddress()) %>" maxlength="255">
    <label>Điện thoại</label>
    <input name="phone" value="<%= WebUtil.escape(editUser.getPhone()) %>" maxlength="30">
    <label>Vai trò</label>
    <select name="roleId">
        <% for (Role role : roles) { %>
        <option value="<%= role.getId() %>" <%= role.getId() == editUser.getRoleId() ? "selected" : "" %>><%= WebUtil.escape(role.getName()) %></option>
        <% } %>
    </select>
    <button type="submit">Lưu nhân viên</button>
    <a href="<%= contextPath %>/users">Nhập mới</a>
</form>
<% } %>
<%@ include file="common/footer.jspf" %>
