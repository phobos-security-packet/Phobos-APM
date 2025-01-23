package mr.demonid.gui.hard;

import java.util.Arrays;

/**
 * Класс ТС (теле сообщений от ретранслятора пользователю)
 */
public class TeleNoticeDesc extends TeleBaseDesc {

    public TeleNoticeDesc()
    {
        elem.addAll(Arrays.asList(
                new TeleDescription(CommandCode.ALARM.getCode(), "Тревога", "Тревога", 0, 1),
                new TeleDescription(CommandCode.TAKEN.getCode(), "Взят", "Взят", 2, 1),
                new TeleDescription(CommandCode.NOT_TAKE.getCode(), "Невзят", "Невзят", 0, 1),
                new TeleDescription(CommandCode.RELEASED.getCode(), "Снят", "Снят", 0, 1),
                new TeleDescription(CommandCode.GUARD.getCode(), "Наряд", "Наряд", 1, 1),
                new TeleDescription(CommandCode.SUBSTITUTION.getCode(), "Подмена УО", " Подмена УО ", 0, 3),
                new TeleDescription(CommandCode.ACCIDENT.getCode(), "Авария", "Авария", 0, 1),
                new TeleDescription(CommandCode.DIRECTION_ON.getCode(), "Направление включено", "Напр. вкл.", 2, 3),
                new TeleDescription(CommandCode.DIRECTION_OFF.getCode(), "Направление выключено", "Напр. выкл.", 0, 3),
                new TeleDescription(CommandCode.TYPE_UO.getCode(), "Тип УО", "Тип УО", 1, 3),
                new TeleDescription(CommandCode.OPENED_UO.getCode(), "Вскрыт УО", "Вскрыт УО", 0, 3),
                new TeleDescription(CommandCode.RECOVERY_UO.getCode(), "Восстановление УО", "Восст. УО", 0, 1),
                new TeleDescription(CommandCode.SHORT_CIRCUIT.getCode(), "Замыкание", "Замыкание", 0, 1),
                new TeleDescription(CommandCode.SYSTEM_INFO.getCode(), "Системная", "Системная", 1, 1)));
    }
}
