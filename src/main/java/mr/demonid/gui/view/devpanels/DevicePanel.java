package mr.demonid.gui.view.devpanels;


import mr.demonid.gui.events.AlarmNoticeListener;
import mr.demonid.gui.hard.DeviceType;
import mr.demonid.gui.hard.Repeater;
import mr.demonid.gui.properties.ScaledType;
import mr.demonid.gui.message.TMessage;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Панель вкладок с ретрансляторами.
 */
public class DevicePanel extends JTabbedPane implements ScaledType {

    List<DeviceBase> devices;
    float scale;

    public DevicePanel() {
        super();
        devices = new ArrayList<>();
        scale = 1.0f;
    }

    /**
     * Добавляет вкладку с новым устройством
     * @param repeater Класс с описанием ретранслятора
     * @param listener Слушатель для событий от оператора
     */
    public void add(Repeater repeater, AlarmNoticeListener listener) {
        DeviceBase dev = null;
        switch (repeater.getType())
        {
            case Phobos:
                dev = new DevicePhobos(repeater, listener);
                break;
            case Phobos3:
                dev = new DevicePhobos3(repeater, listener);
                break;
        };
        if (dev != null)
        {
            devices.add(dev);
            addTab(repeater.getName(), dev);
            dev.setScale(scale);
        }
    }


    public void showTeleNotice(TMessage message) {
        for (DeviceBase device : devices)
        {
            device.showTeleNotice(message);
        }
    }

    public void stopEvent() {}


    @Override
    public void setScale(float koeff) {
        scale = koeff;
        for (DeviceBase dev : devices)
        {
            dev.setScale(koeff);
        }
    }
}
