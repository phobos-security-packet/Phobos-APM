package mr.demonid.gui.view.infopanels;


import mr.demonid.gui.hard.TeleBaseDesc;
import mr.demonid.gui.hard.TeleDescription;
import mr.demonid.gui.hard.TeleFactory;
import mr.demonid.gui.properties.Config;
import mr.demonid.gui.properties.ScaledType;
import mr.demonid.gui.view.infopanels.controls.AlarmLed;
import mr.demonid.gui.message.TMessage;
import mr.demonid.gui.util.MathUtil;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Блок индикаторов для отображения типа пришедшего ТС от оборудования.
 * Эмулирует привычный операторам блок индикаторов на ПЦН.
 */
public class Indicators extends JPanel implements ScaledType {

    private AlarmLed blinkLed;
    private Color[] cols;
    private int fontSize;

    public Indicators() {
        super();
        loadSettings();
        GridLayout grid = new GridLayout(0, 2, 2, 2);
        setLayout(grid);
        blinkLed = null;
        makeIndicators();       // создание индикаторов
    }

    private void loadSettings() {
//                setBorder(new FrameBorder("Индикаторы"));
        String className = getClass().getSimpleName();
        Config prop = Config.getInstance();
        fontSize = MathUtil.clamp(prop.getInteger(className + ".fontSize", 14), 6, 24);
        prop.setInteger(className + ".fontSize", fontSize);
        Color borUp = prop.getColor(className + ".borderColorUp", Color.GRAY.getRGB());
        Color borDn = prop.getColor(className + ".borderColorDown", Color.LIGHT_GRAY.getRGB());
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, borUp, borDn));
        cols = new Color[3];
        cols[0] = prop.getColor(className + ".ledColorLevel0", 0xF03030);
        cols[1] = prop.getColor(className + ".ledColorLevel1", 0x3030F0);
        cols[2] = prop.getColor(className + ".ledColorLevel2", 0x30F030);
    }

    private void makeIndicators() {
        TeleBaseDesc tc = TeleFactory.getTC();
        for (TeleDescription desc : tc) {
            String str = desc.getShortDescription();
            AlarmLed led = new AlarmLed(desc.getCode(), str, cols[desc.getLevel()]);
            // добавляем слушатель
            led.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    firePropertyChange("stop", 0, 1);
                }
            });
            add(led);
        }
    }

    /**
     * Оповещение оператора миганием индикатора
     * @param message Отображаемое событие.
     */
    public void showTeleMessage(TMessage message)
    {
        stopBlink();
        for(Component c: getComponents()) {
            if (c instanceof AlarmLed && ((AlarmLed) c).getId() == message.getCode()) {
                blinkLed = (AlarmLed) c;
                blinkLed.startBlink();
            }
        }
    }

    /**
     * Завершаем оповещение оператор
     */
    public void stopBlink()
    {
        if (blinkLed != null) {
            blinkLed.stopBlink();
            blinkLed = null;
        }
    }

    @Override
    public void setScale(float koeff) {
        Font fnt = new Font("Arial", Font.PLAIN, (int) (fontSize * koeff));
        setFont(fnt);
        for (int i = 0; i < getComponentCount(); i++)
        {
            Component c = getComponent(i);
            c.setFont(fnt);
        }
    }

    //    @Override
//    public Dimension getMaximumSize() {
//        return getCurrentSize();
//    }
//
//    @Override
//    public Dimension getMinimumSize() {
//        return getCurrentSize();
//    }
//
//    @Override
//    public Dimension getPreferredSize() {
//        return getCurrentSize();
//    }

//    private Dimension getCurrentSize()
//    {
//        System.out.println("maxLong = " + maxLong);
//        Graphics g = getGraphics();
//        FontMetrics fm = g.getFontMetrics(); // получение объекта FontMetrics
//        int height = fm.getHeight();
//        Dimension dim = new Dimension(fm.stringWidth(" " + maxLong + " "), height + (height/4));
//        System.out.println(dim);
//        return dim;
//    }
//

}

