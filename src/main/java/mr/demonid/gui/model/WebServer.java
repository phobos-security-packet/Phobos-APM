package mr.demonid.gui.model;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import mr.demonid.gui.events.AlarmNoticeEvent;
import mr.demonid.gui.events.AlarmNoticeListener;
import mr.demonid.gui.json.JsonLight;
import mr.demonid.gui.message.TMessage;
import mr.demonid.gui.view.IView;

import java.io.*;
import java.nio.charset.StandardCharsets;


/**
 * Прием ТУ-сообщений в виде Http-запросов.
 * Сообщения десериализуются из JSON-формата в класс TMessage
 * и отправляются заданному слушателю.
 */
public class WebServer implements HttpHandler {

    private final AlarmNoticeListener listener;

    public WebServer(AlarmNoticeListener listener) {
        this.listener = listener;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            // Читаем тело запроса  new String(exchange.getRequestBody().readAllBytes());
            String requestBody = readBody(exchange);
            try {
                TMessage message = JsonLight.fromJson(requestBody, TMessage.class);
                sendHttpOk(exchange);               // подтверждаем приём
                if (listener != null) {
                    listener.receiveNotice(new AlarmNoticeEvent(this, message));
                }
            } catch (Exception e) {
                sendHttpBadRequest(exchange, 400);  // либо неверный формат, либо просто попорчено
                System.out.println("ERROR: bad message");
            }
        } else {
            sendHttpBadRequest(exchange, 405);      // кроме POST больше ничего не принимаем
        }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        // Читаем тело запроса
        InputStream inputStream = exchange.getRequestBody();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
    }

    private void sendHttpOk(HttpExchange exchange) throws IOException {
        // Отправляем ответ HttpStatus.Ok
        String response = "Webhook received";
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void sendHttpBadRequest(HttpExchange exchange, int code) throws IOException {
        String response = "Request is bad";
        exchange.sendResponseHeaders(400, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

}
