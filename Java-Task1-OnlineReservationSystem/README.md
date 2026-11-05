# ☕ OIBSIP — Java Development Task 1: Online Reservation System

## 📌 Project Overview
A desktop **Online Train Reservation & PNR Cancellation System** built using **Java Swing (GUI)**, **JDBC**, and **SQLite**. Registered users log in through a secure authentication screen, book train tickets with automatic train name lookup and unique PNR generation, and cancel existing reservations by fetching full ticket details via their PNR number.

---

## 🛠️ Tech Stack & Architecture
- **GUI Framework:** Java Swing (`JFrame`, `CardLayout`, `JTabbedPane`, `JTable`, `DocumentListener`)
- **Database:** SQLite (`reservations.db`) via JDBC (`sqlite-jdbc`)
- **Security:** All SQL queries use `PreparedStatement` to prevent SQL injection

### Source Files (`src/`)
| File | Responsibility |
| :--- | :--- |
| [`OnlineReservationApp.java`](src/OnlineReservationApp.java) | Main Swing GUI application with Login Form, Reservation Form, PNR Cancellation Form, and Active Bookings table. |
| [`DatabaseManager.java`](src/DatabaseManager.java) | JDBC + SQLite persistence layer (`users` and `reservations` tables, unique PNR generator, CRUD operations). |
| [`TrainDirectory.java`](src/TrainDirectory.java) | Maps numeric Train Numbers (`12951`, `12002`, `22436`, etc.) to official Train Names for real-time auto-population. |
| [`ReservationRecord.java`](src/ReservationRecord.java) | Data model representing a booked train ticket and its PNR summary. |

---

## ✅ Feature Checklist Completed
- [x] **Login Form:** Username + Password authentication; denies access with an alert dialog on invalid credentials
- [x] **Reservation Form:**
  - Passenger Name, Train Number (numeric), Train Name (**auto-populated from Train Number**), Class Type, Date of Journey (`YYYY-MM-DD`), Source Station, Destination Station
- [x] **Insert / Book Button:** Saves reservation to SQLite database and generates an auto-generated unique **PNR Number**
- [x] **Confirmation Dialog:** Displays full booking details and PNR immediately after booking
- [x] **Cancellation Form:** PNR input field + **Fetch Booking Details** button that retrieves and displays the complete reservation record
- [x] **Confirm Cancellation Button:** Prompts with an *"Are you sure?"* confirmation dialog and removes the booking from SQLite upon confirmation
- [x] **Input Validation:** Rejects empty required fields, non-numeric train numbers, invalid `YYYY-MM-DD` dates, and identical source/destination stations

---

## 🚀 How to Compile & Run

### Option 1: One-Click Run (Windows)
Double-click `run.bat` or run from PowerShell/CMD:
```powershell
.\run.bat
```

### Option 2: Manual Command Line
```powershell
mkdir out
javac -cp "lib/*" -d out src/*.java
java --enable-native-access=ALL-UNNAMED -cp "out;lib/*" OnlineReservationApp
```

---

## 🔑 Demo Credentials & Pre-Loaded Data
- **Login Accounts:**
  - Username: `user1` | Password: `pass123`
  - Username: `admin` | Password: `admin123`
- **Sample PNR for Immediate Lookup:** `PNR1002026`
- **Sample Train Numbers:** `12951` (Mumbai Rajdhani Express), `12002` (Bhopal Shatabdi), `12301` (Howrah Rajdhani), `22436` (Vande Bharat Express), `12627` (Karnataka Express)

---

## 📸 Screenshots
- [`screenshots/1_login_screen.png`](screenshots/1_login_screen.png) — Login Screen
- [`screenshots/2_reservation_form.png`](screenshots/2_reservation_form.png) — Reservation Form & PNR Receipt
- [`screenshots/3_cancellation_form.png`](screenshots/3_cancellation_form.png) — PNR Lookup & Cancellation Form

