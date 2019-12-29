package dto;

/**
 * @author a-vasin
 */
public class TransferRequest {
    public final int fromId;
    public final int toId;
    public final int amount;

    public TransferRequest(int fromId, int toId, int amount) {
        this.fromId = fromId;
        this.toId = toId;
        this.amount = amount;
    }
}
