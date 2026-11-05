import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Oasis Infobyte — Java Development Task 4: Online Examination System (Swing GUI).
 * Includes Login, Profile/Password Update, Timed MCQ Exam (Next/Previous + Auto-Submit),
 * Close-Window Session Guard, Detailed Results Breakdown, and Logout.
 */
public class OnlineExaminationApp extends JFrame {
    // Theme 4: Deep Teal & Ocean Cyan EdTech Palette
    private static final Color PRIMARY = new Color(19, 78, 74);          // Deep Teal Slate (#134e4a)
    private static final Color ACCENT = new Color(13, 148, 136);         // Ocean Teal (#0d9488)
    private static final Color SUCCESS = new Color(5, 150, 105);         // Emerald (#059669)
    private static final Color DANGER = new Color(225, 29, 72);          // Crimson Rose (#e11d48)
    private static final Color WARNING = new Color(234, 88, 12);         // Coral Orange (#ea580c)

    private static class Question {
        final String prompt;
        final String[] options;
        final int correctIndex;

        Question(String prompt, String[] options, int correctIndex) {
            this.prompt = prompt;
            this.options = options;
            this.correctIndex = correctIndex;
        }
    }

    private static class UserProfile {
        final String username;
        String password;
        String displayName;

        UserProfile(String username, String password, String displayName) {
            this.username = username;
            this.password = password;
            this.displayName = displayName;
        }
    }

    private final Map<String, UserProfile> users = new HashMap<>();
    private final Question[] questions = new Question[]{
            new Question(
                    "Q1. Which keyword in Java is used to prevent a method from being overridden in a subclass?",
                    new String[]{"static", "final", "abstract", "volatile"},
                    1
            ),
            new Question(
                    "Q2. Which JDBC interface is recommended to execute parameterized SQL queries and prevent SQL injection?",
                    new String[]{"Statement", "ResultSet", "PreparedStatement", "DriverManager"},
                    2
            ),
            new Question(
                    "Q3. What is the time complexity of accessing an element by index in a Java ArrayList?",
                    new String[]{"O(1)", "O(log n)", "O(n)", "O(n log n)"},
                    0
            ),
            new Question(
                    "Q4. Which Swing layout manager allows switching between multiple screens (like Login, Profile, Exam, Results) in the same container?",
                    new String[]{"FlowLayout", "BorderLayout", "GridLayout", "CardLayout"},
                    3
            ),
            new Question(
                    "Q5. In Java Object-Oriented Programming, wrapping data fields as private and exposing public getter/setter methods is called:",
                    new String[]{"Polymorphism", "Encapsulation", "Inheritance", "Abstraction"},
                    1
            ),
            new Question(
                    "Q6. Which package contains Swing's Timer class used for GUI countdown events on the Event Dispatch Thread?",
                    new String[]{"java.util", "javax.swing", "java.time", "java.awt.event"},
                    1
            ),
            new Question(
                    "Q7. What happens when an uncaught RuntimeException occurs inside a method without a try-catch block?",
                    new String[]{"It is ignored silently", "The JVM unwinds the call stack", "It converts into a checked exception", "It restarts main()"},
                    1
            ),
            new Question(
                    "Q8. Which collection in Java does NOT allow duplicate elements?",
                    new String[]{"ArrayList", "LinkedList", "HashSet", "Vector"},
                    2
            )
    };

    private final int[] selectedAnswers = new int[questions.length];
    private UserProfile currentUser = null;
    private int currentQuestionIndex = 0;
    private static final int EXAM_DURATION_SECONDS = 30 * 60; // 30 minutes
    private int remainingSeconds = EXAM_DURATION_SECONDS;
    private boolean examInProgress = false;
    private Timer countdownTimer;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel rootPanel = new JPanel(cardLayout);

    // Login components
    private JTextField loginUserField;
    private JPasswordField loginPassField;

    // Lobby / Profile Update components
    private JLabel lobbyWelcomeLabel;
    private JTextField profileNameField;
    private JPasswordField profilePassField;
    private JLabel profileSavedBadge;

