import java.util.List;
import java.util.Scanner;

/**
 * Console-based ATM interface providing PIN authentication (max 3 attempts)
 * and standard banking operations (Transaction History, Withdraw, Deposit, Transfer, Quit).
 */
public class ATM {
    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private final Bank bank;
    private final Scanner scanner;
    private Account currentAccount;

    public ATM(Bank bank, Scanner scanner) {
        this.bank = bank;
        this.scanner = scanner;
    }

    public void start() {
        printBanner();
        if (!authenticateUser()) {
            System.out.println("\n[ACCESS DENIED] Maximum of " + MAX_LOGIN_ATTEMPTS
                    + " failed login attempts reached. Your session has been terminated for security.");
            return;
        }
        runMainMenu();
    }

    private void printBanner() {
        System.out.println("==========================================================================");
        System.out.println("                 WELCOME TO " + bank.getBankName().toUpperCase());
        System.out.println("                 OASIS INFOBYTE - JAVA DEVELOPMENT (TASK 3)");
        System.out.println("==========================================================================");
        System.out.println("  Demo Accounts for Testing:");
        System.out.println("    1) User ID: user101  | PIN: 1234  | Holder: Aarav Sharma (Rs. 25,000.00)");
        System.out.println("    2) User ID: user202  | PIN: 5678  | Holder: Priya Nair   (Rs. 15,000.00)");
        System.out.println("    3) User ID: user303  | PIN: 9999  | Holder: Rohan Verma  (Rs. 50,000.00)");
        System.out.println("==========================================================================");
    }

