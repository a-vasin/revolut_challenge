import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author a-vasin
 */
public class MoneyTransferManagerTest {

    private final int ID = 1;
    private final int ANOTHER_ID = 2;
    private final int DEPOSIT_AMOUNT = 100;
    private final int TRANSFER_AMOUNT = 10;
    private final int OPERATIONS_COUNT = 10_000_000;

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

        transferManager.createAccount(ANOTHER_ID);
        int fromBalance = transferManager.transfer(ID, ANOTHER_ID, TRANSFER_AMOUNT);
        Assertions.assertEquals(DEPOSIT_AMOUNT - TRANSFER_AMOUNT, fromBalance);

        fromBalance = transferManager.getBalance(ID);
        Assertions.assertEquals(DEPOSIT_AMOUNT - TRANSFER_AMOUNT, fromBalance);

        int toBalance = transferManager.getBalance(ANOTHER_ID);
        Assertions.assertEquals(TRANSFER_AMOUNT, toBalance);

        // transfer half back
        toBalance = transferManager.transfer(ANOTHER_ID, ID, TRANSFER_AMOUNT / 2);
        Assertions.assertEquals(TRANSFER_AMOUNT / 2, toBalance);

        fromBalance = transferManager.getBalance(ID);
        Assertions.assertEquals(DEPOSIT_AMOUNT - TRANSFER_AMOUNT / 2, fromBalance);
    }

    @Test
    public void testDoubleCreation() throws MissingAccountException {
        boolean created = transferManager.createAccount(ID);
        Assertions.assertTrue(created);

        transferManager.deposit(ID, DEPOSIT_AMOUNT);

        created = transferManager.createAccount(ID);
        Assertions.assertFalse(created);

        int balance = transferManager.getBalance(ID);
        Assertions.assertEquals(DEPOSIT_AMOUNT, balance);
    }

    @Test
    public void testDepositMissingAccount() {
        Assertions.assertThrows(
                MissingAccountException.class,
                () -> transferManager.deposit(ID, DEPOSIT_AMOUNT)
        );
    }

    @Test
    public void testWithdrawMissingAccount() {
        Assertions.assertThrows(
                MissingAccountException.class,
                () -> transferManager.withdraw(ID, DEPOSIT_AMOUNT)
        );
    }

    @Test
    public void testTransferMissingAccountFrom() {
        Assertions.assertThrows(
                MissingAccountException.class,
                () -> transferManager.transfer(ID, ANOTHER_ID, DEPOSIT_AMOUNT)
        );
    }

    @Test
    public void testTransferMissingAccountTo() {
        transferManager.createAccount(ID);

        Assertions.assertThrows(
                MissingAccountException.class,
                () -> transferManager.transfer(ID, ANOTHER_ID, DEPOSIT_AMOUNT)
        );
    }

    @Test
    public void testWithdrawNotEnoughMoney() {
        transferManager.createAccount(ID);
        Assertions.assertThrows(
                NotEnoughMoneyException.class,
                () -> transferManager.withdraw(ID, DEPOSIT_AMOUNT)
        );
    }

    @Test
    public void testTransferNotEnoughMoney() {
        transferManager.createAccount(ID);
        transferManager.createAccount(ANOTHER_ID);

        Assertions.assertThrows(
                NotEnoughMoneyException.class,
                () -> transferManager.transfer(ID, ANOTHER_ID, DEPOSIT_AMOUNT)
        );
    }

    @Test
    public void testAsyncActions() throws MissingAccountException, InterruptedException {
        transferManager.createAccount(ID);
        transferManager.createAccount(ANOTHER_ID);

        transferManager.deposit(ID, OPERATIONS_COUNT);
        transferManager.deposit(ANOTHER_ID, OPERATIONS_COUNT);

        Thread depositThread = new Thread(wrapException(() -> {
            for (int i = 0; i < OPERATIONS_COUNT; ++i) {
                transferManager.deposit(ID, 1);
            }
        }));

        Thread withdrawThread = new Thread(wrapException(() -> {
            for (int i = 0; i < OPERATIONS_COUNT; ++i) {
                transferManager.withdraw(ID, 1);
            }
        }));

        Thread transferThread = new Thread(wrapException(() -> {
            for (int i = 0; i < OPERATIONS_COUNT; ++i) {
                transferManager.transfer(ANOTHER_ID, ID, 1);
            }
        }));

        depositThread.start();
        withdrawThread.start();
        transferThread.start();

        depositThread.join();
        withdrawThread.join();
        transferThread.join();

        int balance = transferManager.getBalance(ID);
        Assertions.assertEquals(2 * OPERATIONS_COUNT, balance);
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static Runnable wrapException(ThrowingRunnable throwingRunnable) {
        return () -> {
            try {
                throwingRunnable.run();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }
}
