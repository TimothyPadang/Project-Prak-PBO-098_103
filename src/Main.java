import view.LoginFrame;
import javax.swing.*;

/**
 * Main - Entry Point Aplikasi
 * Sistem Manajemen Tugas dan Monitoring Deadline
 *
 * Fitur:
 * - 4 Pilar PBO: Enkapsulasi, Abstraksi, Polymorphism, Inheritance
 * - Multithreading: DeadlineMonitorThread
 * - GUI: Java Swing
 * - Database: MySQL via JDBC
 * - CRUD: Task, Category, User
 * - MVC Pattern
 */
public class Main {
    public static void main(String[] args) {
        // Atur Look and Feel ke sistem
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Gunakan default Look and Feel jika gagal
        }

        // Jalankan di Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}
