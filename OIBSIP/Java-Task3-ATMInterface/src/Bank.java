import java.util.HashMap;
import java.util.Map;

/**
 * Manages a collection of accounts, user authentication, and inter-account transfers.
 */
public class Bank {
    private final String bankName;
    private final Map<String, Account> accounts;

    public Bank(String bankName) {
        this.bankName = bankName;
        this.accounts = new HashMap<>();
    }

    public String getBankName() {
        return bankName;
    }

    public void addAccount(Account account) {
        accounts.put(account.getUserId().toLowerCase(), account);
    }

    public Account findAccount(String userId) {
        if (userId == null) {
            return null;
        }
        return accounts.get(userId.trim().toLowerCase());
    }

    public Account authenticate(String userId, String pin) {
        Account account = findAccount(userId);
        if (account != null && account.validatePin(pin)) {
            return account;
        }
        return null;
    }

    /**
     * Transfers funds between sender and recipient accounts after validating balance.
     */
    public boolean transfer(Account sender, Account recipient, double amount) {
        if (sender == null || recipient == null || amount <= 0) {
            return false;
        }
        if (sender.getBalance() < amount) {
            return false;
        }

        sender.adjustBalanceSilently(-amount);
        sender.addCustomTransaction(
                "TRANSFER-OUT",
                amount,
                "Transferred to " + recipient.getHolderName() + " (" + recipient.getUserId() + ")"
        );

        recipient.adjustBalanceSilently(amount);
        recipient.addCustomTransaction(
                "TRANSFER-IN",
                amount,
                "Received from " + sender.getHolderName() + " (" + sender.getUserId() + ")"
        );
        return true;
    }
}

