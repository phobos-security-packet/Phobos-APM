package mr.demonid.gui.hard;

import java.util.ArrayList;
import java.util.List;


/**
 * Класс предоставляет основную информацию о ретрансляторе.
 * Выполнен по паттерну Builder.
 */
public class Repeater {
    private String name;                // имя (для удобства)
    private DeviceType type;            // тип оборудования
    private int id;                     // номер ретранслятора (выставляется на самом ретрансляторе)
    private int startKey;               // номер начального ключа
    private int numKeys;                // кол-во ключей
    private List<Integer> keys;         // номера задействованных ключей
    private List<Integer> badKeys;      // номера испорченных ключей



    public Repeater()
    {
        this.type = DeviceType.Unknown;
        setId(1);
        this.name = "Unknown";
        this.startKey = 0;
        this.numKeys = 120;
        setKeys(null);
        setBadKeys(null);
    }

    public Repeater(String name, DeviceType type, int id, int startKey, int numKeys) {
        setId(id);
        this.type = type;
        setName(name);
        this.startKey = startKey;
        this.numKeys = numKeys;
        setKeys(null);
        setBadKeys(null);
    }

    public void setKeys(List<Integer> keys) {
        if (keys != null && !keys.isEmpty()) {
            this.keys = keys;
        } else {
            this.keys = makeKeys(startKey, numKeys);
        }
    }

    public void setBadKeys(List<Integer> badKeys) {
        if (badKeys != null && !badKeys.isEmpty()) {
            this.badKeys = badKeys;
        } else {
            this.badKeys = new ArrayList<>();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        } else {
            this.name = "Unknown";
        }
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id > 0 && id <= 8) {
            this.id = id;
        } else {
            this.id = 1;
        }
    }

    public int getStartKey() {
        return startKey;
    }

    public void setStartKey(int startKey) {
        this.startKey = startKey;
    }

    public int getNumKeys() {
        return numKeys;
    }

    public void setNumKeys(int numKeys) {
        this.numKeys = numKeys;
    }


    public List<Integer> getKeys() {
        return keys;
    }

    public List<Integer> getBadKeys() {
        return badKeys;
    }

    /**
     * Проверка номера ключа на принадлежность данному ретранслятору
     * @param key Номер ключа в абсолютном формате
     */
    public boolean isEntry(int key)
    {
        return key >= startKey && key < startKey + numKeys;
    }

    private List<Integer> makeKeys(int startKey, int numKeys) {
        List<Integer> key = new ArrayList<>(numKeys);
        for (int i = 0; i < numKeys; i++) {
            key.add(startKey + i);
        }
        return key;
    }

    /**
     * Порождающий класс для билдера.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Сам билдер.
     */
    public static class Builder {
        private final Repeater repeater;

        public Builder() {
            repeater = new Repeater();
        }

        public Builder setName(String name) {
            repeater.setName(name);
            return this;
        }
        public Builder setType(DeviceType type) {
            repeater.type = type;
            return this;
        }
        public Builder setId(int id) {
            repeater.setId(id);
            return this;
        }
        public Builder setStartKey(int startKey) {
            repeater.startKey = startKey;
            return this;
        }
        public Builder setNumKeys(int numKeys) {
            repeater.numKeys = numKeys;
            return this;
        }
        public Builder setKeys(List<Integer> keys) {
            repeater.setKeys(keys);
            return this;
        }
        public Builder setBadKeys(List<Integer> badKeys) {
            repeater.setBadKeys(badKeys);
            return this;
        }
        public Repeater build() {
            return repeater;
        }
    }
}
