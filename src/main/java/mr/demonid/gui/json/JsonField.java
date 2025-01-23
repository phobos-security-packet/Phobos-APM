package mr.demonid.gui.json;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Аннотация, помечающая поля в классе, которые используются в сериализации и десериализации.
 * Параметры:
 *  name() - связывает поле класса с его именем в Json, если они различаются.
 *  exclude() - если true, поле исключается из (де)сериализации.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonField {
    String name() default "";
    boolean exclude() default false;
}
