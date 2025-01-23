package mr.demonid.gui.model;

import mr.demonid.gui.json.JsonLight;
import mr.demonid.gui.message.TMessage;
import mr.demonid.gui.util.StringUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpSender {
    private String address;

    private OAuth2Client authorize;

    public HttpSender(String host, String address, int port, String authHost, String authAddress, int authPort) {
        this.address = host + ":" + port + address;
        System.out.println("-- address gateway: " + this.address);
        System.out.println("-- address auth: " + authHost + ":" + authPort + authAddress);
        authorize = new OAuth2Client("apmid", "hren-ugadaesh", authHost + ":" + authPort + authAddress);
    }

    public boolean sendToDevice(TMessage message) {
        String token = authorize.getAccessToken();
        HttpURLConnection conn = null;
        try {
            // Устанавливаем соединение
            URL url = new URL(address);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Отправляем тело запроса
            String out = JsonLight.toJson(message);
            try (OutputStream outputStream = conn.getOutputStream()) {
                outputStream.write(out.getBytes(StandardCharsets.UTF_8));
            }

            // Читаем ответ
            int statusCode = conn.getResponseCode();
            if (statusCode >= 200 && statusCode < 300) {
                return true;
            }

//            InputStream inputStream = statusCode >= 200 && statusCode < 300
//                    ? conn.getInputStream()
//                    : conn.getErrorStream();
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
//                StringBuilder response = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    response.append(line);
//                }
//                return response.toString(); // Возвращаем ответ сервера
//            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return false;
    }


}
