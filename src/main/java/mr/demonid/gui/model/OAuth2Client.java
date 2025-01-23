package mr.demonid.gui.model;

import mr.demonid.gui.json.JsonLight;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

public class OAuth2Client {

    private static final long DELAY_TIME = 5;   // макс. время на сетевые задержки при передаче токена в запросах (сек)

    private final String clientId;
    private final String clientSecret;
    private final String tokenUrl;

    private String accessToken;                 // кэшируем сам токен
    private Instant tokenExpirationTime;       // и время его действия (в миллисекундах)


    public OAuth2Client(String clientId, String clientSecret, String tokenUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenUrl = tokenUrl;
        this.accessToken = null;
        this.tokenExpirationTime = null;
    }

    public String getAccessToken() {
        if (isTokenValid()) {
            return accessToken;             // текущий токен все еще валиден, используем его.
        }

        HttpURLConnection conn = null;
        try {
            // кодируем client_id и client_secret в Base64
            String authBase64 = Base64.getEncoder().encodeToString(
                    (clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

            // Устанавливаем соединение
            URL url = new URL(tokenUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Basic " + authBase64);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            // Отправляем данные
            String body = "grant_type=client_credentials&scope=read write";
            try (OutputStream outputStream = conn.getOutputStream()) {
                outputStream.write(body.getBytes(StandardCharsets.UTF_8));
            }

            // Читаем ответ
            int statusCode = conn.getResponseCode();
            InputStream inputStream = statusCode == 200 ? conn.getInputStream() : conn.getErrorStream();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
            {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                if (statusCode == 200) {
                    // Разбираем JSON-ответ для получения токена
                    CustomTokenResponse rt = JsonLight.fromJson(response.toString(), CustomTokenResponse.class);
                    accessToken = rt.getAccess_token();
                    tokenExpirationTime = Instant.now().plus(Duration.ofSeconds(rt.getExpires_in() - DELAY_TIME));
                    return accessToken;
                } else {
                    System.out.println("Ошибка получения токена! Код: " + statusCode + ", тело: " + response);
                    return "";
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return "";
    }

    /*
    Проверка токена на валидность (по времени его действия)
     */
    private boolean isTokenValid() {
        return accessToken != null && Instant.now().isBefore(tokenExpirationTime);
    }

}
