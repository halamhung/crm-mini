package com.crm.dao;

import com.crm.model.Project;
import com.crm.model.ProjectMember;
import com.crm.model.User;
import com.crm.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {
    private Project readProject(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getInt("id"));
        project.setName(rs.getString("name"));
        project.setDescription(rs.getString("description"));
        project.setStartDate(rs.getDate("start_date").toLocalDate());
        project.setEndDate(rs.getDate("end_date").toLocalDate());
        project.setOwnerId(rs.getInt("owner_id"));
        project.setOwnerName(rs.getString("owner_name"));
        return project;
    }

    public List<Project> findVisible(User user) throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT p.*, u.name AS owner_name FROM projects p JOIN users u ON u.id=p.owner_id";
        if (!user.isAdmin()) {
            if (user.isManager()) {
                sql += " WHERE p.owner_id=?";
            } else {
                sql += " WHERE EXISTS (SELECT 1 FROM tasks t WHERE t.project_id=p.id AND t.assignee_id=?)";
            }
        }
        sql += " ORDER BY p.id";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (!user.isAdmin()) {
                statement.setInt(1, user.getId());
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    projects.add(readProject(rs));
                }
            }
        }
        return projects;
    }

    public Project findById(int id) throws SQLException {
        String sql = "SELECT p.*, u.name AS owner_name FROM projects p JOIN users u ON u.id=p.owner_id WHERE p.id=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return readProject(rs);
                }
            }
        }
        return null;
    }

    public void insert(Project project) throws SQLException {
        String sql = "INSERT INTO projects(name, description, start_date, end_date, owner_id) VALUES(?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, project.getName());
            statement.setString(2, project.getDescription());
            statement.setDate(3, Date.valueOf(project.getStartDate()));
            statement.setDate(4, Date.valueOf(project.getEndDate()));
            statement.setInt(5, project.getOwnerId());
            statement.executeUpdate();
        }
    }

    public void update(Project project) throws SQLException {
        String sql = "UPDATE projects SET name=?, description=?, start_date=?, end_date=?, owner_id=? WHERE id=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, project.getName());
            statement.setString(2, project.getDescription());
            statement.setDate(3, Date.valueOf(project.getStartDate()));
            statement.setDate(4, Date.valueOf(project.getEndDate()));
            statement.setInt(5, project.getOwnerId());
            statement.setInt(6, project.getId());
            statement.executeUpdate();
        }
    }

    // Xoa cong viec truoc, sau do xoa du an. Rollback neu mot buoc bi loi.
    public void delete(int id) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM tasks WHERE project_id=?")) {
                    statement.setInt(1, id);
                    statement.executeUpdate();
                }
                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM projects WHERE id=?")) {
                    statement.setInt(1, id);
                    statement.executeUpdate();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public List<ProjectMember> findMembers(int projectId) throws SQLException {
        List<ProjectMember> members = new ArrayList<>();
        String sql = "SELECT pm.*, u.name, u.email FROM project_members pm JOIN users u ON u.id=pm.user_id WHERE pm.project_id=? ORDER BY u.id";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, projectId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ProjectMember member = new ProjectMember();
                    member.setProjectId(rs.getInt("project_id"));
                    member.setUserId(rs.getInt("user_id"));
                    member.setUserName(rs.getString("name"));
                    member.setEmail(rs.getString("email"));
                    members.add(member);
                }
            }
        }
        return members;
    }

    public boolean isMember(int projectId, int userId) throws SQLException {
        String sql = "SELECT 1 FROM project_members WHERE project_id=? AND user_id=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, projectId);
            statement.setInt(2, userId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void addMember(int projectId, int userId) throws SQLException {
        String sql = "INSERT INTO project_members(project_id, user_id) VALUES(?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, projectId);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }

    public void removeMember(int projectId, int userId) throws SQLException {
        String sql = "DELETE FROM project_members WHERE project_id=? AND user_id=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, projectId);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }
}
