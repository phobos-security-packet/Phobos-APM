package mr.demonid.gui.view;

import mr.demonid.gui.util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Добавляет окну свойство сворачивания в трей
 */
public class TrayMenu {

    private JFrame frame;

    public TrayMenu(JFrame frame) {
        this.frame = frame;
        System.out.println();
        init();
    }

    private void init() {

        // Проверка, поддерживается ли системный трей на платформе
        if (!SystemTray.isSupported()) {
            return;
        }

        // Создаем меню для трея с кнопкой "Выход"
        PopupMenu trayMenu = new PopupMenu();
        MenuItem item = new MenuItem("Выход");
        item.addActionListener(e -> System.exit(0));
        trayMenu.add(item);

        // задаём иконку для трея
        TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(StringUtil.getResourceURL("images/alarm.png")), frame.getTitle(), trayMenu);
        trayIcon.setImageAutoSize(true);

        // Добавляем обработчик на двойной клик по иконке
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    frame.setVisible(true);
                    frame.setState(Frame.NORMAL);
                    SystemTray.getSystemTray().remove(trayIcon);
                }
            }
        });

        trayIcon.setPopupMenu(trayMenu);

        // Сворачивание в трей при закрытии
        frame.addWindowStateListener(e -> {
            if (e.getNewState() == Frame.ICONIFIED) {
                try {
                    SystemTray.getSystemTray().add(trayIcon);
                    frame.setVisible(false);
                    trayIcon.displayMessage(frame.getTitle(), "Продолжает работу!",
                            TrayIcon.MessageType.INFO);
                } catch (AWTException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

}
