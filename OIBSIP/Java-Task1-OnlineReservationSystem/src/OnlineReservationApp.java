import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/**
 * Oasis Infobyte — Java Development Task 1: Online Reservation System (Swing GUI + JDBC SQLite).
 */
public class OnlineReservationApp extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(23, 55, 94);
    private static final Color ACCENT_COLOR = new Color(37, 99, 235);
    private static final Color DANGER_COLOR = new Color(220, 38, 38);
    private static final Color SUCCESS_COLOR = new Color(22, 163, 74);
    private static final Color BG_LIGHT = new Color(245, 247, 250);

    private final DatabaseManager dbManager;
    private final CardLayout cardLayout;
    private final JPanel rootPanel;

    private String loggedInUsername = null;
    private String loggedInFullName = null;

    // Login components
    private JTextField loginUserField;
    private JPasswordField loginPassField;
    private JLabel loginStatusLabel;

    // Header
    private JLabel userBadgeLabel;
    private JTabbedPane mainTabs;

    // Reservation Form components
    private JTextField passengerNameField;
    private JTextField trainNumberField;
    private JComboBox<String> sampleTrainCombo;
    private JTextField trainNameField;
    private JComboBox<String> classTypeCombo;
    private JTextField journeyDateField;
    private JTextField sourceStationField;
    private JTextField destinationStationField;
    private JTextArea latestBookingSummaryArea;

    // Cancellation Form components
    private JTextField cancelPnrField;
    private JTextArea fetchedBookingDetailsArea;
    private JButton confirmCancelButton;
    private ReservationRecord currentlyFetchedRecord = null;

    // All Bookings Table
    private DefaultTableModel bookingsTableModel;
    private JTable bookingsTable;

    public OnlineReservationApp(DatabaseManager dbManager) {
        super("Oasis Infobyte — Online Train Reservation System (Task 1)");
        this.dbManager = dbManager;
        this.cardLayout = new CardLayout();
        this.rootPanel = new JPanel(cardLayout);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 680);
        setMinimumSize(new Dimension(880, 620));
        setLocationRelativeTo(null);

        rootPanel.add(buildLoginPanel(), "LOGIN");
        rootPanel.add(buildMainDashboardPanel(), "DASHBOARD");

        setContentPane(rootPanel);
        cardLayout.show(rootPanel, "LOGIN");
    }

    private JPanel buildLoginPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG_LIGHT);

        JPanel card = new JPanel(new BorderLayout(0, 18));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 218, 230), 1),
                new EmptyBorder(28, 36, 28, 36)
        ));
        card.setPreferredSize(new Dimension(470, 410));

        // Title Block
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);

        JLabel title = new JLabel("ONLINE RESERVATION SYSTEM", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(PRIMARY_COLOR);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Oasis Infobyte SIP · Java Development (Task 1)", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(100, 116, 139));
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);
        card.add(header, BorderLayout.NORTH);

        // Form Fields
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel userLbl = new JLabel("Username:");
        userLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        form.add(userLbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        loginUserField = new JTextField("user1", 18);
        loginUserField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        form.add(loginUserField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel passLbl = new JLabel("Password:");
        passLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        form.add(passLbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        loginPassField = new JPasswordField("pass123", 18);
        loginPassField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        form.add(loginPassField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginStatusLabel = new JLabel("Demo Credentials: user1 / pass123  or  admin / admin123", SwingConstants.CENTER);
        loginStatusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        loginStatusLabel.setForeground(new Color(71, 85, 105));
        form.add(loginStatusLabel, gbc);

        card.add(form, BorderLayout.CENTER);

        // Login Button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        footer.setBackground(Color.WHITE);

        JButton loginBtn = createStyledButton("Login to Portal", ACCENT_COLOR);
        loginBtn.setPreferredSize(new Dimension(200, 40));
        loginBtn.addActionListener(e -> handleLogin());

        loginPassField.addActionListener(e -> handleLogin());
        footer.add(loginBtn);

        card.add(footer, BorderLayout.SOUTH);
        outer.add(card);
        return outer;
    }

    private JPanel buildMainDashboardPanel() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(BG_LIGHT);

        // Top Navigation Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY_COLOR);
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel brandLabel = new JLabel("🚆 OASIS RAILWAYS — ONLINE RESERVATION & PNR CANCELLATION PORTAL");
        brandLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        brandLabel.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);

        userBadgeLabel = new JLabel("Logged in as: Passenger");
        userBadgeLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        userBadgeLabel.setForeground(new Color(226, 232, 240));

        JButton logoutBtn = createStyledButton("Logout", new Color(71, 85, 105));
        logoutBtn.addActionListener(e -> handleLogout());

        rightPanel.add(userBadgeLabel);
        rightPanel.add(logoutBtn);

        topBar.add(brandLabel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);
        dashboard.add(topBar, BorderLayout.NORTH);

        // Tabbed Workspace
        mainTabs = new JTabbedPane();
        mainTabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        mainTabs.setBorder(new EmptyBorder(12, 16, 12, 16));

        mainTabs.addTab("1. Reservation Form (Book Ticket)", buildReservationTab());
        mainTabs.addTab("2. Cancellation Form (PNR Lookup & Cancel)", buildCancellationTab());
        mainTabs.addTab("3. Active Reservations Directory", buildAllBookingsTab());

        dashboard.add(mainTabs, BorderLayout.CENTER);
        return dashboard;
    }

    private JPanel buildReservationTab() {
        JPanel container = new JPanel(new BorderLayout(16, 0));
        container.setBackground(Color.WHITE);
        container.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Left: Reservation Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Passenger & Train Reservation Details ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13),
                PRIMARY_COLOR
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 10, 7, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        passengerNameField = new JTextField("Aarav Sharma", 20);
        trainNumberField = new JTextField("12951", 10);
        trainNameField = new JTextField(TrainDirectory.lookupTrainName("12951"), 20);
        trainNameField.setEditable(false);
        trainNameField.setBackground(new Color(241, 245, 249));
        trainNameField.setFont(new Font("SansSerif", Font.BOLD, 13));

        sampleTrainCombo = new JComboBox<>();
        sampleTrainCombo.addItem("-- Quick-Select Train Number --");
        for (Map.Entry<Integer, String> entry : TrainDirectory.getSampleTrains().entrySet()) {
            sampleTrainCombo.addItem(entry.getKey() + " - " + entry.getValue());
        }
        sampleTrainCombo.addActionListener(e -> {
            int idx = sampleTrainCombo.getSelectedIndex();
            if (idx > 0) {
                String item = (String) sampleTrainCombo.getSelectedItem();
                if (item != null && item.contains(" - ")) {
                    String numPart = item.substring(0, item.indexOf(" - ")).trim();
                    trainNumberField.setText(numPart);
                }
            }
        });

        // Auto-populate Train Name whenever Train Number changes
        trainNumberField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTrainName();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTrainName();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTrainName();
            }

            private void updateTrainName() {
                String lookedUp = TrainDirectory.lookupTrainName(trainNumberField.getText());
                if (!lookedUp.isEmpty()) {
                    trainNameField.setText(lookedUp);
                    trainNameField.setForeground(new Color(15, 23, 42));
                } else {
                    trainNameField.setText("(Enter valid numeric Train Number)");
                    trainNameField.setForeground(DANGER_COLOR);
                }
            }
        });

        classTypeCombo = new JComboBox<>(new String[]{
                "First AC (1A)",
                "Second AC (2A)",
                "Third AC (3A)",
                "AC Chair Car (CC)",
                "Sleeper Class (SL)",
                "Second Seating (2S)"
        });

        journeyDateField = new JTextField(LocalDate.now().plusDays(7).toString(), 15);
        sourceStationField = new JTextField("Mumbai Central", 20);
        destinationStationField = new JTextField("New Delhi", 20);

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Passenger Name *:", passengerNameField);
        addFormRow(formPanel, gbc, row++, "Quick Train Directory:", sampleTrainCombo);
        addFormRow(formPanel, gbc, row++, "Train Number (Numeric) *:", trainNumberField);
        addFormRow(formPanel, gbc, row++, "Train Name (Auto-Filled):", trainNameField);
        addFormRow(formPanel, gbc, row++, "Class Type *:", classTypeCombo);
        addFormRow(formPanel, gbc, row++, "Date of Journey (YYYY-MM-DD) *:", journeyDateField);
        addFormRow(formPanel, gbc, row++, "Source Station *:", sourceStationField);
        addFormRow(formPanel, gbc, row++, "Destination Station *:", destinationStationField);

        // Buttons row
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        btnPanel.setBackground(Color.WHITE);

        JButton bookBtn = createStyledButton("Insert / Book Ticket", SUCCESS_COLOR);
        bookBtn.addActionListener(e -> handleInsertReservation());

        JButton clearBtn = createStyledButton("Reset Form", new Color(100, 116, 139));
        clearBtn.addActionListener(e -> resetReservationForm());

        btnPanel.add(bookBtn);
        btnPanel.add(clearBtn);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        // Right: Live Ticket / PNR Output Card
        JPanel receiptPanel = new JPanel(new BorderLayout(0, 8));
        receiptPanel.setBackground(Color.WHITE);
        receiptPanel.setPreferredSize(new Dimension(340, 0));
        receiptPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Generated E-Ticket & PNR Receipt ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13),
                PRIMARY_COLOR
        ));

        latestBookingSummaryArea = new JTextArea();
        latestBookingSummaryArea.setEditable(false);
        latestBookingSummaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        latestBookingSummaryArea.setBackground(new Color(248, 250, 252));
        latestBookingSummaryArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        latestBookingSummaryArea.setText(
                "Fill out the Reservation Form and click\n" +
                "'Insert / Book Ticket' to generate a\n" +
                "unique PNR number.\n\n" +
                "Pre-loaded Demo PNR for testing:\n" +
                "  -> PNR1002026"
        );

        JButton sendToCancelTabBtn = createStyledButton("Cancel / Verify a PNR ->", ACCENT_COLOR);
        sendToCancelTabBtn.addActionListener(e -> mainTabs.setSelectedIndex(1));

        receiptPanel.add(new JScrollPane(latestBookingSummaryArea), BorderLayout.CENTER);
        receiptPanel.add(sendToCancelTabBtn, BorderLayout.SOUTH);

        container.add(formPanel, BorderLayout.CENTER);
        container.add(receiptPanel, BorderLayout.EAST);
        return container;
    }

    private JPanel buildCancellationTab() {
        JPanel container = new JPanel(new BorderLayout(16, 16));
        container.setBackground(Color.WHITE);
        container.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top Search Bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        searchBar.setBackground(new Color(248, 250, 252));
        searchBar.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Step 1: Enter PNR Number to Retrieve Booking ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13),
                PRIMARY_COLOR
        ));

        JLabel pnrLbl = new JLabel("PNR Number:");
        pnrLbl.setFont(new Font("SansSerif", Font.BOLD, 13));

        cancelPnrField = new JTextField("PNR1002026", 18);
        cancelPnrField.setFont(new Font("Monospaced", Font.BOLD, 14));

        JButton fetchBtn = createStyledButton("Fetch Booking Details", ACCENT_COLOR);
        fetchBtn.addActionListener(e -> handleFetchByPnr());
        cancelPnrField.addActionListener(e -> handleFetchByPnr());

        searchBar.add(pnrLbl);
        searchBar.add(cancelPnrField);
        searchBar.add(fetchBtn);

        // Center Details Box
        JPanel detailsBox = new JPanel(new BorderLayout(0, 10));
        detailsBox.setBackground(Color.WHITE);
        detailsBox.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Step 2: Review Fetched Reservation Details & Confirm Cancellation ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13),
                PRIMARY_COLOR
        ));

        fetchedBookingDetailsArea = new JTextArea();
        fetchedBookingDetailsArea.setEditable(false);
        fetchedBookingDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        fetchedBookingDetailsArea.setBackground(new Color(248, 250, 252));
        fetchedBookingDetailsArea.setBorder(new EmptyBorder(14, 14, 14, 14));
        fetchedBookingDetailsArea.setText("Enter a valid PNR above (e.g., PNR1002026) and click 'Fetch Booking Details'.");

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        actionRow.setBackground(Color.WHITE);

        confirmCancelButton = createStyledButton("Confirm Cancellation (Delete Booking)", DANGER_COLOR);
        confirmCancelButton.setEnabled(false);
        confirmCancelButton.addActionListener(e -> handleConfirmCancellation());

        actionRow.add(confirmCancelButton);

        detailsBox.add(new JScrollPane(fetchedBookingDetailsArea), BorderLayout.CENTER);
        detailsBox.add(actionRow, BorderLayout.SOUTH);

        container.add(searchBar, BorderLayout.NORTH);
        container.add(detailsBox, BorderLayout.CENTER);
        return container;
    }

    private JPanel buildAllBookingsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] cols = {
                "PNR Number", "Passenger Name", "Train No.", "Train Name",
                "Class", "Journey Date", "From", "To", "Booked By"
        };
        bookingsTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(bookingsTableModel);
        bookingsTable.setRowHeight(26);
        bookingsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        bookingsTable.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        bottomBar.setBackground(Color.WHITE);

        JButton refreshBtn = createStyledButton("Refresh Reservations", ACCENT_COLOR);
        refreshBtn.addActionListener(e -> refreshBookingsTable());

        JButton loadSelectedPnrBtn = createStyledButton("Load Selected PNR into Cancellation Tab", DANGER_COLOR);
        loadSelectedPnrBtn.addActionListener(e -> {
            int row = bookingsTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this,
                        "Please select a booking row in the table first.",
                        "No Booking Selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            String pnr = (String) bookingsTableModel.getValueAt(row, 0);
            cancelPnrField.setText(pnr);
            handleFetchByPnr();
            mainTabs.setSelectedIndex(1);
        });

        bottomBar.add(refreshBtn);
        bottomBar.add(loadSelectedPnrBtn);

        panel.add(new JScrollPane(bookingsTable), BorderLayout.CENTER);
        panel.add(bottomBar, BorderLayout.SOUTH);
        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        gbc.gridwidth = 1;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(field, gbc);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setOpaque(true);
        return btn;
    }

    private void handleLogin() {
        String username = loginUserField.getText().trim();
        String password = new String(loginPassField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both Username and Password.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fullName = dbManager.authenticate(username, password);
        if (fullName == null) {
            loginStatusLabel.setText("Access Denied: Invalid username or password!");
            loginStatusLabel.setForeground(DANGER_COLOR);
            JOptionPane.showMessageDialog(this,
                    "Access Denied! Invalid username or password.\nTry demo account: user1 / pass123",
                    "Authentication Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.loggedInUsername = username;
        this.loggedInFullName = fullName;
        this.userBadgeLabel.setText("Logged in: " + fullName + " (" + username + ")");
        refreshBookingsTable();
        cardLayout.show(rootPanel, "DASHBOARD");
    }

    private void handleLogout() {
        this.loggedInUsername = null;
        this.loggedInFullName = null;
        loginPassField.setText("");
        loginStatusLabel.setText("You have logged out safely. Demo: user1 / pass123");
        loginStatusLabel.setForeground(new Color(71, 85, 105));
        cardLayout.show(rootPanel, "LOGIN");
    }

    private void handleInsertReservation() {
        String passenger = passengerNameField.getText().trim();
        String trainNoStr = trainNumberField.getText().trim();
        String trainName = TrainDirectory.lookupTrainName(trainNoStr);
        String classType = (String) classTypeCombo.getSelectedItem();
        String journeyDate = journeyDateField.getText().trim();
        String source = sourceStationField.getText().trim();
        String destination = destinationStationField.getText().trim();

        // 1. Check empty required fields
        if (passenger.isEmpty() || trainNoStr.isEmpty() || journeyDate.isEmpty()
                || source.isEmpty() || destination.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All reservation fields marked with * are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Validate numeric train number
        int trainNumber;
        try {
            trainNumber = Integer.parseInt(trainNoStr);
            if (trainNumber <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Train Number must be a positive numeric value (e.g., 12951).",
                    "Invalid Train Number",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Validate date format (YYYY-MM-DD)
        try {
            LocalDate.parse(journeyDate);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Date of Journey must be in valid YYYY-MM-DD format (e.g., 2026-12-25).",
                    "Invalid Date Format",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. Validate source != destination
        if (source.equalsIgnoreCase(destination)) {
            JOptionPane.showMessageDialog(this,
                    "Source Station and Destination Station cannot be identical.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ReservationRecord created = dbManager.createReservation(
                    passenger,
                    trainNumber,
                    trainName,
                    classType,
                    journeyDate,
                    source,
                    destination,
                    loggedInUsername != null ? loggedInUsername : "user1"
            );

            String summary = "=== RESERVATION CONFIRMED ===\n\n" + created.toFormattedSummary();
            latestBookingSummaryArea.setText(summary);
            cancelPnrField.setText(created.getPnr());
            refreshBookingsTable();

            JOptionPane.showMessageDialog(this,
                    "Ticket Booked Successfully!\n\n" + created.toFormattedSummary(),
                    "Booking Confirmation — PNR: " + created.getPnr(),
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error while saving reservation:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetReservationForm() {
        passengerNameField.setText("");
        trainNumberField.setText("12951");
        classTypeCombo.setSelectedIndex(0);
        journeyDateField.setText(LocalDate.now().plusDays(7).toString());
        sourceStationField.setText("");
        destinationStationField.setText("");
    }

    private void handleFetchByPnr() {
        String pnr = cancelPnrField.getText().trim();
        if (pnr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a PNR Number to fetch.",
                    "PNR Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ReservationRecord record = dbManager.findByPnr(pnr);
        if (record == null) {
            currentlyFetchedRecord = null;
            confirmCancelButton.setEnabled(false);
            fetchedBookingDetailsArea.setText("No active reservation found for PNR: " + pnr);
            JOptionPane.showMessageDialog(this,
                    "No reservation found with PNR Number: " + pnr,
                    "PNR Not Found",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentlyFetchedRecord = record;
        confirmCancelButton.setEnabled(true);
        fetchedBookingDetailsArea.setText(
                "=== FETCHED RESERVATION RECORD ===\n\n" +
                record.toFormattedSummary() +
                "\n\nStatus: ACTIVE (Click 'Confirm Cancellation' below to cancel this ticket)"
        );
    }

    private void handleConfirmCancellation() {
        if (currentlyFetchedRecord == null) {
            return;
        }

        String pnr = currentlyFetchedRecord.getPnr();
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel the reservation for:\n\n" +
                "PNR Number : " + pnr + "\n" +
                "Passenger  : " + currentlyFetchedRecord.getPassengerName() + "\n" +
                "Train      : " + currentlyFetchedRecord.getTrainNumber() + " - " + currentlyFetchedRecord.getTrainName() + "\n\n" +
                "This action will permanently remove the booking from the database.",
                "Confirm Ticket Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            boolean cancelled = dbManager.cancelByPnr(pnr);
            if (cancelled) {
                fetchedBookingDetailsArea.setText(
                        "=== RESERVATION CANCELLED ===\n\n" +
                        "PNR Number " + pnr + " has been permanently cancelled and removed from the database."
                );
                currentlyFetchedRecord = null;
                confirmCancelButton.setEnabled(false);
                refreshBookingsTable();

                JOptionPane.showMessageDialog(this,
                        "Reservation with PNR " + pnr + " has been successfully cancelled.",
                        "Cancellation Confirmed",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not cancel PNR " + pnr + ". It may have already been removed.",
                        "Cancellation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshBookingsTable() {
        if (bookingsTableModel == null) {
            return;
        }
        bookingsTableModel.setRowCount(0);
        List<ReservationRecord> all = dbManager.getAllReservations();
        for (ReservationRecord r : all) {
            bookingsTableModel.addRow(new Object[]{
                    r.getPnr(),
                    r.getPassengerName(),
                    r.getTrainNumber(),
                    r.getTrainName(),
                    r.getClassType(),
                    r.getJourneyDate(),
                    r.getSourceStation(),
                    r.getDestinationStation(),
                    r.getBookedBy()
            });
        }
    }

    private static void saveComponentScreenshot(JFrame frame, File outputFile) throws Exception {
        frame.validate();
        frame.repaint();
        BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
        frame.paint(image.getGraphics());
        outputFile.getParentFile().mkdirs();
        ImageIO.write(image, "png", outputFile);
    }

    private static void runSelfTestAndCaptureScreenshots(DatabaseManager db) throws Exception {
        System.out.println("=== Running Task 1 Self-Test & Screenshot Generation ===");
        String authName = db.authenticate("user1", "pass123");
        System.out.println("1. Auth Test (user1/pass123): " + (authName != null ? "PASSED (" + authName + ")" : "FAILED"));
        String badAuth = db.authenticate("user1", "wrong");
        System.out.println("2. Invalid Auth Test: " + (badAuth == null ? "PASSED (Access Denied)" : "FAILED"));

        ReservationRecord rec = db.createReservation(
                "Priya Nair",
                22436,
                TrainDirectory.lookupTrainName("22436"),
                "AC Chair Car (CC)",
                "2026-12-20",
                "New Delhi",
                "Varanasi Junction",
                "user1"
        );
        System.out.println("3. Reservation Created with PNR: " + rec.getPnr());

        ReservationRecord fetched = db.findByPnr(rec.getPnr());
        System.out.println("4. PNR Lookup Verified: " + (fetched != null ? fetched.getPassengerName() + " on " + fetched.getTrainName() : "FAILED"));

        SwingUtilities.invokeAndWait(() -> {
            try {
                OnlineReservationApp app = new OnlineReservationApp(db);
                app.setVisible(true);
                saveComponentScreenshot(app, new File("screenshots/1_login_screen.png"));

                app.loggedInUsername = "user1";
                app.loggedInFullName = "Aarav Sharma";
                app.userBadgeLabel.setText("Logged in: Aarav Sharma (user1)");
                app.latestBookingSummaryArea.setText("=== RESERVATION CONFIRMED ===\n\n" + rec.toFormattedSummary());
                app.refreshBookingsTable();
                app.cardLayout.show(app.rootPanel, "DASHBOARD");
                app.mainTabs.setSelectedIndex(0);
                saveComponentScreenshot(app, new File("screenshots/2_reservation_form.png"));

                app.cancelPnrField.setText(rec.getPnr());
                app.handleFetchByPnr();
                app.mainTabs.setSelectedIndex(1);
                saveComponentScreenshot(app, new File("screenshots/3_cancellation_form.png"));

                app.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        boolean cancelled = db.cancelByPnr(rec.getPnr());
        System.out.println("5. Cancellation Test for " + rec.getPnr() + ": " + (cancelled ? "PASSED" : "FAILED"));
        System.out.println("6. Screenshots saved to screenshots/ directory.");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        DatabaseManager dbManager = new DatabaseManager();

        if (args.length > 0 && "--self-test".equalsIgnoreCase(args[0])) {
            try {
                runSelfTestAndCaptureScreenshots(dbManager);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
            return;
        }

        SwingUtilities.invokeLater(() -> {
            OnlineReservationApp app = new OnlineReservationApp(dbManager);
            app.setVisible(true);
        });
    }
}

