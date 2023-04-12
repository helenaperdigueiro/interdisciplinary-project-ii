package com.digitalmoneyhouse.accountservice;

import com.digitalmoneyhouse.accountservice.dto.DepositResponse;
import com.digitalmoneyhouse.accountservice.dto.TransactionResponse;
import com.digitalmoneyhouse.accountservice.dto.TransferenceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountControllerTest {

    private final String HOST = "http://localhost:8081";
    private final String HOST_IAM = "http://localhost:8080";
    private String token;

    @BeforeEach
    @Test
    public void login() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", "bruno@mail.com");
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
        jsonObject.put("userId", "100"); //trocar
        jsonObject.put("userFullName", "Cicrano");

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
        jsonObject.put("number", "200"); // trocar
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
        jsonObject.put("number", "300"); // trocar
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
                .get(HOST + "/accounts/2/cards/1")
                .then()
                .body("id", equalTo(1))
                .body("number", equalTo("5553740017556110"))
                .body("holder", equalTo("Bruno Barbosa"))
                .body("expirationDate", equalTo("10/2030"))
                .body("cvc", equalTo("431"))
                .body("account.id", equalTo(2))
                .body("account.userId", equalTo(14))
                .body("account.accountNumber", equalTo("856189"))
                .body("account.userFullName", equalTo("Carolina Hakamada"))
                .body("account.walletBalance", equalTo(340.0F));
    }

    @Order(7)
    @Test
    public void getCardsByAccountId() {
        given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/accounts/1/cards")
                .then()
                .statusCode(200);
    }

    @Order(8)
    @Test
    public void getTransactionById() {
        given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/accounts/1/transactions/8")
                .then()
                .body("id", equalTo(8))
                .body("amount", equalTo(250000.0F))
                .body("date", equalTo("2023-03-21T00:45:11.778441"))
                .body("type", equalTo("CASH_DEPOSIT"))
                .body("transactionCode", equalTo("66B7A09D-5C14-4C81-BF64-26DB6947432A"))
                .body("description", equalTo("Dinheiro ilimitado"))
                .body("cardId", equalTo(2))
                .body("cardNumber", equalTo("4701 74 ** **** 6110"))
                .body("accountId", equalTo(1))
                .body("accountNumber", equalTo("123456"));
    }

    @Order(9)
    @Test
    public void getTransactionsWithLimit1() {
        given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/accounts/1/transactions?size=1")
                .then()
                .assertThat()
                .body("content.size()", equalTo(1));
    }

    @Order(10)
    @Test
    public void getTransactionsWithTypeCashDeposit() {
        given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/accounts/1/transactions?type=CASH_DEPOSIT")
                .then()
                .assertThat()
                .body("content.type", hasItem("CASH_DEPOSIT"));
    }

    @Order(11)
    @Test
    public void getTransactionsWithTypeCashTransference() {
        given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/accounts/1/transactions?type=CASH_TRANSFERENCE")
                .then()
                .assertThat()
                .body("content.type", hasItem("CASH_TRANSFERENCE"));
    }

    @Order(12)
    @Test
    public void getTransactionsWithLimit1AndTypeCashDeposit() {
        given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/accounts/1/transactions?size=1&type=CASH_DEPOSIT")
                .then()
                .assertThat()
                .body("content.size()", equalTo(1))
                .body("content.type", hasItem("CASH_DEPOSIT"));
    }

    @Order(13)
    @Test
    public void saveTransactionTypeCashDeposit() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", 10.00);
        jsonObject.put("type", "CASH_DEPOSIT");
        jsonObject.put("description", "teste descricao");
        jsonObject.put("cardId", 2);

        given()
                .header("Authorization", "Bearer " + token)
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
        jsonObject.put("destinationAccount", "856189");

        given()
                .header("Authorization", "Bearer " + token)
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
                .statusCode(400);
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
        jsonObject.put("destinationAccount", "856189");

        given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .when()
                .post(HOST + "/accounts/1/transactions")
                .then()
                .body("message", equalTo("Insufficient Balance in account"))
                .statusCode(400);
    }

    @Order(19)
    @Test
    public void getTransactionsByAccountIdAndFilteredByDateRange() throws JSONException {
        Response response = given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/accounts/1/transactions?startDate=2023-03-22&endDate=2023-03-23")
                .then()
                .contentType(ContentType.JSON)
                .extract()
                .response();

        List<Map<String, Object>> childObjectsMapList = response.jsonPath().getList("content");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        List<TransactionResponse> transactions = new ArrayList<>();

        for (Map<String, Object> childObjectMap : childObjectsMapList) {
            if (childObjectMap.containsKey("originAccountNumber")) {
                TransferenceResponse childObject = mapper.convertValue(childObjectMap, TransferenceResponse.class);
                transactions.add(childObject);
            } else if (childObjectMap.containsKey("cardId")) {
                DepositResponse childObject = mapper.convertValue(childObjectMap, DepositResponse.class);
                transactions.add(childObject);
            }
        }
        LocalDateTime startDate = LocalDateTime.parse("2023-03-22T00:00:00.000000", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime endDate = LocalDateTime.parse("2023-03-23T23:59:59.999999", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        for (TransactionResponse transaction : transactions) {
            assertTrue(transaction.getDate().isAfter(startDate) && transaction.getDate().isBefore(endDate));
        }
    }

    @Order(20)
    @Test
    public void getTransactionsByAccountIdAndFilteredByCategoryExpense() throws JSONException {
        Response response = given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/accounts/1/transactions?transactionCategory=EXPENSE")
                .then()
                .contentType(ContentType.JSON)
                .extract()
                .response();

        assertFalse(response.body().asString().contains("CASH_DEPOSIT"));
        assertEquals(200, response.statusCode());
    }

    @Order(21)
    @Test
    public void getTransactionsByAccountIdAndFilteredByCategoryRevenue() throws JSONException {
        given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/accounts/1/transactions?transactionCategory=REVENUE")
                .then()
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Order(22)
    @Test
    public void getTransactionsByAccountIdAndFilteredByAmountRange() throws JSONException {
        Response response = given()
                .header("Authorization", "Bearer " + token)
                .get(HOST + "/accounts/1/transactions?minimumAmount=10&maximumAmount=10000")
                .then()
                .contentType(ContentType.JSON)
                .extract()
                .response();

        List<Map<String, Object>> childObjectsMapList = response.jsonPath().getList("content");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        List<TransactionResponse> transactions = new ArrayList<>();

        for (Map<String, Object> childObjectMap : childObjectsMapList) {
            if (childObjectMap.containsKey("originAccountNumber")) {
                TransferenceResponse childObject = mapper.convertValue(childObjectMap, TransferenceResponse.class);
                transactions.add(childObject);
            } else if (childObjectMap.containsKey("cardId")) {
                DepositResponse childObject = mapper.convertValue(childObjectMap, DepositResponse.class);
                transactions.add(childObject);
            }
        }

        for (TransactionResponse transaction : transactions) {
            assertTrue(transaction.getAmount() >= 10 && transaction.getAmount() <= 10000);
        }
    }
}