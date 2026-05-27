-- ============================================
-- DATABASE: Task Management System
-- Sistem Manajemen Tugas dan Monitoring Deadline
-- ============================================

CREATE DATABASE IF NOT EXISTS task_management;
USE task_management;

-- Tabel Users
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role ENUM('admin', 'user') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabel Categories
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    color VARCHAR(7) DEFAULT '#3498db'
);

-- Tabel Tasks
CREATE TABLE IF NOT EXISTS tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category_id INT,
    assigned_to INT,
    created_by INT,
    priority ENUM('Low', 'Medium', 'High', 'Critical') DEFAULT 'Medium',
    status ENUM('Pending', 'In Progress', 'Completed', 'Overdue') DEFAULT 'Pending',
    deadline DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Tabel Task Notes / Comments
CREATE TABLE IF NOT EXISTS task_notes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL,
    user_id INT NOT NULL,
    note TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert data default
INSERT INTO users (username, password, full_name, email, role) VALUES
('admin', 'admin123', 'Administrator', 'admin@taskmanager.com', 'admin'),
('user1', 'user123', 'Budi Santoso', 'budi@example.com', 'user'),
('user2', 'user123', 'Siti Rahayu', 'siti@example.com', 'user');

INSERT INTO categories (name, description, color) VALUES
('Kuliah', 'Tugas-tugas perkuliahan', '#3498db'),
('Organisasi', 'Kegiatan organisasi kampus', '#e74c3c'),
('Pribadi', 'Tugas pribadi', '#2ecc71'),
('Kerja', 'Tugas pekerjaan', '#f39c12');

INSERT INTO tasks (title, description, category_id, assigned_to, created_by, priority, status, deadline) VALUES
('Laporan Praktikum PBO', 'Membuat laporan lengkap praktikum PBO pertemuan 8', 1, 2, 1, 'High', 'In Progress', DATE_ADD(NOW(), INTERVAL 3 DAY)),
('Tugas Basis Data', 'Normalisasi database hingga 3NF', 1, 2, 1, 'Medium', 'Pending', DATE_ADD(NOW(), INTERVAL 5 DAY)),
('Rapat HIMA', 'Persiapan materi rapat bulanan HIMA', 2, 3, 1, 'Low', 'Pending', DATE_ADD(NOW(), INTERVAL 2 DAY)),
('Proyek Akhir Semester', 'Implementasi sistem manajemen tugas berbasis Java', 1, 2, 1, 'Critical', 'In Progress', DATE_ADD(NOW(), INTERVAL 14 DAY)),
('Tugas Overdue Example', 'Contoh tugas yang sudah lewat deadline', 1, 2, 1, 'High', 'Overdue', DATE_SUB(NOW(), INTERVAL 2 DAY));
