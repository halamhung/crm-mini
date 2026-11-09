package com.crm;

import org.junit.Before;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;
import com.crm.dao.*;
import com.crm.model.*;
import com.crm.util.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class MiniCrmTest {
    private UserDAO userDAO = new UserDAO();
    private ProjectDAO projectDAO = new ProjectDAO();
    private TaskDAO taskDAO = new TaskDAO();

    @Before
    public void prepareDatabase() throws Exception {
        System.setProperty("DB_URL", "jdbc:h2:mem:crm;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
        System.setProperty("DB_USER", "sa");
        System.setProperty("DB_PASSWORD", "");
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP ALL OBJECTS");
            String[] files = {"database/01-schema.sql", "database/02-demo.sql"};
            for (String file : files) {
                String script = Files.readString(Path.of(file)).replaceAll("(?m)^--.*$", "");
                for (String sql : script.split(";")) {
                    if (!sql.isBlank()) {
                        statement.execute(sql);
                    }
                }
            }
            statement.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 10");
            statement.execute("ALTER TABLE projects ALTER COLUMN id RESTART WITH 10");
            statement.execute("ALTER TABLE tasks ALTER COLUMN id RESTART WITH 10");
            statement.execute("INSERT INTO users(id,email,password,name,role_id) VALUES(4,'other@crm.local','unused','Other leader',2)");
        }
    }

    @Test
    public void permissionsFollowThreeRoles() throws Exception {
        PermissionUtil.requireAdmin(userDAO.findById(1));
        PermissionUtil.requireManager(userDAO.findById(2));
        assertThrows(IllegalArgumentException.class, () -> PermissionUtil.requireAdmin(userDAO.findById(2)));
        assertThrows(IllegalArgumentException.class, () -> PermissionUtil.requireManager(userDAO.findById(3)));
    }

    @Test
    public void leaderCannotManageAnotherProject() throws Exception {
        Project project = projectDAO.findById(1);
        PermissionUtil.requireProject(userDAO.findById(2), project);
        assertThrows(IllegalArgumentException.class, () -> PermissionUtil.requireProject(userDAO.findById(4), project));
        assertTrue(projectDAO.findVisible(userDAO.findById(4)).isEmpty());
        assertTrue(taskDAO.findVisible(userDAO.findById(4)).isEmpty());
    }

    @Test
    public void onlyAssigneeCanChangeStatus() throws Exception {
        assertEquals(0, taskDAO.updateStatus(1, 4, 0));
        assertEquals(2, taskDAO.findById(1).getStatus());
        assertEquals(1, taskDAO.updateStatus(1, 3, 1));
        assertEquals(1, taskDAO.findById(1).getStatus());
    }

    @Test
    public void databaseRejectsAssigneeOutsideProject() throws Exception {
        Task task = taskDAO.findById(1);
        task.setAssigneeId(4);
        assertFalse(projectDAO.isMember(1, 4));
        assertThrows(SQLException.class, () -> taskDAO.insert(task));
        assertEquals(3, taskDAO.findVisible(userDAO.findById(1)).size());
    }

    @Test
    public void deletingProjectRemovesTasksAndMembers() throws Exception {
        projectDAO.delete(1);
        assertNull(projectDAO.findById(1));
        assertTrue(taskDAO.findVisible(userDAO.findById(1)).isEmpty());
        assertTrue(projectDAO.findMembers(1).isEmpty());
    }

    @Test
    public void userCrudUsesTypedModel() throws Exception {
        User user = new User();
        user.setName("Nhân viên mới");
        user.setEmail("new@crm.local");
        user.setPassword(BCrypt.hashpw("Password123", BCrypt.gensalt()));
        user.setAddress("Hà Nội");
        user.setPhone("0900000000");
        user.setRoleId(3);
        userDAO.insert(user);
        user = userDAO.findByEmail("new@crm.local");
        user.setName("Tên đã sửa");
        userDAO.update(user);
        assertEquals("Tên đã sửa", userDAO.findById(user.getId()).getName());
        userDAO.delete(user.getId());
        assertNull(userDAO.findByEmail("new@crm.local"));
    }

    @Test
    public void statisticsRespectScopeAndEmptyProjects() throws Exception {
        StatisticDAO statisticDAO = new StatisticDAO();
        Statistic statistic = statisticDAO.findByProject(userDAO.findById(3)).get(0);
        assertEquals(3, statistic.getTotal());
        assertEquals(1, statistic.getDone());
        assertEquals(33, statistic.getCompletedPercent());
        assertTrue(statisticDAO.findByEmployee(userDAO.findById(4)).isEmpty());
        Project project = projectDAO.findById(1);
        project.setName("Dự án trống");
        projectDAO.insert(project);
        assertEquals(2, statisticDAO.findByProject(userDAO.findById(1)).size());
        assertEquals(0, statisticDAO.findByProject(userDAO.findById(1)).get(1).getCompletedPercent());
    }

    @Test
    public void validationAndDemoPasswordWork() throws Exception {
        assertTrue(BCrypt.checkpw("111112113", userDAO.findById(1).getPassword()));
        assertThrows(IllegalArgumentException.class, () -> WebUtil.checkPassword("123"));
        assertThrows(IllegalArgumentException.class, () -> WebUtil.checkDates(LocalDate.of(2026, 12, 1), LocalDate.of(2026, 11, 1)));
        assertEquals("&lt;script&gt;&quot;&#39;&amp;", WebUtil.escape("<script>\"'&"));
    }
}
