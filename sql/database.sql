DROP DATABASE IF EXISTS databasepbo;
CREATE DATABASE databasepbo;
USE databasepbo;

-- Tabel Users
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabel Categories (tanpa warna/hex)
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

-- Tabel Tasks
CREATE TABLE tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category_id INT,
    assigned_to INT,
    created_by INT,
    priority ENUM('EZ', 'Sedang', 'Susah', 'Susah+++') DEFAULT 'Sedang',
    status ENUM('Blm mulai', 'Proses', 'Selesai', 'Overdue') DEFAULT 'Proses',
    deadline DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabel Task Notes / Comments
CREATE TABLE task_notes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL,
    user_id INT NOT NULL,
    note TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO users (username, password, full_name, email) VALUES
('user1', 'user123', 'Budi Santoso', 'budi@example.com'),
('user2', 'user123', 'Siti Rahayu', 'siti@example.com');

INSERT INTO categories (name, description) VALUES
('Kuliah', 'Tugas-tugas perkuliahan'),
('Organisasi', 'Kegiatan organisasi kampus'),
('Pribadi', 'Tugas pribadi'),
('Kerja', 'Tugas pekerjaan');


INSERT INTO tasks (title, description, category_id, assigned_to, created_by, priority, status, deadline) VALUES
('Laporan Praktikum PBO', 'Membuat laporan lengkap praktikum PBO pertemuan 8', 1, 1, 1, 'Susah', 'Proses', DATE_ADD(NOW(), INTERVAL 3 DAY)),
('Tugas Basis Data', 'Normalisasi database hingga 3NF', 1, 1, 1, 'Sedang', 'Blm mulai', DATE_ADD(NOW(), INTERVAL 5 DAY)),
('Rapat HIMA', 'Persiapan materi rapat bulanan HIMA', 2, 2, 2, 'EZ', 'Blm mulai', DATE_ADD(NOW(), INTERVAL 2 DAY)),
('Proyek Akhir Semester', 'Implementasi sistem manajemen tugas berbasis Java', 1, 1, 1, 'Susah+++', 'Proses', DATE_ADD(NOW(), INTERVAL 14 DAY)),
('Tugas Overdue Example', 'Contoh tugas yang sudah lewat deadline', 1, 1, 1, 'Susah', 'Overdue', DATE_SUB(NOW(), INTERVAL 2 DAY));