    private boolean authenticateUser() {
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            System.out.println("\n--- ATM Authentication (Attempt " + attempt + " of " + MAX_LOGIN_ATTEMPTS + ") ---");
            System.out.print("Enter User ID : ");
            String userId = scanner.nextLine().trim();

            System.out.print("Enter PIN     : ");
            String pin = scanner.nextLine().trim();

            Account authenticated = bank.authenticate(userId, pin);
            if (authenticated != null) {
                this.currentAccount = authenticated;
                System.out.println("\n[LOGIN SUCCESSFUL] Welcome, " + currentAccount.getHolderName()
                        + " (" + currentAccount.getUserId() + ")!");
                return true;
            } else {
                int remaining = MAX_LOGIN_ATTEMPTS - attempt;
                if (remaining > 0) {
                    System.out.println("[ERROR] Invalid User ID or PIN. Attempts remaining: " + remaining);
                }
            }
        }
        return false;
    }

    private void runMainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--------------------------------------------------------------------------");
            System.out.printf("  Account: %s (%s)  |  Available Balance: Rs. %.2f%n",
                    currentAccount.getHolderName(),
                    currentAccount.getUserId(),
                    currentAccount.getBalance());
            System.out.println("--------------------------------------------------------------------------");
            System.out.println("  1. Transaction History");
            System.out.println("  2. Withdraw");
            System.out.println("  3. Deposit");
            System.out.println("  4. Transfer");
            System.out.println("  5. Quit");
            System.out.println("--------------------------------------------------------------------------");
            System.out.print("Select an option (1-5): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    showTransactionHistory();
                    break;
                case "2":
                    handleWithdraw();
                    break;
                case "3":
                    handleDeposit();
                    break;
                case "4":
                    handleTransfer();
                    break;
                case "5":
                    System.out.println("\n==========================================================================");
                    System.out.println("  Thank you for banking with " + bank.getBankName() + ", "
                            + currentAccount.getHolderName() + "!");
                    System.out.println("  Please remember to collect your card. Goodbye!");
                    System.out.println("==========================================================================");
                    running = false;
                    break;
                default:
                    System.out.println("[WARNING] Invalid menu option. Please enter a number between 1 and 5.");
            }
        }
    }

    private void showTransactionHistory() {
        System.out.println("\n========================== TRANSACTION HISTORY ===========================");
        List<Transaction> history = currentAccount.getTransactionHistory();
        if (history.isEmpty()) {
            System.out.println("  No transactions recorded in this session yet.");
        } else {
            System.out.printf("%-10s | %-19s | %-12s | %-14s | %-18s | %s%n",
                    "TXN ID", "DATE & TIME", "TYPE", "AMOUNT", "BALANCE AFTER", "DESCRIPTION");
            System.out.println("----------------------------------------------------------------------------------------------------");
            for (Transaction txn : history) {
                System.out.println(txn);
            }
        }
        System.out.println("==========================================================================");
    }

    private void handleWithdraw() {
        System.out.println("\n--- Cash Withdrawal ---");
        double amount = promptPositiveAmount("Enter amount to withdraw (Rs.): ");
        if (amount <= 0) {
            return;
        }

        if (amount > currentAccount.getBalance()) {
            System.out.printf("[ERROR] Insufficient Funds! Requested: Rs. %.2f | Available Balance: Rs. %.2f%n",
                    amount, currentAccount.getBalance());
            return;
        }

        boolean success = currentAccount.withdraw(amount, "ATM Cash Withdrawal");
        if (success) {
            System.out.printf("[SUCCESS] Rs. %.2f withdrawn successfully. Updated Balance: Rs. %.2f%n",
                    amount, currentAccount.getBalance());
        } else {
            System.out.println("[ERROR] Insufficient Funds!");
        }
    }

    private void handleDeposit() {
        System.out.println("\n--- Cash Deposit ---");
        double amount = promptPositiveAmount("Enter amount to deposit (Rs.): ");
        if (amount <= 0) {
            return;
        }

        currentAccount.deposit(amount, "ATM Cash Deposit");
        System.out.printf("[SUCCESS] Rs. %.2f deposited successfully. Updated Balance: Rs. %.2f%n",
                amount, currentAccount.getBalance());
    }

    private void handleTransfer() {
        System.out.println("\n--- Fund Transfer ---");
        System.out.print("Enter Recipient Account User ID (e.g., user202): ");
        String recipientId = scanner.nextLine().trim();

        if (recipientId.isEmpty()) {
            System.out.println("[ERROR] Recipient User ID cannot be empty.");
            return;
        }

        if (recipientId.equalsIgnoreCase(currentAccount.getUserId())) {
            System.out.println("[ERROR] Cannot transfer funds to the same account.");
            return;
        }

        Account recipient = bank.findAccount(recipientId);
        if (recipient == null) {
            System.out.println("[ERROR] Recipient account '" + recipientId + "' not found.");
            return;
        }

        System.out.println("Recipient Verified: " + recipient.getHolderName() + " (" + recipient.getUserId() + ")");
        double amount = promptPositiveAmount("Enter amount to transfer (Rs.): ");
        if (amount <= 0) {
            return;
        }

        if (amount > currentAccount.getBalance()) {
            System.out.printf("[ERROR] Insufficient Funds! Requested: Rs. %.2f | Available Balance: Rs. %.2f%n",
                    amount, currentAccount.getBalance());
            return;
        }

        boolean transferred = bank.transfer(currentAccount, recipient, amount);
        if (transferred) {
            System.out.printf("[SUCCESS] Rs. %.2f transferred to %s (%s). Updated Balance: Rs. %.2f%n",
                    amount, recipient.getHolderName(), recipient.getUserId(), currentAccount.getBalance());
        } else {
            System.out.println("[ERROR] Insufficient Funds!");
        }
    }

    private double promptPositiveAmount(String prompt) {
        System.out.print(prompt);
        String raw = scanner.nextLine().trim();
        try {
            double value = Double.parseDouble(raw);
            if (value <= 0) {
                System.out.println("[ERROR] Amount must be greater than zero.");
                return -1;
            }
            return Math.round(value * 100.0) / 100.0;
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid numeric amount entered.");
            return -1;
        }
    }
}

