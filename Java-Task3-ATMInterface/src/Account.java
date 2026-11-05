import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a bank account with encapsulated fields, balance management,
 * PIN validation, and an ArrayList of Transaction history.
 */
public class Account {
    private final String userId;
    private final String holderName;
    private String pin;
    private double balance;
    private final ArrayList<Transaction> transactionHistory;

    public Account(String userId, String holderName, String pin, double initialBalance) {
        this.userId = userId;
        this.holderName = holderName;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        if (initialBalance > 0) {
            this.transactionHistory.add(new Transaction(
                    "OPENING",
                    initialBalance,
                    this.balance,
                    "Initial account opening balance"
            ));
        }
    }

    public String getUserId() {
        return userId;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public boolean validatePin(String inputPin) {
        return this.pin != null && this.pin.equals(inputPin);
    }

    public void deposit(double amount, String description) {
        this.balance += amount;
        this.transactionHistory.add(new Transaction(
                "DEPOSIT",
                amount,
                this.balance,
                description
        ));
    }

    public boolean withdraw(double amount, String description) {
        if (amount > this.balance) {
            return false;
        }
        this.balance -= amount;
        this.transactionHistory.add(new Transaction(
                "WITHDRAW",
                amount,
                this.balance,
                description
        ));
        return true;
    }

    public void addCustomTransaction(String type, double amount, String description) {
        this.transactionHistory.add(new Transaction(
                type,
                amount,
                this.balance,
                description
        ));
    }

    public void adjustBalanceSilently(double delta) {
        this.balance += delta;
    }

    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }
}

