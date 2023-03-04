package com.digitalmoneyhouse.accountservice.client;

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
public class IamClient {

    @Autowired
    private Gson gson;

    @Value("${iamService.baseUrl}")
    private String BASE_URL;

    public HttpResponse verifyToken(String bearerToken) throws URISyntaxException, IOException, InterruptedException {
        URI url = new URI(BASE_URL);
        JsonObject body = new JsonObject();
        body.addProperty("token", bearerToken.replace("Bearer ", ""));
        String requestBody = gson.toJson(body);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }
}
