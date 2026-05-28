package view;

import controller.AuthController;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * LoginFrame - Halaman Login dan Register
 * VIEW dalam MVC Pattern
 */
public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;
    private JLabel lblStatus;
    private AuthController authController;

    public LoginFrame() {
        this.authController = new AuthController();
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Task Management System - Login");
        setSize(420, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.SIDEBAR_BG, 0, getHeight(), UITheme.PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setBackground(UITheme.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));

        JLabel lblIcon = new JLabel("📋", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        lblIcon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Manajemen Tugas");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.PRIMARY);
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Sistem Manajemen Tugas & Deadline");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        lblSub.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblUser = makeLabel("Username");
        txtUsername = createInputField();

        JLabel lblPass = makeLabel("Password");
        txtPassword = new JPasswordField();
        styleInput(txtPassword);

        btnLogin = new JButton("LOGIN");
        styleButton(btnLogin, UITheme.SECONDARY, 42);

        btnRegister = new JButton("REGISTER AKUN BARU");
        styleButton(btnRegister, UITheme.SUCCESS, 38);

        lblStatus = new JLabel(" ");
        lblStatus.setFont(UITheme.FONT_SMALL);
        lblStatus.setForeground(UITheme.DANGER);
        lblStatus.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblHint = new JLabel("Default: user1 / user123");
        lblHint.setFont(UITheme.FONT_SMALL);
        lblHint.setForeground(UITheme.TEXT_MUTED);
        lblHint.setAlignmentX(CENTER_ALIGNMENT);

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
        card.add(Box.createVerticalStrut(8));
        card.add(btnRegister);
        card.add(Box.createVerticalStrut(10));
        card.add(lblStatus);
        card.add(Box.createVerticalStrut(5));
        card.add(lblHint);

        mainPanel.add(card);
        add(mainPanel);

        btnLogin.addActionListener(e -> doLogin());
        btnRegister.addActionListener(e -> showRegisterDialog());
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin(); }
        });
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) txtPassword.requestFocus(); }
        });
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_DARK);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createInputField() {
        JTextField field = new JTextField();
        styleInput(field);
        return field;
    }

    private void styleInput(JTextField field) {
        field.setFont(UITheme.FONT_BODY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.LIGHT, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }

    private void styleButton(JButton btn, Color bg, int height) {
        btn.setFont(UITheme.FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(UITheme.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        btn.setAlignmentX(CENTER_ALIGNMENT);
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

        new Thread(() -> {
            User user = authController.login(username, password);
            SwingUtilities.invokeLater(() -> {
                btnLogin.setEnabled(true);
                btnLogin.setText("LOGIN");
                if (user != null) {
                    dispose();
                    new MainFrame(user);
                } else {
                    lblStatus.setText("Username atau password salah bos!");
                    txtPassword.setText("");
                }
            });
        }).start();
    }

    private void showRegisterDialog() {
        JDialog dialog = new JDialog(this, "Register Akun Baru", true);
        dialog.setSize(390, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        panel.setBackground(UITheme.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        JTextField regUsername = createInputField();
        JPasswordField regPassword = new JPasswordField(); styleInput(regPassword);
        JPasswordField regConfirm = new JPasswordField(); styleInput(regConfirm);
        JTextField regFullName = createInputField();
        JTextField regEmail = createInputField();

        addDialogRow(panel, gbc, 0, "Username *", regUsername);
        addDialogRow(panel, gbc, 1, "Password *", regPassword);
        addDialogRow(panel, gbc, 2, "Konfirmasi Password *", regConfirm);
        addDialogRow(panel, gbc, 3, "Nama Lengkap *", regFullName);
        addDialogRow(panel, gbc, 4, "Email", regEmail);

        JLabel status = new JLabel(" ");
        status.setFont(UITheme.FONT_SMALL);
        status.setForeground(UITheme.DANGER);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(status, gbc);

        JButton save = new JButton("Daftar");
        save.setBackground(UITheme.SUCCESS);
        save.setForeground(Color.WHITE);
        save.setFont(UITheme.FONT_BOLD);
        save.setBorderPainted(false);
        save.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton cancel = new JButton("Batal");
        cancel.setBackground(UITheme.TEXT_MUTED);
        cancel.setForeground(Color.WHITE);
        cancel.setBorderPainted(false);
        cancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        buttons.add(cancel);
        buttons.add(save);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panel.add(buttons, gbc);

        cancel.addActionListener(e -> dialog.dispose());
        save.addActionListener(e -> {
            String u = regUsername.getText().trim();
            String p = new String(regPassword.getPassword());
            String c = new String(regConfirm.getPassword());
            String n = regFullName.getText().trim();
            String em = regEmail.getText().trim();

            if (u.isEmpty() || p.isEmpty() || n.isEmpty()) {
                status.setText("Username, password, dan nama wajib diisi!");
                return;
            }
            if (!p.equals(c)) {
                status.setText("Konfirmasi password tidak sama!");
                return;
            }
            boolean ok = authController.register(u, p, n, em);
            if (ok) {
                JOptionPane.showMessageDialog(dialog, "Akun berhasil dibuat. Silakan login.");
                txtUsername.setText(u);
                txtPassword.setText("");
                dialog.dispose();
            } else {
                status.setText("Gagal register. Username mungkin sudah dipakai.");
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addDialogRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent input) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.35;
        JLabel lbl = makeLabel(label);
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(input, gbc);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) { }
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
