<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="common/header.jspf" %>
<%
    List<Role> roles = (List<Role>) request.getAttribute("roles");
%>
<h2>Quản lý quyền</h2>
<p>Hệ thống có ba vai trò cố định. Cấp quyền cho tài khoản ở trang Nhân viên.</p>
<div class="table-wrap">
<table>
    <tr><th>ID</th><th>Tên quyền</th><th>Mô tả</th></tr>
    <% for (Role role : roles) { %>
    <tr>
        <td><%= role.getId() %></td>
        <td><%= WebUtil.escape(role.getName()) %></td>
        <td>
            <form method="post" action="<%= contextPath %>/roles" class="inline-form">
                <%@ include file="common/csrf.jspf" %>
                <input type="hidden" name="id" value="<%= role.getId() %>">
                <input name="description" value="<%= WebUtil.escape(role.getDescription()) %>" required maxlength="255">
                <button type="submit">Lưu mô tả</button>
            </form>
        </td>
    </tr>
    <% } %>
</table>
</div>
<%@ include file="common/footer.jspf" %>
