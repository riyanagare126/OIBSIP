import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single banking transaction performed at the ATM.
 */
public class Transaction {
    private static int counter = 1000;

    private final String transactionId;
    private final String type;
    private final double amount;
    private final double balanceAfter;
    private final String description;
    private final LocalDateTime timestamp;

    public Transaction(String type, double amount, double balanceAfter, String description) {
        this.transactionId = "TXN-" + (++counter);
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("%-10s | %-19s | %-12s | Rs. %10.2f | Bal: Rs. %10.2f | %s",
                transactionId,
                timestamp.format(formatter),
                type,
                amount,
                balanceAfter,
                description);
    }
}

