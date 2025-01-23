package mr.demonid.gui.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс для работы с настройками ретрансляторов.
 */
public class RepeaterConfig {

    public static final int FIRST_REPEATER = 1;
    public static final int MAX_REPEATERS = 8;
    public static final int REPEATERS_DIRECT = 120;


    private final Map<Integer, Properties> properties;
    private final Map<Integer, Boolean> isModified;

    static RepeaterConfig instance = new RepeaterConfig();

    public static RepeaterConfig getInstance()
    {
        return instance;
    }

    private RepeaterConfig() {
        properties = new HashMap<>();
        isModified = new HashMap<>();
        init();
    }

    /**
     * Возвращает список номеров всех найденных ретрансляторов.
     */
    public Set<Integer> getRepeaters() {
        return properties.keySet();
    }

    /**
     * Возвращает тип ретранслятора ("Phobos" или "Phobos3").
     * @param repeater Номер ретранслятора (от 1 до 8).
     * @return "" - если такого ретранслятора нет.
     */
    public String getType(int repeater) {
        return properties.get(repeater).getProperty("type", "Unknown");
    }

    /**
     * Возвращает путь и имя иконки для вкладки ретранслятора.
     * @param repeater Номер ретранслятора (от 1 до 8).
     * @return "" - если иконка не задана.
     */
    public String getIconName(int repeater) {
        return properties.get(repeater).getProperty("icon", "");
    }

    /**
     * Возвращает имя ретранслятора.
     * @param repeater Номер ретранслятора (от 1 до 8).
     * @return "Repeater n" - если имя не присвоено.
     */
    public String getName(int repeater) {
        return properties.get(repeater).getProperty("name", "");
    }

    /**
     * Возвращает список задействованных направлений (ключей).
     * @param repeater Номер ретранслятора (от 1 до 8).
     * @return Пустой список, если списка не найдено.
     */
    public List<Integer> getKeys(int repeater) {
        return getList(repeater, "keys");
    }

    /**
     * Возвращает список испорченных направлений (ключей), которые нельзя использовать.
     * @param repeater Номер ретранслятора (от 1 до 8).
     * @return Пустой список, если списка не найдено.
     */
    public List<Integer> getBadKeys(int repeater) {
        return getList(repeater, "bads");
    }


    private List<Integer> getList(int repeater, String keyName) {
        List<Integer> list = new ArrayList<>();
        if (repeater >= FIRST_REPEATER && repeater <= MAX_REPEATERS && properties.containsKey(repeater)) {

            String[] keys = properties.get(repeater).getProperty(keyName, "").split(",");

            for (String key : keys) {
                try {
                    Integer n = Integer.parseInt(key);
                    list.add(n);
                } catch (NumberFormatException ignored) {}
            }
        }
        return list.isEmpty() ? null : list;
    }

    /*
     * Из списка номеров направлений (ключей) создает строку для записи в файл настроек.
     * @param list Список направлений (ключей).
     */
    private String setList(List<Integer> list) {
        List<String> listString = list.stream().map(String::valueOf).collect(Collectors.toList());
        return String.join(",", listString);
    }

    /*
     * Загрузка всех файлов конфигурации для ретрансляторов.
     */
    private void init() {
        for (int i = FIRST_REPEATER; i <= MAX_REPEATERS; i++) {
            isModified.put(i, false);
            Properties prop = loadConfig(i);
            if (prop != null) {
                properties.put(i, prop);
            }
        }
    }

    /*
     * Загружает конфигурацию для заданного ретранслятора.
     */
    private Properties loadConfig(int n) {
        String fName = getPropertiesFileName(n);
        if (fName != null) {
            // Загрузка сохраненных настроек
            try (FileInputStream input = new FileInputStream(fName);
                 InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                Properties prop = new Properties();
                prop.load(reader);
                return prop;
            } catch(Exception ignore) {}        // файла настроек может и не быть, просто игнорируем этот факт
        }
        return null;
    }

    /*
     * Составляем имя файла настроек для указанного ретранслятора.
     */
    private String getPropertiesFileName(int n)
    {
        if (n < FIRST_REPEATER || n > MAX_REPEATERS) {
            return null;
        }
        String fName = "alarm-repeater" + n + ".properties";
        String homeDir = System.getProperty("user.home");
        return homeDir + File.separator + fName;
    }

}
