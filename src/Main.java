import view.LoginFrame;
import javax.swing.*;

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
