package mr.demonid.gui.view.devpanels.controls;


import mr.demonid.gui.view.devpanels.controls.types.ControlType;
import mr.demonid.gui.view.devpanels.controls.types.KeyTypeUI;

import javax.swing.*;
import java.awt.*;

public class ObjectKey extends AbstractControl {

    private final int id;
    private ControlType type;
    private int blink;

    /*
        Храним цвета в простых типах исключительно ради ускорения прорисовки
     */
    private Color colNormal;
    private Color colPressed;
    private Color colRollover;

    private Color borNormal;
    private Color borPressed;
    private Color borRollover;

    private Color textNormal;
    private Color textPressed;
    private Color textRollover;

    private Timer timer;

    public ObjectKey(int id, String title) {
        super(title);
        this.id = id;
        switchType(ControlType.Released);
        timer = null;
    }


    public int getId()
    {
        return id;
    }

    public ControlType getType()
    {
        return type;
    }

    public void switchType(ControlType type)
    {
        this.type = type;
        if (type != ControlType.Blink)
        {
            if (timer != null)
                stopBlink();
            setType(type);
        } else if (timer == null) {
            startBlink();
        }

        repaint();
    }

    private void setType(ControlType type)
    {
        KeyGroupColor cols = KeysUI.getInstance().get(KeyTypeUI.BACKGROUND, type);
        colNormal = cols.normal();
        colPressed = cols.pressed();
        colRollover = cols.rollover();
        cols = KeysUI.getInstance().get(KeyTypeUI.BORDER, type);
        borNormal = cols.normal();
        borPressed = cols.pressed();
        borRollover = cols.rollover();
        cols = KeysUI.getInstance().get(KeyTypeUI.TEXT, type);
        textNormal = cols.normal();
        textPressed = cols.pressed();
        textRollover = cols.rollover();
    }


    private void startBlink() {
        blink = 1;
        timer = new Timer(250, e -> {
            blink ^= 1;
            if (blink == 0)
                setType(ControlType.Released);
            else
                setType(ControlType.Taken);
            repaint();
        });
        timer.setRepeats(true);
        timer.start();
    }

    private void stopBlink() {
        timer.stop();
        timer.removeActionListener(timer.getActionListeners()[0]);
        timer = null;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        Color back = colNormal;
        Color text = textNormal;
        Dimension d = getSize();
        if (isPressed) {
            if (isArmed) {
                back = colPressed;
                text = textPressed;
            } else {
                back = colRollover;
                text = textRollover;
            }
        } else {
            if (isRollover) {
                back = colRollover;
                text = textRollover;
            }
        }
        g.setColor(back);
        g.fillRect(0, 0, d.width, d.height);
        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        String caption = title;
        int x = (d.width - fm.stringWidth(caption)) / 2;
        int y = (d.height - fm.getHeight()) / 2 + fm.getAscent();
        g.setColor(text);
        g.drawString(caption, x, y);
    }

    @Override
    protected void paintBorder(Graphics g)
    {
        if (isPressed)
        {
            g.setColor(borPressed);                 // кнопка в нажатом состоянии
        } else if (isRollover)
        {
            g.setColor(borRollover);                // курсор над кнопкой, можно нарисовать её выделенной
        } else {
            g.setColor(borNormal);
        }
        g.drawRect(0, 0, getSize().width-1, getSize().height-1);
        g.drawRect(1, 1, getSize().width - 3, getSize().height - 3);
    }


}
