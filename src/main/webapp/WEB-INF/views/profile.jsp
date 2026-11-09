<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="common/header.jspf" %>
<h2>Tài khoản cá nhân</h2>
<p>Email: <%= WebUtil.escape(currentUser.getEmail()) %></p>
<form method="post" action="<%= contextPath %>/profile" class="edit-form">
    <%@ include file="common/csrf.jspf" %>
    <label>Họ tên</label>
    <input name="name" value="<%= WebUtil.escape(currentUser.getName()) %>" required maxlength="150">
    <label>Địa chỉ</label>
    <input name="address" value="<%= WebUtil.escape(currentUser.getAddress()) %>" maxlength="255">
    <label>Điện thoại</label>
    <input name="phone" value="<%= WebUtil.escape(currentUser.getPhone()) %>" maxlength="30">
    <label>Mật khẩu hiện tại (chỉ nhập khi đổi mật khẩu)</label>
    <input type="password" name="currentPassword">
    <label>Mật khẩu mới (để trống nếu không đổi)</label>
    <input type="password" name="password" minlength="8" maxlength="72">
    <button type="submit">Lưu tài khoản</button>
</form>
<%@ include file="common/footer.jspf" %>
