package mr.demonid.gui.hard;

import java.util.HashMap;
import java.util.Map;

public enum CommandCode {
    // ТС
    ALARM(125),             // Тревога
    TAKEN(119),             // Взят
    NOT_TAKE(114),          // Невзят (на самом деле код 110, но он пересекается с ТУ и поэтому заменен)
    RELEASED(115),          // Снят
    GUARD(109),             // Наряд
    SUBSTITUTION(105),      // Подмена УО
    ACCIDENT(97),           // Авария
    DIRECTION_ON(101),      // Направление включено
    DIRECTION_OFF(99),      // Направление выключено
    TYPE_UO(106),           // Тип УО
    OPENED_UO(102),         // Вскрыт УО
    RECOVERY_UO(103),       // Восстановление УО
    SHORT_CIRCUIT(104),     // Замыкание
    SYSTEM_INFO(120),       // Системная
    RELEASED_INFO(98),      // Снят (по "Запрос снятых")
    TAKEN_INFO(100),        // Взят (по "Запрос взятых")
    REPEATER_OPEN(107),     // Отказ ретранслятора
    REPEATER_CLOSE(108),    // Восстановление ретранслятора

    // ТУ
    TAKE(112),              // Взять сразу
    REMOVE(110),            // Снять
    TAKE_AFTER(113),        // Взять после выхода
    REQUEST_UO(122),        // Запрос УО
    ENABLE_DIR(124),        // Подключить направление
    DISABLE_DIR(123),       // Отключить направление
    DET_TYPE_UO(111),       // Определить тип УО
    REQUEST_TAKEN(126),     // Запрос взятых
    REQUEST_RELEASED(127);  // Запрос снятых (на самом деле должно быть 126, '1')
//
    private final int code;

    private static final Map<Integer, CommandCode> codeToConst;
    static {
        codeToConst = new HashMap<>();
        codeToConst.put(ALARM.code, ALARM);
        codeToConst.put(TAKEN.code, TAKEN);
        codeToConst.put(NOT_TAKE.code, NOT_TAKE);
        codeToConst.put(RELEASED.code, RELEASED);
        codeToConst.put(GUARD.code, GUARD);
        codeToConst.put(SUBSTITUTION.code, SUBSTITUTION);
        codeToConst.put(ACCIDENT.code, ACCIDENT);
        codeToConst.put(DIRECTION_ON.code, DIRECTION_ON);
        codeToConst.put(DIRECTION_OFF.code, DIRECTION_OFF );
        codeToConst.put(TYPE_UO.code, TYPE_UO);
        codeToConst.put(OPENED_UO.code, OPENED_UO);
        codeToConst.put(RECOVERY_UO.code, RECOVERY_UO);
        codeToConst.put(SHORT_CIRCUIT.code, SHORT_CIRCUIT);
        codeToConst.put(SYSTEM_INFO.code, SYSTEM_INFO);
        codeToConst.put(RELEASED_INFO.code, RELEASED_INFO);
        codeToConst.put(TAKEN_INFO.code, TAKEN_INFO);
        codeToConst.put(REPEATER_OPEN.code, REPEATER_OPEN);
        codeToConst.put(REPEATER_CLOSE.code, REPEATER_CLOSE);
        // ТУ
        codeToConst.put(TAKE.code, TAKE);
        codeToConst.put(REMOVE.code, REMOVE);
        codeToConst.put(TAKE_AFTER.code, TAKE_AFTER);
        codeToConst.put(REQUEST_UO.code, REQUEST_UO);
        codeToConst.put(ENABLE_DIR.code, ENABLE_DIR);
        codeToConst.put(DISABLE_DIR.code, DISABLE_DIR);
        codeToConst.put(DET_TYPE_UO.code, DET_TYPE_UO);
        codeToConst.put(REQUEST_TAKEN.code, REQUEST_TAKEN);
        codeToConst.put(REQUEST_RELEASED.code, REQUEST_RELEASED);
    }

    CommandCode(int code)
    {
        this.code = code;
    }

    public static CommandCode getName(int code)
    {
        return codeToConst.get(code);
    }

    public int getCode() {
        return code;
    }
}
