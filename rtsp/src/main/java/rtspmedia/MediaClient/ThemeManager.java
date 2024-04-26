package rtspmedia.MediaClient;

import java.awt.Color;
import javax.swing.*;

public class ThemeManager {
    private Color background;
    private Color foreground;

    public ThemeManager(boolean isDark) {
        if (isDark) {
            background = Color.BLACK;
            foreground = Color.WHITE;
        } else {
            background = Color.WHITE;
            foreground = Color.BLACK;
        }
    }

    public void applyTheme(JFrame frame, JPanel... panels) {
        frame.getContentPane().setBackground(background);
        for (JPanel panel : panels) {
            panel.setBackground(background);
            panel.setForeground(foreground);
        }
    }
}
