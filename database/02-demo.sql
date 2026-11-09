SET NAMES utf8mb4;

-- 4. Dữ liệu mẫu. Mật khẩu đăng nhập chung: 111112113
INSERT INTO users (id, email, password, name, address, phone, role_id) VALUES
    (1, 'admin@crm.local', '$2a$12$F7G4rc9oLMAPUVkhpVasfuhB6xtPIchvF0C1mfdvJN53ZZW2tJC3S', 'Lâm Hùng', 'TP. Hồ Chí Minh', '0901234567', 1),
    (2, 'leader@crm.local', '$2a$12$F7G4rc9oLMAPUVkhpVasfuhB6xtPIchvF0C1mfdvJN53ZZW2tJC3S', 'Nguyễn Minh', 'Hà Nội', '0912345678', 2),
    (3, 'member@crm.local', '$2a$12$F7G4rc9oLMAPUVkhpVasfuhB6xtPIchvF0C1mfdvJN53ZZW2tJC3S', 'Trần An', 'Đà Nẵng', '0923456789', 3);

INSERT INTO projects (id, name, description, start_date, end_date, owner_id) VALUES
    (1, 'Website quản lý nhân viên',
     'Làm website quản lý nhân viên và công việc',
     '2026-11-01', '2026-11-30', 2);

INSERT INTO project_members (project_id, user_id) VALUES
    (1, 3);

-- Trạng thái: 0 chưa bắt đầu, 1 đang thực hiện, 2 hoàn thành.
INSERT INTO tasks
    (name, description, start_date, end_date, project_id, assignee_id, status)
VALUES
    ('Làm trang đăng nhập', 'Tạo form email và mật khẩu',
     '2026-11-01', '2026-11-05', 1, 3, 2),
    ('Làm danh sách nhân viên', 'Hiển thị nhân viên bằng bảng',
     '2026-11-06', '2026-11-15', 1, 3, 1),
    ('Kiểm tra chức năng', 'Kiểm tra thêm, sửa và xóa nhân viên',
     '2026-11-16', '2026-11-25', 1, 3, 0);
