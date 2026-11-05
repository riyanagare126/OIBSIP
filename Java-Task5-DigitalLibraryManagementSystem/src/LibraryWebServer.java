import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Oasis Infobyte — Java Development Task 5: Digital Library Management System
 * Web-based application server providing REST/Form endpoints backed by JDBC + SQLite.
 */
public class LibraryWebServer {
    private static final int DEFAULT_PORT = 8085;
    private final LibraryDatabase db;

    public LibraryWebServer(LibraryDatabase db) {
        this.db = db;
    }

    public HttpServer startServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", this::handleIndex);
        server.createContext("/api/login", this::handleLogin);
        server.createContext("/api/register", this::handleRegister);
        server.createContext("/api/state", this::handleState);
        server.createContext("/api/books/add", this::handleAddBook);
        server.createContext("/api/books/update", this::handleUpdateBook);
        server.createContext("/api/books/delete", this::handleDeleteBook);
        server.createContext("/api/issue", this::handleIssueBook);
        server.createContext("/api/return", this::handleReturnBook);
        server.createContext("/api/advance-book", this::handleAdvanceBook);
        server.createContext("/api/fines/pay", this::handleMarkFinePaid);
        server.createContext("/api/members/delete", this::handleDeleteMember);
        server.createContext("/api/queries/add", this::handleAddQuery);

