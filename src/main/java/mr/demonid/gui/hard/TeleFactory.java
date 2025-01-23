package mr.demonid.gui.hard;

public class TeleFactory {

    private static final TeleBaseDesc teleControl = new TeleControlDesc();
    private static final TeleBaseDesc teleNotice = new TeleNoticeDesc();

    /**
     * Возвращает ссылку на класс ТУ (теле управления)
     * @return
     */
    public static TeleBaseDesc getTU()
    {
        return teleControl;
    }

    /**
     * Возвращает ссылку на клас ТС (теле сообщений)
     */
    public static TeleBaseDesc getTC()
    {
        return teleNotice;
    }

}
