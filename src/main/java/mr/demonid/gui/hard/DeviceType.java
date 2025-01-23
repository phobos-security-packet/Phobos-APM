package mr.demonid.gui.hard;

/**
 * Тип ретранслятора
 */
public enum DeviceType {
    Unknown(0), Phobos(1), Phobos3(3);

    final int id;

    DeviceType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * Возвращает DeviceType по имени, игнорируя регистр символов.
     * @return Unknown, если ничего не найдено.
     */
    public static DeviceType getDeviceType(String name) {
        if (name != null && !name.isEmpty()) {
            for (DeviceType type : DeviceType.values()) {
                if (type.name().equalsIgnoreCase(name)) {
                    return type;
                }
            }
        }
        return Unknown;
    }
}
