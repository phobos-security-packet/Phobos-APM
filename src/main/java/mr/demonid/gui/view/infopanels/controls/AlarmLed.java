package mr.demonid.gui.view.infopanels.controls;

import mr.demonid.gui.properties.Config;
import mr.demonid.gui.util.MathUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Индикатор ТС. Эмулирует работу реального индикатора ПЦН.
 */
public class AlarmLed extends JLabel implements ActionListener {
    private final int id;
    private Timer timer;
    private int timerWait;
    private int blink;
    private final Color[] colors;

    public AlarmLed(int code, String text, Color blinkBkGr) {
        super(text);
        id = code;
        colors = new Color[2];
        colors[1] = blinkBkGr;
        loadSettings();
        setHorizontalAlignment(JLabel.CENTER);
        setVerticalAlignment(JLabel.CENTER);
        setOpaque(true);                // для рисования фона
        timer = null;
    }

    private void loadSettings() {
        String className = getClass().getSimpleName();
        Config prop = Config.getInstance();
        colors[0] = prop.getColor(className + ".background", 0x505050);
        setBackground(colors[0]);
        setForeground(prop.getColor(className + ".foreground", Color.WHITE.getRGB()));
        timerWait = MathUtil.clamp(prop.getInteger(className + ".timerWait", 250), 50, 5000);
        prop.setInteger(className + ".timerWait", timerWait);
    }

    public int getId() {
        return id;
    }

    public void startBlink() {
        blink = 1;
        timer = new Timer(timerWait, this);
        timer.start();
        setBackground(colors[blink]);
    }

    public void stopBlink() {
        timer.stop();
        timer.removeActionListener(this);
        timer = null;
        setBackground(colors[0]);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        blink ^= 1;
        setBackground(colors[blink]);
    }

}
