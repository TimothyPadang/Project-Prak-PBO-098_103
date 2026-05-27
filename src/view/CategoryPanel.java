package view;

import controller.TaskController;
import model.Category;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * CategoryPanel - CRUD untuk Kategori
 */
public class CategoryPanel extends JPanel {
    private TaskController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtName, txtDesc, txtColor;
    private JButton btnSave;
    private Category editingCat;

    public CategoryPanel(TaskController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.LIGHT);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        refresh();
    }

    private void initComponents() {
        JLabel title = new JLabel("🏷️ Manajemen Kategori");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        // Split: left = form, right = table
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(300);
        split.setBorder(null);

        // === FORM PANEL ===
        JPanel formPanel = new JPanel();
        formPanel.setBackground(UITheme.WHITE);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.LIGHT),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel formTitle = new JLabel("Form Kategori");
        formTitle.setFont(UITheme.FONT_HEADER);
        formTitle.setForeground(UITheme.TEXT_DARK);
        formTitle.setAlignmentX(LEFT_ALIGNMENT);

        txtName = createField("Nama Kategori *");
        txtDesc = createField("Deskripsi");
        txtColor = createField("Warna (hex, contoh: #3498db)");
        txtColor.setText("#3498db");

        btnSave = new JButton("💾 Simpan Kategori");
        btnSave.setFont(UITheme.FONT_BOLD);
        btnSave.setBackground(UITheme.SUCCESS);
        btnSave.setForeground(Color.WHITE);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setAlignmentX(LEFT_ALIGNMENT);
        btnSave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnSave.addActionListener(e -> saveCategory());

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
        formPanel.add(makeLabel("Nama Kategori *"));
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(txtName);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(makeLabel("Deskripsi"));
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(txtDesc);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(makeLabel("Warna (hex)"));
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(txtColor);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(btnSave);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(btnClear);

        split.setLeftComponent(formPanel);

        // === TABEL ===
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setOpaque(false);

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        JLabel tblTitle = new JLabel("Daftar Kategori");
        tblTitle.setFont(UITheme.FONT_HEADER);
        tblTitle.setForeground(UITheme.TEXT_DARK);

        JButton btnDelete = new JButton("🗑️ Hapus");
        btnDelete.setFont(UITheme.FONT_SMALL);
        btnDelete.setBackground(UITheme.DANGER);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setBorderPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener(e -> deleteSelected());
        tableHeader.add(tblTitle, BorderLayout.WEST);
        tableHeader.add(btnDelete, BorderLayout.EAST);
        tablePanel.add(tableHeader, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Deskripsi", "Warna"}, 0) {
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

        // Double click to edit
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) loadToForm();
            }
        });

        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        split.setRightComponent(tablePanel);
        add(split, BorderLayout.CENTER);
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_BOLD);
        l.setForeground(UITheme.TEXT_DARK);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JTextField createField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(UITheme.FONT_BODY);
        f.setAlignmentX(LEFT_ALIGNMENT);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.LIGHT),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return f;
    }

    private void refresh() {
        tableModel.setRowCount(0);
        for (Category c : controller.getAllCategories()) {
            tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getDescription(), c.getColor()});
        }
    }

    private void clearForm() {
        editingCat = null;
        txtName.setText("");
        txtDesc.setText("");
        txtColor.setText("#3498db");
        btnSave.setText("💾 Simpan Kategori");
    }

    private void loadToForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) tableModel.getValueAt(row, 0);
        List<Category> cats = controller.getAllCategories();
        for (Category c : cats) {
            if (c.getId() == id) {
                editingCat = c;
                txtName.setText(c.getName());
                txtDesc.setText(c.getDescription() != null ? c.getDescription() : "");
                txtColor.setText(c.getColor() != null ? c.getColor() : "#3498db");
                btnSave.setText("✏️ Update Kategori");
                break;
            }
        }
    }

    private void saveCategory() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama kategori tidak boleh kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Category cat = editingCat != null ? editingCat : new Category();
        cat.setName(name);
        cat.setDescription(txtDesc.getText().trim());
        cat.setColor(txtColor.getText().trim());

        boolean success = editingCat != null ? controller.updateCategory(cat) : controller.createCategory(cat);
        if (success) {
            JOptionPane.showMessageDialog(this, "Kategori berhasil disimpan!");
            clearForm();
            refresh();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan kategori!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih kategori terlebih dahulu!"); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus kategori ini?",
            "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.deleteCategory(id)) { refresh(); clearForm(); }
            else JOptionPane.showMessageDialog(this, "Gagal menghapus!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
