import java.util.function.Supplier;

import dto.CreateAccountRequest;
import dto.DepositRequest;
import dto.Response;
import dto.TransferRequest;
import dto.WithdrawRequest;
import exception.IllegalAmountException;
import exception.MissingAccountException;
import exception.NotEnoughMoneyException;
import exception.SameAccountException;
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
                CreateAccountRequest request = Utils.fromJson(req.body(), CreateAccountRequest.class);

                return moneyTransferManager.createAccount(request.id)
                        ? Response.ACCOUNT_CREATED
                        : Response.ACCOUNT_ALREADY_EXISTS;
            });

            return Utils.toJson(response);
        });

        Spark.post("/deposit", "application/json", (req, res) -> {
            res.type("application/json");

            Response response = getResponse(() -> {
                DepositRequest request = Utils.fromJson(req.body(), DepositRequest.class);
                int newBalance = moneyTransferManager.deposit(request.id, request.amount);
                return Response.newBalance(newBalance);
            });

            return Utils.toJson(response);
        });

        Spark.post("/withdraw", "application/json", (req, res) -> {
            res.type("application/json");

            Response response = getResponse(() -> {
                WithdrawRequest request = Utils.fromJson(req.body(), WithdrawRequest.class);
                int newBalance = moneyTransferManager.withdraw(request.id, request.amount);
                return Response.newBalance(newBalance);
            });

            return Utils.toJson(response);
        });

        Spark.post("/transfer", "application/json", (req, res) -> {
            res.type("application/json");

            Response response = getResponse(() -> {
                TransferRequest request = Utils.fromJson(req.body(), TransferRequest.class);
                int newBalance = moneyTransferManager.transfer(request.fromId, request.toId, request.amount);
                return Response.newBalance(newBalance);
            });

            return Utils.toJson(response);
        });

        Spark.get("/get-balance", (req, res) -> {
            res.type("application/json");

            Response response = getResponse(() -> {
                int id = Integer.parseInt(req.queryParams("id"));
                int balance = moneyTransferManager.getBalance(id);
                return Response.currentBalance(balance);
            });

            return Utils.toJson(response);
        });
    }

    private static Response getResponse(Supplier<Response> supplier) {
        try {
            return supplier.get();
        } catch (SameAccountException e) {
            return Response.SAME_ACCOUNT;
        } catch (IllegalAmountException e) {
            return Response.ILLEGAL_AMOUNT;
        } catch (MissingAccountException e) {
            return Response.MISSING_ACCOUNT;
        } catch (NotEnoughMoneyException e) {
            return Response.NOT_ENOUGH_MONEY;
        } catch (Exception e) {
            return Response.UNKNOWN_ERROR;
        }
    }
}
