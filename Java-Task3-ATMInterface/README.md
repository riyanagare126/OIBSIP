# ☕ OIBSIP — Java Development Task 3: ATM Interface

## 📌 Project Overview
A console-based simulation of an **Automated Teller Machine (ATM)** built using core Java and Object-Oriented Programming (OOP) principles. Users authenticate with a User ID and PIN (with a strict 3-attempt security lockout) and can perform standard banking operations including viewing transaction history, withdrawing cash, depositing cash, and transferring funds between accounts.

---

## 🛠️ Tech Stack & Architecture
- **Language:** Java (JDK 8+ / tested on OpenJDK 25 LTS)
- **Paradigm:** Object-Oriented Programming (Encapsulation, Composition, Collections)
- **Data Structures:** `ArrayList<Transaction>` for session transaction logs, `HashMap<String, Account>` for bank account lookup

### Five Core Classes (`src/`)
| Class | Responsibility |
| :--- | :--- |
| [`Main.java`](src/Main.java) | Entry point; initializes `Bank`, pre-populates demo accounts, and launches `ATM`. |
| [`ATM.java`](src/ATM.java) | Handles console UI, 3-attempt PIN authentication, and the 5-option interactive menu loop. |
| [`Bank.java`](src/Bank.java) | Manages registered `Account` objects, user authentication, and atomic inter-account transfers. |
| [`Account.java`](src/Account.java) | Encapsulates account state (`userId`, `holderName`, `pin`, `balance`) and `ArrayList<Transaction>`. |
| [`Transaction.java`](src/Transaction.java) | Immutable record of each transaction (`transactionId`, `timestamp`, `type`, `amount`, `balanceAfter`, `description`). |

---

## ✅ Feature Checklist Completed
- [x] **Startup Authentication:** Prompts for User ID and PIN; denies access after **3 incorrect attempts**
- [x] **Main Menu Options:**
  - `1. Transaction History` — displays formatted log of all transactions stored in an `ArrayList`
  - `2. Withdraw` — validates positive amount and sufficient balance (`"Insufficient Funds"` check), updates balance, logs transaction
  - `3. Deposit` — validates positive amount, updates balance, logs transaction
  - `4. Transfer` — verifies recipient account ID, validates sender balance, updates both sender & recipient balances and transaction logs
  - `5. Quit` — displays a personalized goodbye message and exits cleanly
- [x] **5 Distinct Java Classes:** `ATM`, `Account`, `Transaction`, `Bank`, `Main`

---

## 🚀 How to Compile & Run

### Option 1: One-Click Run (Windows)
Double-click `run.bat` or run from terminal:
```powershell
.\run.bat
```

### Option 2: Manual Command Line
```powershell
mkdir out
javac -d out src\*.java
java -cp out Main
```

---

## 🔑 Pre-Configured Demo Accounts
| User ID | PIN | Account Holder | Opening Balance |
| :--- | :--- | :--- | :--- |
| `user101` | `1234` | Aarav Sharma | Rs. 25,000.00 |
| `user202` | `5678` | Priya Nair | Rs. 15,000.00 |
| `user303` | `9999` | Rohan Verma | Rs. 50,000.00 |

---

## 📸 Sample Output
See [`sample_output.txt`](sample_output.txt) for a complete end-to-end terminal walkthrough demonstrating failed login attempt recovery, deposit, withdrawal, insufficient funds validation, inter-account transfer, and transaction history display.

