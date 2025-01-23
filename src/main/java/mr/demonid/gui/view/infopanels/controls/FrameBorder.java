package mr.demonid.gui.view.infopanels.controls;


import mr.demonid.gui.properties.Config;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Бордюр с заголовком, по типу окошек
 */
public class FrameBorder implements Border {

    private final String text;
    private Color foregroundHeader;
    private Color backgroundHeader;
    private Color backgroundUpBorder;
    private Color backgroundDownBorder;

    private Font fontHeader;
    private final Border border;

    public FrameBorder(String text) {
        loadSettings();
        this.text = text;
        this.border = BorderFactory.createBevelBorder(BevelBorder.RAISED, backgroundUpBorder, backgroundDownBorder);
    }

    private void loadSettings() {
        this.fontHeader = UIManager.getFont("TitledBorder.font");
        String className = getClass().getSimpleName();
        Config prop = Config.getInstance();
        foregroundHeader = prop.getColor(className + ".headerForeground", Color.WHITE.getRGB());
        backgroundHeader = prop.getColor(className + ".headerBackground", 0xA0A0FF);
        backgroundUpBorder = prop.getColor(className + ".backgroundBorderUp", Color.GRAY.getRGB());
        backgroundDownBorder = prop.getColor(className + ".backgroundBorderDown", Color.LIGHT_GRAY.getRGB());
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (border != null) {
            border.paintBorder(c, g, x+1, y+getHeadHeight(), width-2, height-getHeadHeight()-1);

            g.setColor(backgroundHeader);
            g.fillRect(x, y, width, getHeadHeight());
            g.drawRect(x, y, width-1, height-1);

            g.setFont(fontHeader);
            FontMetrics fm = g.getFontMetrics();
            int ty = (getHeadHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g.setColor(foregroundHeader);
            g.drawString(text, 4, ty);
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        Insets res;
        if (border != null) {
            res = border.getBorderInsets(c);
            res.top += getHeadHeight();             // добавляем к бордюру заголовок
            res.top++;
            res.bottom++;
            res.left++;
            res.right++;
        } else {
            res = new Insets(0, 0, 0, 0);
        }
        return res;
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    private int getHeadHeight() {
        if (fontHeader != null) {
            return fontHeader.getSize() * 2;
        }
        return 0;
    }

}
