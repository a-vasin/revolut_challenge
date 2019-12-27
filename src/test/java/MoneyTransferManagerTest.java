import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author a-vasin
 */
public class MoneyTransferManagerTest {

    private final int ID = 1;
    private final int TO_ID = 2;
    private final int DEPOSIT_AMOUNT = 100;
    private final int TRANSFER_AMOUNT = 10;

    private MoneyTransferManager transferManager;

    @BeforeEach
    public void initEach() {
        transferManager = new MoneyTransferManager();
    }

    @Test
    public void testAccountCreation() throws MissingAccountException {
        transferManager.createAccount(ID);
        int balance = transferManager.getBalance(ID);
        Assertions.assertEquals(0, balance);
    }

    @Test
    public void testDeposit() throws MissingAccountException {
        transferManager.createAccount(ID);

        int balance = transferManager.deposit(ID, DEPOSIT_AMOUNT);
        Assertions.assertEquals(DEPOSIT_AMOUNT, balance);

        balance = transferManager.getBalance(ID);
        Assertions.assertEquals(DEPOSIT_AMOUNT, balance);
    }

    // assume that DEPOSIT_AMOUNT is even
    @Test
    public void testWithdraw() throws NotEnoughMoneyException, MissingAccountException {
        transferManager.createAccount(ID);
        transferManager.deposit(ID, DEPOSIT_AMOUNT);

        int balance = transferManager.withdraw(ID, DEPOSIT_AMOUNT / 2);
        Assertions.assertEquals(DEPOSIT_AMOUNT / 2, balance);

        transferManager.withdraw(ID, DEPOSIT_AMOUNT / 2);
        balance = transferManager.getBalance(ID);
        Assertions.assertEquals(0, balance);
    }

    // assume TRANSFER_AMOUNT is even
    @Test
    public void testTransfer() throws MissingAccountException, NotEnoughMoneyException {
        transferManager.createAccount(ID);
        transferManager.deposit(ID, DEPOSIT_AMOUNT);

        transferManager.createAccount(TO_ID);
        int fromBalance = transferManager.transfer(ID, TO_ID, TRANSFER_AMOUNT);
        Assertions.assertEquals(DEPOSIT_AMOUNT - TRANSFER_AMOUNT, fromBalance);

        fromBalance = transferManager.getBalance(ID);
        Assertions.assertEquals(DEPOSIT_AMOUNT - TRANSFER_AMOUNT, fromBalance);

        int toBalance = transferManager.getBalance(TO_ID);
        Assertions.assertEquals(TRANSFER_AMOUNT, toBalance);

        // transfer half back
        toBalance = transferManager.transfer(TO_ID, ID, TRANSFER_AMOUNT / 2);
        Assertions.assertEquals(TRANSFER_AMOUNT / 2, toBalance);

        fromBalance = transferManager.getBalance(ID);
        Assertions.assertEquals(DEPOSIT_AMOUNT - TRANSFER_AMOUNT / 2, fromBalance);
    }
}
