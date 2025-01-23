package mr.demonid.gui.view.infopanels;

import mr.demonid.gui.properties.Config;
import mr.demonid.gui.properties.ScaledType;
import mr.demonid.gui.view.infopanels.controls.FrameBorder;
import mr.demonid.gui.message.TMessage;
import mr.demonid.gui.util.MathUtil;
import mr.demonid.gui.util.StringUtil;

import javax.swing.*;
import java.awt.*;


/**
 * Информационная панель, для отображения номера ключа, активного в данный момент
 */
public class Pcn extends JLabel implements ScaledType {

    private int fontSize;

    public Pcn()
    {
        super("");
        loadSettings();
        setBorder(new FrameBorder("ПЦН"));
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setScale(1.0f);
    }

    public void showTeleMessage(TMessage message)
    {
        if (message != null)
            setText(String.format("%d-%d", message.getKey() / 10, message.getKey() % 10));
    }

    public void clear()
    {
        setText("");
    }

    private void loadSettings()
    {
        String className = getClass().getSimpleName();
        Config prop = Config.getInstance();
        setBackground(prop.getColor(className + ".background", 0x9F9FBC));
        fontSize = MathUtil.clamp(prop.getInteger(className + ".fontSize", 54), 32, 64);
        prop.setInteger(className + ".fontSize", fontSize);
    }

    @Override
    public void setScale(float coefficient)
    {
        setFont(new Font("Arial", Font.PLAIN, (int) (fontSize * coefficient)));
    }

    @Override
    public Dimension getPreferredSize()
    {
        return getPcnSize();
    }

    @Override
    public Dimension getMaximumSize()
    {
        return getPcnSize();
    }

    @Override
    public Dimension getMinimumSize()
    {
        return getPcnSize();
    }

    /**
     * Подсчитывает размер для компонента
     */
    private Dimension getPcnSize()
    {
        String text = getText();
        if (StringUtil.isBlank(text))
            text = "00-0";
        Graphics g = getGraphics();
        FontMetrics fm = g.getFontMetrics(); // получение объекта FontMetrics
        int height = fm.getHeight();
        return new Dimension(fm.stringWidth(" " + text + " "), height + (height/4));
    }

}

