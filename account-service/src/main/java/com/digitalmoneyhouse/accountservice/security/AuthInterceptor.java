package com.digitalmoneyhouse.accountservice.security;

import com.digitalmoneyhouse.accountservice.exception.AuthorizationHeaderNotFoundException;
import com.digitalmoneyhouse.accountservice.exception.InvalidTokenException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUrl = request.getRequestURI();
        if (requestUrl.equals("/accounts")) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.length() <= 7) {
            throw new AuthorizationHeaderNotFoundException();
        }
        token = token.substring(7);
        URI url = new URI("http://localhost:8080/validate-token");
        Gson gson = new Gson();
        JsonObject body = new JsonObject();
        body.addProperty("token", token);
        String requestBody = gson.toJson(body);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestIam = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse responseIam = client.send(requestIam, HttpResponse.BodyHandlers.ofString());

        if (responseIam.statusCode() == 200) {
            return true;
        } else {
            throw new InvalidTokenException();
        }
    }
}
