package com.crm.dao;

import com.crm.model.Statistic;
import com.crm.model.User;
import com.crm.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StatisticDAO {
    public List<Statistic> findByProject(User user) throws SQLException {
        String sql = "SELECT p.name AS project_name, '' AS employee_name, COUNT(t.id) AS total, "
                + "COALESCE(SUM(CASE WHEN t.status=0 THEN 1 ELSE 0 END),0) AS pending, "
                + "COALESCE(SUM(CASE WHEN t.status=1 THEN 1 ELSE 0 END),0) AS active, "
                + "COALESCE(SUM(CASE WHEN t.status=2 THEN 1 ELSE 0 END),0) AS done "
                + "FROM projects p LEFT JOIN tasks t ON t.project_id=p.id";
        boolean memberProjects = !user.isManager();
        if (memberProjects) {
            // Chỉ đếm việc của người đang đăng nhập, vẫn giữ dự án chưa có việc của họ.
            sql += " AND t.assignee_id=?"
                    + " WHERE EXISTS (SELECT 1 FROM project_members pm WHERE pm.project_id=p.id AND pm.user_id=?)";
        } else {
            sql += scope(user);
        }
        sql += " GROUP BY p.id, p.name ORDER BY p.id";
        return readStatistics(sql, user, memberProjects);
    }

    public List<Statistic> findByEmployee(User user) throws SQLException {
        String sql = "SELECT p.name AS project_name, u.name AS employee_name, COUNT(*) AS total, "
                + "SUM(CASE WHEN t.status=0 THEN 1 ELSE 0 END) AS pending, "
                + "SUM(CASE WHEN t.status=1 THEN 1 ELSE 0 END) AS active, "
                + "SUM(CASE WHEN t.status=2 THEN 1 ELSE 0 END) AS done "
                + "FROM tasks t JOIN projects p ON p.id=t.project_id JOIN users u ON u.id=t.assignee_id";
        sql += scope(user);
        sql += " GROUP BY p.id, p.name, u.id, u.name ORDER BY p.id, u.id";
        return readStatistics(sql, user, false);
    }

    private String scope(User user) {
        if (user.isAdmin()) {
            return "";
        }
        if (user.isManager()) {
            return " WHERE p.owner_id=?";
        }
        return " WHERE t.assignee_id=?";
    }

    private List<Statistic> readStatistics(String sql, User user, boolean memberProjects) throws SQLException {
        List<Statistic> statistics = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (!user.isAdmin()) {
                statement.setInt(1, user.getId());
                if (memberProjects) {
                    statement.setInt(2, user.getId());
                }
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Statistic statistic = new Statistic();
                    statistic.setProjectName(rs.getString("project_name"));
                    statistic.setEmployeeName(rs.getString("employee_name"));
                    statistic.setTotal(rs.getInt("total"));
                    statistic.setPending(rs.getInt("pending"));
                    statistic.setActive(rs.getInt("active"));
                    statistic.setDone(rs.getInt("done"));
                    statistics.add(statistic);
                }
            }
        }
        return statistics;
    }
}
