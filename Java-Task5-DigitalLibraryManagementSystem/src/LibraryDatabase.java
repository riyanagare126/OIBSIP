import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * JDBC + SQLite persistence layer for the Digital Library Management System (Task 5).
 * Handles Admin & User roles, Book Catalogue CRUD, Issue/Return with Rs. 5/day overdue fine calculation,
 * Advance Bookings, Member Management, and Contact Queries.
 */
public class LibraryDatabase {
    private static final String DB_URL = "jdbc:sqlite:library.db";
    public static final double FINE_PER_DAY = 5.0;

    public LibraryDatabase() {
        initSchemaAndSeed();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initSchemaAndSeed() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS users (" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "  username TEXT UNIQUE NOT NULL," +
                    "  password TEXT NOT NULL," +
                    "  full_name TEXT NOT NULL," +
                    "  email TEXT NOT NULL," +
                    "  role TEXT NOT NULL," +
                    "  created_at TEXT NOT NULL" +
                    ")"
            );

            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS books (" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "  title TEXT NOT NULL," +
                    "  author TEXT NOT NULL," +
                    "  isbn TEXT UNIQUE NOT NULL," +
                    "  category TEXT NOT NULL," +
                    "  total_qty INTEGER NOT NULL," +
                    "  available_qty INTEGER NOT NULL" +
                    ")"
            );

            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS issued_books (" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "  book_id INTEGER NOT NULL," +
                    "  user_id INTEGER NOT NULL," +
                    "  issue_date TEXT NOT NULL," +
                    "  due_date TEXT NOT NULL," +
                    "  return_date TEXT," +
                    "  status TEXT NOT NULL," +
                    "  fine_amount REAL DEFAULT 0.0," +
                    "  fine_paid INTEGER DEFAULT 0" +
                    ")"
            );

            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS advance_bookings (" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "  book_id INTEGER NOT NULL," +
                    "  user_id INTEGER NOT NULL," +
                    "  booking_date TEXT NOT NULL," +
                    "  status TEXT NOT NULL" +
                    ")"
            );

            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS queries (" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "  user_id INTEGER NOT NULL," +
                    "  username TEXT NOT NULL," +
                    "  subject TEXT NOT NULL," +
                    "  message TEXT NOT NULL," +
                    "  submitted_at TEXT NOT NULL" +
                    ")"
            );

            seedInitialData(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize Library SQLite database: " + e.getMessage(), e);
        }
    }

    private void seedInitialData(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String userSql = "INSERT INTO users (username, password, full_name, email, role, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(userSql)) {
            ps.setString(1, "admin");
            ps.setString(2, "admin123");
            ps.setString(3, "Chief Librarian (Admin)");
            ps.setString(4, "admin@oasislibrary.org");
            ps.setString(5, "ADMIN");
            ps.setString(6, now);
            ps.executeUpdate();

            ps.setString(1, "aarav");
            ps.setString(2, "user123");
            ps.setString(3, "Aarav Sharma");
            ps.setString(4, "aarav@student.edu");
            ps.setString(5, "USER");
            ps.setString(6, now);
            ps.executeUpdate();

            ps.setString(1, "priya");
            ps.setString(2, "user123");
            ps.setString(3, "Priya Nair");
            ps.setString(4, "priya@student.edu");
            ps.setString(5, "USER");
            ps.setString(6, now);
            ps.executeUpdate();
        }

