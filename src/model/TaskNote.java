package model;

/**
 * TaskNote Model - Catatan/komentar pada task
 * INHERITANCE: extends BaseModel
 */
public class TaskNote extends BaseModel {
    private int taskId;
    private int userId;
    private String userName;
    private String note;

    public TaskNote() { super(); }

    public TaskNote(int taskId, int userId, String note) {
        super();
        this.taskId = taskId;
        this.userId = userId;
        this.note = note;
    }

    @Override
    public String getDisplayName() { return note; }

    @Override
    public boolean isValid() {
        return note != null && !note.isEmpty() && taskId > 0 && userId > 0;
    }

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    @Override
    public String toString() {
        return "[" + userName + "]: " + note;
    }
}
