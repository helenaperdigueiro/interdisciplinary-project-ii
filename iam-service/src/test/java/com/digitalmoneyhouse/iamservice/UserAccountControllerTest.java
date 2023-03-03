package com.digitalmoneyhouse.iamservice;

import com.digitalmoneyhouse.iamservice.security.AuthenticationRequest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserAccountControllerTest {

    private final String HOST = "http://localhost:8080";
    private String token;

    @Test
    public void createUser() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("firstName", "Pocoyo");
        jsonObject.put("lastName", "Cicrano");
        jsonObject.put("cpf", "335458");
        jsonObject.put("phoneNumber", "3354534432");
        jsonObject.put("email", "pocoyo3@email.com");
        jsonObject.put("password", "123");

        given()
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .when()
                .post(HOST + "/users")
                .then()
                .statusCode(201);
    }

    @Order(1)
    @Test
    public void login() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", "helena@gmail.com");
        jsonObject.put("password", "123");

        Response response = given()
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .when()
                .post(HOST + "/authenticate")
                .then()
                .contentType(ContentType.JSON)
                .extract()
                .response();

        token = response.jsonPath().get("accessToken");
        System.out.println(token);

        assertEquals(200, response.statusCode());
    }

    @Order(2)
    @Test
    public void getUserById() {
        given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/users/1")
                .then()
                .body("firstName", equalTo("Helena"))
                .body("lastName", equalTo("Helena"))
                .body("cpf", equalTo("1"))
                .body("phoneNumber", equalTo("(92)99999-555"))
                .body("email", equalTo("helena@gmail.com"));
    }

    @Order(3)
    @Test
    public void logout() {
        System.out.println(token);
        given()
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .when()
                .post(HOST + "/tokens/revoke/" + token)
                .then()
                .statusCode(200);
    }
}
