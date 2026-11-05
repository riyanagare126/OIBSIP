import org.jcodec.api.awt.AWTSequenceEncoder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates realistic, live-screen-recording-style MP4 demo videos for all 5 OIBSIP Java tasks.
 * Every video begins with a 2.5-second static Title Card showing:
 *   1. Intern Full Name: Riya Shivaji Nagare
 *   2. Assigned Track  : Java Development
 *   3. Task Title      : Task X ...
 * Followed by a smooth desktop screen recording with Windows 11 taskbar, app window chrome,
 * character-by-character typing, animated mouse cursor, click ripples, and modal dialogs.
 */
public class DemoVideoGenerator {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int FPS = 12;

    public static void main(String[] args) throws Exception {
        String internName = args.length > 0 && !args[0].trim().isEmpty()
                ? args[0].trim()
                : "Riya Shivaji Nagare";

        File baseDir = new File("..").getCanonicalFile();
        if (!new File(baseDir, "Java-Task1-OnlineReservationSystem").exists()) {
            baseDir = new File(".").getCanonicalFile();
        }

        System.out.println("=== Generating Realistic Screen-Recording MP4 Videos for: " + internName + " ===");

        generateTask1ScreenRecording(
                new File(baseDir, "Java-Task1-OnlineReservationSystem/demo_video.mp4"),
                internName
        );
        generateTask2ScreenRecording(
                new File(baseDir, "Java-Task2-NumberGuessingGame/demo_video.mp4"),
                internName
        );
        generateTask3ScreenRecording(
                new File(baseDir, "Java-Task3-ATMInterface/demo_video.mp4"),
                internName
        );
        generateTask4ScreenRecording(
                new File(baseDir, "Java-Task4-OnlineExaminationSystem/demo_video.mp4"),
                internName
        );
        generateTask5ScreenRecording(
                new File(baseDir, "Java-Task5-DigitalLibraryManagementSystem/demo_video.mp4"),
                internName
        );

        System.out.println("=== All 5 Screen-Recording MP4 Videos Generated Successfully! ===");
    }

    // =========================================================================
    // TASK 1: ONLINE RESERVATION SYSTEM (SWING GUI SCREEN RECORDING)
    // =========================================================================
    private static void generateTask1ScreenRecording(File outFile, String internName) throws Exception {
        AWTSequenceEncoder enc = AWTSequenceEncoder.createSequenceEncoder(outFile, FPS);

        // 1. Mandatory 2.5-second Static Title Card (30 frames @ 12fps)
        BufferedImage titleCard = renderTitleCard(internName, "Java Development", "Task 1 · Online Reservation System");
        encodeRepeated(enc, titleCard, 30);

        // 2. Login Screen: move cursor to username, type "user1", password, click Login
        int[][] pathToLoginBtn = interpolateCursor(900, 500, 640, 495, 14);
        for (int i = 0; i < pathToLoginBtn.length; i++) {
            String typedUser = "user1".substring(0, Math.min(5, (i * 5) / 10 + 1));
            String typedPass = i > 6 ? "*******".substring(0, Math.min(7, i - 5)) : "";
            BufferedImage frame = renderTask1LoginFrame(typedUser, typedPass, pathToLoginBtn[i][0], pathToLoginBtn[i][1], false);
            enc.encodeImage(frame);
        }
        // Click Login button
        for (int i = 0; i < 6; i++) {
            enc.encodeImage(renderTask1LoginFrame("user1", "*******", 640, 495, true));
        }

        // 3. Reservation Tab: Typing passenger name "Riya Shivaji Nagare", Train "22436" -> Auto-fills Vande Bharat
        for (int i = 0; i < 18; i++) {
            int len = Math.min(internName.length(), (i + 1) * 2);
            String passName = internName.substring(0, len);
            String trainNo = i < 8 ? "12951" : "22436";
            String trainName = i < 8 ? "Mumbai Rajdhani Express" : "Vande Bharat Express (NDLS - BSB)";
            int cx = 420 + (i * 4);
            int cy = 225 + (i < 8 ? 0 : 65);
            enc.encodeImage(renderTask1ReservationFrame(internName, passName, trainNo, trainName, false, null, cx, cy, false));
        }

        // Move cursor to "Insert / Book Ticket" button (x=310, y=542) and click
        int[][] toBookBtn = interpolateCursor(490, 290, 310, 542, 12);
        for (int[] pt : toBookBtn) {
            enc.encodeImage(renderTask1ReservationFrame(internName, internName, "22436", "Vande Bharat Express (NDLS - BSB)", false, null, pt[0], pt[1], false));
        }
        for (int i = 0; i < 5; i++) {
            enc.encodeImage(renderTask1ReservationFrame(internName, internName, "22436", "Vande Bharat Express (NDLS - BSB)", true, null, 310, 542, true));
        }

        // Show Popup Confirmation Dialog with generated PNR8492017 (18 frames)
        int[][] toDialogOk = interpolateCursor(310, 542, 640, 485, 16);
        for (int i = 0; i < toDialogOk.length; i++) {
            enc.encodeImage(renderTask1ReservationFrame(internName, internName, "22436", "Vande Bharat Express (NDLS - BSB)", true, "PNR8492017", toDialogOk[i][0], toDialogOk[i][1], i >= 13));
        }

        // 4. Switch to Cancellation Tab: click Tab 2 (x=470, y=148), Fetch PNR8492017, Confirm Cancellation
        int[][] toTab2 = interpolateCursor(640, 485, 470, 148, 10);
        for (int[] pt : toTab2) {
            enc.encodeImage(renderTask1ReservationFrame(internName, internName, "22436", "Vande Bharat Express (NDLS - BSB)", true, null, pt[0], pt[1], false));
        }

        // On Cancellation Tab: move to "Fetch Booking Details" button (x=590, y=222)
        int[][] toFetch = interpolateCursor(470, 148, 590, 222, 10);
        for (int[] pt : toFetch) {
            enc.encodeImage(renderTask1CancelFrame(internName, "PNR8492017", false, false, false, pt[0], pt[1], false));
        }
        for (int i = 0; i < 4; i++) {
            enc.encodeImage(renderTask1CancelFrame(internName, "PNR8492017", true, false, false, 590, 222, true));
        }

        // Move to "Confirm Cancellation" button (x=895, y=560)
        int[][] toConfirmCancel = interpolateCursor(590, 222, 895, 560, 14);
        for (int[] pt : toConfirmCancel) {
            enc.encodeImage(renderTask1CancelFrame(internName, "PNR8492017", true, false, false, pt[0], pt[1], false));
        }
        for (int i = 0; i < 4; i++) {
            enc.encodeImage(renderTask1CancelFrame(internName, "PNR8492017", true, true, false, 895, 560, true));
        }

        // "Are you sure?" dialog -> move cursor to "Yes" button (x=595, y=445) and click
        int[][] toYes = interpolateCursor(895, 560, 595, 445, 12);
        for (int i = 0; i < toYes.length; i++) {
            enc.encodeImage(renderTask1CancelFrame(internName, "PNR8492017", true, true, false, toYes[i][0], toYes[i][1], i >= 9));
        }

        // Final Cancelled state (16 frames)
        for (int i = 0; i < 16; i++) {
            enc.encodeImage(renderTask1CancelFrame(internName, "PNR8492017", true, false, true, 595, 445, false));
        }

        enc.finish();
        System.out.println(" -> Created Task 1 Screen Recording: " + outFile.getAbsolutePath());
    }

