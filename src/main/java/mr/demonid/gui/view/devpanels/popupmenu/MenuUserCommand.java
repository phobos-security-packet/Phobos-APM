package mr.demonid.gui.view.devpanels.popupmenu;


import mr.demonid.gui.hard.DeviceType;
import mr.demonid.gui.hard.TeleBaseDesc;
import mr.demonid.gui.hard.TeleDescription;
import mr.demonid.gui.hard.TeleFactory;
import mr.demonid.gui.properties.Config;
import mr.demonid.gui.properties.ScaledType;
import mr.demonid.gui.util.MathUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Контекстное меню, через которое оператор задаёт команды на устройства.
 */
public class MenuUserCommand extends JPopupMenu implements ScaledType {

    private Color backgrColor;
    private int fontSize;

    public MenuUserCommand(DeviceType dev, ActionListener listener) {
        loadSettings();

        TeleBaseDesc messages = TeleFactory.getTU();
        for (TeleDescription msg : messages)
        {
            if (msg.getVersion() <= (dev != null ? dev.getId(): 0))
                addItems(listener, msg.getDescription());
        }
    }

    private void loadSettings()
    {
        String className = getClass().getSimpleName();
        Config prop = Config.getInstance();
        backgrColor = prop.getColor(className + ".background", 0x9F9F9F);
        fontSize = MathUtil.clamp(prop.getInteger(className + ".fontSize", 16), 8, 32);
        prop.setInteger(className + ".fontSize", fontSize);
    }

    private void addItems(ActionListener listener, String ... names)
    {
        for (String name : names)
        {
            JMenuItem item = new JMenuItem(name);
            item.addActionListener(listener);
            item.setBackground(backgrColor);
            add(item);
        }
    }

    @Override
    public void setScale(float koeff) {
        Font btn = new Font("Arial", Font.PLAIN, (int) (fontSize * koeff));
        MenuElement[] elems = getSubElements();
        for (MenuElement elem : elems)
        {
            JMenuItem item = (JMenuItem) elem;
            item.setFont(btn);
        }
    }
}
