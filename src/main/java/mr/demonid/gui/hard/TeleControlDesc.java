package mr.demonid.gui.hard;


import java.util.Arrays;

public class TeleControlDesc extends TeleBaseDesc {

    /**
     * Класс ТУ (теле управления, то есть команды от пользователя к оборудованию)
     */
    public TeleControlDesc()
    {
        super();
        elem.addAll(Arrays.asList(
                new TeleDescription(CommandCode.TAKE.getCode(), "Взять сразу", "", 0, 1),
                new TeleDescription(CommandCode.REMOVE.getCode(), "Снять", "", 0, 1),
                new TeleDescription(CommandCode.TAKE_AFTER.getCode(), "Взять после выхода", "", 0, 1),
                new TeleDescription(CommandCode.REQUEST_UO.getCode(), "Запрос УО", "", 0, 1),
                new TeleDescription(CommandCode.ENABLE_DIR.getCode(), "Подключить направление", "", 0, 3),
                new TeleDescription(CommandCode.DISABLE_DIR.getCode(), "Отключить направление", "", 0, 3),
                new TeleDescription(CommandCode.DET_TYPE_UO.getCode(), "Определить тип УО", "", 0, 3),
                new TeleDescription(CommandCode.REQUEST_TAKEN.getCode(), "Запрос взятых", "", 0, 0),
                new TeleDescription(CommandCode.REQUEST_RELEASED.getCode(), "Запрос снятых", "", 0, 0) // на самом деле должно быть 126, '1'
        ));
    }

}
