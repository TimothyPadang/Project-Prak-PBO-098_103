package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Task Model - Model utama sistem
 * INHERITANCE: extends BaseModel
 * ENKAPSULASI: semua field private
 */
public class Task extends BaseModel {
    private String title;
    private String description;
    private int categoryId;
    private String categoryName;
    private int assignedTo;
    private String assignedToName;
    private int createdBy;
    private String createdByName;
    private String priority;
    private String status;
    private LocalDateTime deadline;
    private String updatedAt;

    // Konstanta prioritas dan status (ENKAPSULASI)
    public static final String[] PRIORITIES = {"Low", "Medium", "High", "Critical"};
    public static final String[] STATUSES = {"Pending", "In Progress", "Completed", "Overdue"};

    public Task() { super(); }

    public Task(int id, String title, String description, String priority,
                String status, LocalDateTime deadline) {
        super(id);
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.deadline = deadline;
    }

    // ABSTRAKSI: implementasi abstract methods
    @Override
    public String getDisplayName() { return title; }

    @Override
    public boolean isValid() {
        return title != null && !title.isEmpty() && deadline != null;
    }

    // Method bisnis: hitung sisa hari deadline
    public long getDaysUntilDeadline() {
        if (deadline == null) return 0;
        return ChronoUnit.DAYS.between(LocalDateTime.now(), deadline);
    }

    // Method bisnis: cek apakah overdue
    public boolean isOverdue() {
        return deadline != null && LocalDateTime.now().isAfter(deadline)
                && !"Completed".equals(status);
    }

    // Method bisnis: format deadline
    public String getFormattedDeadline() {
        if (deadline == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return deadline.format(formatter);
    }

    // Method bisnis: status keterangan
    public String getDeadlineStatus() {
        long days = getDaysUntilDeadline();
        if ("Completed".equals(status)) return "Selesai";
        if (isOverdue()) return "Terlambat " + Math.abs(days) + " hari";
        if (days == 0) return "Deadline hari ini!";
        if (days <= 3) return "Segera! " + days + " hari lagi";
        return days + " hari lagi";
    }

    // Getters & Setters (ENKAPSULASI)
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public int getAssignedTo() { return assignedTo; }
    public void setAssignedTo(int assignedTo) { this.assignedTo = assignedTo; }

    public String getAssignedToName() { return assignedToName; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // POLYMORPHISM: override toString
    @Override
    public String toString() {
        return "[" + priority + "] " + title + " - " + getFormattedDeadline();
    }
}
