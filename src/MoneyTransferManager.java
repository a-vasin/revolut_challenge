import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author a-vasin
 */
public class MoneyTransferManager {

    private final ConcurrentHashMap<Integer, AtomicInteger> db;

    public MoneyTransferManager(ConcurrentHashMap<Integer, AtomicInteger> db) {
        this.db = db;
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

    public int deposit(int id, int amount) throws MissingAccountException {
        if (!db.containsKey(id)) {
            throw new MissingAccountException();
        }

        return db.get(id).getAndAdd(amount);
    }

    public int withdraw(int id, int amount) throws MissingAccountException, NotEnoughtMoneyException {
        AtomicInteger balance = getBalanceInner(id);

        if (balance.get() < amount) {
            throw new NotEnoughtMoneyException();
        }

        synchronized (balance) {
            if (balance.get() < amount) {
                throw new NotEnoughtMoneyException();
            }

            return balance.getAndAdd(-amount);
        }
    }

    public int transfer(int fromId, int toId, int amount) throws NotEnoughtMoneyException, MissingAccountException {
        if (!db.containsKey(toId)) {
            throw new MissingAccountException();
        }

        int newBalance = withdraw(fromId, amount);
        deposit(toId, amount);
        return newBalance;
    }

    public int getBalance(int id) throws MissingAccountException {
        return getBalanceInner(id).get();
    }

    private AtomicInteger getBalanceInner(int id) throws MissingAccountException {
        if (!db.containsKey(id)) {
            throw new MissingAccountException();
        }

        return db.get(id);
    }
}
