<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="common/header.jspf" %>
<%
    Statistic summary = (Statistic) request.getAttribute("summary");
    List<Statistic> projectStats = (List<Statistic>) request.getAttribute("projectStats");
    List<Statistic> employeeStats = (List<Statistic>) request.getAttribute("employeeStats");
%>
<h2>Thống kê công việc</h2>
<p class="page-description">Theo dõi số lượng công việc và tiến độ thực hiện.</p>
<div class="summary-grid">
    <div class="summary-box"><span>Tổng công việc</span><strong><%= summary.getTotal() %></strong></div>
    <div class="summary-box pending"><span>Chưa bắt đầu</span><strong><%= summary.getPending() %></strong></div>
    <div class="summary-box active-work"><span>Đang thực hiện</span><strong><%= summary.getActive() %></strong></div>
    <div class="summary-box completed"><span>Đã hoàn thành</span><strong><%= summary.getDone() %></strong></div>
</div>
<h3>Tiến độ theo dự án</h3>
<div class="table-wrap">
<table>
    <tr><th>Dự án</th><th>Tổng việc</th><th>Chưa bắt đầu</th><th>Đang làm</th><th>Hoàn thành</th><th>Tỷ lệ hoàn thành</th></tr>
    <% for (Statistic statistic : projectStats) { %>
    <tr>
        <td><%= WebUtil.escape(statistic.getProjectName()) %></td>
        <td><%= statistic.getTotal() %></td>
        <td><%= statistic.getPending() %></td>
        <td><%= statistic.getActive() %></td>
        <td><%= statistic.getDone() %></td>
        <td><%= statistic.getCompletedPercent() %>%</td>
    </tr>
    <% } %>
</table>
</div>
<h3>Tiến độ nhân viên trong từng dự án</h3>
<div class="table-wrap">
<table>
    <tr><th>Dự án</th><th>Nhân viên</th><th>Tổng việc</th><th>Chưa bắt đầu</th><th>Đang làm</th><th>Hoàn thành</th></tr>
    <% for (Statistic statistic : employeeStats) { %>
    <tr>
        <td><%= WebUtil.escape(statistic.getProjectName()) %></td>
        <td><%= WebUtil.escape(statistic.getEmployeeName()) %></td>
        <td><%= statistic.getTotal() %></td>
        <td><%= statistic.getPending() %></td>
        <td><%= statistic.getActive() %></td>
        <td><%= statistic.getDone() %></td>
    </tr>
    <% } %>
</table>
</div>
<% if (projectStats.isEmpty()) { %>
    <p>Chưa có dữ liệu thống kê.</p>
<% } %>
<%@ include file="common/footer.jspf" %>
