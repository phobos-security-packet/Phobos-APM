package mr.demonid.gui.model;

import com.sun.net.httpserver.HttpServer;
import mr.demonid.gui.events.AlarmNoticeListener;

import java.net.InetSocketAddress;
import java.io.IOException;

/**
 * Прием сообщений по протоколу Http.
 */
public class HttpReceive {

    private final int port;
    private final String address;
    private HttpServer server;
    private final AlarmNoticeListener listener;

    public HttpReceive(String address, int port, AlarmNoticeListener listener) {
        this.address = address;
        this.port = port;
        this.listener = listener;
        start();
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext(address, new WebServer(listener));
            server.start();
        } catch (IOException e) {
            server = null;
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(1);
        }
    }


}
