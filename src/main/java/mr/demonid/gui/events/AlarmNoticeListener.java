package mr.demonid.gui.events;

import java.util.EventListener;

public interface AlarmNoticeListener extends EventListener {

    void receiveNotice(AlarmNoticeEvent event);
}
