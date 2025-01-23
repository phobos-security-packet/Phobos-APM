package mr.demonid.gui.view.devpanels;


import mr.demonid.gui.events.AlarmNoticeEvent;
import mr.demonid.gui.events.AlarmNoticeListener;
import mr.demonid.gui.hard.CommandCode;
import mr.demonid.gui.hard.DeviceType;
import mr.demonid.gui.hard.Repeater;
import mr.demonid.gui.hard.TeleFactory;
import mr.demonid.gui.properties.Config;
import mr.demonid.gui.properties.ScaledType;
import mr.demonid.gui.view.devpanels.controls.AlarmLabel;
import mr.demonid.gui.view.devpanels.controls.ObjectKey;
import mr.demonid.gui.view.devpanels.controls.types.ControlType;
import mr.demonid.gui.view.devpanels.popupmenu.MenuUserCommand;
import mr.demonid.gui.message.TMessage;
import mr.demonid.gui.util.MathUtil;
import mr.demonid.gui.util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Базовый класс для отображения состояния устройства
 */
public abstract class DeviceBase extends JPanel implements ScaledType {

    private final DeviceType type;
    private final int id;                           // номер ретранслятора в системе обмена сообщений
    private final int startKey;                     // номер первого ключа
    private final int numKeys;                      // количество ключей у прибора

    private Map<String, MenuUserCommand> popupMenu; // всплывающее меню на все случаи жизни
    private Map<Integer, ObjectKey> keys;           // хэш-карта ключей, для быстрого доступа к ним

    private final AlarmNoticeListener listener;     // куда пересылаем сообщения от пользователя

    private int labelFontSize;                      // размер фонта разметки
    private int cellFontSize;                       // размер фонта ключей


    public DeviceBase(Repeater repeater, AlarmNoticeListener listener) {
        super();
        this.id = repeater.getId();
        this.type = repeater.getType();
        this.listener = listener;
        this.startKey = repeater.getStartKey();
        this.numKeys = repeater.getNumKeys();
        loadSettings();

        int rows = 12;
        int columns = this.numKeys / rows;
        // сетка расположения контролов
        GridLayout grid = new GridLayout(rows + 1, columns + 1, 2, 2);
        setLayout(grid);
        makeLogo();
        makeContextMenu();
        makeControls(rows, columns, repeater.getKeys(), repeater.getBadKeys());
    }


    private void loadSettings() {
        String className = getClass().getSimpleName();
        Config prop = Config.getInstance();
        labelFontSize = MathUtil.clamp(prop.getInteger(className + ".labelFontSize", 16), 8, 32);
        cellFontSize = MathUtil.clamp(prop.getInteger(className + ".cellFontSize", 12), 8, 32);
        prop.setInteger(className + ".labelFontSize", labelFontSize);
        prop.setInteger(className + ".cellFontSize", cellFontSize);
    }

    /**
     * Формирует контекстное меню для разных типов ключей
     */
    private void makeContextMenu() {
        ActionListener listener =  new PopupListener();
        popupMenu = new HashMap<>();
        MenuUserCommand main = new MenuUserCommand(type, listener);
        MenuUserCommand common = new MenuUserCommand(null, listener);
        popupMenu.put(ControlType.Taken.name(), main);
        popupMenu.put(ControlType.Released.name(), main);
        popupMenu.put(ControlType.Bad.name(), common);
        popupMenu.put(ControlType.Unused.name(), common);
    }

    private void makeLogo() {
        URL resource = StringUtil.getResourceURL("images/LOGO.png");
        add(resource == null ? new JLabel("Penzmash") : new JLabel(new ImageIcon(resource)));
    }

