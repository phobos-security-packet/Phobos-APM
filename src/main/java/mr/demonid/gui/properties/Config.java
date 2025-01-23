package mr.demonid.gui.properties;


import mr.demonid.gui.util.StringUtil;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Config {

    private Properties properties;
    private boolean isModified;     // флаг наличия данных для сохранения в файл

    static Config instance = new Config();

    public static Config getInstance()
    {
        return instance;
    }

    private Config()
    {
        this.isModified = false;
        load();
    }

    public void save()
    {
        if (isModified)
        {
            System.out.println("ViewUI::save()");
            isModified = false;
            if (properties == null)
                properties = new Properties();

            String settingsFileName = getPropertiesFileName();
            try (FileOutputStream output = new FileOutputStream(settingsFileName);
                 OutputStreamWriter writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
                properties.store(writer, "Alarm program settings");
            } catch (Exception ignored) {}        // если не получается сохранить настройки, в следующий раз будут использоваться настройки по умолчанию
        }
    }

    private void load()
    {
        properties = new Properties();
        String settingsFileName = getPropertiesFileName();
        // Загрузка сохраненных настроек
        try (FileInputStream input = new FileInputStream(settingsFileName);
             InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch(Exception ignore) {}        // файла настроек может и не быть, просто игнорируем этот факт
    }

    private String getPropertiesFileName()
    {
        String homeDir = System.getProperty("user.home");
        return homeDir + File.separator + "alarm.properties";
    }


    /**
     * Возвращает значение ключа.
     * Если ключа нет, то создаёт его с дефолтным значением (def)
     * @param key Ключ
     * @param def Значение по умолчанию, если ключа еще нет.
     */
    public Color getColor(String key, int def)
    {
        String value = properties.getProperty(key, "");
        Color res = new Color(str2int(value, def));
        if (StringUtil.isBlank(value))
        {
            properties.setProperty(key, "0x" + Integer.toHexString(res.getRGB()));
            isModified = true;
        }
        return res;
    }

    public void setColor(String key, Color col)
    {
        String val = "0x" + Integer.toHexString(col.getRGB());
        String res = properties.getProperty(key, "");
        if (!res.equalsIgnoreCase(val))
        {
            properties.setProperty(key, val);
            isModified = true;
        }
    }

    /**
     * Возвращает значение ключа.
     * Если ключа нет, то создаёт его с дефолтным значением (если def не пустая строка)
     * @param key Ключ.
     * @param def Значение по умолчанию, если ключа еще нет.
     */
    public String getString(String key, String def)
    {
        String res = properties.getProperty(key, "");
        if (StringUtil.isBlank(res) && !StringUtil.isBlank(def))
        {
            res = def;
            properties.setProperty(key, res);
            isModified = true;
        }
        return res;
    }

    public void setString(String key, String val)
    {
        String res = properties.getProperty(key, "");
        if (!res.equalsIgnoreCase(val))
        {
            properties.setProperty(key, val);
            isModified = true;
        }
    }

    /**
     * Возвращает значение ключа.
     * Если ключа нет, то создаёт его с дефолтным значением (def)
     * @param key Ключ.
     * @param def Значение по умолчанию, если ключа еще нет.
     */
    public int getInteger(String key, int def)
    {
        String str = properties.getProperty(key, "");
        int res = str2int(str, def);
        if (StringUtil.isBlank(str))
        {
            properties.setProperty(key, Integer.toString(res));
            isModified = true;
        }
        return res;
    }

    public void setInteger(String key, int val)
    {
        String to = Integer.toString(val);
        String cur = properties.getProperty(key, "");
        if (!cur.equalsIgnoreCase(to))
        {
            properties.setProperty(key, to);
            isModified = true;
        }
    }

    /**
     * Конвертирует строку в int
     * @param str Исходная строка
     * @return Число, или def, в случае ошибки
     */
    private static int str2int(String str, int def ) {
        int res;
        try {
            res = (int) Long.decode(str).longValue();

        } catch (NumberFormatException | NullPointerException e) {
            res = def;
        }
        return res;
    }
}
