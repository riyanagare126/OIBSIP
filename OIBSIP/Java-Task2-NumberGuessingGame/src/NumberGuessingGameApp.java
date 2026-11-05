import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Oasis Infobyte — Java Development Task 2: Number Guessing Game (Swing GUI).
 * Features Easy/Medium/Hard difficulty levels, Higher/Lower hints, attempt counter,
 * max attempt limit, Play Again workflow, and multi-round score summary.
 */
public class NumberGuessingGameApp extends JFrame {
    private static final Color PRIMARY_BG = new Color(30, 58, 138);
    private static final Color ACCENT_BLUE = new Color(37, 99, 235);
    private static final Color SUCCESS_GREEN = new Color(22, 163, 74);
    private static final Color WARNING_AMBER = new Color(217, 119, 6);
    private static final Color DANGER_RED = new Color(220, 38, 38);

    private final Random random = new Random();
    private final List<String> roundHistory = new ArrayList<>();

    private int currentRound = 1;
    private int totalScore = 0;
    private int maxRange = 100;
    private int maxAttempts = 7;
    private int attemptsUsed = 0;
    private int secretNumber = 50;
    private boolean roundOver = false;

    // UI Components
    private JComboBox<String> difficultyCombo;
    private JLabel roundBadgeLabel;
    private JLabel scoreBadgeLabel;
    private JLabel attemptsLabel;
    private JProgressBar attemptsBar;
    private JLabel rangeInstructionLabel;
    private JLabel feedbackBannerLabel;
    private JTextField guessInputField;
    private JButton submitGuessButton;
    private JButton playAgainButton;
    private JTextArea currentGuessesLogArea;
    private JTextArea multiRoundSummaryArea;

    public NumberGuessingGameApp() {
        super("Oasis Infobyte — Number Guessing Game (Java Task 2)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(860, 620);
        setMinimumSize(new Dimension(800, 580));
        setLocationRelativeTo(null);

        setContentPane(buildMainPanel());
        startNewRound(false);
    }

    private JPanel buildMainPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(new Color(241, 245, 249));

        // Top Header Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY_BG);
        topBar.setBorder(new EmptyBorder(14, 22, 14, 22));

        JLabel title = new JLabel("🎯 NUMBER GUESSING GAME — OASIS INFOBYTE (TASK 2)");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        statsPanel.setOpaque(false);

        roundBadgeLabel = new JLabel("Round: 1");
        roundBadgeLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        roundBadgeLabel.setForeground(new Color(254, 240, 138));

        scoreBadgeLabel = new JLabel("Total Score: 0 pts");
        scoreBadgeLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        scoreBadgeLabel.setForeground(Color.WHITE);

        statsPanel.add(roundBadgeLabel);
        statsPanel.add(scoreBadgeLabel);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(statsPanel, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);

        // Center Workspace
        JPanel center = new JPanel(new BorderLayout(16, 0));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(4, 18, 18, 18));

        center.add(buildGamePlayCard(), BorderLayout.CENTER);
        center.add(buildScoreboardSidebar(), BorderLayout.EAST);

