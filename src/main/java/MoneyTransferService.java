import java.util.function.Supplier;

import com.google.gson.Gson;
import dto.CreateAccountRequest;
import dto.DepositRequest;
import dto.Response;
import dto.TransferRequest;
import dto.WithdrawRequest;
import exception.MissingAccountException;
import exception.NotEnoughMoneyException;
import spark.Spark;

/**
 * @author a-vasin
 */
public class MoneyTransferService {

    private static final MoneyTransferManager moneyTransferManager = new MoneyTransferManager();

    public static void main(String[] args) {
        Spark.post("/create-account", "application/json", (req, res) -> {
            res.type("application/json");

            Response response = getResponse(() -> {
                CreateAccountRequest request = fromJson(req.body(), CreateAccountRequest.class);

                return moneyTransferManager.createAccount(request.id)
                        ? Response.ACCOUNT_CREATED
                        : Response.ACCOUNT_ALREADY_EXISTS;
            });

            return toJson(response);
        });

        Spark.post("/deposit", "application/json", (req, res) -> {
            res.type("application/json");

            Response response = getResponse(() -> {
                DepositRequest request = fromJson(req.body(), DepositRequest.class);
                int newBalance = moneyTransferManager.deposit(request.id, request.amount);
                return Response.newBalance(newBalance);
            });

            return toJson(response);
        });

        Spark.post("/withdraw", "application/json", (req, res) -> {
            res.type("application/json");

            Response response = getResponse(() -> {
                WithdrawRequest request = fromJson(req.body(), WithdrawRequest.class);
                int newBalance = moneyTransferManager.withdraw(request.id, request.amount);
                return Response.newBalance(newBalance);
            });

            return toJson(response);
        });

        Spark.post("/transfer", "application/json", (req, res) -> {
            res.type("application/json");

            Response response = getResponse(() -> {
                TransferRequest request = fromJson(req.body(), TransferRequest.class);
                int newBalance = moneyTransferManager.transfer(request.fromId, request.toId, request.amount);
                return Response.newBalance(newBalance);
            });

            return toJson(response);
        });

        Spark.get("/get-balance", (req, res) -> {
            res.type("application/json");

            Response response = getResponse(() -> {
                int id = Integer.parseInt(req.queryParams("id"));
                int balance = moneyTransferManager.getBalance(id);
                return Response.currentBalance(balance);
            });

            return toJson(response);
        });
    }

    private static Response getResponse(Supplier<Response> supplier) {
        try {
            return supplier.get();
        } catch (MissingAccountException e) {
            return Response.MISSING_ACCOUNT;
        } catch (NotEnoughMoneyException e) {
            return Response.NOT_ENOUGH_MONEY;
        } catch (Exception e) {
            return Response.unknownErrorOccurred(e);
        }
    }

    private static <T> T fromJson(String json, Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
    }

    private static <T> String toJson(T object) {
        return new Gson().toJson(object);
    }
}