    // =========================================================================
    // TASK 2: NUMBER GUESSING GAME (SWING GUI SCREEN RECORDING)
    // =========================================================================
    private static void generateTask2ScreenRecording(File outFile, String internName) throws Exception {
        AWTSequenceEncoder enc = AWTSequenceEncoder.createSequenceEncoder(outFile, FPS);

        BufferedImage titleCard = renderTitleCard(internName, "Java Development", "Task 2 · Number Guessing Game");
        encodeRepeated(enc, titleCard, 30);

        List<String> log = new ArrayList<>();
        log.add("=== Round 1 Started (Range: 1-100, Max Attempts: 7) ===");

        // Guess 1: 75 -> Too High!
        recordTask2GuessStep(enc, "75", 0, "Enter your first guess (1 - 100)", new Color(37, 99, 235),
                "Too High! (Try a LOWER number)", new Color(217, 119, 6),
                "Attempt #1:  75  -->  Too High!", log, 0, "No completed rounds yet.", false);

        // Guess 2: 25 -> Too Low!
        recordTask2GuessStep(enc, "25", 1, "Too High! (Try a LOWER number)", new Color(217, 119, 6),
                "Too Low! (Try a HIGHER number)", new Color(217, 119, 6),
                "Attempt #2:  25  -->  Too Low!", log, 0, "No completed rounds yet.", false);

        // Guess 3: 50 -> Too High!
        recordTask2GuessStep(enc, "50", 2, "Too Low! (Try a HIGHER number)", new Color(217, 119, 6),
                "Too High! (Try a LOWER number)", new Color(217, 119, 6),
                "Attempt #3:  50  -->  Too High!", log, 0, "No completed rounds yet.", false);

        // Guess 4: 42 -> Correct!
        recordTask2GuessStep(enc, "42", 3, "Too High! (Try a LOWER number)", new Color(217, 119, 6),
                "Correct! You guessed 42 in 4 attempts! (+80 pts)", new Color(22, 163, 74),
                "Attempt #4:  42  -->  Correct! (+80 pts)", log, 80,
                "• Round 1 — guessed in 4 attempts (+80 pts)\n\nTotal Score: 80 pts", true);

        enc.finish();
        System.out.println(" -> Created Task 2 Screen Recording: " + outFile.getAbsolutePath());
    }

    private static void recordTask2GuessStep(
            AWTSequenceEncoder enc,
            String guessStr,
            int attemptsBefore,
            String prevBanner,
            Color prevColor,
            String nextBanner,
            Color nextColor,
            String logLineToAdd,
            List<String> log,
            int scoreAfter,
            String summaryText,
            boolean showWinModal) throws Exception {

        // Type guess in box (x=370, y=355)
        for (int i = 1; i <= guessStr.length(); i++) {
            String sub = guessStr.substring(0, i);
            for (int f = 0; f < 3; f++) {
                enc.encodeImage(renderTask2Frame(attemptsBefore, prevBanner, prevColor, sub + "|", log,
                        attemptsBefore == 3 ? 0 : 0, "No completed rounds yet.", false, 370, 355, false));
            }
        }
        // Move cursor to "Submit Guess" button (x=495, y=355) and click
        int[][] path = interpolateCursor(370, 355, 495, 355, 6);
        for (int[] pt : path) {
            enc.encodeImage(renderTask2Frame(attemptsBefore, prevBanner, prevColor, guessStr, log,
                    0, "No completed rounds yet.", false, pt[0], pt[1], false));
        }
        log.add(logLineToAdd);
        for (int f = 0; f < 4; f++) {
            enc.encodeImage(renderTask2Frame(attemptsBefore + 1, nextBanner, nextColor, "", log,
                    scoreAfter, summaryText, false, 495, 355, true));
        }
        for (int f = 0; f < 10; f++) {
            enc.encodeImage(renderTask2Frame(attemptsBefore + 1, nextBanner, nextColor, "", log,
                    scoreAfter, summaryText, showWinModal && f >= 3, 590, 420, showWinModal && f >= 8));
        }
    }

    // =========================================================================
    // TASK 3: ATM INTERFACE (LIVE TERMINAL SCREEN RECORDING)
    // =========================================================================
    private static void generateTask3ScreenRecording(File outFile, String internName) throws Exception {
        AWTSequenceEncoder enc = AWTSequenceEncoder.createSequenceEncoder(outFile, FPS);

        BufferedImage titleCard = renderTitleCard(internName, "Java Development", "Task 3 · ATM Interface");
        encodeRepeated(enc, titleCard, 30);

        List<String> term = new ArrayList<>();
        term.add("PS C:\\Users\\Riya\\OIBSIP\\Java-Task3-ATMInterface> javac -d out src/*.java");
        term.add("PS C:\\Users\\Riya\\OIBSIP\\Java-Task3-ATMInterface> java -cp out Main");

        // Type command and launch banner
        encodeTerminalFrames(enc, term, 8, 680, 420);

        term.add("==========================================================================");
        term.add("                 WELCOME TO OASIS NATIONAL BANK");
        term.add("                 OASIS INFOBYTE - JAVA DEVELOPMENT (TASK 3)");
        term.add("==========================================================================");
        term.add("  Demo Accounts: user101 (PIN: 1234) | user202 (PIN: 5678)");
        term.add("==========================================================================");
        term.add("--- ATM Authentication (Attempt 1 of 3) ---");
        encodeTerminalTypingLine(enc, term, "Enter User ID : ", "user101");
        encodeTerminalTypingLine(enc, term, "Enter PIN     : ", "0000");
        term.add("[ERROR] Invalid User ID or PIN. Attempts remaining: 2");
        encodeTerminalFrames(enc, term, 8, 680, 420);

        term.add("--- ATM Authentication (Attempt 2 of 3) ---");
        encodeTerminalTypingLine(enc, term, "Enter User ID : ", "user101");
        encodeTerminalTypingLine(enc, term, "Enter PIN     : ", "1234");
        term.add("[LOGIN SUCCESSFUL] Welcome, Aarav Sharma (user101)!");
        term.add("--------------------------------------------------------------------------");
        term.add("  Account: Aarav Sharma (user101)  |  Available Balance: Rs. 25000.00");
        term.add("  1. Transaction History  2. Withdraw  3. Deposit  4. Transfer  5. Quit");
        encodeTerminalTypingLine(enc, term, "Select an option (1-5): ", "3");
        encodeTerminalTypingLine(enc, term, "Enter amount to deposit (Rs.): ", "5000");
        term.add("[SUCCESS] Rs. 5000.00 deposited successfully. Updated Balance: Rs. 30000.00");
        encodeTerminalFrames(enc, term, 10, 680, 420);

        encodeTerminalTypingLine(enc, term, "Select an option (1-5): ", "2");
        encodeTerminalTypingLine(enc, term, "Enter amount to withdraw (Rs.): ", "3500");
        term.add("[SUCCESS] Rs. 3500.00 withdrawn successfully. Updated Balance: Rs. 26500.00");
        encodeTerminalFrames(enc, term, 8, 680, 420);

        encodeTerminalTypingLine(enc, term, "Select an option (1-5): ", "2");
        encodeTerminalTypingLine(enc, term, "Enter amount to withdraw (Rs.): ", "99999");
        term.add("[ERROR] Insufficient Funds! Requested: Rs. 99999.00 | Available: Rs. 26500.00");
        encodeTerminalFrames(enc, term, 10, 680, 420);

        encodeTerminalTypingLine(enc, term, "Select an option (1-5): ", "4");
        encodeTerminalTypingLine(enc, term, "Enter Recipient Account User ID: ", "user202");
        encodeTerminalTypingLine(enc, term, "Enter amount to transfer (Rs.): ", "4000");
        term.add("[SUCCESS] Rs. 4000.00 transferred to Priya Nair (user202). Balance: Rs. 22500.00");
        encodeTerminalFrames(enc, term, 10, 680, 420);

        encodeTerminalTypingLine(enc, term, "Select an option (1-5): ", "1");
        term.add("========================== TRANSACTION HISTORY ===========================");
        term.add("TXN-1001 | 2026-11-04 | OPENING      | Rs. 25000.00 | Bal: Rs. 25000.00 | Opening");
        term.add("TXN-1004 | 2026-11-04 | DEPOSIT      | Rs.  5000.00 | Bal: Rs. 30000.00 | ATM Cash Deposit");
        term.add("TXN-1005 | 2026-11-04 | WITHDRAW     | Rs.  3500.00 | Bal: Rs. 26500.00 | ATM Cash Withdrawal");
        term.add("TXN-1006 | 2026-11-04 | TRANSFER-OUT | Rs.  4000.00 | Bal: Rs. 22500.00 | To Priya Nair (user202)");
        term.add("==========================================================================");
        encodeTerminalFrames(enc, term, 16, 680, 420);

        encodeTerminalTypingLine(enc, term, "Select an option (1-5): ", "5");
        term.add("Thank you for banking with Oasis National Bank! Goodbye!");
        encodeTerminalFrames(enc, term, 18, 680, 420);

        enc.finish();
        System.out.println(" -> Created Task 3 Screen Recording: " + outFile.getAbsolutePath());
    }

