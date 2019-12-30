import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import exception.IllegalAmountException;
import exception.MissingAccountException;
import exception.NotEnoughMoneyException;
import exception.SameAccountException;

/**
 * @author a-vasin
 */
public class MoneyTransferManager {

    private final ConcurrentHashMap<Integer, AtomicInteger> db;

    public MoneyTransferManager() {
        this.db = new ConcurrentHashMap<>();
    }

    public boolean createAccount(int id) {
        if (db.containsKey(id)) {
            return false;
        }

        synchronized (db) {
            if (db.containsKey(id)) {
                return false;
            }

            db.put(id, new AtomicInteger());
        }

        return true;
    }

    public int deposit(int id, int amount) {
        checkAmount(amount);

        if (!db.containsKey(id)) {
            throw new MissingAccountException();
        }

        return db.get(id).addAndGet(amount);
    }

    public int withdraw(int id, int amount) {
        checkAmount(amount);

        AtomicInteger balance = getBalanceInner(id);

        if (balance.get() < amount) {
            throw new NotEnoughMoneyException();
        }

        synchronized (balance) {
            if (balance.get() < amount) {
                throw new NotEnoughMoneyException();
            }

            return balance.addAndGet(-amount);
        }
    }

    public int transfer(int fromId, int toId, int amount) {
        checkAmount(amount);

        if (fromId == toId) {
            throw new SameAccountException();
        }

        if (!db.containsKey(toId)) {
            throw new MissingAccountException();
        }

        int newBalance = withdraw(fromId, amount);
        deposit(toId, amount);
        return newBalance;
    }

    public int getBalance(int id) {
        return getBalanceInner(id).get();
    }

    private AtomicInteger getBalanceInner(int id) {
        if (!db.containsKey(id)) {
            throw new MissingAccountException();
        }

        return db.get(id);
    }

    private void checkAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalAmountException();
        }
    }
}
