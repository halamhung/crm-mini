package com.crm.dao;

import com.crm.model.Role;
import com.crm.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {
    public List<Role> findAll() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles ORDER BY id";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setName(rs.getString("name"));
                role.setDescription(rs.getString("description"));
                roles.add(role);
            }
        }
        return roles;
    }

    public void updateDescription(int id, String description) throws SQLException {
        String sql = "UPDATE roles SET description=? WHERE id=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, description);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }
}
