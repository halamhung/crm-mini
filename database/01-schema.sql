CREATE TABLE roles (
    id INT PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL
);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(190) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(150) NOT NULL,
    address VARCHAR(255) NOT NULL DEFAULT '',
    phone VARCHAR(30) NOT NULL DEFAULT '',
    role_id INT NOT NULL
);

CREATE TABLE projects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    owner_id INT NOT NULL,
    CHECK (end_date >= start_date)
);

CREATE TABLE project_members (
    project_id INT NOT NULL,
    user_id INT NOT NULL,
    PRIMARY KEY (project_id, user_id)
);

CREATE TABLE tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    project_id INT NOT NULL,
    assignee_id INT NOT NULL,
    status INT NOT NULL DEFAULT 0,
    CHECK (status BETWEEN 0 AND 2),
    CHECK (end_date >= start_date)
);


ALTER TABLE users
    ADD CONSTRAINT fk_users_roles
    FOREIGN KEY (role_id) REFERENCES roles(id);

ALTER TABLE projects
    ADD CONSTRAINT fk_projects_users
    FOREIGN KEY (owner_id) REFERENCES users(id);

ALTER TABLE project_members
    ADD CONSTRAINT fk_project_members_projects
    FOREIGN KEY (project_id) REFERENCES projects(id)
    ON DELETE CASCADE;

ALTER TABLE project_members
    ADD CONSTRAINT fk_project_members_users
    FOREIGN KEY (user_id) REFERENCES users(id);


ALTER TABLE tasks
    ADD CONSTRAINT fk_tasks_project_members
    FOREIGN KEY (project_id, assignee_id)
    REFERENCES project_members(project_id, user_id);

-- 3. Thêm ba quyền mặc định sau khi tạo xong bảng và quan hệ.
INSERT INTO roles (id, name, description) VALUES
    (1, 'ADMIN', 'Quản trị hệ thống'),
    (2, 'LEADER', 'Quản lý dự án'),
    (3, 'MEMBER', 'Nhân viên');
