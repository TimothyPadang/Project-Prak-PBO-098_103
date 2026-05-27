package model;

import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TaskNoteDAO - CRUD untuk catatan/komentar task
 */
public class TaskNoteDAO {
    private Connection conn;

    public TaskNoteDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public boolean create(TaskNote note) {
        String sql = "INSERT INTO task_notes (task_id, user_id, note) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, note.getTaskId());
            ps.setInt(2, note.getUserId());
            ps.setString(3, note.getNote());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error create note: " + e.getMessage());
            return false;
        }
    }

    public List<TaskNote> findByTask(int taskId) {
        List<TaskNote> notes = new ArrayList<>();
        String sql = "SELECT n.*, u.full_name as user_name FROM task_notes n " +
                     "JOIN users u ON n.user_id = u.id WHERE n.task_id = ? ORDER BY n.created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TaskNote note = new TaskNote();
                note.setId(rs.getInt("id"));
                note.setTaskId(rs.getInt("task_id"));
                note.setUserId(rs.getInt("user_id"));
                note.setUserName(rs.getString("user_name"));
                note.setNote(rs.getString("note"));
                note.setCreatedAt(rs.getString("created_at"));
                notes.add(note);
            }
        } catch (SQLException e) {
            System.err.println("Error findByTask notes: " + e.getMessage());
        }
        return notes;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM task_notes WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error delete note: " + e.getMessage());
            return false;
        }
    }
}
