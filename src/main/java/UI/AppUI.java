package UI;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import UI.Panels.LoginPanel;
import UI.Panels.MainPanel;
import UI.Panels.RegisterPanel;
import UI.Utils.Page.Layout;

public class AppUI extends JFrame {

    public AppUI() {
        super("Email Automation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 640));
        setLocationRelativeTo(null);

        Layout.root.add(LoginPanel.buildLoginPanel(), "login");
        Layout.root.add(RegisterPanel.buildRegisterPanel(), "register");
        Layout.root.add(MainPanel.buildMainPanel(), "main");
        setContentPane(Layout.root);
        Layout.cards.show(Layout.root, "login");
    }

    public static void launch() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> {
            AppUI ui = new AppUI();
            ui.setVisible(true);
        });
    }
}
