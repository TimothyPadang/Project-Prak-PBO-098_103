package view;

import controller.TaskController;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TaskFormPanel - Form Tambah/Edit Tugas
 * Implementasi CREATE dan UPDATE dari CRUD
 */
public class TaskFormPanel extends JPanel {
    private TaskController controller;
    private MainFrame mainFrame;
    private Task editingTask; // null = mode tambah

    // Form fields
    private JTextField txtTitle;
    private JTextArea txtDescription;
    private JComboBox<Category> cmbCategory;
    private JComboBox<String> cmbPriority;
    private JComboBox<String> cmbStatus;
    private JSpinner spnDeadline;
    private JLabel lblFormTitle;
    private JButton btnSave;
    private JPanel notesPanel;
    private JTextArea txtNewNote;

    public TaskFormPanel(TaskController controller, MainFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.LIGHT);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    private void initComponents() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        lblFormTitle = new JLabel("➕ Tambah Tugas Baru");
        lblFormTitle.setFont(UITheme.FONT_TITLE);
        lblFormTitle.setForeground(UITheme.TEXT_DARK);
        header.add(lblFormTitle, BorderLayout.WEST);

        JButton btnBack = new JButton("← Kembali ke Daftar");
        btnBack.setFont(UITheme.FONT_SMALL);
        btnBack.setBackground(UITheme.TEXT_MUTED);
        btnBack.setForeground(Color.WHITE);
        btnBack.setBorderPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> mainFrame.showPanel("TASKS"));
        header.add(btnBack, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Scroll container
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(UITheme.WHITE);
        formContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.LIGHT),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 5, 6, 5);

        // --- Row: Judul Tugas ---
        addFormLabel(formContainer, "Judul Tugas *", gbc, 0, 0);
        txtTitle = new JTextField();
        txtTitle.setFont(UITheme.FONT_BODY);
        addFormField(formContainer, txtTitle, gbc, 0, 1, 3);

        // --- Row: Deskripsi ---
        addFormLabel(formContainer, "Deskripsi", gbc, 1, 0);
        txtDescription = new JTextArea(3, 0);
        txtDescription.setFont(UITheme.FONT_BODY);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(txtDescription);
        descScroll.setBorder(BorderFactory.createLineBorder(UITheme.LIGHT));
        gbc.gridx = 1; gbc.gridy = 1+1; gbc.gridwidth = 3;
        formContainer.add(descScroll, gbc);

        // --- Row: Kategori ---
        addFormLabel(formContainer, "Kategori", gbc, 3, 0);
        cmbCategory = new JComboBox<>();
        cmbCategory.setFont(UITheme.FONT_BODY);
        addFormField(formContainer, cmbCategory, gbc, 3, 1, 3);

        // --- Row: Prioritas & Status ---
        addFormLabel(formContainer, "Prioritas *", gbc, 4, 0);
        cmbPriority = new JComboBox<>(Task.PRIORITIES);
        cmbPriority.setFont(UITheme.FONT_BODY);
        addFormField(formContainer, cmbPriority, gbc, 4, 1, 1);

        addFormLabel(formContainer, "Status", gbc, 4, 2);
        cmbStatus = new JComboBox<>(Task.STATUSES);
        cmbStatus.setFont(UITheme.FONT_BODY);
        addFormField(formContainer, cmbStatus, gbc, 4, 3, 1);

        // --- Row: Deadline ---
        addFormLabel(formContainer, "Deadline *", gbc, 5, 0);
        SpinnerDateModel dateModel = new SpinnerDateModel();
        spnDeadline = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnDeadline, "dd/MM/yyyy HH:mm");
        spnDeadline.setEditor(dateEditor);
        spnDeadline.setFont(UITheme.FONT_BODY);
        addFormField(formContainer, spnDeadline, gbc, 5, 1, 3);

        // --- Row: Notes (hanya saat edit) ---
        notesPanel = new JPanel(new BorderLayout(5, 5));
        notesPanel.setOpaque(false);
        notesPanel.setVisible(false);
        JLabel notesTitle = new JLabel("💬 Tambah Catatan");
        notesTitle.setFont(UITheme.FONT_BOLD);
        txtNewNote = new JTextArea(2, 0);
        txtNewNote.setFont(UITheme.FONT_BODY);
        txtNewNote.setLineWrap(true);
        JButton btnAddNote = new JButton("Tambah Catatan");
        btnAddNote.setFont(UITheme.FONT_SMALL);
        btnAddNote.setBackground(UITheme.SECONDARY);
        btnAddNote.setForeground(Color.WHITE);
        btnAddNote.setBorderPainted(false);
        btnAddNote.addActionListener(e -> addNote());
        notesPanel.add(notesTitle, BorderLayout.NORTH);
        notesPanel.add(new JScrollPane(txtNewNote), BorderLayout.CENTER);
        notesPanel.add(btnAddNote, BorderLayout.EAST);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 4;
        formContainer.add(notesPanel, gbc);

        // --- Tombol Simpan ---
        btnSave = new JButton("💾 Simpan Tugas");
        btnSave.setFont(UITheme.FONT_BOLD);
        btnSave.setBackground(UITheme.SUCCESS);
        btnSave.setForeground(Color.WHITE);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setPreferredSize(new Dimension(160, 40));
        btnSave.addActionListener(e -> saveTask());

        JButton btnClear = new JButton("🗑️ Bersihkan");
        btnClear.setFont(UITheme.FONT_SMALL);
        btnClear.setBackground(UITheme.TEXT_MUTED);
        btnClear.setForeground(Color.WHITE);
        btnClear.setBorderPainted(false);
        btnClear.addActionListener(e -> resetForm());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 4;
        gbc.insets = new Insets(15, 5, 5, 5);
        btnPanel.add(btnClear);
        btnPanel.add(btnSave);
        formContainer.add(btnPanel, gbc);

        JScrollPane mainScroll = new JScrollPane(formContainer);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(10);
        add(mainScroll, BorderLayout.CENTER);

        loadComboData();
    }

    private void addFormLabel(JPanel panel, String text, GridBagConstraints gbc, int row, int col) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_DARK);
        gbc.gridx = col; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(lbl, gbc);
    }

    private void addFormField(JPanel panel, JComponent comp, GridBagConstraints gbc, int row, int col, int width) {
        if (comp instanceof JTextField || comp instanceof JComboBox || comp instanceof JSpinner) {
            comp.setPreferredSize(new Dimension(200, 32));
        }
        if (comp instanceof JTextField || comp instanceof JComboBox || comp instanceof JSpinner) {
            comp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.LIGHT),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
        }
        gbc.gridx = col; gbc.gridy = row; gbc.gridwidth = width;
        panel.add(comp, gbc);
    }

    private void loadComboData() {
        // Load categories
        cmbCategory.removeAllItems();
        for (Category c : controller.getAllCategories()) {
            cmbCategory.addItem(c);
        }

    }

    public void resetForm() {
        editingTask = null;
        lblFormTitle.setText("➕ Tambah Tugas Baru");
        btnSave.setText("💾 Simpan Tugas");
        txtTitle.setText("");
        txtDescription.setText("");
        cmbPriority.setSelectedIndex(1);
        cmbStatus.setSelectedIndex(0);
        spnDeadline.setValue(new java.util.Date());
        notesPanel.setVisible(false);
        loadComboData();
    }

    public void loadTask(Task task) {
        if (task == null) { resetForm(); return; }
        editingTask = task;
        lblFormTitle.setText("✏️ Edit Tugas: " + task.getTitle());
        btnSave.setText("💾 Update Tugas");
        txtTitle.setText(task.getTitle());
        txtDescription.setText(task.getDescription());
        cmbPriority.setSelectedItem(task.getPriority());
        cmbStatus.setSelectedItem(task.getStatus());

        // Set deadline
        if (task.getDeadline() != null) {
            java.util.Date date = java.util.Date.from(
                task.getDeadline().atZone(java.time.ZoneId.systemDefault()).toInstant());
            spnDeadline.setValue(date);
        }

        // Set category combo
        loadComboData();
        for (int i = 0; i < cmbCategory.getItemCount(); i++) {
            if (cmbCategory.getItemAt(i).getId() == task.getCategoryId()) {
                cmbCategory.setSelectedIndex(i);
                break;
            }
        }


        notesPanel.setVisible(true);
    }

    private void saveTask() {
        String title = txtTitle.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Judul tugas tidak boleh kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtTitle.requestFocus();
            return;
        }

        // Build task object
        Task task = editingTask != null ? editingTask : new Task();
        task.setTitle(title);
        task.setDescription(txtDescription.getText().trim());

        Category selCat = (Category) cmbCategory.getSelectedItem();
        if (selCat != null) task.setCategoryId(selCat.getId());

        // assigned_to diisi otomatis di TaskController berdasarkan user yang login
        task.setPriority((String) cmbPriority.getSelectedItem());
        task.setStatus((String) cmbStatus.getSelectedItem());

        // Deadline dari spinner
        java.util.Date date = (java.util.Date) spnDeadline.getValue();
        LocalDateTime deadline = date.toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDateTime();
        task.setDeadline(deadline);

        boolean success;
        if (editingTask != null) {
            success = controller.updateTask(task);
        } else {
            success = controller.createTask(task);
        }

        if (success) {
            JOptionPane.showMessageDialog(this,
                editingTask != null ? "✅ Tugas berhasil diupdate!" : "✅ Tugas berhasil ditambahkan!",
                "Sukses", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            mainFrame.showPanel("TASKS");
        } else {
            JOptionPane.showMessageDialog(this, "❌ Gagal menyimpan tugas!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addNote() {
        if (editingTask == null) return;
        String noteText = txtNewNote.getText().trim();
        if (noteText.isEmpty()) return;
        if (controller.addNote(editingTask.getId(), noteText)) {
            txtNewNote.setText("");
            JOptionPane.showMessageDialog(this, "Catatan berhasil ditambahkan!");
        }
    }
}
