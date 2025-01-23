package mr.demonid.gui.json;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Простенький класс для сериализации и десериализации объектов в/из Json.
 * Ввиду экономии места сделан очень простым и не поддерживает
 * сложные типы (только LocalDateTime) и массивы.
 * Но для данной задачи вполне рабочее решение.
 */
public class JsonLight {

    /**
     * Конвертирует объект в Json-строку.
     */
    public static String toJson(Object object) throws IllegalAccessException
    {
        // собираем информацию о полях класса
        Map<String, Object> jsonElements = mapObject(object);

        // теперь из мэп конструируем сроку Json
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        for (Map.Entry<String, Object> entry : jsonElements.entrySet()) {
            jsonBuilder.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof Number) {
                jsonBuilder.append(value).append(",");
            } else {
                jsonBuilder.append("\"").append(value).append("\",");
            }
        }
        jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);         // удаляем последнюю запятую
        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }


    /**
     * Десериализация объекта из Json-строки.
     * Для корректной обработки строк и дат, пришлось использовать регулярные выражения,
     * поскольку простое разбиение строк по ':' не будет работать со строками и датами,
     * включающими этот символ.
     * @param json  Строка json
     * @param clazz Тип класса, описанный в Json.
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws Exception {

        // создаём экземпляр класса без конструктора
        T instance = clazz.getDeclaredConstructor().newInstance();

        // Регулярное выражение для поиска ключей и значений в JSON
        Pattern pattern = Pattern.compile("\"([^\"]+)\":\"*([^,^}\"]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(json);

        while (matcher.find()) {
            String key = matcher.group(1);          // ключ
            String value = matcher.group(2);        // значение

            // ищем подходящее поле в классе
            for (Field field : clazz.getDeclaredFields()) {
                String fieldName = field.getName();
                if (field.isAnnotationPresent(JsonField.class)) {
                    JsonField jsonField = field.getAnnotation(JsonField.class);
                    if (jsonField.exclude())
                        continue;
                    if (!jsonField.name().isEmpty())
                        fieldName = jsonField.name();
                }
                if (fieldName.equals(key)) {
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();
                    // Преобразуем строку в нужный тип и устанавливаем значение поля
                    if (fieldType == int.class || fieldType == Integer.class) {
                        field.set(instance, Integer.parseInt(value));
                    } else if (fieldType == long.class || fieldType == Long.class) {
                        field.set(instance, Long.parseLong(value));
                    } else if (fieldType == double.class || fieldType == Double.class) {
                        field.set(instance, Double.parseDouble(value));
                    } else if (fieldType == float.class || fieldType == Float.class) {
                        field.set(instance, Float.parseFloat(value));
                    } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                        field.set(instance, Boolean.parseBoolean(value));
                    } else if (fieldType == LocalDateTime.class) {
                        LocalDateTime dateTime = LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        field.set(instance, dateTime);
                    } else {
                        field.set(instance, value);
                    }
                }
            }
        }
        return instance;
    }


    /**
     * Составляет карту полей и значений объекта, с учётом необязательной аннотации @JsonField.
     * @param object Исходный объект.
     */
    public static Map<String, Object> mapObject(Object object) throws IllegalAccessException {
        Map<String, Object> res = new HashMap<>();

        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);              // разрешаем доступ к полям объекта
            String key = field.getName();
            if (field.isAnnotationPresent(JsonField.class)) {
                // поле отмечено аннотацией @JsonField
                JsonField fieldAnnotation = field.getAnnotation(JsonField.class);
                if (fieldAnnotation.exclude())
                    continue;                   // игнорируем поле
                // задаём имя поля в Json
                if (!fieldAnnotation.name().isEmpty())
                    key = fieldAnnotation.name();
            }
            // и добавляем в мэп
            res.put(key, field.get(object));
        }
        return res;
    }

}