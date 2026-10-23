package email.automation.ui.utils;

import java.awt.CardLayout;
import java.awt.Color;

import javax.swing.JPanel;

public class Page {
    public static class Theme {
        public static final Color BG = new Color(245, 247, 250);
        public static final Color PANEL = Color.WHITE;
        public static final Color ACCENT = new Color(30, 90, 160);
        public static final Color SIDEBAR = new Color(28, 36, 48);
        public static final Color SIDEBAR_TEXT = new Color(230, 234, 240);
    }

    public static class Layout {
        public static final CardLayout cards = new CardLayout();
        public static final JPanel root = new JPanel(cards);
    }
}
