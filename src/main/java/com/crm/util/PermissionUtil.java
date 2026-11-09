package com.crm.util;

import com.crm.model.Project;
import com.crm.model.User;

public class PermissionUtil {
    public static void requireAdmin(User user) {
        WebUtil.check(user != null && user.isAdmin(), "Chỉ ADMIN được thực hiện chức năng này.");
    }

    public static void requireManager(User user) {
        WebUtil.check(user != null && user.isManager(), "Chỉ ADMIN hoặc LEADER được quản lý dự án.");
    }

    public static void requireProject(User user, Project project) {
        requireManager(user);
        WebUtil.check(project != null, "Không tìm thấy dự án.");
        WebUtil.check(user.isAdmin() || project.getOwnerId() == user.getId(),
                "Bạn không có quyền quản lý dự án này.");
    }
}
