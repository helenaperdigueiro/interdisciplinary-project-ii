package com.digitalmoneyhouse.accountservice.security;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthInterceptor implements HandlerInterceptor {
//
//    @Value("${iamService.baseUrl}")
//    private String BASE_URL = "http://localhost:8080/validate-token";
//
//    @Autowired
//    private Gson gson = new Gson();
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
//        URI url = new URI(BASE_URL);
//        JsonObject body = new JsonObject();
//        body.addProperty("token", request.getHeader("Authorization").substring(7));
//        String requestBody = gson.toJson(body);
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest requestIam = HttpRequest.newBuilder()
//                .uri(url)
//                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
//                .header("Content-Type", "application/json")
//                .build();
//        HttpResponse responseIam = client.send(requestIam, HttpResponse.BodyHandlers.ofString());
//        if (responseIam.statusCode() == 200) {
//            return true;
//        } else {
//            response.setStatus(HttpStatus.FORBIDDEN.value());
//            return false;
//        }
//    }
}
