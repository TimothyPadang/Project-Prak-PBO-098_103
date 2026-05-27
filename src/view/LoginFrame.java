package view;

import controller.AuthController;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * LoginFrame - Halaman Login
 * VIEW dalam MVC Pattern
 */
public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblStatus;
    private AuthController authController;

    public LoginFrame() {
        this.authController = new AuthController();
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Task Management System - Login");
        setSize(420, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Panel utama
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.SIDEBAR_BG, 0, getHeight(), UITheme.PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        // Card Panel
        JPanel card = new JPanel();
        card.setBackground(UITheme.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));

        // Judul
        JLabel lblTitle = new JLabel("Task Manager");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.PRIMARY);
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Sistem Manajemen Tugas & Deadline");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        lblSub.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblIcon = new JLabel("📋", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        lblIcon.setAlignmentX(CENTER_ALIGNMENT);

        // Form fields
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UITheme.FONT_BOLD);
        lblUser.setForeground(UITheme.TEXT_DARK);

        txtUsername = new JTextField();
        txtUsername.setFont(UITheme.FONT_BODY);
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.LIGHT, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UITheme.FONT_BOLD);
        lblPass.setForeground(UITheme.TEXT_DARK);

        txtPassword = new JPasswordField();
        txtPassword.setFont(UITheme.FONT_BODY);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.LIGHT, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        // Login button
        btnLogin = new JButton("LOGIN");
        btnLogin.setFont(UITheme.FONT_BOLD);
        btnLogin.setBackground(UITheme.SECONDARY);
        btnLogin.setForeground(UITheme.WHITE);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.setAlignmentX(CENTER_ALIGNMENT);

        // Status label
        lblStatus = new JLabel(" ");
        lblStatus.setFont(UITheme.FONT_SMALL);
        lblStatus.setForeground(UITheme.DANGER);
        lblStatus.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblHint = new JLabel("Default: admin / admin123");
        lblHint.setFont(UITheme.FONT_SMALL);
        lblHint.setForeground(UITheme.TEXT_MUTED);
        lblHint.setAlignmentX(CENTER_ALIGNMENT);

        // Susun komponen
        card.add(lblIcon);
        card.add(Box.createVerticalStrut(5));
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(3));
        card.add(lblSub);
        card.add(Box.createVerticalStrut(25));
        card.add(lblUser);
        card.add(Box.createVerticalStrut(5));
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(12));
        card.add(lblPass);
        card.add(Box.createVerticalStrut(5));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(20));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(10));
        card.add(lblStatus);
        card.add(Box.createVerticalStrut(5));
        card.add(lblHint);

        mainPanel.add(card);
        add(mainPanel);

        // Event Listeners
        btnLogin.addActionListener(e -> doLogin());
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        });
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) txtPassword.requestFocus();
            }
        });
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Username dan password tidak boleh kosong!");
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Memverifikasi...");

        // Login dalam thread terpisah agar GUI tidak freeze (MULTITHREADING)
        new Thread(() -> {
            User user = authController.login(username, password);
            SwingUtilities.invokeLater(() -> {
                btnLogin.setEnabled(true);
                btnLogin.setText("LOGIN");
                if (user != null) {
                    dispose();
                    new MainFrame(user);
                } else {
                    lblStatus.setText("Username atau password salah!");
                    txtPassword.setText("");
                }
            });
        }).start();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { /* ignore */ }
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
