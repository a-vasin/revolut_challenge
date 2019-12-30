import dto.CreateAccountRequest;
import dto.DepositRequest;
import dto.TransferRequest;
import dto.WithdrawRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests expect MoneyTransferService to be running on localhost:4567
 * Also they expect service to be without data
 *
 * @author a-vasin
 */
public class MoneyTransferServiceTest {

    private static final String CREATE_ACCOUNT_PATH = "/create-account";
    private static final String DEPOSIT_PATH = "/deposit";
    private static final String WITHDRAW_PATH = "/withdraw";
    private static final String TRANSFER_PATH = "/transfer";
    private static final String GET_BALANCE_PATH = "/get-balance";

    private static final int DEPOSIT_AMOUNT = 100;
    private static final int WITHDRAW_AMOUNT = 10;

    private static int currentID = 1;

    static private int getID() {
        return currentID++;
    }

    @BeforeAll
    static public void setUp() {
        RestAssured.baseURI = "http://localhost:4567";
    }

    @Test
    public void testCreateAccount() {
        int id = getID();

        dto.Response response = createAccount(id);

        dto.Response expectedResponse = dto.Response.ACCOUNT_CREATED;
        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    public void testAccountExists() {
        int id = getID();

        createAccount(id);
        dto.Response response = createAccount(id);

        dto.Response expectedResponse = dto.Response.ACCOUNT_ALREADY_EXISTS;
        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    public void testDeposit() {
        int id = getID();

        createAccount(id);
        dto.Response response = deposit(id, DEPOSIT_AMOUNT);

        dto.Response expectedResponse = dto.Response.newBalance(DEPOSIT_AMOUNT);
        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    public void testMissingAccount() {
        int id = getID();

        dto.Response response = deposit(id, DEPOSIT_AMOUNT);

        dto.Response expectedResponse = dto.Response.MISSING_ACCOUNT;
        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    public void testIllegalAmount() {
        int id = getID();

        createAccount(id);
        dto.Response response = deposit(id, 0);

        dto.Response expectedResponse = dto.Response.ILLEGAL_AMOUNT;
        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    public void testWithdraw() {
        int id = getID();

        createAccount(id);
        deposit(id, DEPOSIT_AMOUNT);

        WithdrawRequest request = new WithdrawRequest(id, WITHDRAW_AMOUNT);
        dto.Response response = post(WITHDRAW_PATH, request);

        dto.Response expectedResponse = dto.Response.newBalance(DEPOSIT_AMOUNT - WITHDRAW_AMOUNT);
        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    public void testNotEnoughMoney() {
        int id = getID();

        createAccount(id);

        WithdrawRequest request = new WithdrawRequest(id, WITHDRAW_AMOUNT);
        dto.Response response = post(WITHDRAW_PATH, request);

        dto.Response expectedResponse = dto.Response.NOT_ENOUGH_MONEY;
        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    public void testTransfer() {
        int id = getID();
        int another_id = getID();

        createAccount(id);
        createAccount(another_id);
        deposit(id, DEPOSIT_AMOUNT);

        TransferRequest request = new TransferRequest(id, another_id, WITHDRAW_AMOUNT);
        dto.Response response = post(TRANSFER_PATH, request);

        dto.Response expectedResponse = dto.Response.newBalance(DEPOSIT_AMOUNT - WITHDRAW_AMOUNT);
        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    public void testSameAccount() {
        int id = getID();

        createAccount(id);
        deposit(id, DEPOSIT_AMOUNT);

        TransferRequest request = new TransferRequest(id, id, DEPOSIT_AMOUNT);
        dto.Response response = post(TRANSFER_PATH, request);

        dto.Response expectedResponse = dto.Response.SAME_ACCOUNT;
        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    public void testGetBalance() {
        int id = getID();

        createAccount(id);
        dto.Response responseDto = getBalance(id);

        dto.Response expectedResponseDto = dto.Response.currentBalance(0);
        Assertions.assertEquals(expectedResponseDto, responseDto);
    }

    @Test
    public void testUnknownError() {
        dto.Response responseDto = getBalance("abc");

        dto.Response expectedResponseDto = dto.Response.UNKNOWN_ERROR;
        Assertions.assertEquals(expectedResponseDto, responseDto);
    }

    private <T> dto.Response post(String path, T data) {
        String json = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Utils.toJson(data))
                .when()
                .post(path)
                .then()
                .statusCode(200)
                .extract()
                .asString();

        return Utils.fromJson(json, dto.Response.class);
    }

    private dto.Response createAccount(int id) {
        CreateAccountRequest request = new CreateAccountRequest(id);
        return post(CREATE_ACCOUNT_PATH, request);
    }

    private dto.Response deposit(int id, int amount) {
        DepositRequest request = new DepositRequest(id, amount);
        return post(DEPOSIT_PATH, request);
    }

    private <T> dto.Response getBalance(T id) {
        Response response = RestAssured.given()
                .param("id", id)
                .when()
                .get(GET_BALANCE_PATH)
                .then()
                .statusCode(200)
                .extract()
                .response();

        return Utils.fromJson(response.asString(), dto.Response.class);
    }
}