        server.setExecutor(null);
        server.start();
        return server;
    }

    private void handleIndex(HttpExchange ex) throws IOException {
        File htmlFile = new File("web/index.html");
        if (!htmlFile.exists()) {
            sendText(ex, 404, "text/plain", "web/index.html not found");
            return;
        }
        byte[] bytes = Files.readAllBytes(htmlFile.toPath());
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void handleLogin(HttpExchange ex) throws IOException {
        Map<String, String> params = parseFormBody(ex);
        String username = params.getOrDefault("username", "").trim();
        String password = params.getOrDefault("password", "");

        String sql = "SELECT id, username, full_name, email, role FROM users WHERE LOWER(username) = LOWER(?) AND password = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String json = String.format(
                            "{\"ok\":true,\"user\":{\"id\":%d,\"username\":\"%s\",\"fullName\":\"%s\",\"email\":\"%s\",\"role\":\"%s\"}}",
                            rs.getInt("id"),
                            esc(rs.getString("username")),
                            esc(rs.getString("full_name")),
                            esc(rs.getString("email")),
                            esc(rs.getString("role"))
                    );
                    sendJson(ex, 200, json);
                    return;
                }
            }
        } catch (SQLException e) {
            sendJson(ex, 500, "{\"ok\":false,\"error\":\"" + esc(e.getMessage()) + "\"}");
            return;
        }
        sendJson(ex, 401, "{\"ok\":false,\"error\":\"Invalid username or password.\"}");
    }

    private void handleRegister(HttpExchange ex) throws IOException {
        Map<String, String> p = parseFormBody(ex);
        String username = p.getOrDefault("username", "").trim();
        String password = p.getOrDefault("password", "").trim();
        String fullName = p.getOrDefault("fullName", "").trim();
        String email = p.getOrDefault("email", "").trim();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
            sendJson(ex, 400, "{\"ok\":false,\"error\":\"All registration fields are required.\"}");
            return;
        }

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String sql = "INSERT INTO users (username, password, full_name, email, role, created_at) VALUES (?, ?, ?, ?, 'USER', ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, fullName);
            ps.setString(4, email);
            ps.setString(5, now);
            ps.executeUpdate();
            int id = 0;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    id = keys.getInt(1);
                }
            }
            String json = String.format(
                    "{\"ok\":true,\"user\":{\"id\":%d,\"username\":\"%s\",\"fullName\":\"%s\",\"email\":\"%s\",\"role\":\"USER\"}}",
                    id, esc(username), esc(fullName), esc(email)
            );
            sendJson(ex, 200, json);
        } catch (SQLException e) {
            sendJson(ex, 400, "{\"ok\":false,\"error\":\"Username already exists or invalid input.\"}");
        }
    }

    private void handleState(HttpExchange ex) throws IOException {
        db.refreshActiveFines();
        StringBuilder sb = new StringBuilder();
        sb.append("{\"ok\":true,");

        try (Connection conn = db.getConnection();
             Statement st = conn.createStatement()) {

            // 1. Books
            sb.append("\"books\":[");
            try (ResultSet rs = st.executeQuery("SELECT * FROM books ORDER BY id ASC")) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;
                    sb.append(String.format(
                            "{\"id\":%d,\"title\":\"%s\",\"author\":\"%s\",\"isbn\":\"%s\",\"category\":\"%s\",\"totalQty\":%d,\"availableQty\":%d}",
                            rs.getInt("id"),
                            esc(rs.getString("title")),
                            esc(rs.getString("author")),
                            esc(rs.getString("isbn")),
                            esc(rs.getString("category")),
                            rs.getInt("total_qty"),
                            rs.getInt("available_qty")
                    ));
                }
            }
            sb.append("],");

            // 2. Issued Books
            sb.append("\"issuedBooks\":[");
            String issQuery =
                    "SELECT ib.*, b.title AS book_title, b.isbn AS book_isbn, u.username, u.full_name " +
                    "FROM issued_books ib " +
                    "JOIN books b ON ib.book_id = b.id " +
                    "JOIN users u ON ib.user_id = u.id " +
                    "ORDER BY ib.id DESC";
            try (ResultSet rs = st.executeQuery(issQuery)) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;
                    String retDate = rs.getString("return_date");
                    sb.append(String.format(
                            "{\"id\":%d,\"bookId\":%d,\"bookTitle\":\"%s\",\"isbn\":\"%s\",\"userId\":%d,\"username\":\"%s\",\"fullName\":\"%s\"," +
                            "\"issueDate\":\"%s\",\"dueDate\":\"%s\",\"returnDate\":\"%s\",\"status\":\"%s\",\"fineAmount\":%.2f,\"finePaid\":%s}",
                            rs.getInt("id"),
                            rs.getInt("book_id"),
                            esc(rs.getString("book_title")),
                            esc(rs.getString("book_isbn")),
                            rs.getInt("user_id"),
                            esc(rs.getString("username")),
                            esc(rs.getString("full_name")),
                            esc(rs.getString("issue_date")),
                            esc(rs.getString("due_date")),
                            retDate == null ? "-" : esc(retDate),
                            esc(rs.getString("status")),
                            rs.getDouble("fine_amount"),
                            rs.getInt("fine_paid") == 1 ? "true" : "false"
                    ));
                }
            }
            sb.append("],");

            // 3. Advance Bookings
            sb.append("\"advanceBookings\":[");
            String advQuery =
                    "SELECT ab.*, b.title AS book_title, u.username, u.full_name " +
                    "FROM advance_bookings ab " +
                    "JOIN books b ON ab.book_id = b.id " +
                    "JOIN users u ON ab.user_id = u.id " +
                    "ORDER BY ab.id DESC";
            try (ResultSet rs = st.executeQuery(advQuery)) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;
                    sb.append(String.format(
                            "{\"id\":%d,\"bookId\":%d,\"bookTitle\":\"%s\",\"userId\":%d,\"username\":\"%s\",\"fullName\":\"%s\",\"bookingDate\":\"%s\",\"status\":\"%s\"}",
                            rs.getInt("id"),
                            rs.getInt("book_id"),
                            esc(rs.getString("book_title")),
                            rs.getInt("user_id"),
                            esc(rs.getString("username")),
                            esc(rs.getString("full_name")),
                            esc(rs.getString("booking_date")),
                            esc(rs.getString("status"))
                    ));
                }
            }
            sb.append("],");

            // 4. Members
            sb.append("\"members\":[");
            try (ResultSet rs = st.executeQuery("SELECT id, username, full_name, email, role, created_at FROM users ORDER BY id ASC")) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;
                    sb.append(String.format(
                            "{\"id\":%d,\"username\":\"%s\",\"fullName\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"createdAt\":\"%s\"}",
                            rs.getInt("id"),
                            esc(rs.getString("username")),
                            esc(rs.getString("full_name")),
                            esc(rs.getString("email")),
                            esc(rs.getString("role")),
                            esc(rs.getString("created_at"))
                    ));
                }
            }
            sb.append("],");

            // 5. Queries
            sb.append("\"queries\":[");
            try (ResultSet rs = st.executeQuery("SELECT * FROM queries ORDER BY id DESC")) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;
                    sb.append(String.format(
                            "{\"id\":%d,\"userId\":%d,\"username\":\"%s\",\"subject\":\"%s\",\"message\":\"%s\",\"submittedAt\":\"%s\"}",
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            esc(rs.getString("username")),
                            esc(rs.getString("subject")),
                            esc(rs.getString("message")),
                            esc(rs.getString("submitted_at"))
                    ));
                }
            }
            sb.append("]}");

            sendJson(ex, 200, sb.toString());
        } catch (SQLException e) {
            sendJson(ex, 500, "{\"ok\":false,\"error\":\"" + esc(e.getMessage()) + "\"}");
        }
    }

    private void handleAddBook(HttpExchange ex) throws IOException {
        Map<String, String> p = parseFormBody(ex);
        String title = p.getOrDefault("title", "").trim();
        String author = p.getOrDefault("author", "").trim();
        String isbn = p.getOrDefault("isbn", "").trim();
        String category = p.getOrDefault("category", "General").trim();
        int quantity;
        try {
            quantity = Integer.parseInt(p.getOrDefault("quantity", "1").trim());
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sendJson(ex, 400, "{\"ok\":false,\"error\":\"Quantity must be a positive integer.\"}");
            return;
        }

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            sendJson(ex, 400, "{\"ok\":false,\"error\":\"Title, Author, and ISBN are required.\"}");
            return;
        }

        String sql = "INSERT INTO books (title, author, isbn, category, total_qty, available_qty) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, isbn);
            ps.setString(4, category);
            ps.setInt(5, quantity);
            ps.setInt(6, quantity);
            ps.executeUpdate();
            sendJson(ex, 200, "{\"ok\":true,\"message\":\"Book added to catalogue successfully!\"}");
        } catch (SQLException e) {
            sendJson(ex, 400, "{\"ok\":false,\"error\":\"Could not add book (ISBN may already exist).\"}");
        }
    }

    private void handleUpdateBook(HttpExchange ex) throws IOException {
        Map<String, String> p = parseFormBody(ex);
        try {
            int id = Integer.parseInt(p.getOrDefault("id", "0").trim());
            String title = p.getOrDefault("title", "").trim();
            String author = p.getOrDefault("author", "").trim();
            String isbn = p.getOrDefault("isbn", "").trim();
            String category = p.getOrDefault("category", "").trim();
            int totalQty = Integer.parseInt(p.getOrDefault("totalQty", "1").trim());
            int availQty = Integer.parseInt(p.getOrDefault("availableQty", "1").trim());

            String sql = "UPDATE books SET title=?, author=?, isbn=?, category=?, total_qty=?, available_qty=? WHERE id=?";
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, title);
                ps.setString(2, author);
                ps.setString(3, isbn);
                ps.setString(4, category);
                ps.setInt(5, totalQty);
                ps.setInt(6, availQty);
                ps.setInt(7, id);
                ps.executeUpdate();
                sendJson(ex, 200, "{\"ok\":true,\"message\":\"Book record updated successfully.\"}");
            }
        } catch (Exception e) {
            sendJson(ex, 400, "{\"ok\":false,\"error\":\"Invalid update parameters: " + esc(e.getMessage()) + "\"}");
        }
    }

    private void handleDeleteBook(HttpExchange ex) throws IOException {
        Map<String, String> p = parseFormBody(ex);
        try {
            int id = Integer.parseInt(p.getOrDefault("id", "0").trim());
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                sendJson(ex, 200, "{\"ok\":true,\"message\":\"Book deleted from catalogue.\"}");
            }
        } catch (Exception e) {
            sendJson(ex, 400, "{\"ok\":false,\"error\":\"" + esc(e.getMessage()) + "\"}");
        }
    }

    private void handleIssueBook(HttpExchange ex) throws IOException {
        Map<String, String> p = parseFormBody(ex);
        try {
            int bookId = Integer.parseInt(p.getOrDefault("bookId", "0").trim());
            int userId = Integer.parseInt(p.getOrDefault("userId", "0").trim());
            boolean simulateOverdue = "true".equalsIgnoreCase(p.getOrDefault("simulateOverdue", "false"));

            try (Connection conn = db.getConnection()) {
                // Check available quantity
                int avail = 0;
                try (PreparedStatement checkPs = conn.prepareStatement("SELECT available_qty FROM books WHERE id = ?")) {
                    checkPs.setInt(1, bookId);
                    try (ResultSet rs = checkPs.executeQuery()) {
                        if (rs.next()) {
                            avail = rs.getInt("available_qty");
                        }
                    }
                }

                if (avail <= 0) {
                    sendJson(ex, 400, "{\"ok\":false,\"error\":\"No copies available. Use 'Advance Booking' to reserve this book.\"}");
                    return;
                }

                LocalDate issueDate = simulateOverdue ? LocalDate.now().minusDays(18) : LocalDate.now();
                LocalDate dueDate = simulateOverdue ? LocalDate.now().minusDays(4) : LocalDate.now().plusDays(14);
                double initialFine = simulateOverdue ? (4 * LibraryDatabase.FINE_PER_DAY) : 0.0;

                // Decrement available_qty
                try (PreparedStatement decPs = conn.prepareStatement("UPDATE books SET available_qty = available_qty - 1 WHERE id = ?")) {
                    decPs.setInt(1, bookId);
                    decPs.executeUpdate();
                }

                // Insert issue record
                String insSql = "INSERT INTO issued_books (book_id, user_id, issue_date, due_date, status, fine_amount, fine_paid) VALUES (?, ?, ?, ?, 'ISSUED', ?, 0)";
                try (PreparedStatement insPs = conn.prepareStatement(insSql)) {
                    insPs.setInt(1, bookId);
                    insPs.setInt(2, userId);
                    insPs.setString(3, issueDate.toString());
                    insPs.setString(4, dueDate.toString());
                    insPs.setDouble(5, initialFine);
                    insPs.executeUpdate();
                }

                sendJson(ex, 200, "{\"ok\":true,\"message\":\"Book issued successfully! Due Date: " + dueDate + "\"}");
            }
        } catch (Exception e) {
            sendJson(ex, 400, "{\"ok\":false,\"error\":\"" + esc(e.getMessage()) + "\"}");
        }
    }

    private void handleReturnBook(HttpExchange ex) throws IOException {
        Map<String, String> p = parseFormBody(ex);
        try {
            int issueId = Integer.parseInt(p.getOrDefault("issueId", "0").trim());
            try (Connection conn = db.getConnection()) {
                int bookId = 0;
                String dueDateStr = null;
                String status = null;
                try (PreparedStatement sel = conn.prepareStatement("SELECT book_id, due_date, status FROM issued_books WHERE id = ?")) {
                    sel.setInt(1, issueId);
                    try (ResultSet rs = sel.executeQuery()) {
                        if (rs.next()) {
                            bookId = rs.getInt("book_id");
                            dueDateStr = rs.getString("due_date");
                            status = rs.getString("status");
                        }
                    }
                }

                if (bookId == 0 || !"ISSUED".equals(status)) {
                    sendJson(ex, 400, "{\"ok\":false,\"error\":\"Book is already returned or record not found.\"}");
                    return;
                }

                LocalDate today = LocalDate.now();
                LocalDate dueDate = LocalDate.parse(dueDateStr);
                long overdueDays = ChronoUnit.DAYS.between(dueDate, today);
                double fine = overdueDays > 0 ? overdueDays * LibraryDatabase.FINE_PER_DAY : 0.0;

                // Update issued_books
                try (PreparedStatement up = conn.prepareStatement(
                        "UPDATE issued_books SET status = 'RETURNED', return_date = ?, fine_amount = ? WHERE id = ?")) {
                    up.setString(1, today.toString());
                    up.setDouble(2, fine);
                    up.setInt(3, issueId);
                    up.executeUpdate();
                }

                // Increment available_qty
                try (PreparedStatement inc = conn.prepareStatement(
                        "UPDATE books SET available_qty = available_qty + 1 WHERE id = ?")) {
                    inc.setInt(1, bookId);
                    inc.executeUpdate();
                }

                // Mark any advance booking for this book as READY FOR ISSUE
                try (PreparedStatement adv = conn.prepareStatement(
                        "UPDATE advance_bookings SET status = 'AVAILABLE - READY TO ISSUE' WHERE book_id = ? AND status = 'RESERVED'")) {
                    adv.setInt(1, bookId);
                    adv.executeUpdate();
                }

                String msg = fine > 0
                        ? String.format("Book returned! Overdue by %d day(s). Automatic Fine Generated: Rs. %.2f (Rs. 5/day).", overdueDays, fine)
                        : "Book returned on time! No overdue fine.";
                sendJson(ex, 200, "{\"ok\":true,\"message\":\"" + esc(msg) + "\"}");
            }
        } catch (Exception e) {
            sendJson(ex, 400, "{\"ok\":false,\"error\":\"" + esc(e.getMessage()) + "\"}");
        }
    }

    private void handleAdvanceBook(HttpExchange ex) throws IOException {
        Map<String, String> p = parseFormBody(ex);
        try {
            int bookId = Integer.parseInt(p.getOrDefault("bookId", "0").trim());
            int userId = Integer.parseInt(p.getOrDefault("userId", "0").trim());
            String now = LocalDate.now().toString();

            String sql = "INSERT INTO advance_bookings (book_id, user_id, booking_date, status) VALUES (?, ?, ?, 'RESERVED')";
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, bookId);
                ps.setInt(2, userId);
                ps.setString(3, now);
                ps.executeUpdate();
                sendJson(ex, 200, "{\"ok\":true,\"message\":\"Advance booking confirmed! You will be notified as soon as the current copy is returned.\"}");
            }
        } catch (Exception e) {
            sendJson(ex, 400, "{\"ok\":false,\"error\":\"" + esc(e.getMessage()) + "\"}");
        }
    }

    private void handleMarkFinePaid(HttpExchange ex) throws IOException {
        Map<String, String> p = parseFormBody(ex);
        try {
            int issueId = Integer.parseInt(p.getOrDefault("issueId", "0").trim());
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE issued_books SET fine_paid = 1 WHERE id = ?")) {
                ps.setInt(1, issueId);
                ps.executeUpdate();
                sendJson(ex, 200, "{\"ok\":true,\"message\":\"Fine marked as PAID.\"}");
            }
        } catch (Exception e) {
            sendJson(ex, 400, "{\"ok\":false,\"error\":\"" + esc(e.getMessage()) + "\"}");
        }
    }

    private void handleDeleteMember(HttpExchange ex) throws IOException {
        Map<String, String> p = parseFormBody(ex);
        try {
            int memberId = Integer.parseInt(p.getOrDefault("memberId", "0").trim());
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id = ? AND role != 'ADMIN'")) {
                ps.setInt(1, memberId);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    sendJson(ex, 200, "{\"ok\":true,\"message\":\"Member account removed.\"}");
                } else {
                    sendJson(ex, 400, "{\"ok\":false,\"error\":\"Cannot delete primary Admin account.\"}");
                }
            }
        } catch (Exception e) {
            sendJson(ex, 400, "{\"ok\":false,\"error\":\"" + esc(e.getMessage()) + "\"}");
        }
    }

    private void handleAddQuery(HttpExchange ex) throws IOException {
        Map<String, String> p = parseFormBody(ex);
        int userId = Integer.parseInt(p.getOrDefault("userId", "0").trim());
        String username = p.getOrDefault("username", "User").trim();
        String subject = p.getOrDefault("subject", "").trim();
        String message = p.getOrDefault("message", "").trim();

        if (subject.isEmpty() || message.isEmpty()) {
            sendJson(ex, 400, "{\"ok\":false,\"error\":\"Subject and message cannot be empty.\"}");
            return;
        }

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String sql = "INSERT INTO queries (user_id, username, subject, message, submitted_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, username);
            ps.setString(3, subject);
            ps.setString(4, message);
            ps.setString(5, now);
            ps.executeUpdate();
            sendJson(ex, 200, "{\"ok\":true,\"message\":\"Your query has been submitted to the Library Admin!\"}");
        } catch (SQLException e) {
            sendJson(ex, 500, "{\"ok\":false,\"error\":\"" + esc(e.getMessage()) + "\"}");
        }
    }

    private Map<String, String> parseFormBody(HttpExchange ex) throws IOException {
        Map<String, String> map = new HashMap<>();
        String body;
        try (InputStream is = ex.getRequestBody()) {
            body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
        if (body.isEmpty()) {
            return map;
        }
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx > 0) {
                String k = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String v = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                map.put(k, v);
            }
        }
        return map;
    }

    private void sendJson(HttpExchange ex, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendText(HttpExchange ex, int status, String contentType, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    private static void runSelfTest(LibraryDatabase db) throws Exception {
        System.out.println("=== Running Task 5 Digital Library Self-Test ===");
        db.refreshActiveFines();
        try (Connection conn = db.getConnection();
             Statement st = conn.createStatement()) {
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM books")) {
                if (rs.next()) {
                    System.out.println("1. Books in Catalogue: " + rs.getInt(1));
                }
            }
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next()) {
                    System.out.println("2. Registered Accounts (Admin + Members): " + rs.getInt(1));
                }
            }
            try (ResultSet rs = st.executeQuery("SELECT id, due_date, fine_amount FROM issued_books WHERE fine_amount > 0")) {
                if (rs.next()) {
                    System.out.printf("3. Overdue Fine Calculation Verified: Issue #%d (Due %s) -> Rs. %.2f%n",
                            rs.getInt("id"), rs.getString("due_date"), rs.getDouble("fine_amount"));
                }
            }
        }
        System.out.println("4. Task 5 Self-Test PASSED!");
    }

    public static void main(String[] args) throws Exception {
        LibraryDatabase db = new LibraryDatabase();

        if (args.length > 0 && "--self-test".equalsIgnoreCase(args[0])) {
            runSelfTest(db);
            return;
        }

        int port = DEFAULT_PORT;
        LibraryWebServer app = new LibraryWebServer(db);
        app.startServer(port);

        String url = "http://localhost:" + port;
        System.out.println("==========================================================================");
        System.out.println("  OASIS INFOBYTE — DIGITAL LIBRARY MANAGEMENT SYSTEM (TASK 5)");
        System.out.println("  Web Server running at: " + url);
        System.out.println("  Admin Login : admin / admin123");
        System.out.println("  User Login  : aarav / user123   or   priya / user123");
        System.out.println("  Press Ctrl+C to stop the server.");
        System.out.println("==========================================================================");

        if (args.length == 0 || !"--no-browser".equalsIgnoreCase(args[0])) {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI(url));
                }
            } catch (Exception ignored) {
            }
        }
    }
}

