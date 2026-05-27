package view;

import controller.TaskController;
import model.User;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * UserManagementPanel - Manajemen User (Admin Only)
 * CRUD untuk pengguna sistem
 */
public class UserManagementPanel extends JPanel {
    private TaskController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtFullName, txtUsername, txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnSave;
    private User editingUser;

    public UserManagementPanel(TaskController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.LIGHT);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        refresh();
    }

    private void initComponents() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("👥 Manajemen Pengguna");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);
        JLabel admin = new JLabel("⚠️ Hanya Admin");
        admin.setFont(UITheme.FONT_SMALL);
        admin.setForeground(UITheme.WARNING);
        header.add(title, BorderLayout.WEST);
        header.add(admin, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(300);
        split.setBorder(null);

        // === FORM ===
        JPanel formPanel = new JPanel();
        formPanel.setBackground(UITheme.WHITE);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.LIGHT),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel formTitle = new JLabel("Form Pengguna");
        formTitle.setFont(UITheme.FONT_HEADER);
        formTitle.setForeground(UITheme.TEXT_DARK);
        formTitle.setAlignmentX(LEFT_ALIGNMENT);

        txtFullName = makeField();
        txtUsername = makeField();
        txtPassword = new JPasswordField();
        txtPassword.setFont(UITheme.FONT_BODY);
        txtPassword.setAlignmentX(LEFT_ALIGNMENT);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.LIGHT), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        txtEmail = makeField();
        cmbRole = new JComboBox<>(new String[]{"user", "admin"});
        cmbRole.setFont(UITheme.FONT_BODY);
        cmbRole.setAlignmentX(LEFT_ALIGNMENT);
        cmbRole.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        btnSave = new JButton("💾 Simpan");
        btnSave.setFont(UITheme.FONT_BOLD);
        btnSave.setBackground(UITheme.SUCCESS);
        btnSave.setForeground(Color.WHITE);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setAlignmentX(LEFT_ALIGNMENT);
        btnSave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnSave.addActionListener(e -> saveUser());

        JButton btnClear = new JButton("Batal");
        btnClear.setFont(UITheme.FONT_SMALL);
        btnClear.setBackground(UITheme.TEXT_MUTED);
        btnClear.setForeground(Color.WHITE);
        btnClear.setBorderPainted(false);
        btnClear.setAlignmentX(LEFT_ALIGNMENT);
        btnClear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btnClear.addActionListener(e -> clearForm());

        formPanel.add(formTitle);
        formPanel.add(Box.createVerticalStrut(15));
        addLabelField(formPanel, "Nama Lengkap *", txtFullName);
        addLabelField(formPanel, "Username *", txtUsername);
        addLabelField(formPanel, "Password *", txtPassword);
        addLabelField(formPanel, "Email", txtEmail);
        addLabelField(formPanel, "Role", cmbRole);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(btnSave);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(btnClear);
        split.setLeftComponent(formPanel);

        // === TABEL ===
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setOpaque(false);

        JPanel tblHeader = new JPanel(new BorderLayout());
        tblHeader.setOpaque(false);
        JLabel tblTitle = new JLabel("Daftar Pengguna");
        tblTitle.setFont(UITheme.FONT_HEADER);
        tblTitle.setForeground(UITheme.TEXT_DARK);

        JButton btnDelete = new JButton("🗑️ Hapus");
        btnDelete.setFont(UITheme.FONT_SMALL);
        btnDelete.setBackground(UITheme.DANGER);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setBorderPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener(e -> deleteSelected());
        tblHeader.add(tblTitle, BorderLayout.WEST);
        tblHeader.add(btnDelete, BorderLayout.EAST);
        tablePanel.add(tblHeader, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Username", "Nama Lengkap", "Email", "Role", "Dibuat"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(30);
        table.getTableHeader().setFont(UITheme.FONT_BOLD);
        table.getTableHeader().setBackground(UITheme.PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setMinWidth(0);

        // Double click edit
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) loadToForm();
            }
        });

        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        split.setRightComponent(tablePanel);
        add(split, BorderLayout.CENTER);
    }

    private void addLabelField(JPanel panel, String label, JComponent field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_DARK);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(4));
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));
    }

    private JTextField makeField() {
        JTextField f = new JTextField();
        f.setFont(UITheme.FONT_BODY);
        f.setAlignmentX(LEFT_ALIGNMENT);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.LIGHT), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return f;
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (User u : controller.getAllUsers()) {
            tableModel.addRow(new Object[]{u.getId(), u.getUsername(), u.getFullName(), u.getEmail(), u.getRole(), u.getCreatedAt()});
        }
    }

    private void clearForm() {
        editingUser = null;
        txtFullName.setText(""); txtUsername.setText("");
        txtPassword.setText(""); txtEmail.setText("");
        cmbRole.setSelectedIndex(0);
        btnSave.setText("💾 Simpan");
        txtUsername.setEditable(true);
    }

    private void loadToForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) tableModel.getValueAt(row, 0);
        for (User u : controller.getAllUsers()) {
            if (u.getId() == id) {
                editingUser = u;
                txtFullName.setText(u.getFullName());
                txtUsername.setText(u.getUsername());
                txtUsername.setEditable(false);
                txtPassword.setText("");
                txtEmail.setText(u.getEmail() != null ? u.getEmail() : "");
                cmbRole.setSelectedItem(u.getRole());
                btnSave.setText("✏️ Update");
                break;
            }
        }
    }

    private void saveUser() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (fullName.isEmpty() || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan username wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (editingUser == null) {
            // CREATE
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password wajib diisi untuk user baru!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            User user = new User();
            user.setFullName(fullName);
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(txtEmail.getText().trim());
            user.setRole((String) cmbRole.getSelectedItem());
            if (controller.createUser(user)) {
                JOptionPane.showMessageDialog(this, "User berhasil ditambahkan!");
                clearForm(); refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal! Username mungkin sudah ada.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // UPDATE
            editingUser.setFullName(fullName);
            editingUser.setEmail(txtEmail.getText().trim());
            editingUser.setRole((String) cmbRole.getSelectedItem());
            if (controller.updateUser(editingUser)) {
                if (!password.isEmpty()) controller.changePassword(editingUser.getId(), password);
                JOptionPane.showMessageDialog(this, "User berhasil diupdate!");
                clearForm(); refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal update user!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih user terlebih dahulu!"); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus user ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.deleteUser(id)) { refresh(); clearForm(); }
            else JOptionPane.showMessageDialog(this, "Gagal hapus! Tidak bisa hapus diri sendiri.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
