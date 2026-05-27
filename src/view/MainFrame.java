package view;

import controller.TaskController;
import model.*;
import thread.DeadlineMonitorThread;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * MainFrame - Jendela Utama Aplikasi
 * Implementasi MVC View + DeadlineMonitorThread listener
 * POLYMORPHISM: implements DeadlineListener interface
 */
public class MainFrame extends JFrame implements DeadlineMonitorThread.DeadlineListener {
    private TaskController controller;
    private User currentUser;
    private DeadlineMonitorThread monitorThread;

    // Panels
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Sub-panels (Views)
    private DashboardPanel dashboardPanel;
    private TaskListPanel taskListPanel;
    private TaskFormPanel taskFormPanel;
    private CategoryPanel categoryPanel;
    private UserManagementPanel userManagementPanel;

    // Sidebar buttons
    private JButton[] sidebarButtons;
    private JLabel lblUserInfo;
    private JLabel lblNotifBadge;
    private int notifCount = 0;

    public MainFrame(User user) {
        this.currentUser = user;
        this.controller = new TaskController(user);
        initComponents();
        startMonitorThread();
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Task Management System - " + currentUser.getFullName());
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));

        setLayout(new BorderLayout());

        // Sidebar
        sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        // Content area dengan CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UITheme.LIGHT);

        dashboardPanel = new DashboardPanel(controller);
        taskListPanel = new TaskListPanel(controller, this);
        taskFormPanel = new TaskFormPanel(controller, this);
        categoryPanel = new CategoryPanel(controller);
        userManagementPanel = new UserManagementPanel(controller);

        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(taskListPanel, "TASKS");
        contentPanel.add(taskFormPanel, "TASK_FORM");
        contentPanel.add(categoryPanel, "CATEGORIES");
        contentPanel.add(userManagementPanel, "USERS");

        add(contentPanel, BorderLayout.CENTER);

        // Status bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);

        // Tampilkan dashboard default
        showPanel("DASHBOARD");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Header sidebar
        JPanel header = new JPanel();
        header.setBackground(UITheme.PRIMARY);
        header.setMaximumSize(new Dimension(200, 70));
        header.setPreferredSize(new Dimension(200, 70));
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel appName = new JLabel("📋 TaskManager");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        appName.setForeground(Color.WHITE);
        header.add(appName, BorderLayout.CENTER);
        sidebar.add(header);

        // User info
        JPanel userInfo = new JPanel();
        userInfo.setBackground(new Color(44, 62, 80));
        userInfo.setMaximumSize(new Dimension(200, 60));
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        lblUserInfo = new JLabel( currentUser.getFullName());
        lblUserInfo.setFont(UITheme.FONT_SMALL);
        lblUserInfo.setForeground(UITheme.LIGHT);

        JLabel lblRole = new JLabel(currentUser.getRole().toUpperCase());
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblRole.setForeground(currentUser.isAdmin() ? UITheme.WARNING : new Color(52, 152, 219));

        userInfo.add(lblUserInfo);
        userInfo.add(lblRole);
        sidebar.add(userInfo);

        sidebar.add(Box.createVerticalStrut(15));

        // Nav items
        String[] labels = {"  Dashboard", " Daftar Tugas", "  Tambah Tugas",
                           "️  Kategori", "  Manajemen User"};
        String[] panels = {"DASHBOARD", "TASKS", "TASK_FORM", "CATEGORIES", "USERS"};

        sidebarButtons = new JButton[labels.length];
        for (int i = 0; i < labels.length; i++) {
            JButton btn = createSidebarButton(labels[i]);
            final String panel = panels[i];
            final int idx = i;
            btn.addActionListener(e -> {
                showPanel(panel);
                updateSidebarSelection(idx);
                if ("TASK_FORM".equals(panel)) taskFormPanel.resetForm();
                if ("DASHBOARD".equals(panel)) dashboardPanel.refresh();
                if ("TASKS".equals(panel)) taskListPanel.refresh();
            });

            // Sembunyikan User Management jika bukan admin
            if ("USERS".equals(panels[i]) && !currentUser.isAdmin()) {
                btn.setVisible(false);
            }

            sidebarButtons[i] = btn;
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
        }

        sidebar.add(Box.createVerticalGlue());

        // Notifikasi badge
        JPanel notifPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        notifPanel.setBackground(UITheme.SIDEBAR_BG);
        notifPanel.setMaximumSize(new Dimension(200, 40));
        lblNotifBadge = new JLabel(" Tidak ada notifikasi");
        lblNotifBadge.setFont(UITheme.FONT_SMALL);
        lblNotifBadge.setForeground(UITheme.TEXT_MUTED);
        notifPanel.add(lblNotifBadge);
        sidebar.add(notifPanel);

        // Logout button
        JButton btnLogout = createSidebarButton("  Logout");
        btnLogout.setBackground(new Color(192, 57, 43, 200));
        btnLogout.addActionListener(e -> logout());
        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalStrut(10));

        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_SIDEBAR);
        btn.setForeground(UITheme.LIGHT);
        btn.setBackground(UITheme.SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 10));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn.getBackground() != UITheme.SECONDARY) {
                    btn.setBackground(new Color(52, 73, 94));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn.getBackground() != UITheme.SECONDARY) {
                    btn.setBackground(UITheme.SIDEBAR_BG);
                }
            }
        });
        return btn;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(UITheme.PRIMARY);
        statusBar.setBorder(BorderFactory.createEmptyBorder(4, 15, 4, 15));
        statusBar.setPreferredSize(new Dimension(0, 28));

        JLabel lblLeft = new JLabel("Task Management System v1.0 | " + currentUser.getFullName());
        lblLeft.setFont(UITheme.FONT_SMALL);
        lblLeft.setForeground(UITheme.LIGHT);

        JLabel lblRight = new JLabel(" Monitoring aktif setiap 30 detik");
        lblRight.setFont(UITheme.FONT_SMALL);
        lblRight.setForeground(UITheme.TEXT_MUTED);

        statusBar.add(lblLeft, BorderLayout.WEST);
        statusBar.add(lblRight, BorderLayout.EAST);
        return statusBar;
    }

    // ===== Navigation =====

    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }

    private void updateSidebarSelection(int selectedIdx) {
        for (int i = 0; i < sidebarButtons.length; i++) {
            if (i == selectedIdx) {
                sidebarButtons[i].setBackground(UITheme.SECONDARY);
            } else {
                sidebarButtons[i].setBackground(UITheme.SIDEBAR_BG);
            }
        }
    }

    public void showTaskForm(Task taskToEdit) {
        taskFormPanel.loadTask(taskToEdit);
        showPanel("TASK_FORM");
        updateSidebarSelection(2);
    }

    // ===== MULTITHREADING: DeadlineListener callbacks =====

    @Override
    public void onOverdueDetected(List<Task> overdueTasks) {
        notifCount = overdueTasks.size();
        lblNotifBadge.setText( notifCount + " task terlambat!");
        lblNotifBadge.setForeground(UITheme.DANGER);
    }

    @Override
    public void onUpcomingDeadline(List<Task> upcomingTasks) {
        // Notifikasi popup untuk deadline dalam 24 jam
        if (!upcomingTasks.isEmpty()) {
            StringBuilder msg = new StringBuilder("️ Deadline dalam 24 jam:\n\n");
            for (Task t : upcomingTasks) {
                msg.append("• ").append(t.getTitle()).append(" - ").append(t.getFormattedDeadline()).append("\n");
            }
            // Hanya tampilkan jika window fokus
            if (isFocused()) {
                JOptionPane.showMessageDialog(this, msg.toString(),
                    "Peringatan Deadline", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    @Override
    public void onRefreshNeeded() {
        dashboardPanel.refresh();
        taskListPanel.refresh();
    }

    private void startMonitorThread() {
        monitorThread = new DeadlineMonitorThread(this);
        monitorThread.start();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Yakin ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (monitorThread != null) monitorThread.stopMonitoring();
            dispose();
            new LoginFrame();
        }
    }

    @Override
    public void dispose() {
        if (monitorThread != null) monitorThread.stopMonitoring();
        super.dispose();
    }
}
