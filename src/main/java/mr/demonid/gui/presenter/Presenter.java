package mr.demonid.gui.presenter;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import mr.demonid.gui.events.AlarmNoticeEvent;
import mr.demonid.gui.events.AlarmNoticeListener;
import mr.demonid.gui.hard.DeviceType;
import mr.demonid.gui.hard.Repeater;
import mr.demonid.gui.message.TMessage;
import mr.demonid.gui.model.HttpReceive;
import mr.demonid.gui.model.HttpSender;
import mr.demonid.gui.properties.Config;
import mr.demonid.gui.properties.RepeaterConfig;
import mr.demonid.gui.view.IView;

/**
 * Представление.
 */
public class Presenter {

    IView view;

    HttpReceive httpReceive;                            // интерфейс для приема сообщений с сервера
    HttpSender httpSender;                              // интерфейс для отправки сообщений на сервер

    private final ExecutorService executorService;      // поток отправки сообщений на сервер
    private volatile boolean running = true;


    public Presenter(IView view) {
        this.view = view;
        executorService = Executors.newSingleThreadExecutor();
        init();
        Config.getInstance().save();

        startSendingPolling();

        /*
            Вешаем обработчик на выход из программы.
         */
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run() {
                // Обработка завершения приложения
                System.out.println("Close program");
                Config.getInstance().save();            // сохраняем текущие настройки
                httpReceive.stop();
                stopSendingPolling();
            }
        });

    }

    private void init() {
        initHttp();
        initRepeaters();
    }


    /**
     * Поток для отправки сообщений на сервер.
     * Сообщения берутся из блокирующей очереди.
     */
    public void startSendingPolling() {
        executorService.submit(() -> {
            while (running) {
                try {
                    TMessage message = view.getTeleCommand();
                    httpSender.sendToDevice(message);       // переправляем на сервер
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error while polling: " + e.getMessage());
                    break;
                }
            }
        });
    }

    public void stopSendingPolling() {
        running = false;
        executorService.shutdownNow();
        // Ожидаем завершения потока
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {}
    }

    /**
     * Слушатель сообщений ТС от модели к оператору.
     * Просто перенаправляет их в IView
     */
    AlarmNoticeListener receiverTeleCommand = new AlarmNoticeListener() {
        @Override
        public void receiveNotice(AlarmNoticeEvent event) {
            view.showTeleNotice(event.getMessage());
        }
    };


    /*
     * Инициализация Http-ресурсов для обмена данными с сервером.
     */
    private void initHttp() {
        Config config = Config.getInstance();
        String addrReceive = config.getString(getClass().getSimpleName() + ".receive.address", "/api/get-event");
        int receivePort = config.getInteger(getClass().getSimpleName() + ".receive.port", 4310);

        String senderHost = config.getString(getClass().getSimpleName() + ".sender.host", "http://localhost");
        String senderAddress = config.getString(getClass().getSimpleName() + ".sender.address", "/api/transfer/awp/send-to-dev");
        int senderPort = config.getInteger(getClass().getSimpleName() + ".sender.port", 8080);
        String authHost = config.getString(getClass().getSimpleName() + ".sender.auth.host", "http://localhost");
        String authAddress = config.getString(getClass().getSimpleName() + ".sender.auth.address", "/oauth2/token");
        int authPort = config.getInteger(getClass().getSimpleName() + ".sender.auth.port", 8090);

        httpReceive = new HttpReceive(addrReceive, receivePort, receiverTeleCommand);
        httpSender = new HttpSender(senderHost, senderAddress, senderPort, authHost, authAddress, authPort);
    }

    /*
     * Инициализация данных о ретрансляторах.
     */
    private void initRepeaters() {
        RepeaterConfig rc = RepeaterConfig.getInstance();
        Set<Integer> ids = rc.getRepeaters();
        if (!ids.isEmpty()) {
            ids.forEach(id -> {
                String name = rc.getName(id);
                DeviceType type = DeviceType.getDeviceType(rc.getType(id));
                List<Integer> keys = rc.getKeys(id);
                List<Integer> bads = rc.getBadKeys(id);
                Repeater repeater = Repeater.builder()
                        .setId(id)
                        .setName(rc.getName(id))
                        .setType(type)
                        .setStartKey((id-1)*RepeaterConfig.REPEATERS_DIRECT)
                        .setNumKeys(RepeaterConfig.REPEATERS_DIRECT)
                        .setKeys(keys)
                        .setBadKeys(bads)
                        .build();
                view.addTabControl(repeater);
            });
        } else {
            view.addTabControl(new Repeater("Охрана", DeviceType.Phobos, 1, 0, RepeaterConfig.REPEATERS_DIRECT));
            view.addTabControl(new Repeater("Пожарная охрана", DeviceType.Phobos3, 2, (2-1)*RepeaterConfig.REPEATERS_DIRECT, RepeaterConfig.REPEATERS_DIRECT));
        }
    }

}
