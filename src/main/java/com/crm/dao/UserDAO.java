package com.crm.dao;

import com.crm.model.User;
import com.crm.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private User readUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setName(rs.getString("name"));
        user.setAddress(rs.getString("address"));
        user.setPhone(rs.getString("phone"));
        user.setRoleId(rs.getInt("role_id"));
        return user;
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                users.add(readUser(rs));
            }
        }
        return users;
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return readUser(rs);
                }
            }
        }
        return null;
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return readUser(rs);
                }
            }
        }
        return null;
    }

  
    public void insert(User user) throws SQLException {
        String sql = "INSERT INTO users(email, password, name, address, phone, role_id) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getName());
            statement.setString(4, user.getAddress());
            statement.setString(5, user.getPhone());
            statement.setInt(6, user.getRoleId());
            statement.executeUpdate();
        }
    }

    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET email=?, password=?, name=?, address=?, phone=?, role_id=? WHERE id=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getName());
            statement.setString(4, user.getAddress());
            statement.setString(5, user.getPhone());
            statement.setInt(6, user.getRoleId());
            statement.setInt(7, user.getId());
            statement.executeUpdate();
        }
    }

    public boolean hasProjectRelations(int userId) throws SQLException {
        String sql = "SELECT id FROM projects WHERE owner_id=? UNION ALL SELECT project_id FROM project_members WHERE user_id=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
}
