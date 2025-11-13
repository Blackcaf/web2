package com.nlshakal.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/weather")
public class WeatherServlet extends HttpServlet {
    private static final String WEATHER_URL = "https://yandex.ru/pogoda/ru/saint-petersburg?lat=59.938784&lon=30.314997";
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Map<String, Object> windData = getWindData();
            String json = gson.toJson(windData);
            response.getWriter().write(json);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", "Не удалось получить данные о погоде: " + e.getMessage());
            response.getWriter().write(gson.toJson(error));
        }
    }

    private Map<String, Object> getWindData() throws IOException {
        URL url = new URL(WEATHER_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }

        return parseWindData(content.toString());
    }

    private Map<String, Object> parseWindData(String html) {
        Map<String, Object> result = new HashMap<>();

        // Ищем паттерн: число м/с, направление
        // Пример: >2,8 м/с, З</li>
        Pattern pattern = Pattern.compile(">(\\d+(?:,\\d+)?)\\s*м/с,\\s*([А-Я-]+)<");
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            String speedStr = matcher.group(1).replace(",", ".");
            double speed = Double.parseDouble(speedStr);
            String direction = matcher.group(2);

            result.put("speed", speed);
            result.put("direction", direction);
            result.put("success", true);
        } else {
            result.put("success", false);
            result.put("error", "Не удалось распарсить данные о ветре");
        }

        return result;
    }
}

