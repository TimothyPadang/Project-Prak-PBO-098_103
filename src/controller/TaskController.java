package controller;

import model.*;
import java.util.List;

/**
 * TaskController - MVC CONTROLLER
 * Menghubungkan View (GUI) dengan Model (DAO)
 * Semua logika bisnis ada di sini
 */
public class TaskController {
    // Enkapsulasi: private DAO references
    private TaskDAO taskDAO;
    private CategoryDAO categoryDAO;
    private UserDAO userDAO;
    private TaskNoteDAO taskNoteDAO;
    private User currentUser; // user yang sedang login

    public TaskController(User currentUser) {
        this.currentUser = currentUser;
        this.taskDAO = new TaskDAO();
        this.categoryDAO = new CategoryDAO();
        this.userDAO = new UserDAO();
        this.taskNoteDAO = new TaskNoteDAO();
    }

    // ===== TASK CRUD =====

    public boolean createTask(Task task) {
        if (!task.isValid()) return false;
        task.setCreatedBy(currentUser.getId());
        return taskDAO.create(task);
    }

    public List<Task> getAllTasks() {
        if (currentUser.isAdmin()) {
            return taskDAO.findAll();
        } else {
            return taskDAO.findByUser(currentUser.getId());
        }
    }

    public List<Task> getTasksByStatus(String status) {
        return taskDAO.findByStatus(status);
    }

    public List<Task> searchTasks(String keyword) {
        return taskDAO.search(keyword);
    }

    public List<Task> getUpcomingDeadlines(int days) {
        return taskDAO.findUpcomingDeadlines(days);
    }

    public Task getTaskById(int id) {
        return taskDAO.findById(id);
    }

    public boolean updateTask(Task task) {
        if (!task.isValid()) return false;
        return taskDAO.update(task);
    }

    public boolean updateTaskStatus(int taskId, String newStatus) {
        return taskDAO.updateStatus(taskId, newStatus);
    }

    public boolean deleteTask(int taskId) {
        // Hanya admin atau pembuat task yang boleh hapus
        Task task = taskDAO.findById(taskId);
        if (task == null) return false;
        if (!currentUser.isAdmin() && task.getCreatedBy() != currentUser.getId()) {
            return false;
        }
        return taskDAO.delete(taskId);
    }

    // ===== CATEGORY CRUD =====

    public boolean createCategory(Category cat) {
        if (!cat.isValid()) return false;
        return categoryDAO.create(cat);
    }

    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    public boolean updateCategory(Category cat) {
        return categoryDAO.update(cat);
    }

    public boolean deleteCategory(int id) {
        return categoryDAO.delete(id);
    }

    // ===== USER MANAGEMENT =====

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public boolean createUser(User user) {
        if (!user.isValid()) return false;
        return userDAO.create(user);
    }

    public boolean updateUser(User user) {
        return userDAO.update(user);
    }

    public boolean deleteUser(int userId) {
        if (userId == currentUser.getId()) return false; // tidak bisa hapus diri sendiri
        return userDAO.delete(userId);
    }

    public boolean changePassword(int userId, String newPassword) {
        return userDAO.updatePassword(userId, newPassword);
    }

    // ===== NOTES =====

    public boolean addNote(int taskId, String noteText) {
        TaskNote note = new TaskNote(taskId, currentUser.getId(), noteText);
        return taskNoteDAO.create(note);
    }

    public List<TaskNote> getNotesByTask(int taskId) {
        return taskNoteDAO.findByTask(taskId);
    }

    public boolean deleteNote(int noteId) {
        return taskNoteDAO.delete(noteId);
    }

    // ===== STATISTIK DASHBOARD =====

    public int countTasksByStatus(String status) {
        return taskDAO.countByStatus(status);
    }

    // Getter
    public User getCurrentUser() { return currentUser; }
}
