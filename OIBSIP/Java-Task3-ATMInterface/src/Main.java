import java.util.Scanner;

/**
 * Entry point for the Oasis Infobyte Java Development Task 3: ATM Interface.
 */
public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank("Oasis National Bank");

        // Pre-populate sample accounts for instant evaluation & transfer testing
        bank.addAccount(new Account("user101", "Aarav Sharma", "1234", 25000.00));
        bank.addAccount(new Account("user202", "Priya Nair", "5678", 15000.00));
        bank.addAccount(new Account("user303", "Rohan Verma", "9999", 50000.00));

        try (Scanner scanner = new Scanner(System.in)) {
            ATM atm = new ATM(bank, scanner);
            atm.start();
        }
    }
}

