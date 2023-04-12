package com.digitalmoneyhouse.accountservice.security;

import com.digitalmoneyhouse.accountservice.exception.AuthorizationHeaderNotFoundException;
import com.digitalmoneyhouse.accountservice.exception.BusinessException;
import com.digitalmoneyhouse.accountservice.exception.InvalidTokenException;
import com.digitalmoneyhouse.accountservice.exception.ServiceUnavailableException;
import com.digitalmoneyhouse.accountservice.service.AccountService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class AuthInterceptor implements HandlerInterceptor {

    private AccountService accountService;
    private String baseUrl;

    public AuthInterceptor(AccountService accountService, String baseUrl) {
        this.accountService = accountService;
        this.baseUrl = baseUrl;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUrl = request.getRequestURI();
        List<String> noAuthEndpoint = Arrays.asList("/accounts", "/health", "/favicon.ico");
        if (noAuthEndpoint.contains(requestUrl)) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.length() <= 7) {
            throw new AuthorizationHeaderNotFoundException();
        }
        token = token.substring(7);

//        String[] chunks = token.split("\\.");
//        String payload = new String(Base64.getDecoder().decode(chunks[1]));
//        Integer userIdFromToken = Integer.valueOf(stringPayloadToMap(payload).get("userId"));
//        Integer userIdByAccountPathId = accountService.findById(getAccountIdFromPath(requestUrl)).getUserId();
//        if (userIdByAccountPathId != userIdFromToken) {
//            throw new BusinessException(403, "Access denied");
//        }

        try {
            URI url = new URI(baseUrl + "/validate-token");
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
        } catch (ConnectException exc) {
            throw new ServiceUnavailableException("IAM");
        }

    }

        private Integer getAccountIdFromPath(String requestUrl) {
        try {
            URI uri = new URI(requestUrl);
            String[] segments = uri.getPath().split("/");
            return Integer.valueOf(segments[2]);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> stringPayloadToMap(String payload) {
        Map<String, String> payloadMap = new HashMap<>();
        String[] payloadList = payload
                .replace("\"", "")
                .replace("{", "")
                .replace("}", "")
                .split(",");
        for (String item : payloadList) {
            String[] keyAndValue = item.split(":");
            String key = keyAndValue[0];
            String value = keyAndValue[1];
            payloadMap.put(key, value);
        }
        return payloadMap;
    }
}
