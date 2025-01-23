package mr.demonid.gui.message;

import mr.demonid.gui.json.JsonField;

import java.time.LocalDateTime;

/**
 * Сообщения ТС (от оборудования к АРМ) и ТУ (от оператора АРМ к оборудованию)
 *
 */
public class TMessage {
    @JsonField(name = "repeater")
    private int rtr;       // номер ретранслятора

    @JsonField
    private int key;            // номер направления (ключа)

    @JsonField(name = "code")
    private int code;           // код сообщений (команды)

    @JsonField
    private int line;           // номер шлейфа сигнализации (-1 если не используется)

    @JsonField
    private int type;           // тип УО, или номер служебного сообщения (-1 если не используется)

    @JsonField
    private LocalDateTime date;

    //region Конструкторы

    public TMessage(int repeater, int key, int code, int line, int type, LocalDateTime date) {
        this.rtr = repeater;
        this.key = key;
        this.code = code;
        this.line = line;
        this.type = type;
        this.date = date;
    }

    public TMessage(int repeater, int key, int code, int line, int type) {
        this(repeater, key, code, line, type, LocalDateTime.now());
    }

    public TMessage(int repeater, int key, int code) {
        this(repeater, key, code, -1, -1, LocalDateTime.now());
    }

    public TMessage() {
    }
    //endregion

    //region Сеттеры & Геттеры

    public int getRepeater() {
        return rtr;
    }

    public void setRepeater(int rtr) {
        this.rtr = rtr;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }


    //endregion

    public String getFormattedTime() {
        return String.format("%02d:%02d:%02d", date.getHour(), date.getMinute(), date.getSecond());
    }

    public String encode()
    {
        return String.format("%s;%d;%d;%d", date.toString(), rtr, key, code);
    }

    @Override
    public String toString()
    {
        return encode();
    }

}