    /*
    Создаем ключи для устройства
     */
    private void makeControls(int rows, int columns, List<Integer> usedKeys, List<Integer> badKeys) {
        keys = new HashMap<>();

        for (int i = 0; i < columns; i++)
            add(new AlarmLabel(Integer.toString(i)));

        for (int j = rows-1; j >= 0; j--) {
            add(new AlarmLabel(Integer.toString(j + startKey / columns)));
            int cnt = j * columns + startKey;
            for (int i = 0; i < columns; i++)
            {
                ObjectKey key = new ObjectKey(cnt, String.format("%d-%d", cnt / 10, cnt % 10));
                if (usedKeys.contains(key.getId())) {
                    key.switchType(ControlType.Released);
                } else {
                    key.switchType(ControlType.Unused);
                }
                if (badKeys.contains(key.getId())) {
                    key.switchType(ControlType.Bad);
                }
                key.setComponentPopupMenu(popupMenu.get(key.getType().name()));
                keys.put(cnt, key);
                add(key);
                cnt++;
            }
        }
    }

    /**
     * Обработка пришедшего ТС.
     */
    public void showTeleNotice(TMessage message) {
        if (id == message.getRepeater()) {
            // сообщение пришло от этого ретранслятора
            ObjectKey key = keys.get(message.getKey());
            if (key != null) {
                int code = message.getCode();

                switch (CommandCode.getName(code)) {
                    case ALARM:         // Тревога
                        key.switchType(ControlType.Blink);
                        break;

                    case TAKEN:         // Взят
                    case TAKEN_INFO:    // Взят (по запросу взятых)
                        key.switchType(ControlType.Taken); // + delete blink
                        break;

                    case ACCIDENT:      // Авария
                    case SHORT_CIRCUIT: // Замыкание
                    case SUBSTITUTION:  // Подмена УО
                    case NOT_TAKE:      // Невзят
                    case RELEASED:      // Снят
                    case RELEASED_INFO: // Снят (по запросу снятых)
                        key.switchType(ControlType.Released);  // + delete blink
                        break;

                    case OPENED_UO:     // Вскрыт УО
                        key.switchType(ControlType.Blink);
                        break;

                    case DIRECTION_ON:  // Направление включено
                        key.switchType(ControlType.Released);
                        break;
                    case DIRECTION_OFF: // Направление выключено
                        key.switchType(ControlType.Unused);
                        break;

                    case GUARD:         // Наряд
                    case TYPE_UO:       // Тип УО
                    case RECOVERY_UO:   // Восстановление УО
                    case SYSTEM_INFO:   // Системная
                        // это нам не интересно, игнорируем
                        break;
                }
            }
        }
    }


    /**
     * Масштабирует все входящие в устройство элементы
     * @param koeff Коэффициент масштабирования
     */
    @Override
    public void setScale(float koeff) {
        Font btn = new Font("Courier New", Font.PLAIN, (int) (cellFontSize * koeff));
        Font lab = new Font("Arial", Font.PLAIN, (int) (labelFontSize * koeff));
        for (int i = 0; i < getComponentCount(); i++)
        {
            Component c = getComponent(i);
            if (c instanceof AlarmLabel)
                c.setFont(lab);
            else
                c.setFont(btn);
        }
        MenuUserCommand main = popupMenu.get(ControlType.Taken.name());
        MenuUserCommand common = popupMenu.get(ControlType.Bad.name());
        main.setScale(koeff);
        common.setScale(koeff);
    }

    /**
     * Формирует сообщения о командах оператора и отсылает заданному слушателю
     */
    class PopupListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem) e.getSource();
            JPopupMenu menu = (JPopupMenu) item.getParent();
            ObjectKey key = (ObjectKey) menu.getInvoker();
            int code = TeleFactory.getTU().getCode(e.getActionCommand());
            // отправляем в контроллер
            if (listener != null) {
                listener.receiveNotice(new AlarmNoticeEvent(this, id, key.getId(), code));
            }
        }
    }

    @Override
    public String toString() {
        return "DeviceBase{" +
                "type=" + type.name() +
                ", id=" + id +
                ", startKey=" + startKey +
                ", numKeys=" + numKeys +
                '}';
    }
}
