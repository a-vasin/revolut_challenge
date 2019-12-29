package dto;

/**
 * @author a-vasin
 */
public class WithdrawRequest extends DepositRequest {
    public WithdrawRequest(int id, int amount) {
        super(id, amount);
    }
}
