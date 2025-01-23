package mr.demonid.gui.hard;


/**
 * Класс для сопоставления соответствий кодов событий и их описания
 */
public class TeleDescription {
    int code;                   // код сообщения, в формате Фобос
    String description;         // Описание
    String shortDescription;    // Короткое описание
    int level;                  // уровень важности
    int version;                // версия ретранслятора (1 или 3)


    public TeleDescription(int code, String description, String shortDescription, int level, int version)
    {
        this.code = code;
        this.description = description;
        this.shortDescription = shortDescription;
        this.level = level;
        this.version = version;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public int getLevel() {
        return level;
    }

    public int getVersion() {
        return version;
    }
}
