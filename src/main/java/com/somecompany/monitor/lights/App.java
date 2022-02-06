package com.somecompany.monitor.lights;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException {
        Logger logger = LogManager.getLogger(App.class);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://demo.openhab.org/rest/items"))
                .build();
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SmartHomeItem[] list = objectMapper.readValue(json, SmartHomeItem[].class);

        for (SmartHomeItem smartHomeItem : list) {
            if (smartHomeItem.getName().startsWith("Light_")) {
                logger.info("Name: " + smartHomeItem.getName() + ", state: " + smartHomeItem.getState());
            }
        }
    }
}
