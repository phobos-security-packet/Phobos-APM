package mr.demonid.gui.view.devpanels.controls;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Своя реализация кнопки. Поскольку заточена под конкретную задачу,
 * то менее "тяжеловесна" по сравнению с JButton. Это здорово экономит
 * ресурсы (в том числе вычислительные), учитывая что экземпляров могут
 * быть не одна сотня (до 8 ретрансляторов по 120 ключей).
 */
public abstract class AbstractControl extends JPanel implements MouseListener {

    protected String title;

    private final EventListenerList listenerList;   // список получателей извещений

    protected boolean isPressed;        // кнопка в нажатом состоянии
    protected boolean isArmed;          // курсор мышки над нажатой кнопкой
    protected boolean isRollover;       // курсор над кнопкой, можно нарисовать её выделенной

    public AbstractControl(String title)
    {
        super();
        isPressed = false;
        isArmed = false;
        isRollover = false;
        
        this.title = title;
        listenerList = new EventListenerList();
        addMouseListener(this);
        calcDimension();
        setBorder(null);                    // у еас своя отрисовка
    }

    private Dimension calcDimension()
    {
        FontMetrics fm = getFontMetrics(getFont());
        int strw = fm.stringWidth(title);
        int strh = fm.getHeight();
        return new Dimension(strw + 34, strh + 10);
    }

    @Override
    public Dimension getMinimumSize() {
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(fm.stringWidth(title), fm.getHeight());
    }

    @Override
    public Dimension getPreferredSize()
    {
        return calcDimension();
    }

    @Override
    public Dimension getMaximumSize() {
        return calcDimension();
    }

    @Override
    protected abstract void paintComponent(Graphics g);

    @Override
    public void mousePressed(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            isPressed = true;
            if (isRollover)
                isArmed = true;
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        isPressed = false;
        isArmed = false;
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            if (isRollover)
                fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, title));
        }
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        isRollover = true;
        if (isPressed)
            isArmed = true;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        isArmed = false;
        isRollover = false;
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}


    public void addActionListener(ActionListener listener)
    {
        listenerList.add(ActionListener.class, listener);
    }

    public void removeActionListener(ActionListener listener)
    {
        listenerList.remove(ActionListener.class, listener);
    }


    protected void fireActionPerformed(ActionEvent event)
    {
        Object[] listeners = listenerList.getListeners(ActionListener.class);
        for (int i = listeners.length-1; i>=0; i--)
            ((ActionListener)listeners[i]).actionPerformed(event);
    }

    /*
    Пока не нужно

implements ItemSelectable

    public void addItemListener(ItemListener l)
    {
        listenerList.add(ItemListener.class, l);
    }

    public void removeItemListener(ItemListener l)
    {
        listenerList.remove(ItemListener.class, l);
    }

    @Override
    public Object[] getSelectedObjects() {
        return new Object[0];
    }

    protected void fireItemStateChanged(ItemEvent event)
    {
        Object[] listeners = listenerList.getListeners(ItemListener.class);

        if (listeners.length > 0)
        {
            ItemEvent e = new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, this, event.getStateChange());
            for (int i = listeners.length-1; i>=0; i---)
                ((ItemListener) listeners[i]).itemStateChanged(e);
        }
    }
   */
}
