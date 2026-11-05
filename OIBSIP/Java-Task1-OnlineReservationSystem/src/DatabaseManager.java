import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * JDBC + SQLite database layer for the Online Reservation System.
 * Uses PreparedStatement throughout to prevent SQL injection.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:reservations.db";

    public DatabaseManager() {
        initializeDatabase();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initializeDatabase() {
        String createUsersSql =
                "CREATE TABLE IF NOT EXISTS users (" +
                "  username TEXT PRIMARY KEY," +
                "  password TEXT NOT NULL," +
                "  full_name TEXT NOT NULL" +
                ")";

        String createReservationsSql =
                "CREATE TABLE IF NOT EXISTS reservations (" +
                "  pnr TEXT PRIMARY KEY," +
                "  passenger_name TEXT NOT NULL," +
                "  train_number INTEGER NOT NULL," +
                "  train_name TEXT NOT NULL," +
                "  class_type TEXT NOT NULL," +
                "  journey_date TEXT NOT NULL," +
                "  source_station TEXT NOT NULL," +
                "  destination_station TEXT NOT NULL," +
                "  booked_by TEXT NOT NULL," +
                "  booked_at TEXT NOT NULL" +
                ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersSql);
            stmt.execute(createReservationsSql);

            seedDefaultUsers(conn);
            seedSampleReservation(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize SQLite database: " + e.getMessage(), e);
        }
    }

    private void seedDefaultUsers(Connection conn) throws SQLException {
        String sql = "INSERT OR IGNORE INTO users (username, password, full_name) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "admin");
            ps.setString(2, "admin123");
            ps.setString(3, "System Administrator");
            ps.executeUpdate();

            ps.setString(1, "user1");
            ps.setString(2, "pass123");
            ps.setString(3, "Aarav Sharma");
            ps.executeUpdate();
        }
    }

    private void seedSampleReservation(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM reservations";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        String insertSql =
                "INSERT OR IGNORE INTO reservations " +
                "(pnr, passenger_name, train_number, train_name, class_type, journey_date, " +
                "source_station, destination_station, booked_by, booked_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, "PNR1002026");
            ps.setString(2, "Aarav Sharma");
            ps.setInt(3, 12951);
            ps.setString(4, "Mumbai Rajdhani Express");
            ps.setString(5, "First AC (1A)");
            ps.setString(6, "2026-12-15");
            ps.setString(7, "Mumbai Central");
            ps.setString(8, "New Delhi");
            ps.setString(9, "user1");
            ps.setString(10, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            ps.executeUpdate();
        }
    }

    public String authenticate(String username, String password) {
        String sql = "SELECT full_name FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("full_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String generateUniquePnr() {
        for (int i = 0; i < 20; i++) {
            int randomDigits = ThreadLocalRandom.current().nextInt(1000000, 9999999);
            String candidate = "PNR" + randomDigits;
            if (findByPnr(candidate) == null) {
                return candidate;
            }
        }
        return "PNR" + (System.currentTimeMillis() % 10000000);
    }

    public ReservationRecord createReservation(
            String passengerName,
            int trainNumber,
            String trainName,
            String classType,
            String journeyDate,
            String sourceStation,
            String destinationStation,
            String bookedBy) throws SQLException {

        String pnr = generateUniquePnr();
        String bookedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String sql =
                "INSERT INTO reservations " +
                "(pnr, passenger_name, train_number, train_name, class_type, journey_date, " +
                "source_station, destination_station, booked_by, booked_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pnr);
            ps.setString(2, passengerName);
            ps.setInt(3, trainNumber);
            ps.setString(4, trainName);
            ps.setString(5, classType);
            ps.setString(6, journeyDate);
            ps.setString(7, sourceStation);
            ps.setString(8, destinationStation);
            ps.setString(9, bookedBy);
            ps.setString(10, bookedAt);
            ps.executeUpdate();
        }

        return new ReservationRecord(
                pnr, passengerName, trainNumber, trainName,
                classType, journeyDate, sourceStation, destinationStation,
                bookedBy, bookedAt
        );
    }

    public ReservationRecord findByPnr(String pnr) {
        if (pnr == null || pnr.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT * FROM reservations WHERE UPPER(pnr) = UPPER(?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pnr.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean cancelByPnr(String pnr) {
        if (pnr == null || pnr.trim().isEmpty()) {
            return false;
        }
        String sql = "DELETE FROM reservations WHERE UPPER(pnr) = UPPER(?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pnr.trim());
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ReservationRecord> getAllReservations() {
        List<ReservationRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations ORDER BY booked_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private ReservationRecord mapRow(ResultSet rs) throws SQLException {
        return new ReservationRecord(
                rs.getString("pnr"),
                rs.getString("passenger_name"),
                rs.getInt("train_number"),
                rs.getString("train_name"),
                rs.getString("class_type"),
                rs.getString("journey_date"),
                rs.getString("source_station"),
                rs.getString("destination_station"),
                rs.getString("booked_by"),
                rs.getString("booked_at")
        );
    }
}

