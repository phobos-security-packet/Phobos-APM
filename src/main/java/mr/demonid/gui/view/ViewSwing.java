package mr.demonid.gui.view;

import mr.demonid.gui.events.AlarmNoticeEvent;
import mr.demonid.gui.events.AlarmNoticeListener;
import mr.demonid.gui.hard.Repeater;
import mr.demonid.gui.properties.Config;
import mr.demonid.gui.view.devpanels.DevicePanel;
import mr.demonid.gui.view.infopanels.InfoPanel;
import mr.demonid.gui.message.TMessage;
import mr.demonid.gui.util.MathUtil;
import mr.demonid.gui.util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class ViewSwing implements IView {

    private static final int MAX_QUEUE = 100;

    private String titleWindow;

    /*
        Layouts окна нашего приложения
     */
    private JFrame window;                  // окно

    private DevicePanel devicePanel;
    private InfoPanel infoPanel;

    private final TrayMenu trayMenu;

    /*
        Очередь сообщений ТС
     */
    private final LinkedBlockingQueue<TMessage> queueTeleNotice;

    /*
        Очередь сообщений ТУ от оператора к ретранслятору
     */
    private final ArrayBlockingQueue<TMessage> queueTeleCommand;

    Timer timer;
    int indicateTime;                               // время вывода информации на панели


    public ViewSwing()
    {
        loadSettings();
        makeViews();
        makeMenu();
        queueTeleNotice = new LinkedBlockingQueue<>();
        queueTeleCommand = new ArrayBlockingQueue<>(MAX_QUEUE);

        timer = new Timer(indicateTime, timerListener);
        timer.setRepeats(false);
        rescale();
        window.addWindowListener(new ViewWindowAdapter());

        trayMenu = new TrayMenu(window);

        window.setVisible(true);
    }

    private void loadSettings()
    {
        Config prop = Config.getInstance();
        titleWindow = prop.getString(getClass().getSimpleName() + ".title", "Фобос АРМ");
        indicateTime = MathUtil.clamp(prop.getInteger(getClass().getSimpleName() + ".timerWait", 5000), 100, 15000);
        prop.setInteger(getClass().getSimpleName() + ".timerWait", indicateTime);
    }

    /**
     * Создаёт элементы интерфейса
     */
    private void makeViews()
    {
        // создаём окно и настраиваем его
        window = new JFrame(titleWindow);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // создаём панели
        devicePanel = new DevicePanel();
        infoPanel = new InfoPanel();
        infoPanel.addPropertyChangeListener("stopIndicate", evt -> {
            // досрочно прерываем индикацию
            stopEvent();
        });
        // подключаем layouts
        window.getContentPane().add(infoPanel , BorderLayout.EAST);
        window.getContentPane().add(devicePanel, BorderLayout.CENTER);
    }

    /**
     * Создание строки меню приложения
     */
    private void makeMenu()
    {
        // Создание строки главного меню
        JMenuBar menuBar = new JMenuBar();

        // Добавление в главное меню выпадающих пунктов меню
        JMenu file = new JMenu("Файл");
        file.add(new JMenuItem("Открыть", new ImageIcon(StringUtil.getResourceURL("images/open.png"))));
        file.add(new JMenuItem("Сохранить", new ImageIcon(StringUtil.getResourceURL("images/save.png"))));
        file.addSeparator();
        file.add(new JMenuItem("Выход", new ImageIcon(StringUtil.getResourceURL("images/exit.png"))));

        JMenu about = new JMenu("О программе");
        about.add(new JMenuItem("О программе"));

        // Подключаем меню к интерфейсу приложения
        menuBar.add(file);
        menuBar.add(about);
        window.setJMenuBar(menuBar);
    }

    public void rescale()
    {
        float coefficient = getScale();
        devicePanel.setScale(coefficient);
        infoPanel.setScale(coefficient);
    }

    /**
     * Возвращает коэффициент масштабирования для интерфейса.
     * @return Коэффициент, рассчитанный по текущему видеорежиму, от базового видеорежима 640x480
     */
    public float getScale()
    {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        return screenSize.height / 480f;
    }

    /**
     * Рассылка сообщений ТС из очереди
     */
    private void broadcastTeleNotice()
    {
        if (!queueTeleNotice.isEmpty() && !timer.isRunning())
        {
            TMessage message = queueTeleNotice.poll();  // читаем без блокировки
            if (message != null)
            {
                // выводим информацию из очередного сообщения
                devicePanel.showTeleNotice(message);
                infoPanel.showTeleNotice(message);
                timer.start();
            }
        }
    }

    /**
     * Таймер, для рассылки сообщений из очереди
     */
    private final ActionListener timerListener = e -> stopEvent();

    private void stopEvent()
    {
        devicePanel.stopEvent();
        infoPanel.stopEvent();
        timer.stop();
        broadcastTeleNotice();
    }

    /* ============================================================================

        Реализация интерфейса View

       ============================================================================ */

    /**
     * Приём сообщения ТС от оборудования о каких-то событиях,
     * для последующей рассылки всем подписанным виджетам
     */
    @Override
    public void showTeleNotice(TMessage message)
    {
        if (queueTeleNotice.size() >= MAX_QUEUE || !queueTeleNotice.offer(message))
            return;
        broadcastTeleNotice();
    }

    /**
     * Возвращает из очереди команды ТУ от оператора к оборудованию.
     * Блокирующая, поэтому вызывать только из другого потока.
     */
    @Override
    public TMessage getTeleCommand() throws InterruptedException {
        return queueTeleCommand.take();
    }

    /**
     * Монтирование нового ретранслятора
     * @param rtr      Ретранслятор
     */
    @Override
    public void addTabControl(Repeater rtr)
    {
        devicePanel.add(rtr, accumTeleCommand);
    }

    /**
     * Слушатель сообщений ТУ от оператора к оборудованию.
     * Складирует в потокобезопасную очередь.
     * Далее их можно извлечь с помощью getTeleCommand()
     */
    AlarmNoticeListener accumTeleCommand = new AlarmNoticeListener() {
        @Override
        public void receiveNotice(AlarmNoticeEvent event) {
            queueTeleCommand.offer(event.getMessage());
        }
    };

    /**
     *
     *  Слушатель эвентов окна приложения, для загрузки и сохранения параметров окна
     *
     */
    public class ViewWindowAdapter extends WindowAdapter {

        @Override
        public void windowDeactivated(WindowEvent e) {
            System.out.println("View: deactivated");
            super.windowDeactivated(e);
        }

        @Override
        public void windowOpened(WindowEvent e) {
            super.windowOpened(e);
            if (e.getSource() instanceof JFrame && (JFrame) e.getSource() == window)
            {
                String parent = getClass().getSimpleName();
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Dimension screenSize = toolkit.getScreenSize();
                // получаем размеры и положение окна и сразу подкорректируем их
                Config prop = Config.getInstance();
                int windowWidth = MathUtil.clamp(prop.getInteger(parent + ".windowWidth", 0), 640, screenSize.width);
                int windowHeight = MathUtil.clamp(prop.getInteger(parent + ".windowHeight", 0), 480, screenSize.height);
                int windowPosX = MathUtil.clamp(prop.getInteger(parent + ".windowPosX", 0), 0, screenSize.width-windowWidth);
                int windowPosY = MathUtil.clamp(prop.getInteger(parent + ".windowPosY", 0), 0, screenSize.height-windowHeight);
                window.setBounds(windowPosX, windowPosY, windowWidth, windowHeight);
            }
        }

        @Override
        public void windowClosing(WindowEvent e)
        {
            if (e.getSource() instanceof JFrame && (JFrame) e.getSource() == window)
            {
                String parent = getClass().getSimpleName();
                Config prop = Config.getInstance();
                prop.setInteger(parent + ".windowWidth", window.getWidth());
                prop.setInteger(parent + ".windowHeight", window.getHeight());
                prop.setInteger(parent + ".windowPosX", window.getLocation().x);
                prop.setInteger(parent + ".windowPosY", window.getLocation().y);
            }
            System.exit(0);
        }

    }


    //    /**
//     * Слушатель на изменения размеров окна
//     */
//    private ComponentListener getBoundsListener ()
//    {
//        return new ComponentAdapter()
//        {
//            @Override
//            public void componentResized ( final ComponentEvent e )
//            {
//                // get component
//                final Component component = e.getComponent ();
//                final Rectangle bounds = component.getBounds ();
//                System.out.println("resize: " + bounds.width + "x" + bounds.height);
////                bounds.height = bounds.width;
////                component.setBounds ( bounds );
//            }
//        };
//    }
//
//        window.addComponentListener(getBoundsListener());

}
