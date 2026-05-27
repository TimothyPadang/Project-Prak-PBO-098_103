package controller;

import model.*;
import java.util.List;

/**
 * TaskController - MVC CONTROLLER
 * Menghubungkan View (GUI) dengan Model (DAO)
 */
public class TaskController {
    private TaskDAO taskDAO;
    private CategoryDAO categoryDAO;
    private UserDAO userDAO;
    private TaskNoteDAO taskNoteDAO;
    private User currentUser;

    public TaskController(User currentUser) {
        this.currentUser = currentUser;
        this.taskDAO = new TaskDAO();
        this.categoryDAO = new CategoryDAO();
        this.userDAO = new UserDAO();
        this.taskNoteDAO = new TaskNoteDAO();
    }

    public boolean createTask(Task task) {
        task.setCreatedBy(currentUser.getId());
        task.setAssignedTo(currentUser.getId()); // otomatis diberikan ke user yang login
        if (!task.isValid()) return false;
        return taskDAO.create(task);
    }

    public List<Task> getAllTasks() {
        return taskDAO.findByUser(currentUser.getId());
    }

    public List<Task> getTasksByStatus(String status) {
        return taskDAO.findByUserAndStatus(currentUser.getId(), status);
    }

    public List<Task> getUpcomingDeadlines(int days) {
        return taskDAO.findUpcomingDeadlinesByUser(currentUser.getId(), days);
    }

    public Task getTaskById(int id) {
        Task task = taskDAO.findById(id);
        if (task == null) return null;
        return task.getAssignedTo() == currentUser.getId() ? task : null;
    }

    public boolean updateTask(Task task) {
        task.setAssignedTo(currentUser.getId());
        if (!task.isValid()) return false;
        return taskDAO.update(task);
    }

    public boolean updateTaskStatus(int taskId, String newStatus) {
        Task task = getTaskById(taskId);
        if (task == null) return false;
        return taskDAO.updateStatus(taskId, newStatus);
    }

    public boolean deleteTask(int taskId) {
        Task task = getTaskById(taskId);
        if (task == null) return false;
        return taskDAO.delete(taskId);
    }

    public boolean createCategory(Category cat) {
        if (!cat.isValid()) return false;
        return categoryDAO.create(cat);
    }

    public List<Category> getAllCategories() { return categoryDAO.findAll(); }
    public boolean updateCategory(Category cat) { return categoryDAO.update(cat); }
    public boolean deleteCategory(int id) { return categoryDAO.delete(id); }

    public List<User> getAllUsers() { return userDAO.findAll(); }
    public boolean createUser(User user) { if (!user.isValid()) return false; return userDAO.create(user); }
    public boolean updateUser(User user) { return userDAO.update(user); }
    public boolean deleteUser(int userId) { if (userId == currentUser.getId()) return false; return userDAO.delete(userId); }
    public boolean changePassword(int userId, String newPassword) { return userDAO.updatePassword(userId, newPassword); }

    public boolean addNote(int taskId, String noteText) {
        Task task = getTaskById(taskId);
        if (task == null) return false;
        TaskNote note = new TaskNote(taskId, currentUser.getId(), noteText);
        return taskNoteDAO.create(note);
    }

    public List<TaskNote> getNotesByTask(int taskId) { return taskNoteDAO.findByTask(taskId); }
    public boolean deleteNote(int noteId) { return taskNoteDAO.delete(noteId); }
    public int countTasksByStatus(String status) { return taskDAO.findByUserAndStatus(currentUser.getId(), status).size(); }
    public User getCurrentUser() { return currentUser; }
}
