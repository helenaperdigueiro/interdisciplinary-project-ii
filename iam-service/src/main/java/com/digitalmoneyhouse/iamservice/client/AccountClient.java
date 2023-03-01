package com.digitalmoneyhouse.iamservice.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class AccountClient {

    @Autowired
    private Gson gson;

    @Value("${accountService.baseUrl}")
    private String BASE_URL;

    public void createAccount(Integer userId) throws URISyntaxException, IOException, InterruptedException {
        URI url = new URI(BASE_URL + "/accounts");

        JsonObject body = new JsonObject();

        body.addProperty("userId", userId);

        String requestBody = gson.toJson(body);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}