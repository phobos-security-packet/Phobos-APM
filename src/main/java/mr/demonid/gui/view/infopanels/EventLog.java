package mr.demonid.gui.view.infopanels;


import mr.demonid.gui.hard.TeleBaseDesc;
import mr.demonid.gui.hard.TeleDescription;
import mr.demonid.gui.hard.TeleFactory;
import mr.demonid.gui.properties.Config;
import mr.demonid.gui.properties.ScaledType;
import mr.demonid.gui.view.infopanels.controls.FrameBorder;
import mr.demonid.gui.message.TMessage;
import mr.demonid.gui.util.MathUtil;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.util.ArrayList;

/**
 * Журнал событий. Отображает список последних пришедших ТС.
 */
public class EventLog extends JPanel implements ScaledType {

    private final ArrayList<String[]> area;
    private Color[] descCols;
    private Color foregroundTime;
    private Color foregroundKey;
    private int fontSize;
    private final String maxLengthDesc;         // для вычисления ширины панели

    public EventLog() {
        super();
        loadSettings();
        setBorder(new FrameBorder("Протокол"));
        setBackground(Color.BLACK);

        area = new ArrayList<>();
        maxLengthDesc = getMaxLengthDesc();
    }

    public void showTeleMessage(TMessage message) {
        TeleBaseDesc tc = TeleFactory.getTC();
        insert(String.format("%s;%2d-%d;%s;%d",
                message.getFormattedTime(),
                message.getKey() / 10, message.getKey() % 10,
                tc.getDescription(message.getCode()),
                tc.getLevel(message.getCode())));
    }

    /**
     * Вставляет информационную строку в список панели
     * @param str Строка, вида: "hh:mm:ss;key;desc;level"
     */
    public void insert(String str) {
        if (!str.isEmpty()) {
            String[] comp = str.split(";");
            if (comp.length == 4) {
                area.add(0, comp);
                if (area.size() > 100)
                    area.remove(area.size()-1);     // area.removeLast();
                repaint();
            }
        }
    }

    private void loadSettings() {
        String className = getClass().getSimpleName();
        Config prop = Config.getInstance();
        foregroundTime = prop.getColor(className + ".foregroundTime", Color.MAGENTA.getRGB());
        foregroundKey = prop.getColor(className + ".foregroundKey", Color.YELLOW.getRGB());
        Color lev0 = prop.getColor(className + ".backgroundLevel0", 0xFF6030);
        Color lev1 = prop.getColor(className + ".backgroundLevel1", 0x3060FF);
        Color lev2 = prop.getColor(className + ".backgroundLevel2", 0x60FF30);
        descCols= new Color[] {lev0, lev1, lev2};
        fontSize = MathUtil.clamp(prop.getInteger(className + ".fontSize", 8), 4, 24);
        prop.setInteger(className + ".fontSize", fontSize);
    }

    private String getMaxLengthDesc() {
        String max = "";
        TeleBaseDesc base = TeleFactory.getTC();
        for (TeleDescription tm : base) {
            String s = tm.getShortDescription();
            if (s.length() > max.length())
                max = s;
        }
        return max;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Rectangle rect = AbstractBorder.getInteriorRectangle(this, getBorder(), 0, 0, getWidth(), getHeight());
        FontMetrics fm = getFontMetrics(getFont());
        int height = fm.getHeight();
        int maxy = rect.height + rect.y + height;
        int y = (rect.y + fm.getHeight()) / 2 + fm.getAscent() + 2;

        for (String[] comp : area) {
            int level = str2int(comp[3]);
            if (level >= 0 && level < 3) {
                int x = rect.x + 2;
                g.setColor(foregroundTime);
                g.drawString(comp[0], x, y);
                x += fm.stringWidth(comp[0] + "  ");

                g.setColor(foregroundKey);
                g.drawString(comp[1], x, y);
                x += fm.stringWidth(comp[1] + " ");

                g.setColor(descCols[level]);
                g.drawString(comp[2], x, y);
                y += height;
                if (y > maxy)
                    break;
            }
        }
    }

    private int str2int(String s)
    {
        int res;
        try {
            res = Integer.parseInt(s);
        } catch (NumberFormatException e)
        {
            res = -1;
        }
        return res;
    }

    @Override
    public void setScale(float koeff) {
        setFont(new Font("Courier New", Font.PLAIN, (int) (fontSize * koeff)));
    }


    @Override
    public Dimension getPreferredSize() {
        Graphics g = getGraphics();
        FontMetrics fm = g.getFontMetrics();
        Dimension def = super.getPreferredSize();
        def.width = fm.stringWidth("00:00:00  00-0  " + maxLengthDesc);
        return def;
    }

}