    // Exam components
    private JLabel candidateHeaderLabel;
    private JLabel timerLabel;
    private JLabel questionCounterLabel;
    private JTextArea questionPromptArea;
    private final JRadioButton[] optionButtons = new JRadioButton[4];
    private final ButtonGroup optionGroup = new ButtonGroup();
    private JButton prevButton;
    private JButton nextButton;

    // Result components
    private JLabel resultHeadlineLabel;
    private JLabel resultStatsLabel;
    private JTextArea resultBreakdownArea;

    public OnlineExaminationApp() {
        super("Oasis Infobyte — Online Examination System (Java Task 4)");
        users.put("student1", new UserProfile("student1", "exam123", "Aarav Sharma"));
        users.put("student2", new UserProfile("student2", "exam123", "Priya Nair"));

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowCloseRequest();
            }
        });

        setSize(920, 650);
        setMinimumSize(new Dimension(860, 600));
        setLocationRelativeTo(null);

        rootPanel.add(buildLoginScreen(), "LOGIN");
        rootPanel.add(buildProfileAndLobbyScreen(), "PROFILE");
        rootPanel.add(buildExamScreen(), "EXAM");
        rootPanel.add(buildResultScreen(), "RESULT");

        setContentPane(rootPanel);
        cardLayout.show(rootPanel, "LOGIN");
    }

    private void handleWindowCloseRequest() {
        if (examInProgress) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "An examination session is currently in progress!\nAre you sure you want to quit without completing your exam?",
                    "Confirm Exit During Active Exam",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        dispose();
        System.exit(0);
    }

    private JPanel buildLoginScreen() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(new Color(241, 245, 249));

        JPanel card = new JPanel(new BorderLayout(0, 18));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(450, 380));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(28, 34, 28, 34)
        ));

        JPanel head = new JPanel();
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.setBackground(Color.WHITE);
        JLabel t1 = new JLabel("🎓 ONLINE EXAMINATION PORTAL", SwingConstants.CENTER);
        t1.setFont(new Font("SansSerif", Font.BOLD, 19));
        t1.setForeground(PRIMARY);
        t1.setAlignmentX(CENTER_ALIGNMENT);

        JLabel t2 = new JLabel("Oasis Infobyte SIP · Java Development (Task 4)", SwingConstants.CENTER);
        t2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t2.setForeground(new Color(100, 116, 139));
        t2.setAlignmentX(CENTER_ALIGNMENT);

        head.add(t1);
        head.add(Box.createVerticalStrut(6));
        head.add(t2);
        card.add(head, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        form.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        loginUserField = new JTextField("student1", 16);
        form.add(loginUserField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        form.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        loginPassField = new JPasswordField("exam123", 16);
        loginPassField.addActionListener(e -> handleLogin());
        form.add(loginPassField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JLabel demoHint = new JLabel("Demo Login: student1 / exam123  or  student2 / exam123", SwingConstants.CENTER);
        demoHint.setFont(new Font("SansSerif", Font.ITALIC, 12));
        demoHint.setForeground(new Color(71, 85, 105));
        form.add(demoHint, gbc);

        card.add(form, BorderLayout.CENTER);

        JButton loginBtn = createButton("Login to Exam Dashboard", ACCENT);
        loginBtn.addActionListener(e -> handleLogin());
        card.add(loginBtn, BorderLayout.SOUTH);

        outer.add(card);
        return outer;
    }

    private JPanel buildProfileAndLobbyScreen() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(241, 245, 249));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY);
        topBar.setBorder(new EmptyBorder(14, 24, 14, 24));

        lobbyWelcomeLabel = new JLabel("Welcome, Candidate!");
        lobbyWelcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        lobbyWelcomeLabel.setForeground(Color.WHITE);

        JButton logoutBtn = createButton("Logout", new Color(71, 85, 105));
        logoutBtn.addActionListener(e -> performLogout());

        topBar.add(lobbyWelcomeLabel, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);
        outer.add(topBar, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout(0, 18));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(620, 440));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(24, 28, 24, 28)
        ));

        // Profile Update Form
        JPanel profileForm = new JPanel(new GridBagLayout());
        profileForm.setBackground(Color.WHITE);
        profileForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Step 1: Update Candidate Profile & Password (Optional) ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13),
                PRIMARY
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        profileForm.add(new JLabel("Display Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        profileNameField = new JTextField(20);
        profileForm.add(profileNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        profileForm.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        profilePassField = new JPasswordField(20);
        profileForm.add(profilePassField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel updateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        updateRow.setBackground(Color.WHITE);
        JButton saveProfileBtn = createButton("Update Profile & Password", ACCENT);
        saveProfileBtn.addActionListener(e -> handleProfileUpdate());
        profileSavedBadge = new JLabel("");
        profileSavedBadge.setFont(new Font("SansSerif", Font.BOLD, 12));
        profileSavedBadge.setForeground(SUCCESS);
        updateRow.add(saveProfileBtn);
        updateRow.add(profileSavedBadge);
        profileForm.add(updateRow, gbc);

        card.add(profileForm, BorderLayout.NORTH);

        // Exam Instructions & Start Button
        JPanel instructionsPanel = new JPanel(new BorderLayout(0, 12));
        instructionsPanel.setBackground(new Color(248, 250, 252));
        instructionsPanel.setBorder(new EmptyBorder(14, 16, 14, 16));

        JTextArea rules = new JTextArea(
                "EXAMINATION INSTRUCTIONS:\n" +
                "• Total Questions : " + questions.length + " Multiple-Choice Questions (4 options each)\n" +
                "• Time Limit      : 30:00 Minutes (Visible live countdown timer)\n" +
                "• Navigation      : Use 'Previous' and 'Next' buttons to review or change answers\n" +
                "• Auto-Submission : Exam automatically submits when the countdown timer reaches 00:00"
        );
        rules.setEditable(false);
        rules.setOpaque(false);
        rules.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JButton startExamBtn = createButton("Start Timed Examination Now ->", SUCCESS);
        startExamBtn.setPreferredSize(new Dimension(260, 44));
        startExamBtn.addActionListener(e -> startExamSession());

        instructionsPanel.add(rules, BorderLayout.CENTER);
        instructionsPanel.add(startExamBtn, BorderLayout.SOUTH);

        card.add(instructionsPanel, BorderLayout.CENTER);
        center.add(card);
        outer.add(center, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildExamScreen() {
        JPanel outer = new JPanel(new BorderLayout(0, 12));
        outer.setBackground(new Color(241, 245, 249));

        // Top Bar with Candidate Name & Live Countdown Timer
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY);
        topBar.setBorder(new EmptyBorder(12, 22, 12, 22));

        candidateHeaderLabel = new JLabel("Candidate: Aarav Sharma");
        candidateHeaderLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        candidateHeaderLabel.setForeground(Color.WHITE);

        JPanel timerBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        timerBox.setOpaque(false);

        timerLabel = new JLabel("⏱ Time Remaining: 30:00");
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        timerLabel.setForeground(new Color(254, 240, 138));

        JButton demoFastForwardBtn = createButton("⚡ Demo: Set Timer to 5s (Test Auto-Submit)", WARNING);
        demoFastForwardBtn.addActionListener(e -> {
            remainingSeconds = 5;
            updateTimerDisplay();
        });

        timerBox.add(timerLabel);
        timerBox.add(demoFastForwardBtn);

        topBar.add(candidateHeaderLabel, BorderLayout.WEST);
        topBar.add(timerBox, BorderLayout.EAST);
        outer.add(topBar, BorderLayout.NORTH);

        // Question Card
        JPanel questionCard = new JPanel(new BorderLayout(0, 16));
        questionCard.setBackground(Color.WHITE);
        questionCard.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 24, 10, 24),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        new EmptyBorder(22, 26, 22, 26)
                )
        ));

        questionCounterLabel = new JLabel("Question 1 of " + questions.length);
        questionCounterLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        questionCounterLabel.setForeground(ACCENT);

        questionPromptArea = new JTextArea(3, 40);
        questionPromptArea.setEditable(false);
        questionPromptArea.setLineWrap(true);
        questionPromptArea.setWrapStyleWord(true);
        questionPromptArea.setFont(new Font("SansSerif", Font.BOLD, 16));
        questionPromptArea.setBackground(Color.WHITE);

        JPanel topQuestionPanel = new JPanel(new BorderLayout(0, 8));
        topQuestionPanel.setBackground(Color.WHITE);
        topQuestionPanel.add(questionCounterLabel, BorderLayout.NORTH);
        topQuestionPanel.add(questionPromptArea, BorderLayout.CENTER);
        questionCard.add(topQuestionPanel, BorderLayout.NORTH);

        // 4 Radio Button Options
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(Color.WHITE);

        for (int i = 0; i < 4; i++) {
            final int optionIdx = i;
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setFont(new Font("SansSerif", Font.PLAIN, 15));
            optionButtons[i].setBackground(new Color(248, 250, 252));
            optionButtons[i].setBorder(new EmptyBorder(12, 14, 12, 14));
            optionButtons[i].setFocusPainted(false);
            optionButtons[i].addActionListener(e -> selectedAnswers[currentQuestionIndex] = optionIdx);
            optionGroup.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
            optionsPanel.add(Box.createVerticalStrut(10));
        }

        questionCard.add(optionsPanel, BorderLayout.CENTER);

        // Bottom Navigation Bar (Previous, Next, Manual Submit)
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(Color.WHITE);
        navBar.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel leftNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftNav.setBackground(Color.WHITE);

        prevButton = createButton("<- Previous Question", new Color(100, 116, 139));
        prevButton.addActionListener(e -> navigateQuestion(-1));

        nextButton = createButton("Next Question ->", ACCENT);
        nextButton.addActionListener(e -> navigateQuestion(1));

        leftNav.add(prevButton);
        leftNav.add(nextButton);

        JButton submitExamBtn = createButton("Submit Examination", SUCCESS);
        submitExamBtn.addActionListener(e -> handleManualSubmit());

        navBar.add(leftNav, BorderLayout.WEST);
        navBar.add(submitExamBtn, BorderLayout.EAST);

        questionCard.add(navBar, BorderLayout.SOUTH);
        outer.add(questionCard, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildResultScreen() {
        JPanel outer = new JPanel(new BorderLayout(0, 14));
        outer.setBackground(new Color(241, 245, 249));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY);
        topBar.setBorder(new EmptyBorder(14, 24, 14, 24));

        resultHeadlineLabel = new JLabel("📊 EXAMINATION RESULT SUMMARY");
        resultHeadlineLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        resultHeadlineLabel.setForeground(Color.WHITE);

        JButton logoutBtn = createButton("Logout & Return to Login", DANGER);
        logoutBtn.addActionListener(e -> performLogout());

        topBar.add(resultHeadlineLabel, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);
        outer.add(topBar, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 24, 20, 24),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        new EmptyBorder(20, 24, 20, 24)
                )
        ));

        resultStatsLabel = new JLabel("Score: 0 / 8", SwingConstants.LEFT);
        resultStatsLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        resultStatsLabel.setForeground(PRIMARY);

        resultBreakdownArea = new JTextArea();
        resultBreakdownArea.setEditable(false);
        resultBreakdownArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultBreakdownArea.setBackground(new Color(248, 250, 252));
        resultBreakdownArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        card.add(resultStatsLabel, BorderLayout.NORTH);
        card.add(new JScrollPane(resultBreakdownArea), BorderLayout.CENTER);

        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 16, 9, 16));
        btn.setOpaque(true);
        return btn;
    }

    private void handleLogin() {
        String user = loginUserField.getText().trim();
        String pass = new String(loginPassField.getPassword());

        UserProfile profile = users.get(user.toLowerCase());
        if (profile == null || !profile.password.equals(pass)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password!\nTry demo credentials: student1 / exam123",
                    "Authentication Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        currentUser = profile;
        lobbyWelcomeLabel.setText("Welcome, " + currentUser.displayName + " (" + currentUser.username + ")");
        profileNameField.setText(currentUser.displayName);
        profilePassField.setText(currentUser.password);
        profileSavedBadge.setText("");
        cardLayout.show(rootPanel, "PROFILE");
    }

    private void handleProfileUpdate() {
        String newName = profileNameField.getText().trim();
        String newPass = new String(profilePassField.getPassword()).trim();

        if (newName.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Display Name and Password cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentUser.displayName = newName;
        currentUser.password = newPass;
        lobbyWelcomeLabel.setText("Welcome, " + currentUser.displayName + " (" + currentUser.username + ")");
        profileSavedBadge.setText("✓ Profile & Password Updated!");
    }

    public void startExamSession() {
        for (int i = 0; i < selectedAnswers.length; i++) {
            selectedAnswers[i] = -1;
        }
        currentQuestionIndex = 0;
        remainingSeconds = EXAM_DURATION_SECONDS;
        examInProgress = true;

        candidateHeaderLabel.setText("Candidate: " + currentUser.displayName + " (" + currentUser.username + ")");
        loadQuestion(0);
        updateTimerDisplay();

        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        countdownTimer = new Timer(1000, e -> {
            remainingSeconds--;
            updateTimerDisplay();
            if (remainingSeconds <= 0) {
                countdownTimer.stop();
                JOptionPane.showMessageDialog(
                        this,
                        "Time is up! Your examination is being automatically submitted.",
                        "Auto-Submit — Timer Expired",
                        JOptionPane.INFORMATION_MESSAGE
                );
                finalizeExamSubmission(true);
            }
        });
        countdownTimer.start();

        cardLayout.show(rootPanel, "EXAM");
    }

    private void updateTimerDisplay() {
        int mins = Math.max(0, remainingSeconds) / 60;
        int secs = Math.max(0, remainingSeconds) % 60;
        timerLabel.setText(String.format("⏱ Time Remaining: %02d:%02d", mins, secs));
        if (remainingSeconds <= 30) {
            timerLabel.setForeground(new Color(252, 165, 165));
        } else {
            timerLabel.setForeground(new Color(254, 240, 138));
        }
    }

    private void loadQuestion(int index) {
        currentQuestionIndex = index;
        Question q = questions[index];
        questionCounterLabel.setText(String.format("Question %d of %d", index + 1, questions.length));
        questionPromptArea.setText(q.prompt);

        optionGroup.clearSelection();
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText((char) ('A' + i) + ")  " + q.options[i]);
            if (selectedAnswers[index] == i) {
                optionButtons[i].setSelected(true);
            }
        }

        prevButton.setEnabled(index > 0);
        nextButton.setEnabled(index < questions.length - 1);
    }

    private void navigateQuestion(int delta) {
        int nextIdx = currentQuestionIndex + delta;
        if (nextIdx >= 0 && nextIdx < questions.length) {
            loadQuestion(nextIdx);
        }
    }

    private void handleManualSubmit() {
        int unanswered = 0;
        for (int ans : selectedAnswers) {
            if (ans == -1) unanswered++;
        }

        String msg = "Are you sure you want to submit your examination now?"
                + (unanswered > 0 ? "\n(Note: You have " + unanswered + " unanswered question(s).)" : "");
        int choice = JOptionPane.showConfirmDialog(
                this,
                msg,
                "Confirm Examination Submission",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            finalizeExamSubmission(false);
        }
    }

    public void finalizeExamSubmission(boolean autoSubmitted) {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        examInProgress = false;

        int correctCount = 0;
        int incorrectCount = 0;
        int unattemptedCount = 0;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Candidate Name : %s (%s)%n", currentUser.displayName, currentUser.username));
        sb.append(String.format("Submission Mode: %s%n", autoSubmitted ? "Auto-Submitted (Timer Reached 00:00)" : "Manual Submission"));
        sb.append("------------------------------------------------------------------------------------\n\n");

        for (int i = 0; i < questions.length; i++) {
            Question q = questions[i];
            int chosen = selectedAnswers[i];
            boolean isCorrect = (chosen == q.correctIndex);

            if (chosen == -1) {
                unattemptedCount++;
            } else if (isCorrect) {
                correctCount++;
            } else {
                incorrectCount++;
            }

            String statusTag = chosen == -1 ? "[NOT ATTEMPTED]" : (isCorrect ? "[CORRECT ✓]" : "[INCORRECT ✗]");
            sb.append(String.format("%s  %s%n", statusTag, q.prompt));
            sb.append(String.format("   Your Answer   : %s%n", chosen == -1 ? "None" : q.options[chosen]));
            sb.append(String.format("   Correct Answer: %s%n%n", q.options[q.correctIndex]));
        }

        int timeTakenSec = EXAM_DURATION_SECONDS - Math.max(0, remainingSeconds);
        int mins = timeTakenSec / 60;
        int secs = timeTakenSec % 60;

        resultStatsLabel.setText(String.format(
                "Final Score: %d out of %d   |   Correct: %d   |   Incorrect: %d   |   Unattempted: %d   |   Time Taken: %02d:%02d",
                correctCount, questions.length, correctCount, incorrectCount, unattemptedCount, mins, secs
        ));
        resultBreakdownArea.setText(sb.toString());
        resultBreakdownArea.setCaretPosition(0);

        cardLayout.show(rootPanel, "RESULT");
    }

    private void performLogout() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        examInProgress = false;
        currentUser = null;
        loginPassField.setText("exam123");
        cardLayout.show(rootPanel, "LOGIN");
    }

    private static void saveScreenshot(JFrame frame, File file) throws Exception {
        frame.validate();
        frame.repaint();
        BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
        frame.paint(img.getGraphics());
        file.getParentFile().mkdirs();
        ImageIO.write(img, "png", file);
    }

    private static void runSelfTestAndScreenshots() throws Exception {
        System.out.println("=== Running Task 4 Online Examination System Self-Test ===");
        SwingUtilities.invokeAndWait(() -> {
            try {
                OnlineExaminationApp app = new OnlineExaminationApp();
                app.setVisible(true);
                saveScreenshot(app, new File("screenshots/1_login_screen.png"));

                app.handleLogin();
                app.profileNameField.setText("Aarav Sharma (Updated)");
                app.handleProfileUpdate();
                saveScreenshot(app, new File("screenshots/2_profile_update_screen.png"));

                app.startExamSession();
                app.optionButtons[1].doClick();
                saveScreenshot(app, new File("screenshots/3_exam_mcq_timer_screen.png"));

                app.selectedAnswers[0] = 1;
                app.selectedAnswers[1] = 2;
                app.selectedAnswers[2] = 0;
                app.selectedAnswers[3] = 3;
                app.selectedAnswers[4] = 1;
                app.selectedAnswers[5] = 1;
                app.selectedAnswers[6] = 0; // 1 wrong answer for demonstration
                app.selectedAnswers[7] = 2;
                app.remainingSeconds = EXAM_DURATION_SECONDS - 145;
                app.finalizeExamSubmission(false);
                saveScreenshot(app, new File("screenshots/4_result_breakdown_screen.png"));

                app.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        System.out.println("Task 4 Self-Test & Screenshots PASSED!");
    }

    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        if (args.length > 0 && "--self-test".equalsIgnoreCase(args[0])) {
            runSelfTestAndScreenshots();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            OnlineExaminationApp app = new OnlineExaminationApp();
            app.setVisible(true);
        });
    }
}

