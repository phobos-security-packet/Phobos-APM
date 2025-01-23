package mr.demonid.gui.view;

import mr.demonid.gui.hard.Repeater;
import mr.demonid.gui.message.TMessage;

import java.util.List;

public interface IView {
    /**
     * Приём GUI сообщения ТС от оборудования о каких-то событиях
     */
    void showTeleNotice(TMessage message);

    /**
     * Возврат команды ТУ от оператора к ретранслятору.
     * Блокирующая функция, поэтому вызывать только из другого потока.
     */
    TMessage getTeleCommand() throws InterruptedException;

    /**
     * Монтирование новой вкладки с ретранслятором
     */
    void addTabControl(Repeater rtr);
}
