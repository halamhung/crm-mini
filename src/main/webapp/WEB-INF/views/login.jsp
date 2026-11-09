<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="common/header.jspf" %>
<div class="login-box">
    <h2>Đăng nhập CRM</h2>
    <form method="post" action="<%= contextPath %>/login">
        <%@ include file="common/csrf.jspf" %>
        <label>Email</label>
        <input type="email" name="email" required maxlength="190">
        <label>Mật khẩu</label>
        <input type="password" name="password" required>
        <button type="submit">Đăng nhập</button>
    </form>
</div>
<%@ include file="common/footer.jspf" %>
