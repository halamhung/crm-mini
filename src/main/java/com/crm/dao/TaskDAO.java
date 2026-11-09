package com.crm.dao;

import com.crm.model.Task;
import com.crm.model.User;
import com.crm.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private static final String SELECT_TASKS = "SELECT t.*, p.name AS project_name, u.name AS assignee_name "
            + "FROM tasks t JOIN projects p ON p.id=t.project_id JOIN users u ON u.id=t.assignee_id";

    private Task readTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setName(rs.getString("name"));
        task.setDescription(rs.getString("description"));
        task.setStartDate(rs.getDate("start_date").toLocalDate());
        task.setEndDate(rs.getDate("end_date").toLocalDate());
        task.setProjectId(rs.getInt("project_id"));
        task.setAssigneeId(rs.getInt("assignee_id"));
        task.setStatus(rs.getInt("status"));
        task.setProjectName(rs.getString("project_name"));
        task.setAssigneeName(rs.getString("assignee_name"));
        return task;
    }

    public List<Task> findVisible(User user) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = SELECT_TASKS;
        if (!user.isAdmin()) {
            if (user.isManager()) {
                sql += " WHERE p.owner_id=?";
            } else {
                sql += " WHERE t.assignee_id=?";
            }
        }
        sql += " ORDER BY t.id";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            // ADMIN xem tất cả; LEADER xem dự án mình quản lý; MEMBER xem việc của mình.
            if (!user.isAdmin()) {
                statement.setInt(1, user.getId());
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    tasks.add(readTask(rs));
                }
            }
        }
        return tasks;
    }

    public Task findById(int id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_TASKS + " WHERE t.id=?")) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return readTask(rs);
                }
            }
        }
        return null;
    }

    public void insert(Task task) throws SQLException {
        String sql = "INSERT INTO tasks(name, description, start_date, end_date, project_id, assignee_id, status) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, task.getName());
            statement.setString(2, task.getDescription());
            statement.setDate(3, Date.valueOf(task.getStartDate()));
            statement.setDate(4, Date.valueOf(task.getEndDate()));
            statement.setInt(5, task.getProjectId());
            statement.setInt(6, task.getAssigneeId());
            statement.setInt(7, task.getStatus());
            statement.executeUpdate();
        }
    }

    public void update(Task task) throws SQLException {
        String sql = "UPDATE tasks SET name=?, description=?, start_date=?, end_date=?, project_id=?, assignee_id=?, status=? WHERE id=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, task.getName());
            statement.setString(2, task.getDescription());
            statement.setDate(3, Date.valueOf(task.getStartDate()));
            statement.setDate(4, Date.valueOf(task.getEndDate()));
            statement.setInt(5, task.getProjectId());
            statement.setInt(6, task.getAssigneeId());
            statement.setInt(7, task.getStatus());
            statement.setInt(8, task.getId());
            statement.executeUpdate();
        }
    }

    public int updateStatus(int id, int assigneeId, int status) throws SQLException {
        String sql = "UPDATE tasks SET status=? WHERE id=? AND assignee_id=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, status);
            statement.setInt(2, id);
            statement.setInt(3, assigneeId);
            return statement.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM tasks WHERE id=?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
}
