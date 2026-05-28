package view;

import controller.TaskController;
import model.Task;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * DashboardPanel - Panel Dashboard statistik tugas
 * VIEW dalam MVC
 */
public class DashboardPanel extends JPanel {
    private TaskController controller;
    private JLabel lblPending, lblInProgress, lblCompleted, lblOverdue;
    private JPanel upcomingPanel;

    public DashboardPanel(TaskController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.LIGHT);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        refresh();
    }

    private void initComponents() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Dashboard");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);
        JLabel subtitle = new JLabel("Selamat datang, " + controller.getCurrentUser().getFullName() + "!");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);
        header.add(titlePanel, BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(UITheme.FONT_SMALL);
        btnRefresh.setBackground(UITheme.SECONDARY);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> refresh());
        header.add(btnRefresh, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Kartu statistik
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);

        lblPending    = new JLabel("0");
        lblInProgress = new JLabel("0");
        lblCompleted  = new JLabel("0");
        lblOverdue    = new JLabel("0");

        statsPanel.add(createStatCard("Blm mulai",     lblPending,    UITheme.WARNING,   "📌"));
        statsPanel.add(createStatCard("proses", lblInProgress, UITheme.SECONDARY, "⚡"));
        statsPanel.add(createStatCard("Selesai",     lblCompleted,  UITheme.SUCCESS,   "✅"));
        statsPanel.add(createStatCard("Terlambat",   lblOverdue,    UITheme.DANGER,    "🔴"));

        add(statsPanel, BorderLayout.CENTER);

        // Panel upcoming deadlines
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);

        JLabel upcomingTitle = new JLabel("Deadline Dalam 3 Hari");
        upcomingTitle.setFont(UITheme.FONT_HEADER);
        upcomingTitle.setForeground(UITheme.TEXT_DARK);
        bottomPanel.add(upcomingTitle, BorderLayout.NORTH);

        upcomingPanel = new JPanel();
        upcomingPanel.setLayout(new BoxLayout(upcomingPanel, BoxLayout.Y_AXIS));
        upcomingPanel.setBackground(UITheme.WHITE);

        JScrollPane scrollPane = new JScrollPane(upcomingPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.LIGHT));
        scrollPane.setPreferredSize(new Dimension(0, 220));
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createStatCard(String label, JLabel countLabel, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        card.setOpaque(false);
        card.setBackground(UITheme.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Left accent bar
        JPanel accent = new JPanel();
        accent.setBackground(color);
        accent.setPreferredSize(new Dimension(5, 0));
        card.add(accent, BorderLayout.WEST);

        JPanel content = new JPanel(new GridLayout(3, 1, 0, 5));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        countLabel.setForeground(color);

        JLabel lblText = new JLabel(label);
        lblText.setFont(UITheme.FONT_BODY);
        lblText.setForeground(UITheme.TEXT_MUTED);

        content.add(lblIcon);
        content.add(countLabel);
        content.add(lblText);
        card.add(content, BorderLayout.CENTER);

        // White card wrapper
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(card);

        // Rounded border effect
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(UITheme.WHITE);
        card.setOpaque(true);

        return card;
    }

    public void refresh() {
        // Update statistik
        lblPending.setText(String.valueOf(controller.countTasksByStatus("Blm mulai")));
        lblInProgress.setText(String.valueOf(controller.countTasksByStatus("proses")));
        lblCompleted.setText(String.valueOf(controller.countTasksByStatus("Selesai")));
        lblOverdue.setText(String.valueOf(controller.countTasksByStatus("Overdue")));

        // Update upcoming deadlines
        upcomingPanel.removeAll();
        List<Task> upcoming = controller.getUpcomingDeadlines(3);

        if (upcoming.isEmpty()) {
            JLabel empty = new JLabel("  Tidak ada deadline dalam 3 hari ke depan");
            empty.setFont(UITheme.FONT_BODY);
            empty.setForeground(UITheme.TEXT_MUTED);
            empty.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            upcomingPanel.add(empty);
        } else {
            for (Task t : upcoming) {
                upcomingPanel.add(createDeadlineRow(t));
                upcomingPanel.add(new JSeparator());
            }
        }
        upcomingPanel.revalidate();
        upcomingPanel.repaint();
    }

    private JPanel createDeadlineRow(Task task) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(task.isOverdue() ? UITheme.OVERDUE_BG :
                          task.getDaysUntilDeadline() <= 1 ? UITheme.URGENT_BG : UITheme.WHITE);
        row.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);

        JLabel titleLbl = new JLabel(task.getTitle());
        titleLbl.setFont(UITheme.FONT_BOLD);
        titleLbl.setForeground(UITheme.TEXT_DARK);

        JLabel catLbl = new JLabel((task.getCategoryName() != null ? task.getCategoryName() : "Tanpa Kategori"));
        catLbl.setFont(UITheme.FONT_SMALL);
        catLbl.setForeground(UITheme.TEXT_MUTED);

        left.add(titleLbl);
        left.add(catLbl);

        JPanel right = new JPanel(new GridLayout(2, 1));
        right.setOpaque(false);

        JLabel deadlineLbl = new JLabel(task.getFormattedDeadline(), SwingConstants.RIGHT);
        deadlineLbl.setFont(UITheme.FONT_SMALL);
        deadlineLbl.setForeground(UITheme.TEXT_DARK);

        JLabel statusLbl = new JLabel(task.getDeadlineStatus(), SwingConstants.RIGHT);
        statusLbl.setFont(UITheme.FONT_SMALL);
        statusLbl.setForeground(task.isOverdue() ? UITheme.DANGER : UITheme.WARNING);

        right.add(deadlineLbl);
        right.add(statusLbl);

        // Priority badge
        JLabel priorityBadge = new JLabel(" " + task.getPriority() + " ");
        priorityBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        priorityBadge.setForeground(UITheme.WHITE);
        priorityBadge.setBackground(UITheme.getPriorityColor(task.getPriority()));
        priorityBadge.setOpaque(true);
        priorityBadge.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        JPanel leftWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftWrap.setOpaque(false);
        leftWrap.add(priorityBadge);
        leftWrap.add(Box.createHorizontalStrut(8));
        leftWrap.add(left);

        row.add(leftWrap, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);
        return row;
    }
}
