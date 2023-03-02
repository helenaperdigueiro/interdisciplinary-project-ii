package com.digitalmoneyhouse.iamservice;

import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserAccountControllerTest {

    private final String HOST = "http://localhost:8080/users";

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
                .post(HOST)
                .then()
                .statusCode(201);
    }

    @Test
    public void getUserById() {
        given()
                .when()
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwb2NveW8zQGVtYWlsLmNvbSIsImV4cCI6MTY3NzcyNzIzMiwiaWF0IjoxNjc3NzIzNjMyfQ.pLwJSRNx9ng45IgNXrdmwQu1TekkmqTfh5rkzPHV_7U")
                .get(HOST + "/17")
                .then()
                .body("firstName", equalTo("Pocoyo"))
                .body("lastName", equalTo("Cicrano"))
                .body("cpf", equalTo("335458"))
                .body("phoneNumber", equalTo("3354534432"))
                .body("email", equalTo("pocoyo3@email.com"));
    }
}
