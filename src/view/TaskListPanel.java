package view;

import controller.TaskController;
import model.Task;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;


public class TaskListPanel extends JPanel {
    private TaskController controller;
    private MainFrame mainFrame;

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbFilter;
    private JLabel lblCount;

    private static final String[] COLUMNS = {
        "ID", "Judul Tugas", "Kategori", "Prioritas", "Status", "Deadline", "Sisa Waktu"
    };

    public TaskListPanel(TaskController controller, MainFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.LIGHT);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        refresh();
    }

    private void initComponents() {
        // === HEADER ===
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("📋 Daftar Tugas");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);
        lblCount = new JLabel("  (0 tugas)");
        lblCount.setFont(UITheme.FONT_BODY);
        lblCount.setForeground(UITheme.TEXT_MUTED);
        titlePanel.add(title);
        titlePanel.add(lblCount);
        header.add(titlePanel, BorderLayout.NORTH);

        // Toolbar: filter status (fitur cari dihapus)
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setOpaque(false);

        cmbFilter = new JComboBox<>(new String[]{"Semua Status", "Pending", "In Progress", "Completed", "Overdue"});
        cmbFilter.setFont(UITheme.FONT_BODY);
        cmbFilter.addActionListener(e -> doFilter());

        JButton btnReset = new JButton("↺ Reset");
        styleButton(btnReset, UITheme.TEXT_MUTED);
        btnReset.addActionListener(e -> { cmbFilter.setSelectedIndex(0); refresh(); });

        toolbar.add(new JLabel("Filter:"));
        toolbar.add(cmbFilter);
        toolbar.add(btnReset);
        header.add(toolbar, BorderLayout.CENTER);

        // Tombol aksi
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        actionBar.setOpaque(false);

        JButton btnAdd = new JButton("➕ Tambah Tugas");
        styleButton(btnAdd, UITheme.SUCCESS);
        btnAdd.addActionListener(e -> { mainFrame.showTaskForm(null); });

        JButton btnEdit = new JButton("✏️ Edit");
        styleButton(btnEdit, UITheme.WARNING);
        btnEdit.addActionListener(e -> doEdit());

        JButton btnDelete = new JButton("🗑️ Hapus");
        styleButton(btnDelete, UITheme.DANGER);
        btnDelete.addActionListener(e -> doDelete());

        JButton btnMarkDone = new JButton("✅ Tandai Selesai");
        styleButton(btnMarkDone, UITheme.SUCCESS);
        btnMarkDone.addActionListener(e -> doMarkDone());

        JButton btnRefresh = new JButton("🔄");
        styleButton(btnRefresh, UITheme.TEXT_MUTED);
        btnRefresh.addActionListener(e -> refresh());

        actionBar.add(btnAdd);
        actionBar.add(btnEdit);
        actionBar.add(btnMarkDone);
        actionBar.add(btnDelete);
        actionBar.add(btnRefresh);
        header.add(actionBar, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // === TABEL ===
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(32);
        table.setSelectionBackground(new Color(52, 152, 219, 80));
        table.setGridColor(UITheme.LIGHT);
        table.setShowVerticalLines(false);
        UITheme.styleTableHeader(table);

        // Sembunyikan kolom ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Lebar kolom
        int[] widths = {0, 260, 120, 90, 100, 150, 130};
        for (int i = 0; i < widths.length; i++) {
            if (widths[i] > 0) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Custom renderer untuk warna baris
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String status = (String) t.getValueAt(row, 4);
                String priority = (String) t.getValueAt(row, 3);

                if (!sel) {
                    if ("Overdue".equals(status)) c.setBackground(UITheme.OVERDUE_BG);
                    else if ("Completed".equals(status)) c.setBackground(new Color(240, 255, 240));
                    else if ("In Progress".equals(status)) c.setBackground(new Color(240, 248, 255));
                    else c.setBackground(UITheme.WHITE);
                }

                // Warna kolom prioritas
                if (col == 3 && !sel) {
                    ((JLabel) c).setForeground(UITheme.getPriorityColor(priority));
                    ((JLabel) c).setFont(UITheme.FONT_BOLD);
                } else if (col == 4 && !sel) {
                    ((JLabel) c).setForeground(UITheme.getStatusColor(status));
                    ((JLabel) c).setFont(UITheme.FONT_BOLD);
                } else {
                    ((JLabel) c).setForeground(UITheme.TEXT_DARK);
                    ((JLabel) c).setFont(UITheme.FONT_BODY);
                }

                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        // Double-click untuk edit
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) doEdit();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.LIGHT));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refresh() {
        List<Task> tasks = controller.getAllTasks();
        loadData(tasks);
    }

    private void doFilter() {
        String selected = (String) cmbFilter.getSelectedItem();
        if ("Semua Status".equals(selected)) { refresh(); return; }
        loadData(controller.getTasksByStatus(selected));
    }

    private void loadData(List<Task> tasks) {
        tableModel.setRowCount(0);
        for (Task t : tasks) {
            tableModel.addRow(new Object[]{
                t.getId(),
                t.getTitle(),
                t.getCategoryName() != null ? t.getCategoryName() : "-",
                t.getPriority(),
                t.getStatus(),
                t.getFormattedDeadline(),
                t.getDeadlineStatus()
            });
        }
        lblCount.setText("  (" + tasks.size() + " tugas)");
    }

    private int getSelectedTaskId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih tugas terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        return (int) tableModel.getValueAt(row, 0);
    }

    private void doEdit() {
        int id = getSelectedTaskId();
        if (id < 0) return;
        Task task = controller.getTaskById(id);
        if (task != null) mainFrame.showTaskForm(task);
    }

    private void doDelete() {
        int id = getSelectedTaskId();
        if (id < 0) return;
        int confirm = JOptionPane.showConfirmDialog(this,
            "Yakin ingin menghapus tugas ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.deleteTask(id)) {
                JOptionPane.showMessageDialog(this, "Tugas berhasil dihapus!");
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus tugas! Anda tidak punya izin.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void doMarkDone() {
        int id = getSelectedTaskId();
        if (id < 0) return;
        if (controller.updateTaskStatus(id, "Completed")) {
            JOptionPane.showMessageDialog(this, "✅ Tugas ditandai selesai!");
            refresh();
        }
    }

    private void styleButton(JButton btn, Color color) {
        btn.setFont(UITheme.FONT_SMALL);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
    }
}