        String bookSql = "INSERT INTO books (title, author, isbn, category, total_qty, available_qty) VALUES (?, ?, ?, ?, ?, ?)";
        Object[][] sampleBooks = {
                {"Effective Java (3rd Edition)", "Joshua Bloch", "978-0134685991", "Computer Science", 4, 3},
                {"Clean Code", "Robert C. Martin", "978-0132350884", "Computer Science", 3, 3},
                {"Design Patterns: GoF", "Erich Gamma et al.", "978-0201633610", "Computer Science", 1, 0},
                {"A Brief History of Time", "Stephen Hawking", "978-0553380163", "Science", 5, 5},
                {"The Pragmatic Programmer", "Andrew Hunt & David Thomas", "978-0135957059", "Computer Science", 2, 2},
                {"Sapiens: A Brief History of Humankind", "Yuval Noah Harari", "978-0062316097", "History", 3, 2},
                {"The Alchemist", "Paulo Coelho", "978-0062315007", "Fiction", 4, 4},
                {"Atomic Habits", "James Clear", "978-0735211292", "Self-Help", 3, 3}
        };
        try (PreparedStatement ps = conn.prepareStatement(bookSql)) {
            for (Object[] b : sampleBooks) {
                ps.setString(1, (String) b[0]);
                ps.setString(2, (String) b[1]);
                ps.setString(3, (String) b[2]);
                ps.setString(4, (String) b[3]);
                ps.setInt(5, (Integer) b[4]);
                ps.setInt(6, (Integer) b[5]);
                ps.executeUpdate();
            }
        }

        // Seed 1 overdue issued book (so fine calculation & payment can be demonstrated immediately)
        // and 1 active issued book that makes "Design Patterns: GoF" have available_qty = 0 for Advance Booking demo!
        String issueSql = "INSERT INTO issued_books (book_id, user_id, issue_date, due_date, return_date, status, fine_amount, fine_paid) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(issueSql)) {
            // Overdue book issued to aarav (user_id=2), due 6 days ago -> Rs. 30 fine
            ps.setInt(1, 1); // Effective Java
            ps.setInt(2, 2); // aarav
            ps.setString(3, LocalDate.now().minusDays(20).toString());
            ps.setString(4, LocalDate.now().minusDays(6).toString());
            ps.setString(5, null);
            ps.setString(6, "ISSUED");
            ps.setDouble(7, 30.0);
            ps.setInt(8, 0);
            ps.executeUpdate();

            // Design Patterns (book_id=3) issued to priya (user_id=3) -> available_qty is 0 so aarav can Advance Book it!
            ps.setInt(1, 3);
            ps.setInt(2, 3);
            ps.setString(3, LocalDate.now().minusDays(3).toString());
            ps.setString(4, LocalDate.now().plusDays(11).toString());
            ps.setString(5, null);
            ps.setString(6, "ISSUED");
            ps.setDouble(7, 0.0);
            ps.setInt(8, 0);
            ps.executeUpdate();

            // Sapiens (book_id=6) issued to aarav (user_id=2), not overdue yet
            ps.setInt(1, 6);
            ps.setInt(2, 2);
            ps.setString(3, LocalDate.now().minusDays(2).toString());
            ps.setString(4, LocalDate.now().plusDays(12).toString());
            ps.setString(5, null);
            ps.setString(6, "ISSUED");
            ps.setDouble(7, 0.0);
            ps.setInt(8, 0);
            ps.executeUpdate();
        }

        // Seed a sample user query
        String qSql = "INSERT INTO queries (user_id, username, subject, message, submitted_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(qSql)) {
            ps.setInt(1, 2);
            ps.setString(2, "aarav");
            ps.setString(3, "Request for Spring Microservices Book");
            ps.setString(4, "Could the library please add 'Spring Microservices in Action' to the Computer Science section?");
            ps.setString(5, now);
            ps.executeUpdate();
        }
    }

    /**
     * Dynamically recalculates overdue fines (Rs. 5 per day) for all currently ISSUED books.
     */
    public void refreshActiveFines() {
        String selectSql = "SELECT id, due_date FROM issued_books WHERE status = 'ISSUED' AND fine_paid = 0";
        String updateSql = "UPDATE issued_books SET fine_amount = ? WHERE id = ?";
        LocalDate today = LocalDate.now();
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(selectSql);
             PreparedStatement ups = conn.prepareStatement(updateSql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                LocalDate due = LocalDate.parse(rs.getString("due_date"));
                long overdueDays = ChronoUnit.DAYS.between(due, today);
                double fine = overdueDays > 0 ? overdueDays * FINE_PER_DAY : 0.0;
                ups.setDouble(1, fine);
                ups.setInt(2, id);
                ups.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