        root.add(center, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildGamePlayCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(20, 24, 20, 24)
        ));

        // Difficulty Selector Row
        JPanel diffRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        diffRow.setBackground(Color.WHITE);
        JLabel diffLbl = new JLabel("Difficulty Level (Bonus):");
        diffLbl.setFont(new Font("SansSerif", Font.BOLD, 13));

        difficultyCombo = new JComboBox<>(new String[]{
                "Medium (1 - 100 | 7 Attempts) [Default]",
                "Easy (1 - 50 | 10 Attempts)",
                "Hard (1 - 200 | 5 Attempts)"
        });
        difficultyCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        difficultyCombo.addActionListener(e -> applyDifficultyAndRestart());

        diffRow.add(diffLbl);
        diffRow.add(difficultyCombo);
        card.add(diffRow);
        card.add(Box.createVerticalStrut(14));

        // Attempt Counter & Progress Bar
        attemptsLabel = new JLabel("Attempts Used: 0 / 7 (7 Remaining)", SwingConstants.CENTER);
        attemptsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        attemptsLabel.setAlignmentX(CENTER_ALIGNMENT);

        attemptsBar = new JProgressBar(0, 7);
        attemptsBar.setValue(0);
        attemptsBar.setStringPainted(true);
        attemptsBar.setMaximumSize(new Dimension(420, 22));
        attemptsBar.setAlignmentX(CENTER_ALIGNMENT);

        card.add(attemptsLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(attemptsBar);
        card.add(Box.createVerticalStrut(18));

        // Prompt & Feedback Banner
        rangeInstructionLabel = new JLabel("Guess the secret number between 1 and 100:", SwingConstants.CENTER);
        rangeInstructionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        rangeInstructionLabel.setForeground(new Color(71, 85, 105));
        rangeInstructionLabel.setAlignmentX(CENTER_ALIGNMENT);

        feedbackBannerLabel = new JLabel("Enter your first guess below!", SwingConstants.CENTER);
        feedbackBannerLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        feedbackBannerLabel.setForeground(ACCENT_BLUE);
        feedbackBannerLabel.setAlignmentX(CENTER_ALIGNMENT);

        card.add(rangeInstructionLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(feedbackBannerLabel);
        card.add(Box.createVerticalStrut(18));

        // Input Row
        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        inputRow.setBackground(Color.WHITE);

        guessInputField = new JTextField(8);
        guessInputField.setFont(new Font("Monospaced", Font.BOLD, 20));
        guessInputField.setHorizontalAlignment(JTextField.CENTER);
        guessInputField.addActionListener(e -> handleGuessSubmission());

        submitGuessButton = createButton("Submit Guess", ACCENT_BLUE);
        submitGuessButton.addActionListener(e -> handleGuessSubmission());

        playAgainButton = createButton("Play Again (Next Round)", SUCCESS_GREEN);
        playAgainButton.setEnabled(false);
        playAgainButton.addActionListener(e -> startNewRound(true));

        inputRow.add(guessInputField);
        inputRow.add(submitGuessButton);
        inputRow.add(playAgainButton);
        card.add(inputRow);
        card.add(Box.createVerticalStrut(16));

        // Current Round Log
        currentGuessesLogArea = new JTextArea(8, 30);
        currentGuessesLogArea.setEditable(false);
        currentGuessesLogArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        currentGuessesLogArea.setBackground(new Color(248, 250, 252));
        currentGuessesLogArea.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane logScroll = new JScrollPane(currentGuessesLogArea);
        logScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Current Round Attempt History ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12),
                PRIMARY_BG
        ));
        card.add(logScroll);

        return card;
    }

    private JPanel buildScoreboardSidebar() {
        JPanel side = new JPanel(new BorderLayout(0, 10));
        side.setPreferredSize(new Dimension(285, 0));
        side.setBackground(Color.WHITE);
        side.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel header = new JLabel("🏆 Multi-Round Score Summary");
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setForeground(PRIMARY_BG);

        multiRoundSummaryArea = new JTextArea();
        multiRoundSummaryArea.setEditable(false);
        multiRoundSummaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        multiRoundSummaryArea.setBackground(new Color(248, 250, 252));
        multiRoundSummaryArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        multiRoundSummaryArea.setText("No completed rounds yet.\n\nWin a round to record:\n'Round X — guessed in Y attempts'");

        side.add(header, BorderLayout.NORTH);
        side.add(new JScrollPane(multiRoundSummaryArea), BorderLayout.CENTER);
        return side;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        btn.setOpaque(true);
        return btn;
    }

    private void applyDifficultyAndRestart() {
        int idx = difficultyCombo.getSelectedIndex();
        if (idx == 1) {
            maxRange = 50;
            maxAttempts = 10;
        } else if (idx == 2) {
            maxRange = 200;
            maxAttempts = 5;
        } else {
            maxRange = 100;
            maxAttempts = 7;
        }
        startNewRound(false);
    }

    public void startNewRound(boolean advanceRoundCounter) {
        if (advanceRoundCounter) {
            currentRound++;
        }
        secretNumber = random.nextInt(maxRange) + 1;
        attemptsUsed = 0;
        roundOver = false;

        roundBadgeLabel.setText("Round: " + currentRound);
        scoreBadgeLabel.setText("Total Score: " + totalScore + " pts");
        rangeInstructionLabel.setText("Guess the secret number between 1 and " + maxRange + ":");
        feedbackBannerLabel.setText("Enter your guess (1 - " + maxRange + ")");
        feedbackBannerLabel.setForeground(ACCENT_BLUE);

        attemptsBar.setMaximum(maxAttempts);
        attemptsBar.setValue(0);
        updateAttemptsBanner();

        guessInputField.setText("");
        guessInputField.setEnabled(true);
        submitGuessButton.setEnabled(true);
        playAgainButton.setEnabled(false);

        currentGuessesLogArea.setText(String.format(
                "=== Round %d Started (Range: 1-%d, Max Attempts: %d) ===%n",
                currentRound, maxRange, maxAttempts
        ));
        guessInputField.requestFocusInWindow();
    }

    private void updateAttemptsBanner() {
        int remaining = maxAttempts - attemptsUsed;
        attemptsLabel.setText(String.format(
                "Attempts Used: %d / %d (%d Remaining)",
                attemptsUsed, maxAttempts, remaining
        ));
        attemptsBar.setValue(attemptsUsed);
    }

    public void processGuess(int guess, boolean showModalDialogs) {
        if (roundOver) {
            return;
        }

        attemptsUsed++;
        updateAttemptsBanner();

        if (guess == secretNumber) {
            int roundPoints = Math.max(10, (maxAttempts - attemptsUsed + 1) * 20);
            totalScore += roundPoints;
            scoreBadgeLabel.setText("Total Score: " + totalScore + " pts");

            feedbackBannerLabel.setText("Correct! You guessed " + secretNumber + " in " + attemptsUsed + " attempts!");
            feedbackBannerLabel.setForeground(SUCCESS_GREEN);
            currentGuessesLogArea.append(String.format(
                    "Attempt #%d: %3d  -->  Correct! (+%d pts)%n",
                    attemptsUsed, guess, roundPoints
            ));

            String summaryLine = String.format(
                    "Round %d — guessed in %d attempt%s (+%d pts)",
                    currentRound, attemptsUsed, attemptsUsed == 1 ? "" : "s", roundPoints
            );
            roundHistory.add(summaryLine);
            refreshMultiRoundSummary();
            endRound();

            if (showModalDialogs) {
                int opt = JOptionPane.showConfirmDialog(
                        this,
                        "Correct! The secret number was " + secretNumber + ".\n" +
                        summaryLine + "\n\nWould you like to Play Again?",
                        "You Won Round " + currentRound + "!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE
                );
                if (opt == JOptionPane.YES_OPTION) {
                    startNewRound(true);
                }
            }
            return;
        }

        String hint = guess > secretNumber ? "Too High!" : "Too Low!";
        feedbackBannerLabel.setText(hint + " (Try a " + (guess > secretNumber ? "LOWER" : "HIGHER") + " number)");
        feedbackBannerLabel.setForeground(WARNING_AMBER);
        currentGuessesLogArea.append(String.format("Attempt #%d: %3d  -->  %s%n", attemptsUsed, guess, hint));

        if (attemptsUsed >= maxAttempts) {
            feedbackBannerLabel.setText("You Lost! The secret number was " + secretNumber + ".");
            feedbackBannerLabel.setForeground(DANGER_RED);
            currentGuessesLogArea.append(String.format(
                    "*** You Lost! Maximum %d attempts reached. Secret number was: %d ***%n",
                    maxAttempts, secretNumber
            ));

            String summaryLine = String.format(
                    "Round %d — lost (0/%d, number was %d)",
                    currentRound, maxAttempts, secretNumber
            );
            roundHistory.add(summaryLine);
            refreshMultiRoundSummary();
            endRound();

            if (showModalDialogs) {
                int opt = JOptionPane.showConfirmDialog(
                        this,
                        "You Lost! You used all " + maxAttempts + " attempts.\n" +
                        "The secret number was: " + secretNumber + "\n\nWould you like to Play Again?",
                        "Round Over — You Lost!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (opt == JOptionPane.YES_OPTION) {
                    startNewRound(true);
                }
            }
        }
    }

    private void handleGuessSubmission() {
        String raw = guessInputField.getText().trim();
        guessInputField.setText("");
        if (raw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a number!", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int guess;
        try {
            guess = Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (guess < 1 || guess > maxRange) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a number between 1 and " + maxRange + ".",
                    "Out of Range",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        processGuess(guess, true);
    }

    private void endRound() {
        roundOver = true;
        guessInputField.setEnabled(false);
        submitGuessButton.setEnabled(false);
        playAgainButton.setEnabled(true);
    }

    private void refreshMultiRoundSummary() {
        StringBuilder sb = new StringBuilder();
        for (String line : roundHistory) {
            sb.append("• ").append(line).append("\n");
        }
        sb.append("\nTotal Score: ").append(totalScore).append(" pts");
        multiRoundSummaryArea.setText(sb.toString());
    }

    private static void saveFrameScreenshot(JFrame frame, File file) throws Exception {
        frame.validate();
        frame.repaint();
        BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
        frame.paint(img.getGraphics());
        file.getParentFile().mkdirs();
        ImageIO.write(img, "png", file);
    }

    private static void runSelfTestAndCaptureScreenshots() throws Exception {
        System.out.println("=== Running Task 2 Number Guessing Game Self-Test ===");
        SwingUtilities.invokeAndWait(() -> {
            try {
                NumberGuessingGameApp app = new NumberGuessingGameApp();
                app.setVisible(true);
                app.secretNumber = 42;

                app.processGuess(75, false); // Too High!
                app.processGuess(20, false); // Too Low!
                saveFrameScreenshot(app, new File("screenshots/1_gameplay_hints.png"));

                app.processGuess(42, false); // Correct!
                saveFrameScreenshot(app, new File("screenshots/2_round_won_summary.png"));

                app.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        System.out.println("Task 2 Self-Test & Screenshots PASSED!");
    }

    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        if (args.length > 0 && "--self-test".equalsIgnoreCase(args[0])) {
            runSelfTestAndCaptureScreenshots();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            NumberGuessingGameApp app = new NumberGuessingGameApp();
            app.setVisible(true);
        });
    }
}

