package view;

import java.awt.*;

/**
 * UITheme - Konfigurasi tampilan UI
 * Enkapsulasi konstanta warna dan font
 */
public class UITheme {
    // Warna utama
    public static final Color PRIMARY       = new Color(52, 73, 94);    // Dark Blue-Gray
    public static final Color SECONDARY     = new Color(41, 128, 185);  // Blue
    public static final Color SUCCESS       = new Color(39, 174, 96);   // Green
    public static final Color WARNING       = new Color(243, 156, 18);  // Orange
    public static final Color DANGER        = new Color(192, 57, 43);   // Red
    public static final Color LIGHT         = new Color(236, 240, 241); // Light Gray
    public static final Color WHITE         = Color.WHITE;
    public static final Color TEXT_DARK     = new Color(44, 62, 80);
    public static final Color TEXT_MUTED    = new Color(127, 140, 141);
    public static final Color SIDEBAR_BG    = new Color(34, 49, 63);
    public static final Color CARD_BG       = new Color(250, 250, 252);
    public static final Color OVERDUE_BG    = new Color(255, 235, 238);
    public static final Color URGENT_BG     = new Color(255, 248, 225);

    // Prioritas warna
    public static final Color PRIORITY_LOW      = new Color(46, 204, 113);
    public static final Color PRIORITY_MEDIUM   = new Color(52, 152, 219);
    public static final Color PRIORITY_HIGH     = new Color(230, 126, 34);
    public static final Color PRIORITY_CRITICAL = new Color(192, 57, 43);

    // Font
    public static final Font FONT_TITLE     = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER    = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY      = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL     = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BOLD      = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SIDEBAR   = new Font("Segoe UI", Font.BOLD, 13);

    // Helper: warna berdasar prioritas
    public static Color getPriorityColor(String priority) {
        if (priority == null) return PRIORITY_MEDIUM;
        switch (priority) {
            case "Low":      return PRIORITY_LOW;
            case "High":     return PRIORITY_HIGH;
            case "Critical": return PRIORITY_CRITICAL;
            default:         return PRIORITY_MEDIUM;
        }
    }

    // Helper: warna berdasar status
    public static Color getStatusColor(String status) {
        if (status == null) return TEXT_MUTED;
        switch (status) {
            case "Completed":  return SUCCESS;
            case "In Progress":return SECONDARY;
            case "Overdue":    return DANGER;
            default:           return WARNING;
        }
    }
}
