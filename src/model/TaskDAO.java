package model;

import database.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TaskDAO - Data Access Object untuk Task
 * CRUD lengkap dengan JOIN ke tabel relasi
 */
public class TaskDAO {
    private Connection conn;

    public TaskDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    private static final String SELECT_BASE =
        "SELECT t.*, c.name as category_name, " +
        "u1.full_name as assigned_name, u2.full_name as created_name " +
        "FROM tasks t " +
        "LEFT JOIN categories c ON t.category_id = c.id " +
        "LEFT JOIN users u1 ON t.assigned_to = u1.id " +
        "LEFT JOIN users u2 ON t.created_by = u2.id ";

    // CREATE
    public boolean create(Task task) {
        String sql = "INSERT INTO tasks (title, description, category_id, assigned_to, created_by, priority, status, deadline) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setInt(3, task.getCategoryId());
            ps.setInt(4, task.getAssignedTo());
            ps.setInt(5, task.getCreatedBy());
            ps.setString(6, task.getPriority());
            ps.setString(7, task.getStatus());
            ps.setTimestamp(8, Timestamp.valueOf(task.getDeadline()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error create task: " + e.getMessage());
            return false;
        }
    }

    // READ - semua task
    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY t.deadline ASC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) tasks.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error findAll tasks: " + e.getMessage());
        }
        return tasks;
    }

    // READ - task by user (assigned)
    public List<Task> findByUser(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE t.assigned_to = ? ORDER BY t.deadline ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) tasks.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error findByUser: " + e.getMessage());
        }
        return tasks;
    }

    // READ - by id
    public Task findById(int id) {
        String sql = SELECT_BASE + "WHERE t.id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("Error findById task: " + e.getMessage());
        }
        return null;
    }

    // READ - by status
    public List<Task> findByStatus(String status) {
        List<Task> tasks = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE t.status = ? ORDER BY t.deadline ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) tasks.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error findByStatus: " + e.getMessage());
        }
        return tasks;
    }

    // READ - by status untuk user yang sedang login
    public List<Task> findByUserAndStatus(int userId, String status) {
        List<Task> tasks = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE t.assigned_to = ? AND t.status = ? ORDER BY t.deadline ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) tasks.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error findByUserAndStatus: " + e.getMessage());
        }
        return tasks;
    }

    // READ - tasks mendekati deadline (dalam N hari)
    public List<Task> findUpcomingDeadlines(int days) {
        List<Task> tasks = new ArrayList<>();
        String sql = SELECT_BASE +
            "WHERE t.deadline BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL ? DAY) " +
            "AND t.status != 'Selesai' ORDER BY t.deadline ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) tasks.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error findUpcomingDeadlines: " + e.getMessage());
        }
        return tasks;
    }

    // READ - tasks mendekati deadline untuk user yang sedang login
    public List<Task> findUpcomingDeadlinesByUser(int userId, int days) {
        List<Task> tasks = new ArrayList<>();
        String sql = SELECT_BASE +
            "WHERE t.assigned_to = ? AND t.deadline BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL ? DAY) " +
            "AND t.status != 'Selesai' ORDER BY t.deadline ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, days);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) tasks.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error findUpcomingDeadlinesByUser: " + e.getMessage());
        }
        return tasks;
    }

    // UPDATE
    public boolean update(Task task) {
        String sql = "UPDATE tasks SET title=?, description=?, category_id=?, assigned_to=?, " +
                     "priority=?, status=?, deadline=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setInt(3, task.getCategoryId());
            ps.setInt(4, task.getAssignedTo());
            ps.setString(5, task.getPriority());
            ps.setString(6, task.getStatus());
            ps.setTimestamp(7, Timestamp.valueOf(task.getDeadline()));
            ps.setInt(8, task.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error update task: " + e.getMessage());
            return false;
        }
    }

    // UPDATE status only
    public boolean updateStatus(int taskId, String status) {
        String sql = "UPDATE tasks SET status=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, taskId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updateStatus: " + e.getMessage());
            return false;
        }
    }

    // UPDATE overdue tasks secara otomatis
    public int updateOverdueTasks() {
        String sql = "UPDATE tasks SET status='Overdue' WHERE deadline < NOW() AND status NOT IN ('Selesai', 'Overdue')";
        try (Statement st = conn.createStatement()) {
            return st.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error updateOverdueTasks: " + e.getMessage());
            return 0;
        }
    }

    // DELETE
    public boolean delete(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error delete task: " + e.getMessage());
            return false;
        }
    }

    // COUNT statistik
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM tasks WHERE status = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error countByStatus: " + e.getMessage());
        }
        return 0;
    }

    // Helper: map ResultSet ke Task
    private Task mapRow(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setCategoryId(rs.getInt("category_id"));
        task.setCategoryName(rs.getString("category_name"));
        task.setAssignedTo(rs.getInt("assigned_to"));
        task.setAssignedToName(rs.getString("assigned_name"));
        task.setCreatedBy(rs.getInt("created_by"));
        task.setCreatedByName(rs.getString("created_name"));
        task.setPriority(rs.getString("priority"));
        task.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("deadline");
        if (ts != null) task.setDeadline(ts.toLocalDateTime());
        task.setCreatedAt(rs.getString("created_at"));
        task.setUpdatedAt(rs.getString("updated_at"));
        return task;
    }
}