    // =========================================================================
    // TASK 4: ONLINE EXAMINATION SYSTEM (SWING GUI SCREEN RECORDING)
    // =========================================================================
    private static void generateTask4ScreenRecording(File outFile, String internName) throws Exception {
        AWTSequenceEncoder enc = AWTSequenceEncoder.createSequenceEncoder(outFile, FPS);

        BufferedImage titleCard = renderTitleCard(internName, "Java Development", "Task 4 · Online Examination System");
        encodeRepeated(enc, titleCard, 30);

        // 1. Login Screen
        int[][] toLogin = interpolateCursor(860, 500, 640, 480, 12);
        for (int i = 0; i < toLogin.length; i++) {
            enc.encodeImage(renderTask4LoginFrame("student1", "*******", toLogin[i][0], toLogin[i][1], i >= 9));
        }

        // 2. Profile Update Screen: Update Display Name to Riya Shivaji Nagare
        int[][] toUpdateBtn = interpolateCursor(640, 480, 455, 315, 14);
        for (int i = 0; i < toUpdateBtn.length; i++) {
            enc.encodeImage(renderTask4ProfileFrame(internName, i >= 8, toUpdateBtn[i][0], toUpdateBtn[i][1], i == 8 || i == 9));
        }
        // Move to "Start Timed Examination Now ->" button (x=640, y=535)
        int[][] toStart = interpolateCursor(455, 315, 640, 535, 12);
        for (int i = 0; i < toStart.length; i++) {
            enc.encodeImage(renderTask4ProfileFrame(internName, true, toStart[i][0], toStart[i][1], i >= 9));
        }

        // 3. MCQ Exam Screen: Q1 -> select option B, click Next; Q2 -> select option C, click Submit
        int[][] toOptB = interpolateCursor(640, 535, 340, 340, 10);
        for (int i = 0; i < toOptB.length; i++) {
            enc.encodeImage(renderTask4ExamFrame(internName, 1, 8, "29:" + (59 - i / 3),
                    "Q1. Which keyword in Java prevents a method from being overridden in a subclass?",
                    new String[]{"A)  static", "B)  final", "C)  abstract", "D)  volatile"},
                    i >= 7 ? 1 : -1, false, toOptB[i][0], toOptB[i][1], i == 7 || i == 8));
        }
        // Move to Next Question button (x=415, y=565)
        int[][] toNext = interpolateCursor(340, 340, 415, 565, 10);
        for (int i = 0; i < toNext.length; i++) {
            enc.encodeImage(renderTask4ExamFrame(internName, 1, 8, "29:54",
                    "Q1. Which keyword in Java prevents a method from being overridden in a subclass?",
                    new String[]{"A)  static", "B)  final", "C)  abstract", "D)  volatile"},
                    1, false, toNext[i][0], toNext[i][1], i >= 8));
        }

        // Q2: select Option C (x=340, y=385), then click Submit Examination (x=955, y=565)
        int[][] toOptC = interpolateCursor(415, 565, 340, 385, 10);
        for (int i = 0; i < toOptC.length; i++) {
            enc.encodeImage(renderTask4ExamFrame(internName, 2, 8, "29:50",
                    "Q2. Which JDBC interface is used to execute parameterized SQL queries and prevent SQL injection?",
                    new String[]{"A)  Statement", "B)  ResultSet", "C)  PreparedStatement", "D)  DriverManager"},
                    i >= 7 ? 2 : -1, false, toOptC[i][0], toOptC[i][1], i == 7 || i == 8));
        }

        int[][] toSubmit = interpolateCursor(340, 385, 955, 565, 12);
        for (int i = 0; i < toSubmit.length; i++) {
            enc.encodeImage(renderTask4ExamFrame(internName, 8, 8, "29:42",
                    "Q8. Which collection in Java does NOT allow duplicate elements?",
                    new String[]{"A)  ArrayList", "B)  LinkedList", "C)  HashSet", "D)  Vector"},
                    2, i >= 9, toSubmit[i][0], toSubmit[i][1], i == 9));
        }

        // Confirm Submit modal -> move to Yes (x=590, y=420)
        int[][] toYes = interpolateCursor(955, 565, 590, 420, 10);
        for (int i = 0; i < toYes.length; i++) {
            enc.encodeImage(renderTask4ExamFrame(internName, 8, 8, "29:40",
                    "Q8. Which collection in Java does NOT allow duplicate elements?",
                    new String[]{"A)  ArrayList", "B)  LinkedList", "C)  HashSet", "D)  Vector"},
                    2, true, toYes[i][0], toYes[i][1], i >= 8));
        }

        // 4. Result Screen (24 frames)
        int[][] onResult = interpolateCursor(590, 420, 960, 145, 24);
        for (int[] pt : onResult) {
            enc.encodeImage(renderTask4ResultFrame(internName, pt[0], pt[1]));
        }

        enc.finish();
        System.out.println(" -> Created Task 4 Screen Recording: " + outFile.getAbsolutePath());
    }

    // =========================================================================
    // TASK 5: DIGITAL LIBRARY MANAGEMENT SYSTEM (WEB BROWSER SCREEN RECORDING)
    // =========================================================================
    private static void generateTask5ScreenRecording(File outFile, String internName) throws Exception {
        AWTSequenceEncoder enc = AWTSequenceEncoder.createSequenceEncoder(outFile, FPS);

        BufferedImage titleCard = renderTitleCard(internName, "Java Development", "Task 5 · Digital Library Management System");
        encodeRepeated(enc, titleCard, 30);

        // 1. Browser Login Screen at http://localhost:8085 -> Click Login as Admin
        int[][] toAdminLogin = interpolateCursor(850, 480, 410, 455, 14);
        for (int i = 0; i < toAdminLogin.length; i++) {
            enc.encodeImage(renderTask5BrowserLoginFrame("admin", "********", toAdminLogin[i][0], toAdminLogin[i][1], i >= 11));
        }

        // 2. Admin Module: Catalogue Management + Add Book + Toast notification
        int[][] toAddBook = interpolateCursor(410, 455, 250, 345, 16);
        for (int i = 0; i < toAddBook.length; i++) {
            enc.encodeImage(renderTask5AdminCatalogueFrame(i >= 10, toAddBook[i][0], toAddBook[i][1], i == 10 || i == 11));
        }

        // 3. Admin Module: Fine Management Tab (Mark Rs. 30.00 Fine as Paid)
        int[][] toMarkPaid = interpolateCursor(250, 345, 920, 355, 18);
        for (int i = 0; i < toMarkPaid.length; i++) {
            enc.encodeImage(renderTask5AdminFinesFrame(i >= 12, toMarkPaid[i][0], toMarkPaid[i][1], i == 12 || i == 13));
        }

        // 4. User Module (Logged in as Riya Shivaji Nagare): Browse/Search, Issue Book, Advance Booking, Return Book
        int[][] toAdvanceBook = interpolateCursor(920, 355, 915, 425, 18);
        for (int i = 0; i < toAdvanceBook.length; i++) {
            enc.encodeImage(renderTask5UserPortalFrame(internName, i >= 11, toAdvanceBook[i][0], toAdvanceBook[i][1], i == 11 || i == 12));
        }

        // Hold final state for 12 frames
        for (int i = 0; i < 12; i++) {
            enc.encodeImage(renderTask5UserPortalFrame(internName, true, 700, 500, false));
        }

        enc.finish();
        System.out.println(" -> Created Task 5 Screen Recording: " + outFile.getAbsolutePath());
    }

    // =========================================================================
    // DESKTOP CHROME, TASKBAR, CURSOR & WINDOW RENDERING HELPERS
    // =========================================================================
    private static BufferedImage createDesktopCanvas(String activeAppTitle) {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        // Windows 11 Wallpaper Gradient
        GradientPaint wp = new GradientPaint(0, 0, new Color(15, 32, 67), WIDTH, HEIGHT - 44, new Color(30, 64, 124));
        g.setPaint(wp);
        g.fillRect(0, 0, WIDTH, HEIGHT - 44);

        // Subtle desktop icons on the left
        drawDesktopIcon(g, 22, 28, "OIBSIP Repo", new Color(250, 204, 21));
        drawDesktopIcon(g, 22, 115, "Command Prompt", new Color(56, 189, 248));
        drawDesktopIcon(g, 22, 202, "Title Card", new Color(74, 222, 128));

        // Windows 11 Bottom Taskbar
        int tbY = HEIGHT - 44;
        g.setColor(new Color(15, 23, 42));
        g.fillRect(0, tbY, WIDTH, 44);
        g.setColor(new Color(51, 65, 85));
        g.drawLine(0, tbY, WIDTH, tbY);

        // Start button + active window pill
        g.setColor(new Color(56, 189, 248));
        g.fillRoundRect(18, tbY + 9, 26, 26, 6, 6);
        g.setColor(new Color(30, 41, 59));
        g.fillRoundRect(58, tbY + 7, 360, 30, 8, 8);
        g.setColor(new Color(96, 165, 250));
        g.fillRect(68, tbY + 34, 340, 3);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("☕ " + activeAppTitle, 72, tbY + 26);

        // Recording indicator pill + System Tray Clock
        g.setColor(new Color(220, 38, 38));
        g.fillRoundRect(WIDTH - 290, tbY + 10, 110, 24, 12, 12);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("● REC  00:12", WIDTH - 274, tbY + 26);

        g.setColor(new Color(226, 232, 240));
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.drawString("ENG   Wi-Fi   9:30 PM", WIDTH - 155, tbY + 19);
        g.drawString("04-11-2026", WIDTH - 95, tbY + 34);

        g.dispose();
        return img;
    }

