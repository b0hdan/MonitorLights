package com.somecompany.monitor.lights;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App
{
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main( String[] args ) throws IOException, InterruptedException {
        Logger logger = LogManager.getLogger(App.class);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://demo.openhab.org/rest/items"))
                .build();

        Runnable monitorLights = () -> {
            String json = getResponseJson(client, request);
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            logLightsStates(logger, json, objectMapper);
        };
        scheduler.scheduleAtFixedRate(monitorLights, 0, 10, TimeUnit.MINUTES);
    }

    private static String getResponseJson(HttpClient client, HttpRequest request) {
        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response.body();
    }

    private static void logLightsStates(Logger logger, String json, ObjectMapper objectMapper) {
        SmartHomeItem[] list = new SmartHomeItem[0];
        try {
            list = objectMapper.readValue(json, SmartHomeItem[].class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        for (SmartHomeItem smartHomeItem : list) {
            if (smartHomeItem.getName().startsWith("Light_")) {
                logger.info("Name: " + smartHomeItem.getName() + ", state: " + smartHomeItem.getState());
            }
        }
    }
}
