package mr.demonid.gui.view.devpanels.controls;


import mr.demonid.gui.properties.Config;
import mr.demonid.gui.view.devpanels.controls.types.ControlType;
import mr.demonid.gui.view.devpanels.controls.types.KeyTypeUI;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс в стиле Singleton, поскольку смысла в нескольких экземплярах нет.
 * Используем ленивую инициализацию, чтобы точно быть уверенными,
 * что Config уже будет проинициализирован.
 */
public final class KeysUI {

    // ключ на сигнализации
    static final int CELL_TAKEN                 = 0x6B9EDC;
    static final int CELL_TAKEN_PRESSED         = 0x5680B0;
    static final int CELL_TAKEN_ROLLOVER        = 0x6296D0;
    // ключ снят с сигнализации
    static final int CELL_REMOVED               = 0xFA6B8B;
    static final int CELL_REMOVED_PRESSED       = 0xC65268;
    static final int CELL_REMOVED_ROLLOVER      = 0xF36282;
    // ключ не задействован
    static final int CELL_EMPTY                 = 0xC0C0C0;
    static final int CELL_EMPTY_PRESSED         = 0xA8A2A4;
    static final int CELL_EMPTY_ROLLOVER        = 0xB7B8B4;
    // ключ не рабочий
    static final int CELL_BAD                   = 0xB0B0B0;
    static final int CELL_BAD_PRESSED           = 0x989294;
    static final int CELL_BAD_ROLLOVER          = 0xA7A8A4;

    // ключ на сигнализации
    static final int BORDER_TAKEN               = 0x2163F1;
    static final int BORDER_TAKEN_PRESSED       = 0x163060;
    static final int BORDER_TAKEN_ROLLOVER      = 0x3266A0;
    // ключ снят с сигнализации
    static final int BORDER_REMOVED             = 0xA030A0;
    static final int BORDER_REMOVED_PRESSED     = 0x860218;
    static final int BORDER_REMOVED_ROLLOVER    = 0xC33252;
    // ключ не задействован
    static final int BORDER_EMPTY               = 0xA6A6A6;
    static final int BORDER_EMPTY_PRESSED       = 0x516151;
    static final int BORDER_EMPTY_ROLLOVER      = 0x8D8D80;
    // ключ не рабочий
    static final int BORDER_BAD                 = 0xA6A6A6;
    static final int BORDER_BAD_PRESSED         = 0x516151;
    static final int BORDER_BAD_ROLLOVER        = 0x8D8D80;

    static final int CELL_TEXT                  = 0x000000;
    static final int CELL_TEXT_PRESSED          = 0x300030;
    static final int CELL_TEXT_ROLLOVER         = 0xFFFFFF;


    private Map<ControlType, Map<KeyTypeUI, KeyGroupColor>> colors;

    private static volatile KeysUI instance;

    public static KeysUI getInstance()
    {
        KeysUI localInstance = instance;
        if (localInstance == null)
        {
            synchronized (KeysUI.class)
            {
                localInstance = instance;
                if (localInstance == null)
                {
                    instance = localInstance = new KeysUI();
                }
            }
        }
        return localInstance;
    }

    private KeysUI()
    {
        load();
    }

    private void load()
    {
        colors = new HashMap<>();

        Map<KeyTypeUI, KeyGroupColor> group;
        group = loadGroup(".taken", new int[] {CELL_TAKEN, CELL_TAKEN_PRESSED, CELL_TAKEN_ROLLOVER, BORDER_TAKEN, BORDER_TAKEN_PRESSED, BORDER_TAKEN_ROLLOVER});
        colors.put(ControlType.Taken, group);
        colors.put(ControlType.Blink, group);   // вообще-то он не нужен, но так лучше, чем null

        group = loadGroup(".released", new int[]{CELL_REMOVED, CELL_REMOVED_PRESSED, CELL_REMOVED_ROLLOVER, BORDER_REMOVED, BORDER_REMOVED_PRESSED, BORDER_REMOVED_ROLLOVER});
        colors.put(ControlType.Released, group);

        group = loadGroup(".unused", new int[]{CELL_EMPTY, CELL_EMPTY_PRESSED, CELL_EMPTY_ROLLOVER, BORDER_EMPTY, BORDER_EMPTY_PRESSED, BORDER_EMPTY_ROLLOVER});
        colors.put(ControlType.Unused, group);

        group = loadGroup(".bad", new int[]{CELL_BAD, CELL_BAD_PRESSED, CELL_BAD_ROLLOVER, BORDER_BAD, BORDER_BAD_PRESSED, BORDER_BAD_ROLLOVER});
        colors.put(ControlType.Bad, group);

    }

    private Map<KeyTypeUI, KeyGroupColor> loadGroup(String type, int[] cols)
    {
        Map<KeyTypeUI, KeyGroupColor> group = new HashMap<>();
        String className = getClass().getSimpleName() + type;
        Config prop = Config.getInstance();

        Color normal = prop.getColor(className + "BkGrNormal", cols[0]);
        Color pressed = prop.getColor(className + "BkGrPressed", cols[1]);
        Color rollover = prop.getColor(className + "BkGrRollover", cols[2]);
        group.put(KeyTypeUI.BACKGROUND, new KeyGroupColor(normal, pressed, rollover));

        normal = prop.getColor(className + "BorderNormal", cols[3]);
        pressed = prop.getColor(className + "BorderPressed", cols[4]);
        rollover = prop.getColor(className + "BorderRollover", cols[5]);
        group.put(KeyTypeUI.BORDER, new KeyGroupColor(normal, pressed, rollover));

        normal = prop.getColor(className + "TextNormal", CELL_TEXT);
        pressed = prop.getColor(className + "TextPressed", CELL_TEXT_PRESSED);
        rollover = prop.getColor(className + "TextRollover", CELL_TEXT_ROLLOVER);
        group.put(KeyTypeUI.TEXT, new KeyGroupColor(normal, pressed, rollover));

        return group;
    }

    public KeyGroupColor get(KeyTypeUI uiType, ControlType type)
    {
        KeyGroupColor color = null;
        Map<KeyTypeUI, KeyGroupColor> group = colors.get(type);
        if (group != null)
        {
            color = group.get(uiType);
        }
        return color;
    }
}
