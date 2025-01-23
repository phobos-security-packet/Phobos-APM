package mr.demonid.gui.events;

import mr.demonid.gui.message.TMessage;

import java.time.LocalDateTime;
import java.util.EventObject;

/**
 * Сообщения ТС (от оборудования к АРМ) и ТУ (от оператора АРМ к оборудованию)
 *
 */
public class AlarmNoticeEvent extends EventObject {

    private final TMessage message;

    public AlarmNoticeEvent(Object source, LocalDateTime date, int rtr, int key, int code)
    {
        super(source);
        this.message = new TMessage(rtr, key, code, -1, -1, date);
    }

    public AlarmNoticeEvent(Object source, int rtr, int key, int code)
    {
        this(source, LocalDateTime.now(), rtr, key, code);
    }

    public AlarmNoticeEvent(Object source, TMessage message) {
        super(source);
        this.message = message;
    }

    public TMessage getMessage() {
        return message;
    }
}
