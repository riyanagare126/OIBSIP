# ☕ OIBSIP — Java Development Task 5: Digital Library Management System

## 📌 Project Overview
A full-stack, web-based **Digital Library Management System** built in **Java** with a role-based **Admin Module** and **User Module**, backed by **SQLite (JDBC)** and an interactive **HTML5/CSS3/JS** web portal served at `http://localhost:8085`.

---

## 🛠️ Tech Stack & Architecture
- **Backend:** Java (`com.sun.net.httpserver.HttpServer` REST/Form Controller + `PreparedStatement` JDBC DAO)
- **Database:** SQLite (`library.db` via `sqlite-jdbc`) with 5 relational tables (`users`, `books`, `issued_books`, `advance_bookings`, `queries`)
- **Frontend:** Responsive HTML5, CSS3, and Vanilla JavaScript (`web/index.html`)

### Source Files
| File | Responsibility |
| :--- | :--- |
| [`src/LibraryWebServer.java`](src/LibraryWebServer.java) | Embedded Java HTTP Web Server on port `8085` handling authentication, book CRUD, issue/return, ₹5/day fine calculation, advance bookings, and contact queries. |
| [`src/LibraryDatabase.java`](src/LibraryDatabase.java) | SQLite JDBC schema initialization, automatic overdue fine calculator (`₹5 per overdue day`), and demo data seeding. |
| [`web/index.html`](web/index.html) | Role-based Admin & User web interface with category filtering, live search, modal book editor, and fine management. |

---

## ✅ Feature Checklist Completed

### Admin Module
- [x] **Admin Login:** Full access to all catalogue, member, loan, fine, and query management features
- [x] **Add New Book:** Title, Author, ISBN, Category, and Total Quantity
- [x] **Edit or Delete Existing Book Records:** Modal editor for updating book metadata/stock and 1-click delete
- [x] **View All Issued Books & Due Dates:** Real-time loan ledger showing issue dates, due dates, return dates, and overdue status
- [x] **View & Manage Registered Member Accounts:** Inspect member details and remove non-admin accounts
- [x] **Fine Management:** View overdue fines and click **"Mark Fine as Paid"**
- [x] **Query Inbox:** View support/contact messages submitted by users

### User Module
- [x] **User Registration & Login:** Self-service member sign-up and login
- [x] **Browse Book Catalogue by Category:** Interactive category filter pills (`All`, `Computer Science`, `Science`, `History`, `Fiction`, `Self-Help`)
- [x] **Search by Title or Author:** Real-time search bar filtering catalogue rows
- [x] **Issue a Book:** Decrements available quantity and records a 14-day due date (includes a Demo Overdue toggle to immediately test overdue fine generation)
- [x] **Return a Book:** Increments available quantity and updates any pending advance bookings
- [x] **Automatic Fine Generation:** Automatically calculates **₹5 per overdue day** (`ChronoUnit.DAYS.between(dueDate, returnDate) * 5.0`)
- [x] **Advance Booking:** Reserve a book whose available quantity is `0` (currently issued to another member)
- [x] **Contact / Query Form:** Stores member inquiries in SQLite for Admin review

---

## 🚀 How to Compile & Run

### Option 1: One-Click Run (Windows)
Double-click `run.bat` or run from terminal:
```powershell
.\run.bat
```
Your default browser will automatically open `http://localhost:8085`.

### Option 2: Manual Command Line
```powershell
mkdir out
javac -cp "lib/*" -d out src/*.java
java --enable-native-access=ALL-UNNAMED -cp "out;lib/*" LibraryWebServer
```

---

## 🔑 Pre-Configured Demo Credentials
| Role | Username | Password | Pre-Loaded Scenario |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` | Full catalogue CRUD, member management, fine settlement, and query inbox |
| **User 1** | `aarav` | `user123` | Has 1 overdue book (*Effective Java*, 6 days overdue = **₹30.00 fine**) and can place an **Advance Booking** on *Design Patterns: GoF* (`0 available`) |
| **User 2** | `priya` | `user123` | Currently holds the only copy of *Design Patterns: GoF* |

