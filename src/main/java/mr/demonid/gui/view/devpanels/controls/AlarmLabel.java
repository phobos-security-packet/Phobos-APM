package mr.demonid.gui.view.devpanels.controls;


import mr.demonid.gui.properties.Config;

import javax.swing.*;
import java.awt.*;

public class AlarmLabel extends JLabel {

    public AlarmLabel(String text) {
        super(text);
        setHorizontalAlignment(JLabel.CENTER);
        setVerticalAlignment(JLabel.CENTER);
        loadSettings();
        setOpaque(true);                // для закраски фона
    }

    private void loadSettings() {
        String className = getClass().getSimpleName();
        Config prop = Config.getInstance();
        Color border = prop.getColor(className + ".borderColor", Color.BLACK.getRGB());
        Color foreground = prop.getColor(className + ".foreground", Color.WHITE.getRGB());
        Color background = prop.getColor(className + ".background", 0x82AAA0);
        setBorder(BorderFactory.createLineBorder(border));
        setForeground(foreground);
        setBackground(background);
    }
}
