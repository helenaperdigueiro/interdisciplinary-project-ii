package com.digitalmoneyhouse.accountservice;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
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

    @Order(8)
    @Test
    public void getTransactionById() {
        given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/accounts/1/transactions/1")
                .then()
                .body("id", equalTo(1))
                .body("amount", equalTo(100000.0F))
                .body("date", equalTo("2023-03-13T23:48:45.019118"))
                .body("type", equalTo("CASH_DEPOSIT"))
                .body("transactionCode", equalTo("89FE22AC-EFEE-465C-B159-CAACF01064A4"))
                .body("description", equalTo("teste descricao"))
                .body("cardId", equalTo(1))
                .body("cardNumber", equalTo("5553 74 ** **** 6110"))
                .body("accountId", equalTo(1))
                .body("accountNumber", equalTo("337828"));
    }

    @Order(9)
    @Test
    public void getTransactionsWithLimit1() {
        given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/accounts/1/transactions?limit=1")
                .then()
                .assertThat()
                .body("size()", equalTo(1));
    }

    @Order(10)
    @Test
    public void getTransactionsWithTypeCashDeposit() {
        given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/accounts/1/transactions?type=CASH_DEPOSIT")
                .then()
                .assertThat()
                .body("type", hasItem("CASH_DEPOSIT"));
    }

    @Order(11)
    @Test
    public void getTransactionsWithTypeCashTransference() {
        given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/accounts/1/transactions?type=CASH_TRANSFERENCE")
                .then()
                .assertThat()
                .body("type", hasItem("CASH_TRANSFERENCE"));
    }

    @Order(12)
    @Test
    public void getTransactionsWithLimit1AndTypeCashDeposit() {
        given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/accounts/1/transactions?limit=1&type=CASH_DEPOSIT")
                .then()
                .assertThat()
                .body("size()", equalTo(1))
                .body("type", hasItem("CASH_DEPOSIT"));
    }

    @Order(13)
    @Test
    public void saveTransactionTypeCashDeposit() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", 10.00);
        jsonObject.put("type", "CASH_DEPOSIT");
        jsonObject.put("description", "teste descricao");
        jsonObject.put("cardId", 1);

        given()
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .when()
                .post(HOST + "/accounts/1/transactions")
                .then()
                .statusCode(201);
    }

    @Order(14)
    @Test
    public void saveTransactionTypeCashTransference() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", 10.00);
        jsonObject.put("type", "CASH_TRANSFERENCE");
        jsonObject.put("description", "teste descricao");
        jsonObject.put("destinationAccount", "147055");

        given()
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .when()
                .post(HOST + "/accounts/1/transactions")
                .then()
                .statusCode(201);
    }

    @Order(15)
    @Test
    public void saveTransactionInvalidAccount() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", 10.00);
        jsonObject.put("type", "CASH_TRANSFERENCE");
        jsonObject.put("description", "teste descricao");
        jsonObject.put("destinationAccount", "147055");

        given()
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .when()
                .post(HOST + "/accounts/10/transactions")
                .then()
                .statusCode(404);
    }

    @Order(16)
    @Test
    public void saveTransactionInvalidType() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", 10.00);
        jsonObject.put("type", "CHECK_TRANSFERENCE");
        jsonObject.put("description", "teste descricao");
        jsonObject.put("destinationAccount", "147055");

        given()
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .when()
                .post(HOST + "/accounts/1/transactions")
                .then()
                .statusCode(400);
    }

    @Order(17)
    @Test
    public void getTransactionsWithInvalidType() {
        given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/accounts/1/transactions?type=CHECK_DEPOSIT")
                .then()
                .statusCode(400);
    }

    @Order(18)
    @Test
    public void saveTransactionTypeCashTransferenceInsufficientBalance() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", 2000000.00);
        jsonObject.put("type", "CASH_TRANSFERENCE");
        jsonObject.put("description", "teste descricao");
        jsonObject.put("destinationAccount", "147055");

        given()
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .when()
                .post(HOST + "/accounts/1/transactions")
                .then()
                .body("message", equalTo("Insufficient Balance in account"))
                .statusCode(400);
    }
}