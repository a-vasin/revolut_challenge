package dto;

/**
 * @author a-vasin
 */
public class DepositRequest {
    public final int id;
    public final int amount;

    public DepositRequest(int id, int amount) {
        this.id = id;
        this.amount = amount;
    }
}
