package email.automation.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Taskbar;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import email.automation.ui.panels.LoginPanel;
import email.automation.ui.panels.MainPanel;
import email.automation.ui.panels.RegisterPanel;
import email.automation.ui.utils.Page.Layout;

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
        var icons = AppIcon.images();
        installDockIcon(icons.get(icons.size() - 1));
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> {
            AppUI ui = new AppUI();
            ui.setIconImages(icons);
            ui.setVisible(true);
        });
    }

    private static void installDockIcon(Image image) {
        if (!Taskbar.isTaskbarSupported()) {
            return;
        }
        Taskbar taskbar = Taskbar.getTaskbar();
        if (!taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
            return;
        }
        try {
            taskbar.setIconImage(image);
        } catch (UnsupportedOperationException ignored) {
        }
    }
}