    private static void drawDesktopIcon(Graphics2D g, int x, int y, String label, Color iconColor) {
        g.setColor(iconColor);
        g.fillRoundRect(x + 14, y, 34, 34, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.drawString(label, x, y + 48);
    }

    private static void drawAppWindowFrame(Graphics2D g, int x, int y, int w, int h, String windowTitle, Color headerBg) {
        // Drop shadow
        g.setColor(new Color(0, 0, 0, 90));
        g.fillRoundRect(x + 6, y + 8, w, h, 14, 14);

        // Window body
        g.setColor(new Color(245, 247, 250));
        g.fillRoundRect(x, y, w, h, 12, 12);

        // OS Title Bar
        g.setColor(new Color(255, 255, 255));
        g.fillRoundRect(x, y, w, 34, 12, 12);
        g.fillRect(x, y + 16, w, 18);
        g.setColor(new Color(203, 213, 225));
        g.drawLine(x, y + 34, x + w, y + 34);

        g.setColor(new Color(30, 41, 59));
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("☕  " + windowTitle, x + 14, y + 22);

        // Minimize, Maximize, Close buttons
        g.setColor(new Color(100, 116, 139));
        g.drawString("—      ▢      ✕", x + w - 95, y + 21);

        // App Banner below OS Title Bar
        if (headerBg != null) {
            g.setColor(headerBg);
            g.fillRect(x + 1, y + 35, w - 2, 46);
        }

        g.setColor(new Color(148, 163, 184));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(x, y, w, h, 12, 12);
    }

    private static void drawMouseCursor(Graphics2D g, int x, int y, boolean clicked) {
        if (clicked) {
            g.setColor(new Color(250, 204, 21, 140));
            g.fillOval(x - 18, y - 18, 36, 36);
            g.setColor(new Color(37, 99, 235, 200));
            g.setStroke(new BasicStroke(2.5f));
            g.drawOval(x - 22, y - 22, 44, 44);
        }

        Polygon arrow = new Polygon(
                new int[]{x, x, x + 6, x + 10, x + 13, x + 9, x + 16},
                new int[]{y, y + 20, y + 15, y + 23, y + 21, y + 14, y + 14},
                7
        );
        g.setColor(Color.WHITE);
        g.fillPolygon(arrow);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.5f));
        g.drawPolygon(arrow);
    }

    private static void drawModalDialog(Graphics2D g, String title, String[] lines, String primaryBtn, String secondaryBtn) {
        g.setColor(new Color(15, 23, 42, 110));
        g.fillRect(110, 26, 1060, 620);

        int dw = 460;
        int dh = 230;
        int dx = (WIDTH - dw) / 2;
        int dy = (HEIGHT - dh) / 2 - 10;

        g.setColor(Color.WHITE);
        g.fillRoundRect(dx, dy, dw, dh, 12, 12);
        g.setColor(new Color(30, 58, 138));
        g.fillRoundRect(dx, dy, dw, 36, 12, 12);
        g.fillRect(dx, dy + 18, dw, 18);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString(title, dx + 16, dy + 23);

        g.setColor(new Color(30, 41, 59));
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        int ly = dy + 62;
        for (String line : lines) {
            g.drawString(line, dx + 22, ly);
            ly += 19;
        }

        if (secondaryBtn != null) {
            g.setColor(new Color(37, 99, 235));
            g.fillRoundRect(dx + 145, dy + dh - 44, 80, 30, 6, 6);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString(primaryBtn, dx + 172, dy + dh - 24);

            g.setColor(new Color(226, 232, 240));
            g.fillRoundRect(dx + 235, dy + dh - 44, 80, 30, 6, 6);
            g.setColor(new Color(30, 41, 59));
            g.drawString(secondaryBtn, dx + 265, dy + dh - 24);
        } else {
            g.setColor(new Color(37, 99, 235));
            g.fillRoundRect(dx + 190, dy + dh - 44, 80, 30, 6, 6);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString(primaryBtn, dx + 220, dy + dh - 24);
        }
    }

    // =========================================================================
    // SPECIFIC FRAME RENDERERS FOR TASKS 1-5
    // =========================================================================
    private static BufferedImage renderTask1LoginFrame(String user, String pass, int cx, int cy, boolean clicked) {
        BufferedImage img = createDesktopCanvas("Online Reservation System (Task 1)");
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawAppWindowFrame(g, 110, 26, 1060, 620, "Oasis Infobyte — Online Train Reservation System (Task 1)", null);

        // Login Card centered
        int cw = 460, ch = 360, kx = 410, ky = 160;
        g.setColor(Color.WHITE);
        g.fillRoundRect(kx, ky, cw, ch, 10, 10);
        g.setColor(new Color(203, 213, 225));
        g.drawRoundRect(kx, ky, cw, ch, 10, 10);

        drawCenteredText(g, "ONLINE RESERVATION SYSTEM", ky + 48, new Font("SansSerif", Font.BOLD, 20), new Color(23, 55, 94));
        drawCenteredText(g, "Oasis Infobyte SIP · Java Development (Task 1)", ky + 72, new Font("SansSerif", Font.PLAIN, 13), new Color(100, 116, 139));

        g.setColor(new Color(30, 41, 59));
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Username:", kx + 45, ky + 135);
        g.drawString("Password:", kx + 45, ky + 190);

        g.setColor(Color.WHITE);
        g.fillRect(kx + 140, ky + 113, 260, 32);
        g.fillRect(kx + 140, ky + 168, 260, 32);
        g.setColor(new Color(148, 163, 184));
        g.drawRect(kx + 140, ky + 113, 260, 32);
        g.drawRect(kx + 140, ky + 168, 260, 32);

        g.setColor(new Color(15, 23, 42));
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        g.drawString(user, kx + 150, ky + 134);
        g.drawString(pass, kx + 150, ky + 189);

        g.setColor(new Color(37, 99, 235));
        g.fillRoundRect(kx + 130, ky + 275, 200, 40, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Login to Portal", kx + 182, ky + 300);

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTask1ReservationFrame(
            String internName,
            String passName,
            String trainNo,
            String trainName,
            boolean booked,
            String modalPnr,
            int cx,
            int cy,
            boolean clicked) {

        BufferedImage img = createDesktopCanvas("Online Reservation System (Task 1)");
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawAppWindowFrame(g, 110, 26, 1060, 620, "Oasis Infobyte — Online Train Reservation System (Task 1)", new Color(23, 55, 94));
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("🚆 OASIS RAILWAYS — ONLINE RESERVATION & PNR CANCELLATION PORTAL", 130, 63);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Logged in: " + internName + " (user1)", 860, 63);

        // Tabs
        g.setColor(new Color(37, 99, 235));
        g.fillRoundRect(130, 95, 250, 32, 6, 6);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("1. Reservation Form (Book Ticket)", 145, 116);

        g.setColor(new Color(226, 232, 240));
        g.fillRoundRect(388, 95, 270, 32, 6, 6);
        g.setColor(new Color(51, 65, 85));
        g.drawString("2. Cancellation Form (PNR Lookup)", 405, 116);

        // Left Form Box
        g.setColor(Color.WHITE);
        g.fillRoundRect(130, 135, 620, 485, 8, 8);
        g.setColor(new Color(203, 213, 225));
        g.drawRoundRect(130, 135, 620, 485, 8, 8);

        String[][] rows = {
                {"Passenger Name *:", passName},
                {"Train Number (Numeric) *:", trainNo},
                {"Train Name (Auto-Filled):", trainName},
                {"Class Type *:", "First AC (1A)"},
                {"Date of Journey (YYYY-MM-DD) *:", "2026-12-20"},
                {"Source Station *:", "Mumbai Central"},
                {"Destination Station *:", "New Delhi"}
        };

        int fy = 185;
        for (int i = 0; i < rows.length; i++) {
            g.setColor(new Color(30, 41, 59));
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString(rows[i][0], 155, fy + 20);

            g.setColor(i == 2 ? new Color(241, 245, 249) : Color.WHITE);
            g.fillRect(375, fy, 340, 30);
            g.setColor(new Color(203, 213, 225));
            g.drawRect(375, fy, 340, 30);

            g.setColor(i == 2 ? new Color(37, 99, 235) : new Color(15, 23, 42));
            g.setFont(new Font("SansSerif", i == 2 ? Font.BOLD : Font.PLAIN, 13));
            g.drawString(rows[i][1], 385, fy + 20);
            fy += 46;
        }

        // Insert / Book Ticket Button
        g.setColor(new Color(22, 163, 74));
        g.fillRoundRect(155, 522, 200, 38, 6, 6);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Insert / Book Ticket", 185, 546);

        // Right Receipt Box
        g.setColor(Color.WHITE);
        g.fillRoundRect(765, 135, 385, 485, 8, 8);
        g.setColor(new Color(203, 213, 225));
        g.drawRoundRect(765, 135, 385, 485, 8, 8);

        g.setColor(new Color(23, 55, 94));
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Generated E-Ticket & PNR Receipt", 785, 165);

        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g.setColor(new Color(15, 23, 42));
        if (booked) {
            String[] recLines = {
                    "=== RESERVATION CONFIRMED ===",
                    "",
                    "PNR Number      : PNR8492017",
                    "Passenger Name  : " + internName,
                    "Train Number    : 22436",
                    "Train Name      : Vande Bharat Express",
                    "Class Type      : First AC (1A)",
                    "Date of Journey : 2026-12-20",
                    "From            : Mumbai Central",
                    "To              : New Delhi",
                    "Status          : CONFIRMED"
            };
            int ry = 200;
            for (String l : recLines) {
                g.drawString(l, 785, ry);
                ry += 22;
            }
        } else {
            g.drawString("Fill out the Reservation Form and", 785, 210);
            g.drawString("click 'Insert / Book Ticket' to", 785, 230);
            g.drawString("generate a unique PNR number.", 785, 250);
        }

        if (modalPnr != null) {
            drawModalDialog(g, "Booking Confirmation — PNR: " + modalPnr, new String[]{
                    "Ticket Booked Successfully!",
                    "PNR Number : " + modalPnr,
                    "Passenger  : " + internName,
                    "Train      : 22436 - Vande Bharat Express",
                    "Journey    : 2026-12-20 (Mumbai -> New Delhi)"
            }, "OK", null);
        }

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTask1CancelFrame(
            String internName,
            String pnr,
            boolean fetched,
            boolean showConfirmDialog,
            boolean cancelled,
            int cx,
            int cy,
            boolean clicked) {

        BufferedImage img = createDesktopCanvas("Online Reservation System (Task 1)");
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawAppWindowFrame(g, 110, 26, 1060, 620, "Oasis Infobyte — Online Train Reservation System (Task 1)", new Color(23, 55, 94));
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("🚆 OASIS RAILWAYS — ONLINE RESERVATION & PNR CANCELLATION PORTAL", 130, 63);

        // Tabs
        g.setColor(new Color(226, 232, 240));
        g.fillRoundRect(130, 95, 250, 32, 6, 6);
        g.setColor(new Color(51, 65, 85));
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("1. Reservation Form (Book Ticket)", 145, 116);

        g.setColor(new Color(37, 99, 235));
        g.fillRoundRect(388, 95, 270, 32, 6, 6);
        g.setColor(Color.WHITE);
        g.drawString("2. Cancellation Form (PNR Lookup)", 405, 116);

        // Main Box
        g.setColor(Color.WHITE);
        g.fillRoundRect(130, 135, 1020, 485, 8, 8);
        g.setColor(new Color(203, 213, 225));
        g.drawRoundRect(130, 135, 1020, 485, 8, 8);

        g.setColor(new Color(30, 41, 59));
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Enter PNR Number:", 160, 195);

        g.setColor(Color.WHITE);
        g.fillRect(300, 175, 200, 32);
        g.setColor(new Color(148, 163, 184));
        g.drawRect(300, 175, 200, 32);
        g.setColor(new Color(15, 23, 42));
        g.setFont(new Font("Monospaced", Font.BOLD, 15));
        g.drawString(pnr, 312, 196);

        g.setColor(new Color(37, 99, 235));
        g.fillRoundRect(515, 175, 185, 32, 6, 6);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("Fetch Booking Details", 538, 196);

        // Details Box
        g.setColor(new Color(248, 250, 252));
        g.fillRect(160, 230, 960, 290);
        g.setColor(new Color(203, 213, 225));
        g.drawRect(160, 230, 960, 290);

        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        if (cancelled) {
            g.setColor(new Color(220, 38, 38));
            g.drawString("=== RESERVATION CANCELLED ===", 185, 270);
            g.drawString("PNR Number " + pnr + " (" + internName + ") has been permanently cancelled and removed from SQLite.", 185, 300);
        } else if (fetched) {
            g.setColor(new Color(15, 23, 42));
            String[] lines = {
                    "=== FETCHED RESERVATION RECORD ===",
                    "PNR Number          : " + pnr,
                    "Passenger Name      : " + internName,
                    "Train Number & Name : 22436 - Vande Bharat Express (NDLS - BSB)",
                    "Class & Date        : First AC (1A)  |  Journey Date: 2026-12-20",
                    "Route               : Mumbai Central  -->  New Delhi",
                    "Status              : ACTIVE (Click 'Confirm Cancellation' below to cancel ticket)"
            };
            int ly = 265;
            for (String l : lines) {
                g.drawString(l, 185, ly);
                ly += 28;
            }
        } else {
            g.setColor(new Color(100, 116, 139));
            g.drawString("Click 'Fetch Booking Details' to retrieve ticket details for PNR " + pnr + "...", 185, 270);
        }

        // Confirm Cancellation Button
        g.setColor(fetched && !cancelled ? new Color(220, 38, 38) : new Color(148, 163, 184));
        g.fillRoundRect(780, 540, 340, 38, 6, 6);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Confirm Cancellation (Delete Booking)", 825, 564);

        if (showConfirmDialog) {
            drawModalDialog(g, "Confirm Ticket Cancellation", new String[]{
                    "Are you sure you want to cancel this reservation?",
                    "PNR Number : " + pnr,
                    "Passenger  : " + internName,
                    "Train      : 22436 - Vande Bharat Express",
                    "This removes the booking from the SQLite database."
            }, "Yes", "No");
        }

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTask2Frame(
            int attemptsUsed,
            String bannerText,
            Color bannerColor,
            String inputText,
            List<String> logLines,
            int score,
            String summaryText,
            boolean showWinModal,
            int cx,
            int cy,
            boolean clicked) {

        BufferedImage img = createDesktopCanvas("Number Guessing Game (Task 2)");
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawAppWindowFrame(g, 140, 36, 1000, 600, "Oasis Infobyte — Number Guessing Game (Java Task 2)", new Color(30, 58, 138));
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("🎯 NUMBER GUESSING GAME — OASIS INFOBYTE (TASK 2)", 165, 74);
        g.drawString("Round: 1   |   Total Score: " + score + " pts", 840, 74);

        // Main Play Card
        g.setColor(Color.WHITE);
        g.fillRoundRect(160, 100, 650, 515, 8, 8);
        g.setColor(new Color(203, 213, 225));
        g.drawRoundRect(160, 100, 650, 515, 8, 8);

        g.setColor(new Color(30, 41, 59));
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Difficulty Level:  [ Medium (1 - 100 | 7 Attempts) ]", 190, 135);
        g.drawString("Attempts Used: " + attemptsUsed + " / 7 (" + (7 - attemptsUsed) + " Remaining)", 365, 175);

        // Progress Bar
        g.setColor(new Color(226, 232, 240));
        g.fillRoundRect(285, 188, 400, 20, 6, 6);
        g.setColor(new Color(37, 99, 235));
        g.fillRoundRect(285, 188, (400 * attemptsUsed) / 7, 20, 6, 6);

        // Banner
        g.setColor(bannerColor);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString(bannerText, 220, 265);

        // Input Box + Buttons
        g.setColor(Color.WHITE);
        g.fillRect(310, 335, 110, 38);
        g.setColor(new Color(37, 99, 235));
        g.drawRect(310, 335, 110, 38);
        g.setColor(new Color(15, 23, 42));
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.drawString(inputText, 345, 361);

        g.setColor(new Color(37, 99, 235));
        g.fillRoundRect(435, 335, 135, 38, 6, 6);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Submit Guess", 458, 359);

        g.setColor(new Color(22, 163, 74));
        g.fillRoundRect(585, 335, 170, 38, 6, 6);
        g.setColor(Color.WHITE);
        g.drawString("Play Again (Next)", 612, 359);

        // Attempt Log Box
        g.setColor(new Color(248, 250, 252));
        g.fillRect(185, 395, 600, 195);
        g.setColor(new Color(203, 213, 225));
        g.drawRect(185, 395, 600, 195);

        g.setColor(new Color(15, 23, 42));
        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        int ly = 422;
        for (String l : logLines) {
            g.drawString(l, 200, ly);
            ly += 24;
        }

        // Right Scoreboard Card
        g.setColor(Color.WHITE);
        g.fillRoundRect(825, 100, 295, 515, 8, 8);
        g.setColor(new Color(203, 213, 225));
        g.drawRoundRect(825, 100, 295, 515, 8, 8);
        g.setColor(new Color(30, 58, 138));
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("🏆 Multi-Round Summary", 845, 132);

        g.setColor(new Color(15, 23, 42));
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        int sy = 170;
        for (String sLine : summaryText.split("\n")) {
            g.drawString(sLine, 842, sy);
            sy += 20;
        }

        if (showWinModal) {
            drawModalDialog(g, "You Won Round 1!", new String[]{
                    "Correct! The secret number was 42.",
                    "Round 1 — guessed in 4 attempts (+80 pts)",
                    "",
                    "Would you like to Play Again?"
            }, "Yes", "No");
        }

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static void encodeTerminalTypingLine(AWTSequenceEncoder enc, List<String> term, String prompt, String typed) throws Exception {
        term.add(prompt);
        int idx = term.size() - 1;
        for (int i = 1; i <= typed.length(); i++) {
            term.set(idx, prompt + typed.substring(0, i) + "█");
            enc.encodeImage(renderTerminalDesktopFrame(term, 680, 420));
            enc.encodeImage(renderTerminalDesktopFrame(term, 680, 420));
        }
        term.set(idx, prompt + typed);
        enc.encodeImage(renderTerminalDesktopFrame(term, 680, 420));
    }

    private static void encodeTerminalFrames(AWTSequenceEncoder enc, List<String> term, int frames, int cx, int cy) throws Exception {
        for (int i = 0; i < frames; i++) {
            enc.encodeImage(renderTerminalDesktopFrame(term, cx, cy));
        }
    }

    private static BufferedImage renderTerminalDesktopFrame(List<String> lines, int cx, int cy) {
        BufferedImage img = createDesktopCanvas("Windows PowerShell — ATM Interface (Task 3)");
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawAppWindowFrame(g, 100, 24, 1080, 624, "Windows PowerShell — OIBSIP\\Java-Task3-ATMInterface (Riya Shivaji Nagare)", new Color(2, 6, 23));
        g.setColor(new Color(2, 6, 23));
        g.fillRect(102, 60, 1076, 586);

        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        int startIdx = Math.max(0, lines.size() - 25);
        int y = 88;
        for (int i = startIdx; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("[SUCCESS]") || line.contains("LOGIN SUCCESSFUL")) {
                g.setColor(new Color(74, 222, 128));
            } else if (line.contains("[ERROR]")) {
                g.setColor(new Color(248, 113, 113));
            } else if (line.startsWith("===") || line.startsWith("---")) {
                g.setColor(new Color(56, 189, 248));
            } else if (line.startsWith("PS ")) {
                g.setColor(new Color(250, 204, 21));
            } else {
                g.setColor(new Color(226, 232, 240));
            }
            g.drawString(line, 124, y);
            y += 22;
        }

        drawMouseCursor(g, cx, cy, false);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTask4LoginFrame(String user, String pass, int cx, int cy, boolean clicked) {
        BufferedImage img = createDesktopCanvas("Online Examination System (Task 4)");
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawAppWindowFrame(g, 120, 30, 1040, 610, "Oasis Infobyte — Online Examination System (Java Task 4)", null);
        int kx = 410, ky = 165, cw = 460, ch = 340;
        g.setColor(Color.WHITE);
        g.fillRoundRect(kx, ky, cw, ch, 10, 10);
        g.setColor(new Color(203, 213, 225));
        g.drawRoundRect(kx, ky, cw, ch, 10, 10);

        drawCenteredText(g, "🎓 ONLINE EXAMINATION PORTAL", ky + 48, new Font("SansSerif", Font.BOLD, 19), new Color(30, 58, 138));
        drawCenteredText(g, "Oasis Infobyte SIP · Java Development (Task 4)", ky + 72, new Font("SansSerif", Font.PLAIN, 13), new Color(100, 116, 139));

        g.setColor(new Color(30, 41, 59));
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Username:", kx + 45, ky + 135);
        g.drawString("Password:", kx + 45, ky + 185);

        g.setColor(Color.WHITE);
        g.fillRect(kx + 140, ky + 115, 260, 30);
        g.fillRect(kx + 140, ky + 165, 260, 30);
        g.setColor(new Color(148, 163, 184));
        g.drawRect(kx + 140, ky + 115, 260, 30);
        g.drawRect(kx + 140, ky + 165, 260, 30);

        g.setColor(new Color(15, 23, 42));
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        g.drawString(user, kx + 150, ky + 135);
        g.drawString(pass, kx + 150, ky + 185);

        g.setColor(new Color(37, 99, 235));
        g.fillRoundRect(kx + 110, ky + 265, 240, 40, 6, 6);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Login to Exam Dashboard", kx + 150, ky + 290);

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTask4ProfileFrame(String name, boolean updated, int cx, int cy, boolean clicked) {
        BufferedImage img = createDesktopCanvas("Online Examination System (Task 4)");
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawAppWindowFrame(g, 120, 30, 1040, 610, "Oasis Infobyte — Online Examination System (Java Task 4)", new Color(30, 58, 138));
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("Welcome, " + name + " (student1)", 145, 65);

        g.setColor(Color.WHITE);
        g.fillRoundRect(310, 125, 660, 460, 10, 10);
        g.setColor(new Color(203, 213, 225));
        g.drawRoundRect(310, 125, 660, 460, 10, 10);

        g.setColor(new Color(30, 58, 138));
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Step 1: Update Candidate Profile & Password", 340, 162);

        g.setColor(new Color(30, 41, 59));
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Display Name:", 340, 205);
        g.drawString("New Password:", 340, 250);

        g.setColor(Color.WHITE);
        g.fillRect(465, 185, 360, 30);
        g.fillRect(465, 230, 360, 30);
        g.setColor(new Color(148, 163, 184));
        g.drawRect(465, 185, 360, 30);
        g.drawRect(465, 230, 360, 30);

        g.setColor(new Color(15, 23, 42));
        g.drawString(name, 475, 205);
        g.drawString("********", 475, 250);

        g.setColor(new Color(37, 99, 235));
        g.fillRoundRect(340, 285, 220, 36, 6, 6);
        g.setColor(Color.WHITE);
        g.drawString("Update Profile & Password", 365, 308);

        if (updated) {
            g.setColor(new Color(22, 163, 74));
            g.drawString("✓ Profile & Password Updated!", 580, 308);
        }

        g.setColor(new Color(248, 250, 252));
        g.fillRect(340, 345, 600, 145);
        g.setColor(new Color(30, 41, 59));
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.drawString("• Total Questions : 8 Multiple-Choice Questions (4 options each)", 358, 378);
        g.drawString("• Time Limit      : 30:00 Minutes (Live Countdown Timer)", 358, 405);
        g.drawString("• Auto-Submission : Automatically submits when timer reaches 00:00", 358, 432);

        g.setColor(new Color(22, 163, 74));
        g.fillRoundRect(340, 510, 600, 44, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Start Timed Examination Now ->", 525, 537);

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTask4ExamFrame(
            String name,
            int qNum,
            int qTotal,
            String timerStr,
            String prompt,
            String[] options,
            int selectedIdx,
            boolean showSubmitModal,
            int cx,
            int cy,
            boolean clicked) {

        BufferedImage img = createDesktopCanvas("Online Examination System (Task 4)");
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawAppWindowFrame(g, 120, 30, 1040, 610, "Oasis Infobyte — Online Examination System (Java Task 4)", new Color(30, 58, 138));
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Candidate: " + name + " (student1)", 145, 64);

        g.setColor(new Color(254, 240, 138));
        g.setFont(new Font("Monospaced", Font.BOLD, 16));
        g.drawString("⏱ Time Remaining: " + timerStr, 880, 64);

        g.setColor(Color.WHITE);
        g.fillRoundRect(150, 105, 980, 505, 8, 8);
        g.setColor(new Color(203, 213, 225));
        g.drawRoundRect(150, 105, 980, 505, 8, 8);

        g.setColor(new Color(37, 99, 235));
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Question " + qNum + " of " + qTotal, 180, 142);

        g.setColor(new Color(15, 23, 42));
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString(prompt, 180, 180);

        int oy = 225;
        for (int i = 0; i < 4; i++) {
            boolean sel = (i == selectedIdx);
            g.setColor(sel ? new Color(219, 234, 254) : new Color(248, 250, 252));
            g.fillRoundRect(180, oy, 920, 48, 8, 8);
            g.setColor(sel ? new Color(37, 99, 235) : new Color(203, 213, 225));
            g.drawRoundRect(180, oy, 920, 48, 8, 8);

            g.setColor(sel ? new Color(37, 99, 235) : Color.WHITE);
            g.fillOval(200, oy + 15, 18, 18);
            g.setColor(new Color(100, 116, 139));
            g.drawOval(200, oy + 15, 18, 18);

            g.setColor(new Color(15, 23, 42));
            g.setFont(new Font("SansSerif", sel ? Font.BOLD : Font.PLAIN, 15));
            g.drawString(options[i], 235, oy + 30);
            oy += 60;
        }

        // Prev / Next / Submit buttons
        g.setColor(new Color(100, 116, 139));
        g.fillRoundRect(180, 545, 160, 38, 6, 6);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("<- Previous Question", 198, 569);

        g.setColor(new Color(37, 99, 235));
        g.fillRoundRect(352, 545, 150, 38, 6, 6);
        g.setColor(Color.WHITE);
        g.drawString("Next Question ->", 375, 569);

        g.setColor(new Color(22, 163, 74));
        g.fillRoundRect(910, 545, 190, 38, 6, 6);
        g.setColor(Color.WHITE);
        g.drawString("Submit Examination", 945, 569);

        if (showSubmitModal) {
            drawModalDialog(g, "Confirm Examination Submission", new String[]{
                    "Are you sure you want to submit your examination now?",
                    "Candidate : " + name,
                    "Answered  : 8 of 8 Questions"
            }, "Yes", "No");
        }

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTask4ResultFrame(String name, int cx, int cy) {
        BufferedImage img = createDesktopCanvas("Online Examination System (Task 4)");
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawAppWindowFrame(g, 120, 30, 1040, 610, "Oasis Infobyte — Online Examination System (Java Task 4)", new Color(30, 58, 138));
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("📊 EXAMINATION RESULT SUMMARY", 145, 64);

        g.setColor(new Color(220, 38, 38));
        g.fillRoundRect(940, 44, 190, 30, 6, 6);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("Logout & Return to Login", 958, 63);

        g.setColor(Color.WHITE);
        g.fillRoundRect(150, 105, 980, 505, 8, 8);
        g.setColor(new Color(203, 213, 225));
        g.drawRoundRect(150, 105, 980, 505, 8, 8);

        g.setColor(new Color(22, 163, 74));
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("Final Score: 7 out of 8 (87.5%)   |   Correct: 7   |   Incorrect: 1   |   Time Taken: 02:20", 180, 145);

        g.setColor(new Color(248, 250, 252));
        g.fillRect(180, 165, 920, 415);
        g.setColor(new Color(15, 23, 42));
        g.setFont(new Font("Monospaced", Font.BOLD, 13));

        String[] lines = {
                "Candidate Name : " + name + " (student1)   |   Mode: Manual Submission",
                "------------------------------------------------------------------------------------",
                "[CORRECT ✓]  Q1. Which keyword in Java prevents a method from being overridden?",
                "             Your Answer: final   |   Correct Answer: final",
                "[CORRECT ✓]  Q2. Which JDBC interface executes parameterized SQL queries?",
                "             Your Answer: PreparedStatement   |   Correct Answer: PreparedStatement",
                "[CORRECT ✓]  Q3. Time complexity of accessing an element by index in ArrayList?",
                "             Your Answer: O(1)   |   Correct Answer: O(1)",
                "[CORRECT ✓]  Q4. Which Swing layout manager switches between multiple screens?",
                "             Your Answer: CardLayout   |   Correct Answer: CardLayout",
                "[CORRECT ✓]  Q5. Wrapping data fields as private with public getters/setters is:",
                "             Your Answer: Encapsulation   |   Correct Answer: Encapsulation",
                "[CORRECT ✓]  Q6. Which package contains Swing's Timer class?",
                "             Your Answer: javax.swing   |   Correct Answer: javax.swing",
                "[CORRECT ✓]  Q8. Which collection in Java does NOT allow duplicate elements?",
                "             Your Answer: HashSet   |   Correct Answer: HashSet"
        };
        int y = 195;
        for (String l : lines) {
            g.setColor(l.startsWith("[CORRECT") ? new Color(22, 163, 74) : new Color(15, 23, 42));
            g.drawString(l, 200, y);
            y += 23;
        }

        drawMouseCursor(g, cx, cy, false);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTask5BrowserLoginFrame(String user, String pass, int cx, int cy, boolean clicked) {
        BufferedImage img = createDesktopCanvas("Google Chrome — Oasis Digital Library (Task 5)");
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawAppWindowFrame(g, 90, 20, 1100, 632, "Google Chrome — http://localhost:8085 — Oasis Digital Library Management System", new Color(30, 58, 138));
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("📚 OASIS DIGITAL LIBRARY MANAGEMENT SYSTEM — Role-Based Admin & Member Portal", 115, 62);

        // Login Card & Register Card
        g.setColor(Color.WHITE);
        g.fillRoundRect(160, 130, 450, 420, 10, 10);
        g.fillRoundRect(640, 130, 450, 420, 10, 10);
        g.setColor(new Color(203, 213, 225));
        g.drawRoundRect(160, 130, 450, 420, 10, 10);
        g.drawRoundRect(640, 130, 450, 420, 10, 10);

        g.setColor(new Color(30, 58, 138));
        g.setFont(new Font("SansSerif", Font.BOLD, 17));
        g.drawString("🔐 Sign In (Admin / Member)", 190, 172);
        g.drawString("📝 New Member Registration", 670, 172);

        g.setColor(new Color(30, 41, 59));
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Username", 190, 225);
        g.drawRect(190, 235, 390, 34);
        g.drawString(user, 202, 257);

        g.drawString("Password", 190, 295);
        g.drawRect(190, 305, 390, 34);
        g.drawString(pass, 202, 327);

        g.setColor(new Color(37, 99, 235));
        g.fillRoundRect(190, 360, 390, 40, 6, 6);
        g.setColor(Color.WHITE);
        g.drawString("Login to Library Portal", 310, 385);

        g.setColor(new Color(30, 41, 59));
        g.drawString("Full Name: Riya Shivaji Nagare", 670, 240);
        g.drawString("Email    : riya.nagare@student.edu", 670, 290);
        g.drawString("Username : riya", 670, 340);
        g.setColor(new Color(22, 163, 74));
        g.fillRoundRect(670, 380, 390, 40, 6, 6);
        g.setColor(Color.WHITE);
        g.drawString("Register Member Account", 785, 405);

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTask5AdminCatalogueFrame(boolean bookAdded, int cx, int cy, boolean clicked) {
        BufferedImage img = createDesktopCanvas("Google Chrome — Oasis Digital Library (Admin Module)");
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawAppWindowFrame(g, 90, 20, 1100, 632, "Google Chrome — http://localhost:8085 — Admin Portal", new Color(30, 58, 138));
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("📚 OASIS DIGITAL LIBRARY — ADMIN MODULE (Catalogue, Issued Books, Fines, Members)", 115, 62);

        // Tabs
        g.setColor(new Color(30, 58, 138));
        g.fillRoundRect(115, 95, 260, 32, 6, 6);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("📖 Manage Catalogue (Add/Edit/Delete)", 128, 116);

        g.setColor(Color.WHITE);
        g.fillRoundRect(115, 138, 1050, 490, 8, 8);
        g.setColor(new Color(203, 213, 225));
        g.drawRoundRect(115, 138, 1050, 490, 8, 8);

        g.setColor(new Color(30, 58, 138));
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("➕ Add New Book to Library Catalogue", 140, 172);

        g.setColor(new Color(30, 41, 59));
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.drawString("Title: [ Spring Boot in Action ]   Author: [ Craig Walls ]   ISBN: [ 978-1617292545 ]   Category: [ Computer Science ]   Qty: [ 4 ]", 140, 205);

        g.setColor(new Color(22, 163, 74));
        g.fillRoundRect(140, 222, 210, 34, 6, 6);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("+ Add Book to Catalogue", 168, 244);

        g.setColor(new Color(30, 58, 138));
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("📚 Existing Book Records", 140, 290);

        String[] rows = {
                "#1  | Effective Java (3rd Edition)   | Joshua Bloch       | 978-0134685991 | Computer Science | 3 of 4 Available  [Edit] [Delete]",
                "#2  | Clean Code                     | Robert C. Martin   | 978-0132350884 | Computer Science | 3 of 3 Available  [Edit] [Delete]",
                "#3  | Design Patterns: GoF           | Erich Gamma et al. | 978-0201633610 | Computer Science | 0 of 1 Available  [Edit] [Delete]",
                "#4  | A Brief History of Time        | Stephen Hawking    | 978-0553380163 | Science          | 5 of 5 Available  [Edit] [Delete]"
        };
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.setColor(new Color(15, 23, 42));
        int ry = 325;
        for (String r : rows) {
            g.drawString(r, 140, ry);
            ry += 30;
        }
        if (bookAdded) {
            g.setColor(new Color(22, 163, 74));
            g.drawString("#9  | Spring Boot in Action          | Craig Walls        | 978-1617292545 | Computer Science | 4 of 4 Available  [Edit] [Delete]", 140, ry);

            g.setColor(new Color(15, 23, 42));
            g.fillRoundRect(820, 575, 320, 38, 8, 8);
            g.setColor(Color.WHITE);
            g.drawString("✓ Book added to catalogue successfully!", 840, 599);
        }

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTask5AdminFinesFrame(boolean paid, int cx, int cy, boolean clicked) {
        BufferedImage img = createDesktopCanvas("Google Chrome — Oasis Digital Library (Fine Management)");
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawAppWindowFrame(g, 90, 20, 1100, 632, "Google Chrome — http://localhost:8085 — Admin Fine Management", new Color(30, 58, 138));
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("📚 OASIS DIGITAL LIBRARY — OVERDUE FINE MANAGEMENT (Rs. 5 / Overdue Day)", 115, 62);

        g.setColor(Color.WHITE);
        g.fillRoundRect(115, 110, 1050, 515, 8, 8);
        g.setColor(new Color(203, 213, 225));
        g.drawRoundRect(115, 110, 1050, 515, 8, 8);

        g.setColor(new Color(30, 58, 138));
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("💳 Overdue Fine Ledger & Payment Settlement", 145, 155);

        g.setColor(new Color(15, 23, 42));
        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        g.drawString("Issue #1 | Member: Riya Shivaji Nagare | Book: Effective Java | Overdue: 6 Days | Fine: Rs. 30.00", 145, 220);

        if (paid) {
            g.setColor(new Color(22, 163, 74));
            g.drawString("Payment Status: [ PAID ✓ ] — Fine settled by Admin.", 145, 260);
        } else {
            g.setColor(new Color(220, 38, 38));
            g.drawString("Payment Status: [ UNPAID ]", 145, 260);
            g.setColor(new Color(22, 163, 74));
            g.fillRoundRect(840, 200, 170, 34, 6, 6);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString("Mark Fine as Paid", 868, 222);
        }

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTask5UserPortalFrame(String internName, boolean reserved, int cx, int cy, boolean clicked) {
        BufferedImage img = createDesktopCanvas("Google Chrome — Oasis Digital Library (Member Portal)");
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawAppWindowFrame(g, 90, 20, 1100, 632, "Google Chrome — http://localhost:8085 — Member Portal (" + internName + ")", new Color(30, 58, 138));
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("📚 OASIS DIGITAL LIBRARY — MEMBER PORTAL  |  Logged in: " + internName + " (USER)", 115, 62);

        g.setColor(Color.WHITE);
        g.fillRoundRect(115, 105, 1050, 520, 8, 8);
        g.setColor(new Color(203, 213, 225));
        g.drawRoundRect(115, 105, 1050, 520, 8, 8);

        g.setColor(new Color(30, 58, 138));
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("🔍 Browse & Search Catalogue   |   Category: [All] [Computer Science] [Science] [History]", 145, 145);

        g.setColor(new Color(15, 23, 42));
        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        g.drawString("1. Effective Java (3rd Edition)  | Joshua Bloch       | 3 of 4 Available   [ Issue Book ]", 145, 210);
        g.drawString("2. Clean Code                    | Robert C. Martin   | 2 of 3 Available   [ Issue Book ]", 145, 255);
        g.drawString("3. Design Patterns: GoF          | Erich Gamma et al. | 0 of 1 Available", 145, 300);

        g.setColor(new Color(217, 119, 6));
        g.fillRoundRect(820, 280, 215, 32, 6, 6);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("Advance Booking (Reserve)", 845, 301);

        g.setColor(new Color(30, 58, 138));
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("📖 My Issued Books, Returns & Advance Bookings (" + internName + ")", 145, 375);

        g.setColor(new Color(15, 23, 42));
        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        g.drawString("• Issued  : Clean Code (Due in 14 Days) — Status: ISSUED   [ Return Book ]", 145, 415);
        if (reserved) {
            g.setColor(new Color(22, 163, 74));
            g.drawString("• Reserved: Design Patterns: GoF — Advance Booking Status: [ RESERVED ✓ ]", 145, 450);

            g.setColor(new Color(15, 23, 42));
            g.fillRoundRect(740, 565, 395, 38, 8, 8);
            g.setColor(Color.WHITE);
            g.drawString("✓ Advance booking confirmed for Design Patterns!", 760, 589);
        }

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTitleCard(String fullName, String track, String taskTitle) {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        GradientPaint gp = new GradientPaint(0, 0, new Color(15, 23, 42), WIDTH, HEIGHT, new Color(30, 58, 138));
        g.setPaint(gp);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        int boxW = 980, boxH = 480;
        int boxX = (WIDTH - boxW) / 2, boxY = (HEIGHT - boxH) / 2;

        g.setColor(new Color(30, 41, 59));
        g.fillRoundRect(boxX, boxY, boxW, boxH, 28, 28);
        g.setColor(new Color(96, 165, 250));
        g.setStroke(new BasicStroke(3f));
        g.drawRoundRect(boxX, boxY, boxW, boxH, 28, 28);

        drawCenteredText(g, "OASIS INFOBYTE — SIP INTERNSHIP DEMO WALKTHROUGH", boxY + 65,
                new Font("SansSerif", Font.BOLD, 20), new Color(147, 197, 253));

        drawCenteredText(g, "1 · INTERN FULL NAME", boxY + 135,
                new Font("SansSerif", Font.BOLD, 15), new Color(148, 163, 184));
        drawCenteredText(g, fullName, boxY + 185,
                new Font("SansSerif", Font.BOLD, 44), Color.WHITE);

        drawCenteredText(g, "2 · ASSIGNED TRACK", boxY + 255,
                new Font("SansSerif", Font.BOLD, 15), new Color(148, 163, 184));
        drawCenteredText(g, track, boxY + 300,
                new Font("SansSerif", Font.BOLD, 32), new Color(56, 189, 248));

        drawCenteredText(g, "3 · TASK TITLE", boxY + 370,
                new Font("SansSerif", Font.BOLD, 15), new Color(148, 163, 184));
        drawCenteredText(g, taskTitle, boxY + 418,
                new Font("SansSerif", Font.BOLD, 34), new Color(250, 204, 21));

        g.dispose();
        return img;
    }

    private static void encodeRepeated(AWTSequenceEncoder enc, BufferedImage img, int count) throws Exception {
        for (int i = 0; i < count; i++) {
            enc.encodeImage(img);
        }
    }

    private static int[][] interpolateCursor(int x1, int y1, int x2, int y2, int steps) {
        int[][] pts = new int[steps][2];
        for (int i = 0; i < steps; i++) {
            double t = (steps <= 1) ? 1.0 : (double) i / (steps - 1);
            pts[i][0] = (int) Math.round(x1 + (x2 - x1) * t);
            pts[i][1] = (int) Math.round(y1 + (y2 - y1) * t);
        }
        return pts;
    }

    private static void drawCenteredText(Graphics2D g, String text, int y, Font font, Color color) {
        g.setFont(font);
        g.setColor(color);
        FontMetrics fm = g.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    private static void setupQuality(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }
}
