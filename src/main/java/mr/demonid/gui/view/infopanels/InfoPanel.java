package mr.demonid.gui.view.infopanels;

import mr.demonid.gui.properties.ScaledType;
import mr.demonid.gui.view.infopanels.controls.InfoLayout;
import mr.demonid.gui.message.TMessage;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * Правая информационная панель (из нескольких компонентов)
 */
public class InfoPanel extends JPanel implements ScaledType {

    Pcn pcn;
    Indicators indicators;
    EventLog log;


    public InfoPanel() {
        // создаём информационные панели:
        // Pnc и Indicators - имитируют реальный пульт оператора,
        // LogInfo - просто краткая история событий
        setLayout(new InfoLayout(3));
        pcn = new Pcn();
        add(pcn);
        indicators = new Indicators();
        add(indicators);
        log = new EventLog();
        add(log);

        indicators.addPropertyChangeListener("stop", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                firePropertyChange("stopIndicate", 0, 1);
            }
        });
    }

    public void showTeleNotice(TMessage message) {
        if (message != null) {
            pcn.showTeleMessage(message);
            indicators.showTeleMessage(message);
            log.showTeleMessage(message);
        }
    }

    public void stopEvent() {
        pcn.clear();
        indicators.stopBlink();
    }

    @Override
    public void setScale(float coefficient) {
        pcn.setScale(coefficient);
        indicators.setScale(coefficient);
        log.setScale(coefficient);
    }


}

