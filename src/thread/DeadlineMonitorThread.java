package thread;

import model.Task;
import model.TaskDAO;
import javax.swing.*;
import java.util.List;

/**
 * DeadlineMonitorThread - MULTITHREADING
 * Thread daemon yang berjalan di background untuk:
 * 1. Memperbarui status task yang sudah overdue secara otomatis
 * 2. Mengirim notifikasi popup jika ada deadline yang mendekat
 * 3. Refresh tampilan dashboard secara berkala
 */
public class DeadlineMonitorThread extends Thread {
    private boolean running;
    private static final int CHECK_INTERVAL_MS = 30000; // 30 detik
    private DeadlineListener listener;
    private TaskDAO taskDAO;

    // Interface untuk callback ke GUI (ABSTRAKSI)
    public interface DeadlineListener {
        void onOverdueDetected(List<Task> overdueTasks);
        void onUpcomingDeadline(List<Task> upcomingTasks);
        void onRefreshNeeded();
    }

    public DeadlineMonitorThread(DeadlineListener listener) {
        this.listener = listener;
        this.taskDAO = new TaskDAO();
        this.running = true;
        setDaemon(true); // Thread daemon - otomatis berhenti saat app ditutup
        setName("DeadlineMonitorThread");
    }

    @Override
    public void run() {
        System.out.println("DeadlineMonitorThread mulai berjalan...");
        while (running) {
            try {
                // 1. Update status task yang sudah overdue di database
                int updatedCount = taskDAO.updateOverdueTasks();
                if (updatedCount > 0) {
                    System.out.println("Updated " + updatedCount + " task(s) menjadi Overdue");
                }

                // 2. Ambil task overdue
                List<Task> overdueTasks = taskDAO.findByStatus("Overdue");

                // 3. Ambil task yang deadline dalam 1 hari
                List<Task> upcomingTasks = taskDAO.findUpcomingDeadlines(1);

                // 4. Callback ke GUI via EDT (Event Dispatch Thread)
                SwingUtilities.invokeLater(() -> {
                    if (listener != null) {
                        if (!overdueTasks.isEmpty()) {
                            listener.onOverdueDetected(overdueTasks);
                        }
                        if (!upcomingTasks.isEmpty()) {
                            listener.onUpcomingDeadline(upcomingTasks);
                        }
                        listener.onRefreshNeeded();
                    }
                });

                // Tunggu interval berikutnya
                Thread.sleep(CHECK_INTERVAL_MS);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("DeadlineMonitorThread dihentikan.");
                break;
            } catch (Exception e) {
                System.err.println("Error pada DeadlineMonitorThread: " + e.getMessage());
            }
        }
    }

    public void stopMonitoring() {
        this.running = false;
        this.interrupt();
    }
}
