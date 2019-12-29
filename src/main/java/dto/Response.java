package dto;

/**
 * @author a-vasin
 */
public class Response {
    public final ResponseStatus status;
    public final String message;

    public static final Response MISSING_ACCOUNT = new Response(
            ResponseStatus.ERROR,
            "Provided account ID does not exist"
    );

    public static final Response NOT_ENOUGH_MONEY = new Response(
            ResponseStatus.ERROR,
            "Not enough money"
    );

    public static final Response ACCOUNT_CREATED = new Response(
            ResponseStatus.SUCCESS,
            "Account was successfully created"
    );

    public static final Response ACCOUNT_ALREADY_EXISTS = new Response(
            ResponseStatus.SUCCESS,
            "Account already exists"
    );

    public static Response success(String message) {
        return new Response(
                ResponseStatus.SUCCESS,
                message
        );
    }

    public static Response unknownErrorOccurred(Exception e) {
        return new Response(
                ResponseStatus.ERROR,
                "Unknown error occurred: " + e.getMessage()
        );
    }

    public static Response newBalance(int balance) {
        return new Response(
                ResponseStatus.SUCCESS,
                "New balance: " + balance
        );
    }

    public static Response currentBalance(int balance) {
        return new Response(
                ResponseStatus.SUCCESS,
                "Current balance: " + balance
        );
    }

    private Response(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
