package UI.Components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import UI.Utils.Page.Theme;

public class Components {
    public static JPanel centeredCardPage() {
        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(Theme.BG);
        return page;
    }

    public static JPanel formCard() {
        JPanel card = new JPanel();
        card.setBackground(Theme.PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 234)),
                new EmptyBorder(8, 8, 8, 8)));
        card.setPreferredSize(new Dimension(400, 420));
        return card;
    }

    public static JLabel heading(String title) {
        JLabel heading = new JLabel(title);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 22f));
        return heading;
    }

    public static JTextField field() {
        JTextField f = new JTextField();
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 216, 224)),
                new EmptyBorder(6, 8, 6, 8)));
        return f;
    }

    public static JPasswordField passwordField() {
        JPasswordField f = new JPasswordField();
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 216, 224)),
                new EmptyBorder(6, 8, 6, 8)));
        return f;
    }

    public static JTextField darkField() {
        JTextField f = new JTextField();
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        f.setBackground(new Color(38, 48, 62));
        f.setForeground(Theme.SIDEBAR_TEXT);
        f.setCaretColor(Theme.SIDEBAR_TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 72, 90)),
                new EmptyBorder(4, 6, 4, 6)));
        return f;
    }

    public static JPanel sidebarLabeled(String label, JTextField field) {
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setForeground(new Color(160, 170, 185));
        l.setFont(l.getFont().deriveFont(11f));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(l);
        wrap.add(Box.createVerticalStrut(2));
        wrap.add(field);
        return wrap;
    }

    public static JPanel labeledBlock(String label, JComponent field) {
        JPanel wrap = new JPanel(new BorderLayout(0, 4));
        wrap.setOpaque(false);
        wrap.add(new JLabel(label), BorderLayout.NORTH);
        wrap.add(field, BorderLayout.CENTER);
        return wrap;
    }

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setOpaque(true);
        b.setBackground(Theme.ACCENT);
        b.setForeground(Theme.PANEL);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton linkButton(String text) {
        JButton b = new JButton(text);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setForeground(Theme.ACCENT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JLabel statusLabel() {
        JLabel l = new JLabel(" ");
        l.setForeground(new Color(90, 100, 115));
        return l;
    }

    public static JLabel sectionTitle(String text, float size) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, size));
        return label;
    }

    public static JLabel sectionTitle(String text, float size, Color color) {
        JLabel label = sectionTitle(text, size);
        label.setForeground(color);
        return label;
    }

    public static <E> JList<E> contactList(ListModel<E> model) {
        JList<E> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBackground(new Color(38, 48, 62));
        list.setForeground(Theme.SIDEBAR_TEXT);
        list.setSelectionBackground(Theme.ACCENT);
        list.setSelectionForeground(Theme.PANEL);
        list.setFixedCellHeight(32);
        list.setBorder(new EmptyBorder(4, 4, 4, 4));
        return list;
    }

    public static JTextArea bodyArea() {
        JTextArea area = new JTextArea(12, 40);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 216, 224)),
                new EmptyBorder(8, 8, 8, 8)));
        return area;
    }
}
