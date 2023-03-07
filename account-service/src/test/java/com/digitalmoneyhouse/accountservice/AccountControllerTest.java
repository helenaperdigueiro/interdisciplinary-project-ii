package com.digitalmoneyhouse.accountservice;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountControllerTest {

    private final String HOST = "http://localhost:8081";
    private final String HOST_IAM = "http://localhost:8080";
    private String token;

    @BeforeEach
    @Test
    public void login() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", "fulano@mail.com");
        jsonObject.put("password", "123");

        Response response = given()
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .when()
                .post(HOST_IAM + "/authenticate")
                .then()
                .contentType(ContentType.JSON)
                .extract()
                .response();

        token = response.jsonPath().get("accessToken");
        System.out.println(token);

        assertEquals(200, response.statusCode());
    }

    @Order(1)
    @Test
    public void createAccount() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", "54");

        given()
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .when()
                .post(HOST + "/accounts")
                .then()
                .statusCode(201);
    }

    @Order(2)
    @Test
    public void saveCardWithoutAuthorizationHeaders() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number", "40");
        jsonObject.put("holder", "Cicrano");
        jsonObject.put("expirationDate", "10/10/2030");
        jsonObject.put("cvc", "123");

        given()
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .when()
                .post(HOST + "/accounts/1/cards")
                .then()
                .statusCode(400);
    }

    @Order(3)
    @Test
    public void saveCardWithAuthorizationHeaders() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number", "43");
        jsonObject.put("holder", "Cicrano");
        jsonObject.put("expirationDate", "10/10/2030");
        jsonObject.put("cvc", "123");

        given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .when()
                .post(HOST + "/accounts/1/cards")
                .then()
                .statusCode(201);
    }

    @Order(4)
    @Test
    public void saveCardInvalidAuthorizationHeaders() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number", "42");
        jsonObject.put("holder", "Cicrano");
        jsonObject.put("expirationDate", "10/10/2030");
        jsonObject.put("cvc", "123");

        given()
                .header("Authorization", "Bearer " + "invalidToken")
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .when()
                .post(HOST + "/accounts/1/cards")
                .then()
                .statusCode(401);
    }

    @Order(5)
    @Test
    public void saveCardValidAuthorizationHeaders() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number", "47");
        jsonObject.put("holder", "Cicrano");
        jsonObject.put("expirationDate", "10/10/2030");
        jsonObject.put("cvc", "123");

        given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .when()
                .post(HOST + "/accounts/1/cards")
                .then()
                .statusCode(201);
    }

    @Order(6)
    @Test
    public void getCardById() {
        given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/accounts/13/cards/3")
                .then()
                .body("id", equalTo(3))
                .body("number", equalTo("003"))
                .body("holder", equalTo("cicrano da silva"))
                .body("expirationDate", equalTo("10/2050"))
                .body("cvc", equalTo("431"))
                .body("account.id", equalTo(13))
                .body("account.userId", equalTo(70))
                .body("account.accountNumber", equalTo("807102"))
                .body("account.walletBalance", equalTo(0.0F));
    }

    @Order(7)
    @Test
    public void getCardsByAccountId() {
        given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/accounts/13/cards")
                .then()
                .statusCode(200);
    }
}
