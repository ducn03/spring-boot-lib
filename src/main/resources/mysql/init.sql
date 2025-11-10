create database springbootlib;

CREATE TABLE U_USER (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(255),
    full_name VARCHAR(255),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_date TIMESTAMP,
    role_group_id BIGINT,
    EStatus INT DEFAULT 1, -- Cột trạng thái người dùng
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Thời điểm tạo bản ghi
    updatedAt TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- Thời điểm cập nhật bản ghi
    -- Các cột khác có thể thêm vào tùy nhu cầu
);