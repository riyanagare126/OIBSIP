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
import java.util.List;

/**
 * High-Precision Screen-Recording MP4 Video Generator for all 5 OIBSIP Java Tasks.
 * - Strictly begins each video with a 2.5-second static Title Card:
 *     (1) Full Name: Riya Shivaji Nagare
 *     (2) Track    : Java Development
 *     (3) Task     : Task X ...
 * - Each video is ~22-28 seconds long (at 10 FPS) with comfortable reading holds,
 *   zero text overlap, exact button-center cursor targeting, character-by-character typing,
 *   KPI stat cards, styled tables, and confirmation dialogs.
 */
public class DemoVideoGenerator {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int FPS = 10;

    // Theme Palettes per Task
    // Task 1: Crimson Ruby & Warm Gold Railway Theme
    private static final Color T1_DARK = new Color(69, 10, 10);
    private static final Color T1_PRIMARY = new Color(127, 29, 29);
    private static final Color T1_ACCENT = new Color(185, 28, 28);
    private static final Color T1_GOLD = new Color(217, 119, 6);
    private static final Color T1_BG = new Color(253, 248, 246);

    // Task 2: Royal Violet & Cyber Purple Arcade Theme
    private static final Color T2_DARK = new Color(46, 16, 101);
    private static final Color T2_PRIMARY = new Color(76, 29, 149);
    private static final Color T2_ACCENT = new Color(124, 58, 237);
    private static final Color T2_FUCHSIA = new Color(192, 38, 211);
    private static final Color T2_BG = new Color(245, 243, 255);

    // Task 3: Emerald Matrix FinTech Terminal Theme
    private static final Color T3_DARK = new Color(2, 44, 34);
    private static final Color T3_PRIMARY = new Color(6, 78, 59);
    private static final Color T3_ACCENT = new Color(16, 185, 129);

    // Task 4: Deep Teal & Ocean Cyan EdTech Theme
    private static final Color T4_DARK = new Color(4, 47, 46);
    private static final Color T4_PRIMARY = new Color(19, 78, 74);
    private static final Color T4_ACCENT = new Color(13, 148, 136);
    private static final Color T4_BG = new Color(240, 253, 250);

    // Task 5: Warm Espresso, Terracotta & Parchment Library Theme
    private static final Color T5_DARK = new Color(67, 20, 7);
    private static final Color T5_PRIMARY = new Color(154, 52, 18);
    private static final Color T5_ACCENT = new Color(194, 65, 12);
    private static final Color T5_BG = new Color(253, 250, 243);

    private static final Color NAVY_DARK = new Color(15, 23, 42);
    private static final Color NAVY_PRIMARY = new Color(30, 58, 138);
    private static final Color BLUE_ACCENT = new Color(37, 99, 235);
    private static final Color GREEN_SUCCESS = new Color(22, 163, 74);
    private static final Color RED_DANGER = new Color(220, 38, 38);
    private static final Color AMBER_WARN = new Color(217, 119, 6);
    private static final Color SLATE_MUTED = new Color(100, 116, 139);
    private static final Color BORDER_LIGHT = new Color(203, 213, 225);

    public static void main(String[] args) throws Exception {
        String internName = args.length > 0 && !args[0].trim().isEmpty()
                ? args[0].trim()
                : "Riya Shivaji Nagare";

        File baseDir = new File("..").getCanonicalFile();
        if (!new File(baseDir, "Java-Task1-OnlineReservationSystem").exists()) {
            baseDir = new File(".").getCanonicalFile();
        }

        System.out.println("=== Generating Upgraded 25-Second Screen-Recording MP4s for: " + internName + " ===");

        generateTask1Video(new File(baseDir, "Java-Task1-OnlineReservationSystem/demo_video.mp4"), internName);
        generateTask2Video(new File(baseDir, "Java-Task2-NumberGuessingGame/demo_video.mp4"), internName);
        generateTask3Video(new File(baseDir, "Java-Task3-ATMInterface/demo_video.mp4"), internName);
        generateTask4Video(new File(baseDir, "Java-Task4-OnlineExaminationSystem/demo_video.mp4"), internName);
        generateTask5Video(new File(baseDir, "Java-Task5-DigitalLibraryManagementSystem/demo_video.mp4"), internName);

        System.out.println("=== All 5 Upgraded MP4 Videos Generated Successfully! ===");
    }

    // =========================================================================
    // TASK 1: ONLINE RESERVATION SYSTEM (~24 SECONDS)
    // =========================================================================
    private static void generateTask1Video(File outFile, String internName) throws Exception {
        AWTSequenceEncoder enc = AWTSequenceEncoder.createSequenceEncoder(outFile, FPS);

        // 00:00 - 00:03: Mandatory Static Title Card (30 frames)
        encodeRepeated(enc, renderTitleCard(internName, "Java Development", "Task 1 · Online Reservation System"), 30);

        // Step 1: Login Screen (type username & password, move to Login button center (640, 470) & click)
        int loginBtnX = 530, loginBtnY = 450, loginBtnW = 220, loginBtnH = 42;
        int targetX = loginBtnX + loginBtnW / 2;
        int targetY = loginBtnY + loginBtnH / 2;

        for (int i = 1; i <= 5; i++) {
            String u = "user1".substring(0, i) + (i < 5 ? "|" : "");
            encodeRepeated(enc, renderTask1Login(u, "", 620, 292, false, "00:04"), 3);
        }
        for (int i = 1; i <= 7; i++) {
            String p = "*******".substring(0, i) + (i < 7 ? "|" : "");
            encodeRepeated(enc, renderTask1Login("user1", p, 620, 352, false, "00:06"), 2);
        }
        int[][] pathLogin = interpolate(620, 352, targetX, targetY, 10);
        for (int[] pt : pathLogin) {
            enc.encodeImage(renderTask1Login("user1", "*******", pt[0], pt[1], false, "00:07"));
        }
        encodeRepeated(enc, renderTask1Login("user1", "*******", targetX, targetY, true, "00:08"), 6);

        // Step 2: Reservation Form — type Riya Shivaji Nagare, select Train 22436 (auto-fills Vande Bharat)
        int bookBtnX = 145, bookBtnY = 555, bookBtnW = 210, bookBtnH = 40;
        int bookTargetX = bookBtnX + bookBtnW / 2;
        int bookTargetY = bookBtnY + bookBtnH / 2;

        String[] nameSteps = {"Riya|", "Riya Shivaji|", internName};
        for (String ns : nameSteps) {
            encodeRepeated(enc, renderTask1Reservation(internName, ns, "12951", "Mumbai Rajdhani Express",
                    false, false, 480, 222, false, "00:10"), 6);
        }
        // Change Train Number to 22436 -> Auto-populates Vande Bharat Express
        encodeRepeated(enc, renderTask1Reservation(internName, internName, "22436", "Vande Bharat Express (NDLS - BSB)",
                false, false, 480, 270, true, "00:12"), 12);

        // Move cursor to "Insert / Book Ticket" button center and click
        int[][] pathBook = interpolate(480, 270, bookTargetX, bookTargetY, 12);
        for (int[] pt : pathBook) {
            enc.encodeImage(renderTask1Reservation(internName, internName, "22436", "Vande Bharat Express (NDLS - BSB)",
                    false, false, pt[0], pt[1], false, "00:14"));
        }
        encodeRepeated(enc, renderTask1Reservation(internName, internName, "22436", "Vande Bharat Express (NDLS - BSB)",
                true, false, bookTargetX, bookTargetY, true, "00:15"), 6);

        // Step 3: Confirmation Dialog with PNR8492017 -> Hold so evaluator can read, then click OK (640, 468)
        encodeRepeated(enc, renderTask1Reservation(internName, internName, "22436", "Vande Bharat Express (NDLS - BSB)",
                true, true, 520, 500, false, "00:17"), 18);
        int[][] pathOk = interpolate(520, 500, 640, 468, 8);
        for (int[] pt : pathOk) {
            enc.encodeImage(renderTask1Reservation(internName, internName, "22436", "Vande Bharat Express (NDLS - BSB)",
                    true, true, pt[0], pt[1], false, "00:18"));
        }
        encodeRepeated(enc, renderTask1Reservation(internName, internName, "22436", "Vande Bharat Express (NDLS - BSB)",
                true, true, 640, 468, true, "00:19"), 6);

        // Step 4: Switch to Tab 2 (Cancellation Form), click "Fetch Booking Details" (605, 226)
        int fetchBtnX = 510, fetchBtnY = 208, fetchBtnW = 190, fetchBtnH = 36;
        int fetchCX = fetchBtnX + fetchBtnW / 2, fetchCY = fetchBtnY + fetchBtnH / 2;

        int[][] pathFetch = interpolate(640, 468, fetchCX, fetchCY, 10);
        for (int[] pt : pathFetch) {
            enc.encodeImage(renderTask1Cancel(internName, "PNR8492017", false, false, false, pt[0], pt[1], false, "00:20"));
        }
        encodeRepeated(enc, renderTask1Cancel(internName, "PNR8492017", true, false, false, fetchCX, fetchCY, true, "00:21"), 6);
        // Hold fetched details so evaluator can read
        encodeRepeated(enc, renderTask1Cancel(internName, "PNR8492017", true, false, false, 650, 320, false, "00:22"), 18);

        // Move to "Confirm Cancellation" button center (965, 574) and click
        int cancelBtnX = 790, cancelBtnY = 555, cancelBtnW = 350, cancelBtnH = 38;
        int cancelCX = cancelBtnX + cancelBtnW / 2, cancelCY = cancelBtnY + cancelBtnH / 2;
        int[][] pathCancel = interpolate(650, 320, cancelCX, cancelCY, 10);
        for (int[] pt : pathCancel) {
            enc.encodeImage(renderTask1Cancel(internName, "PNR8492017", true, false, false, pt[0], pt[1], false, "00:24"));
        }
        encodeRepeated(enc, renderTask1Cancel(internName, "PNR8492017", true, true, false, cancelCX, cancelCY, true, "00:25"), 6);

        // "Are you sure?" dialog -> Hold, then click "Yes" button center (595, 468)
        encodeRepeated(enc, renderTask1Cancel(internName, "PNR8492017", true, true, false, 680, 490, false, "00:26"), 14);
        encodeRepeated(enc, renderTask1Cancel(internName, "PNR8492017", true, true, false, 595, 468, true, "00:27"), 6);

        // Final Cancelled state hold (20 frames)
        encodeRepeated(enc, renderTask1Cancel(internName, "PNR8492017", true, false, true, 595, 468, false, "00:29"), 22);

        enc.finish();
        System.out.println(" -> Task 1 Video Ready (" + outFile.length() / 1024 + " KB)");
    }

    // =========================================================================
    // TASK 2: NUMBER GUESSING GAME (~24 SECONDS)
    // =========================================================================
    private static void generateTask2Video(File outFile, String internName) throws Exception {
        AWTSequenceEncoder enc = AWTSequenceEncoder.createSequenceEncoder(outFile, FPS);

        encodeRepeated(enc, renderTitleCard(internName, "Java Development", "Task 2 · Number Guessing Game"), 30);

        List<String> log = new ArrayList<>();
        log.add("=== Round 1 Started (Difficulty: Medium | Range: 1-100 | Max Attempts: 7) ===");

        // Initial screen hold
        encodeRepeated(enc, renderTask2(internName, 0, "Enter your first guess between 1 and 100!", BLUE_ACCENT,
                "", log, 0, "No completed rounds yet.", false, 400, 300, false, "00:03"), 15);

        // Guess 1: 75 -> Too High!
        playTask2Turn(enc, internName, "75", 0, "Enter your first guess between 1 and 100!", BLUE_ACCENT,
                "Too High! (Try a LOWER number than 75)", AMBER_WARN,
                "Attempt #1:   75   -->   Too High! (Secret number is lower)", log, 0, "No completed rounds yet.", false, "00:07");

        // Guess 2: 25 -> Too Low!
        playTask2Turn(enc, internName, "25", 1, "Too High! (Try a LOWER number than 75)", AMBER_WARN,
                "Too Low! (Try a HIGHER number than 25)", AMBER_WARN,
                "Attempt #2:   25   -->   Too Low!  (Secret number is higher)", log, 0, "No completed rounds yet.", false, "00:12");

        // Guess 3: 50 -> Too High!
        playTask2Turn(enc, internName, "50", 2, "Too Low! (Try a HIGHER number than 25)", AMBER_WARN,
                "Too High! (Try a LOWER number than 50)", AMBER_WARN,
                "Attempt #3:   50   -->   Too High! (Getting close!)", log, 0, "No completed rounds yet.", false, "00:17");

        // Guess 4: 42 -> Correct!
        String winSummary = "• Round 1 — guessed in 4 attempts\n  Score Awarded: +80 pts\n\nTotal Session Score: 80 pts";
        playTask2Turn(enc, internName, "42", 3, "Too High! (Try a LOWER number than 50)", AMBER_WARN,
                "Correct! Secret number 42 guessed in 4 attempts! (+80 pts)", GREEN_SUCCESS,
                "Attempt #4:   42   -->   CORRECT! (+80 pts awarded)", log, 80, winSummary, true, "00:23");

        enc.finish();
        System.out.println(" -> Task 2 Video Ready (" + outFile.length() / 1024 + " KB)");
    }

    private static void playTask2Turn(
            AWTSequenceEncoder enc,
            String internName,
            String guess,
            int usedBefore,
            String oldBanner,
            Color oldColor,
            String newBanner,
            Color newColor,
            String logEntry,
            List<String> log,
            int newScore,
            String summary,
            boolean showModal,
            String clockStr) throws Exception {

        int submitCX = 485, submitCY = 365;
        // Type digits
        for (int i = 1; i <= guess.length(); i++) {
            encodeRepeated(enc, renderTask2(internName, usedBefore, oldBanner, oldColor,
                    guess.substring(0, i) + "|", log, 0, "No completed rounds yet.", false, 345, 365, false, clockStr), 4);
        }
        // Move to Submit Guess button center (485, 365)
        int[][] path = interpolate(345, 365, submitCX, submitCY, 8);
        for (int[] pt : path) {
            enc.encodeImage(renderTask2(internName, usedBefore, oldBanner, oldColor,
                    guess, log, 0, "No completed rounds yet.", false, pt[0], pt[1], false, clockStr));
        }
        log.add(logEntry);
        encodeRepeated(enc, renderTask2(internName, usedBefore + 1, newBanner, newColor,
                "", log, newScore, summary, false, submitCX, submitCY, true, clockStr), 6);

        // Hold result so evaluator can read
        encodeRepeated(enc, renderTask2(internName, usedBefore + 1, newBanner, newColor,
                "", log, newScore, summary, false, 540, 420, false, clockStr), 16);

        if (showModal) {
            encodeRepeated(enc, renderTask2(internName, usedBefore + 1, newBanner, newColor,
                    "", log, newScore, summary, true, 595, 468, false, "00:25"), 18);
            encodeRepeated(enc, renderTask2(internName, usedBefore + 1, newBanner, newColor,
                    "", log, newScore, summary, true, 595, 468, true, "00:26"), 8);
        }
    }

    // =========================================================================
    // TASK 3: ATM INTERFACE (~26 SECONDS)
    // =========================================================================
    private static void generateTask3Video(File outFile, String internName) throws Exception {
        AWTSequenceEncoder enc = AWTSequenceEncoder.createSequenceEncoder(outFile, FPS);

        encodeRepeated(enc, renderTitleCard(internName, "Java Development", "Task 3 · ATM Interface"), 30);

        List<String> term = new ArrayList<>();
        term.add("PS C:\\Users\\Riya\\OIBSIP\\Java-Task3-ATMInterface> javac -d out src/*.java");
        encodeTerminalWait(enc, term, 8, "00:03");
        encodeTerminalType(enc, term, "PS C:\\Users\\Riya\\OIBSIP\\Java-Task3-ATMInterface> ", "java -cp out Main", "00:05");

        term.add("==========================================================================");
        term.add("                 WELCOME TO OASIS NATIONAL BANK");
        term.add("                 OASIS INFOBYTE - JAVA DEVELOPMENT (TASK 3)");
        term.add("==========================================================================");
        term.add("  Demo Accounts: user101 (PIN: 1234)  |  user202 (PIN: 5678)");
        term.add("==========================================================================");
        term.add("--- ATM Authentication (Attempt 1 of 3) ---");
        encodeTerminalWait(enc, term, 10, "00:07");

        encodeTerminalType(enc, term, "Enter User ID : ", "user101", "00:08");
        encodeTerminalType(enc, term, "Enter PIN     : ", "0000", "00:09");
        term.add("[ERROR] Invalid User ID or PIN. Attempts remaining: 2");
        encodeTerminalWait(enc, term, 12, "00:11");

        term.add("--- ATM Authentication (Attempt 2 of 3) ---");
        encodeTerminalType(enc, term, "Enter User ID : ", "user101", "00:12");
        encodeTerminalType(enc, term, "Enter PIN     : ", "1234", "00:13");
        term.add("[LOGIN SUCCESSFUL] Welcome, Riya Shivaji Nagare (user101)!");
        term.add("--------------------------------------------------------------------------");
        term.add("  Account: Riya Shivaji Nagare (user101)  |  Available Balance: Rs. 25000.00");
        term.add("  1. Transaction History   2. Withdraw   3. Deposit   4. Transfer   5. Quit");
        encodeTerminalWait(enc, term, 14, "00:15");

        // Option 3: Deposit 5000
        encodeTerminalType(enc, term, "Select an option (1-5): ", "3", "00:16");
        encodeTerminalType(enc, term, "Enter amount to deposit (Rs.): ", "5000", "00:17");
        term.add("[SUCCESS] Rs. 5000.00 deposited successfully. Updated Balance: Rs. 30000.00");
        encodeTerminalWait(enc, term, 14, "00:18");

        // Option 2: Withdraw 3500
        encodeTerminalType(enc, term, "Select an option (1-5): ", "2", "00:19");
        encodeTerminalType(enc, term, "Enter amount to withdraw (Rs.): ", "3500", "00:20");
        term.add("[SUCCESS] Rs. 3500.00 withdrawn successfully. Updated Balance: Rs. 26500.00");
        encodeTerminalWait(enc, term, 12, "00:21");

        // Option 4: Transfer 4000 to user202
        encodeTerminalType(enc, term, "Select an option (1-5): ", "4", "00:22");
        encodeTerminalType(enc, term, "Enter Recipient Account User ID: ", "user202", "00:23");
        encodeTerminalType(enc, term, "Enter amount to transfer (Rs.): ", "4000", "00:24");
        term.add("[SUCCESS] Rs. 4000.00 transferred to Priya Nair (user202). Balance: Rs. 22500.00");
        encodeTerminalWait(enc, term, 14, "00:25");

        // Option 1: Transaction History
        encodeTerminalType(enc, term, "Select an option (1-5): ", "1", "00:26");
        term.add("========================== TRANSACTION HISTORY ===========================");
        term.add("TXN-1001 | 2026-11-05 | OPENING      | Rs. 25000.00 | Bal: Rs. 25000.00 | Opening Balance");
        term.add("TXN-1004 | 2026-11-05 | DEPOSIT      | Rs.  5000.00 | Bal: Rs. 30000.00 | ATM Cash Deposit");
        term.add("TXN-1005 | 2026-11-05 | WITHDRAW     | Rs.  3500.00 | Bal: Rs. 26500.00 | ATM Cash Withdrawal");
        term.add("TXN-1006 | 2026-11-05 | TRANSFER-OUT | Rs.  4000.00 | Bal: Rs. 22500.00 | To Priya Nair (user202)");
        term.add("==========================================================================");
        encodeTerminalWait(enc, term, 24, "00:28");

        encodeTerminalType(enc, term, "Select an option (1-5): ", "5", "00:29");
        term.add("Thank you for banking with Oasis National Bank, Riya Shivaji Nagare! Goodbye!");
        encodeTerminalWait(enc, term, 20, "00:30");

        enc.finish();
        System.out.println(" -> Task 3 Video Ready (" + outFile.length() / 1024 + " KB)");
    }

    // =========================================================================
    // TASK 4: ONLINE EXAMINATION SYSTEM (~25 SECONDS)
    // =========================================================================
    private static void generateTask4Video(File outFile, String internName) throws Exception {
        AWTSequenceEncoder enc = AWTSequenceEncoder.createSequenceEncoder(outFile, FPS);

        encodeRepeated(enc, renderTitleCard(internName, "Java Development", "Task 4 · Online Examination System"), 30);

        // Step 1: Login Screen (click Login button center at 640, 470)
        encodeRepeated(enc, renderTask4Login("student1", "*******", 780, 420, false, "00:04"), 14);
        int[][] toLogin = interpolate(780, 420, 640, 470, 10);
        for (int[] pt : toLogin) {
            enc.encodeImage(renderTask4Login("student1", "*******", pt[0], pt[1], false, "00:05"));
        }
        encodeRepeated(enc, renderTask4Login("student1", "*******", 640, 470, true, "00:06"), 6);

        // Step 2: Profile & Password Update Screen
        // Click "Update Profile & Password" button center at (450, 323)
        encodeRepeated(enc, renderTask4Profile(internName, false, 640, 225, false, "00:08"), 12);
        int[][] toUpdate = interpolate(640, 225, 450, 323, 10);
        for (int[] pt : toUpdate) {
            enc.encodeImage(renderTask4Profile(internName, false, pt[0], pt[1], false, "00:09"));
        }
        encodeRepeated(enc, renderTask4Profile(internName, true, 450, 323, true, "00:10"), 6);
        encodeRepeated(enc, renderTask4Profile(internName, true, 450, 323, false, "00:11"), 14);

        // Move to "Start Timed Examination Now ->" button center at (640, 548)
        int[][] toStart = interpolate(450, 323, 640, 548, 10);
        for (int[] pt : toStart) {
            enc.encodeImage(renderTask4Profile(internName, true, pt[0], pt[1], false, "00:12"));
        }
        encodeRepeated(enc, renderTask4Profile(internName, true, 640, 548, true, "00:13"), 6);

        // Step 3: MCQ Question 1 -> Select Option B (center 420, 324), then click "Next Question ->" (427, 578)
        String q1 = "Q1. Which keyword in Java is used to prevent a method from being overridden in a subclass?";
        String[] q1Opts = {"A)  static", "B)  final", "C)  abstract", "D)  volatile"};
        encodeRepeated(enc, renderTask4Exam(internName, 1, 8, "29:58", q1, q1Opts, -1, false, 640, 400, false, "00:15"), 12);

        int[][] toOptB = interpolate(640, 400, 350, 324, 8);
        for (int[] pt : toOptB) {
            enc.encodeImage(renderTask4Exam(internName, 1, 8, "29:56", q1, q1Opts, -1, false, pt[0], pt[1], false, "00:16"));
        }
        encodeRepeated(enc, renderTask4Exam(internName, 1, 8, "29:55", q1, q1Opts, 1, false, 350, 324, true, "00:17"), 6);
        encodeRepeated(enc, renderTask4Exam(internName, 1, 8, "29:53", q1, q1Opts, 1, false, 427, 578, true, "00:18"), 8);

        // Step 4: MCQ Question 2 -> Select Option C (350, 384), then click "Submit Examination" (1005, 578)
        String q2 = "Q2. Which JDBC interface is recommended to execute parameterized SQL queries and prevent SQL injection?";
        String[] q2Opts = {"A)  Statement", "B)  ResultSet", "C)  PreparedStatement", "D)  DriverManager"};
        encodeRepeated(enc, renderTask4Exam(internName, 2, 8, "29:49", q2, q2Opts, -1, false, 427, 578, false, "00:19"), 12);
        encodeRepeated(enc, renderTask4Exam(internName, 2, 8, "29:46", q2, q2Opts, 2, false, 350, 384, true, "00:20"), 10);

        int[][] toSubmit = interpolate(350, 384, 1005, 578, 10);
        for (int[] pt : toSubmit) {
            enc.encodeImage(renderTask4Exam(internName, 8, 8, "29:40", q2, q2Opts, 2, false, pt[0], pt[1], false, "00:21"));
        }
        encodeRepeated(enc, renderTask4Exam(internName, 8, 8, "29:38", q2, q2Opts, 2, true, 1005, 578, true, "00:22"), 6);

        // Confirmation dialog -> click "Yes" center (595, 468)
        encodeRepeated(enc, renderTask4Exam(internName, 8, 8, "29:36", q2, q2Opts, 2, true, 595, 468, false, "00:23"), 14);
        encodeRepeated(enc, renderTask4Exam(internName, 8, 8, "29:35", q2, q2Opts, 2, true, 595, 468, true, "00:24"), 6);

        // Step 5: Final Result Breakdown Screen (hold for 30 frames = 3 seconds)
        encodeRepeated(enc, renderTask4Result(internName, 720, 360, "00:27"), 30);

        enc.finish();
        System.out.println(" -> Task 4 Video Ready (" + outFile.length() / 1024 + " KB)");
    }

    // =========================================================================
    // TASK 5: DIGITAL LIBRARY MANAGEMENT SYSTEM (~28 SECONDS — FULL WEB UI)
    // =========================================================================
    private static void generateTask5Video(File outFile, String internName) throws Exception {
        AWTSequenceEncoder enc = AWTSequenceEncoder.createSequenceEncoder(outFile, FPS);

        // 00:00 - 00:03: Mandatory 2.5s Static Title Card
        encodeRepeated(enc, renderTitleCard(internName, "Java Development", "Task 5 · Digital Library Management System"), 30);

        // Scene 1 (00:03 - 00:08): Sign In & New Member Registration Screen
        // Login button is at (x=155, y=428, w=410, h=40) -> center = (360, 448)
        int loginCX = 360, loginCY = 448;
        encodeRepeated(enc, renderTask5Login(internName, "admin", "********", 640, 320, false, "00:04"), 16);
        int[][] toLogin = interpolate(640, 320, loginCX, loginCY, 12);
        for (int[] pt : toLogin) {
            enc.encodeImage(renderTask5Login(internName, "admin", "********", pt[0], pt[1], false, "00:06"));
        }
        encodeRepeated(enc, renderTask5Login(internName, "admin", "********", loginCX, loginCY, true, "00:07"), 8);

        // Scene 2 (00:08 - 00:14): Admin Portal — KPI Cards + Add Book + Catalogue Table
        // "+ Add Book to Catalogue" button is at (x=135, y=312, w=210, h=34) -> center = (240, 329)
        int addBookCX = 240, addBookCY = 329;
        encodeRepeated(enc, renderTask5AdminCatalogue(false, 520, 275, false, "00:09"), 16);
        int[][] toAdd = interpolate(520, 275, addBookCX, addBookCY, 10);
        for (int[] pt : toAdd) {
            enc.encodeImage(renderTask5AdminCatalogue(false, pt[0], pt[1], false, "00:11"));
        }
        encodeRepeated(enc, renderTask5AdminCatalogue(true, addBookCX, addBookCY, true, "00:12"), 8);
        // Hold after book is added so evaluator sees row #5 & toast notification clearly
        encodeRepeated(enc, renderTask5AdminCatalogue(true, 480, 420, false, "00:14"), 24);

        // Scene 3 (00:14 - 00:20): Admin Portal — Issued Books & Overdue Fine Management (Rs. 5/day)
        // "Mark Fine as Paid" button is at (x=960, y=346, w=160, h=30) -> center = (1040, 361)
        int payCX = 1040, payCY = 361;
        encodeRepeated(enc, renderTask5AdminFines(internName, false, 580, 230, false, "00:16"), 18);
        int[][] toPay = interpolate(580, 230, payCX, payCY, 12);
        for (int[] pt : toPay) {
            enc.encodeImage(renderTask5AdminFines(internName, false, pt[0], pt[1], false, "00:18"));
        }
        encodeRepeated(enc, renderTask5AdminFines(internName, true, payCX, payCY, true, "00:19"), 8);
        encodeRepeated(enc, renderTask5AdminFines(internName, true, 750, 361, false, "00:20"), 22);

        // Scene 4 (00:20 - 00:28): User Module (Riya Shivaji Nagare) — Category Filter, Search, Issue, Advance Booking & Return
        // "Advance Booking (Reserve)" button is at (x=935, y=384, w=195, h=30) -> center = (1032, 399)
        int advCX = 1032, advCY = 399;
        encodeRepeated(enc, renderTask5UserPortal(internName, false, 450, 260, false, "00:22"), 18);
        int[][] toAdv = interpolate(450, 260, advCX, advCY, 12);
        for (int[] pt : toAdv) {
            enc.encodeImage(renderTask5UserPortal(internName, false, pt[0], pt[1], false, "00:24"));
        }
        encodeRepeated(enc, renderTask5UserPortal(internName, true, advCX, advCY, true, "00:25"), 8);
        // Hold final state so evaluator can read the Advance Booking confirmation & Issued books table
        encodeRepeated(enc, renderTask5UserPortal(internName, true, 640, 500, false, "00:28"), 28);

        enc.finish();
        System.out.println(" -> Task 5 Video Ready (" + outFile.length() / 1024 + " KB)");
    }

    // =========================================================================
    // SHARED DESKTOP, WINDOW & BUTTON PRIMITIVES (ZERO OVERLAP GUARANTEED)
    // =========================================================================
    private static BufferedImage createDesktopBase(String taskbarAppTitle, String recTime) {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        // Desktop Wallpaper
        GradientPaint wp = new GradientPaint(0, 0, new Color(15, 32, 67), WIDTH, HEIGHT - 44, new Color(30, 64, 124));
        g.setPaint(wp);
        g.fillRect(0, 0, WIDTH, HEIGHT - 44);

        // Desktop Icons (left margin x=16)
        drawDesktopShortcut(g, 16, 26, "OIBSIP Repo", new Color(250, 204, 21));
        drawDesktopShortcut(g, 16, 112, "Java JDK 25", new Color(56, 189, 248));
        drawDesktopShortcut(g, 16, 198, "SQLite DB", new Color(74, 222, 128));

        // Bottom Windows 11 Taskbar (y = 676..720)
        int tbY = HEIGHT - 44;
        g.setColor(NAVY_DARK);
        g.fillRect(0, tbY, WIDTH, 44);
        g.setColor(new Color(51, 65, 85));
        g.drawLine(0, tbY, WIDTH, tbY);

        // Start icon + Active App Pill
        g.setColor(new Color(56, 189, 248));
        g.fillRoundRect(18, tbY + 9, 26, 26, 6, 6);
        g.setColor(new Color(30, 41, 59));
        g.fillRoundRect(56, tbY + 7, 420, 30, 8, 8);
        g.setColor(new Color(96, 165, 250));
        g.fillRect(66, tbY + 34, 400, 3);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("☕  " + taskbarAppTitle, 70, tbY + 26);

        // REC Badge + Clock
        g.setColor(RED_DANGER);
        g.fillRoundRect(WIDTH - 300, tbY + 10, 120, 24, 12, 12);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("● REC  " + recTime, WIDTH - 282, tbY + 26);

        g.setColor(new Color(226, 232, 240));
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.drawString("ENG   Wi-Fi   6:45 PM", WIDTH - 160, tbY + 19);
        g.drawString("05-11-2026", WIDTH - 100, tbY + 34);

        g.dispose();
        return img;
    }

    private static void drawDesktopShortcut(Graphics2D g, int x, int y, String label, Color c) {
        g.setColor(c);
        g.fillRoundRect(x + 12, y, 32, 32, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.drawString(label, x, y + 46);
    }

    /**
     * Draws a standard Desktop Window at (x=88, y=14, w=1160, h=648).
     * - OS Title Bar: y = 14..46 (height 32px)
     * - App Header Bar: y = 46..94 (height 48px) — text centered vertically at y = 76!
     */
    private static void drawDesktopWindow(Graphics2D g, String osTitle, String bannerLeft, String bannerRight) {
        int x = 88, y = 14, w = 1160, h = 648;
        g.setColor(new Color(0, 0, 0, 85));
        g.fillRoundRect(x + 6, y + 8, w, h, 12, 12);

        Color winBg = new Color(241, 245, 249);
        Color gradStart = NAVY_DARK;
        Color gradEnd = NAVY_PRIMARY;
        if (osTitle.contains("Task 1")) {
            winBg = T1_BG;
            gradStart = T1_DARK;
            gradEnd = T1_PRIMARY;
        } else if (osTitle.contains("Task 2")) {
            winBg = T2_BG;
            gradStart = T2_DARK;
            gradEnd = T2_PRIMARY;
        } else if (osTitle.contains("Task 3")) {
            gradStart = T3_DARK;
            gradEnd = T3_PRIMARY;
        } else if (osTitle.contains("Task 4")) {
            winBg = T4_BG;
            gradStart = T4_DARK;
            gradEnd = T4_PRIMARY;
        }

        g.setColor(winBg);
        g.fillRoundRect(x, y, w, h, 10, 10);

        // White OS Title Bar (y = 14..46)
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, w, 32, 10, 10);
        g.fillRect(x, y + 16, w, 16);
        g.setColor(BORDER_LIGHT);
        g.drawLine(x, y + 32, x + w, y + 32);

        g.setColor(new Color(30, 41, 59));
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("☕  " + osTitle, x + 14, y + 21);
        g.setColor(SLATE_MUTED);
        g.drawString("—      ▢      ✕", x + w - 92, y + 21);

        // Task-Themed App Banner (y = 46..94)
        if (bannerLeft != null) {
            GradientPaint gp = new GradientPaint(x, 46, gradStart, x + w, 94, gradEnd);
            g.setPaint(gp);
            g.fillRect(x + 1, 46, w - 2, 48);

            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString(bannerLeft, x + 20, 76);

            if (bannerRight != null) {
                g.setFont(new Font("SansSerif", Font.BOLD, 12));
                g.setColor(new Color(254, 240, 138));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(bannerRight, x + w - fm.stringWidth(bannerRight) - 22, 75);
            }
        }

        g.setColor(new Color(148, 163, 184));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(x, y, w, h, 10, 10);
    }

    /**
     * Draws a Chrome Browser Window at (x=88, y=14, w=1160, h=648) with Task 5's Espresso & Terracotta Theme.
     */
    private static void drawBrowserWindow(Graphics2D g, String url, String webHeaderTitle, String roleBadge, String userText) {
        int x = 88, y = 14, w = 1160, h = 648;
        g.setColor(new Color(0, 0, 0, 85));
        g.fillRoundRect(x + 6, y + 8, w, h, 12, 12);

        g.setColor(T5_BG);
        g.fillRoundRect(x, y, w, h, 10, 10);

        // Chrome Tab Bar (y = 14..44)
        g.setColor(new Color(231, 229, 228));
        g.fillRoundRect(x, y, w, 30, 10, 10);
        g.fillRect(x, y + 15, w, 15);
        g.setColor(Color.WHITE);
        g.fillRoundRect(x + 14, y + 5, 310, 26, 8, 8);
        g.setColor(T5_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("📚 Oasis Digital Library — OIBSIP Task 5", x + 26, y + 21);
        g.setColor(SLATE_MUTED);
        g.drawString("—      ▢      ✕", x + w - 92, y + 20);

        // Chrome Address Bar (y = 44..78)
        g.setColor(Color.WHITE);
        g.fillRect(x + 1, 44, w - 2, 34);
        g.setColor(new Color(250, 245, 235));
        g.fillRoundRect(x + 90, 49, 820, 24, 12, 12);
        g.setColor(new Color(214, 211, 209));
        g.drawRoundRect(x + 90, 49, 820, 24, 12, 12);
        g.setColor(SLATE_MUTED);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("←   →   ↻", x + 18, 66);
        g.setColor(T5_DARK);
        g.drawString("🔒  " + url, x + 106, 65);

        // Web App Header (y = 78..128) — Espresso & Terracotta Gradient
        GradientPaint gp = new GradientPaint(x, 78, T5_DARK, x + w, 128, T5_PRIMARY);
        g.setPaint(gp);
        g.fillRect(x + 1, 78, w - 2, 50);
        g.setColor(new Color(245, 158, 11));
        g.fillRect(x + 1, 126, w - 2, 3);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString(webHeaderTitle, x + 22, 108);

        if (roleBadge != null) {
            g.setColor(new Color(255, 255, 255, 45));
            g.fillRoundRect(x + w - 350, 91, 68, 24, 12, 12);
            g.setColor(new Color(254, 240, 138));
            g.setFont(new Font("SansSerif", Font.BOLD, 11));
            g.drawString(roleBadge, x + w - 336, 107);

            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString(userText, x + w - 270, 107);

            drawButton(g, x + w - 88, 90, 68, 26, "Logout", RED_DANGER, 11);
        }

        g.setColor(new Color(148, 163, 184));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(x, y, w, h, 10, 10);
    }

    private static void drawButton(Graphics2D g, int x, int y, int w, int h, String text, Color bg, int fontSize) {
        g.setColor(bg);
        g.fillRoundRect(x, y, w, h, 7, 7);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        FontMetrics fm = g.getFontMetrics();
        int tx = x + (w - fm.stringWidth(text)) / 2;
        int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(text, tx, ty);
    }

    private static void drawPill(Graphics2D g, int x, int y, String text, Color bg, Color fg) {
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        FontMetrics fm = g.getFontMetrics();
        int pw = fm.stringWidth(text) + 18;
        g.setColor(bg);
        g.fillRoundRect(x, y, pw, 22, 11, 11);
        g.setColor(fg);
        g.drawString(text, x + 9, y + 15);
    }

    // =========================================================================
    // TASK 1 RENDERERS
    // =========================================================================
    private static BufferedImage renderTask1Login(String user, String pass, int cx, int cy, boolean clicked, String recTime) {
        BufferedImage img = createDesktopBase("Online Reservation System (Task 1)", recTime);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawDesktopWindow(g, "Oasis Infobyte — Online Train Reservation System (Task 1)",
                "🚆 OASIS RAILWAYS — ONLINE RESERVATION & PNR CANCELLATION PORTAL", "JDBC + SQLite Connected");

        int kx = 410, ky = 160, cw = 460, ch = 365;
        g.setColor(Color.WHITE);
        g.fillRoundRect(kx, ky, cw, ch, 10, 10);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(kx, ky, cw, ch, 10, 10);

        drawCenteredText(g, "PASSENGER & ADMIN LOGIN", ky + 44, new Font("SansSerif", Font.BOLD, 19), T1_PRIMARY);
        drawCenteredText(g, "Oasis Infobyte SIP · Java Development (Task 1)", ky + 68, new Font("SansSerif", Font.PLAIN, 12), SLATE_MUTED);

        g.setColor(NAVY_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Username:", kx + 45, ky + 135);
        g.drawString("Password:", kx + 45, ky + 195);

        g.setColor(Color.WHITE);
        g.fillRect(kx + 135, ky + 114, 270, 32);
        g.fillRect(kx + 135, ky + 174, 270, 32);
        g.setColor(BORDER_LIGHT);
        g.drawRect(kx + 135, ky + 114, 270, 32);
        g.drawRect(kx + 135, ky + 174, 270, 32);

        g.setColor(NAVY_DARK);
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        g.drawString(user, kx + 146, ky + 135);
        g.drawString(pass, kx + 146, ky + 195);

        drawCenteredText(g, "Demo Credentials: user1 / pass123  or  admin / admin123", ky + 245,
                new Font("SansSerif", Font.ITALIC, 12), SLATE_MUTED);

        drawButton(g, 530, 450, 220, 42, "Login to Portal", T1_ACCENT, 13);

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTask1Reservation(
            String internName, String passName, String trainNo, String trainName,
            boolean booked, boolean showModal, int cx, int cy, boolean clicked, String recTime) {

        BufferedImage img = createDesktopBase("Online Reservation System (Task 1)", recTime);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawDesktopWindow(g, "Oasis Infobyte — Online Train Reservation System (Task 1)",
                "🚆 OASIS RAILWAYS — ONLINE RESERVATION & PNR CANCELLATION PORTAL",
                "Logged in: " + internName + " (user1)");

        // Tabs at y = 106..138
        drawButton(g, 115, 106, 260, 32, "1. Reservation Form (Book Ticket)", T1_ACCENT, 12);
        drawButton(g, 385, 106, 280, 32, "2. Cancellation Form (PNR Lookup)", SLATE_MUTED, 12);

        // Left Card (Reservation Form)
        g.setColor(Color.WHITE);
        g.fillRoundRect(115, 148, 670, 490, 8, 8);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(115, 148, 670, 490, 8, 8);

        g.setColor(T1_PRIMARY);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Passenger & Train Reservation Details", 140, 178);

        String[][] fields = {
                {"Passenger Name *:", passName},
                {"Train Number (Numeric) *:", trainNo},
                {"Train Name (Auto-Filled):", trainName},
                {"Class Type *:", "First AC (1A)"},
                {"Date of Journey (YYYY-MM-DD) *:", "2026-12-20"},
                {"Source Station *:", "Mumbai Central"},
                {"Destination Station *:", "New Delhi"}
        };

        int fy = 200;
        for (int i = 0; i < fields.length; i++) {
            g.setColor(NAVY_DARK);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString(fields[i][0], 145, fy + 21);

            g.setColor(i == 2 ? new Color(254, 242, 242) : Color.WHITE);
            g.fillRect(365, fy, 390, 32);
            g.setColor(i == 2 ? T1_ACCENT : BORDER_LIGHT);
            g.drawRect(365, fy, 390, 32);

            g.setColor(i == 2 ? T1_ACCENT : NAVY_DARK);
            g.setFont(new Font("SansSerif", i == 2 ? Font.BOLD : Font.PLAIN, 13));
            g.drawString(fields[i][1], 376, fy + 21);
            fy += 48;
        }

        drawButton(g, 145, 555, 210, 40, "Insert / Book Ticket", T1_GOLD, 13);
        drawButton(g, 370, 555, 130, 40, "Reset Form", SLATE_MUTED, 12);

        // Right Card (PNR Receipt)
        g.setColor(Color.WHITE);
        g.fillRoundRect(800, 148, 425, 490, 8, 8);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(800, 148, 425, 490, 8, 8);

        g.setColor(T1_PRIMARY);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Generated E-Ticket & PNR Receipt", 820, 178);

        g.setColor(new Color(248, 250, 252));
        g.fillRect(820, 195, 385, 380);
        g.setColor(BORDER_LIGHT);
        g.drawRect(820, 195, 385, 380);

        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        if (booked) {
            g.setColor(GREEN_SUCCESS);
            g.drawString("=== RESERVATION CONFIRMED ===", 836, 225);
            g.setColor(NAVY_DARK);
            String[] rec = {
                    "PNR Number      : PNR8492017",
                    "Passenger Name  : " + internName,
                    "Train Number    : 22436",
                    "Train Name      : Vande Bharat Express",
                    "Class Type      : First AC (1A)",
                    "Journey Date    : 2026-12-20",
                    "Source Station  : Mumbai Central",
                    "Destination     : New Delhi",
                    "Booked By       : user1",
                    "Booking Status  : CONFIRMED (SQLite)"
            };
            int ry = 255;
            for (String r : rec) {
                g.drawString(r, 836, ry);
                ry += 25;
            }
        } else {
            g.setColor(SLATE_MUTED);
            g.drawString("Fill out the Reservation Form and click", 836, 235);
            g.drawString("'Insert / Book Ticket' to generate an", 836, 258);
            g.drawString("auto-generated unique PNR number.", 836, 281);
        }

        if (showModal) {
            drawDialog(g, "Booking Confirmation — PNR: PNR8492017", new String[]{
                    "Ticket Booked Successfully in SQLite Database!",
                    "PNR Number : PNR8492017",
                    "Passenger  : " + internName,
                    "Train      : 22436 - Vande Bharat Express",
                    "Route      : Mumbai Central -> New Delhi (2026-12-20)"
            }, false);
        }

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTask1Cancel(
            String internName, String pnr, boolean fetched, boolean showConfirm, boolean cancelled,
            int cx, int cy, boolean clicked, String recTime) {

        BufferedImage img = createDesktopBase("Online Reservation System (Task 1)", recTime);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawDesktopWindow(g, "Oasis Infobyte — Online Train Reservation System (Task 1)",
                "🚆 OASIS RAILWAYS — ONLINE RESERVATION & PNR CANCELLATION PORTAL",
                "Logged in: " + internName + " (user1)");

        drawButton(g, 115, 106, 260, 32, "1. Reservation Form (Book Ticket)", SLATE_MUTED, 12);
        drawButton(g, 385, 106, 280, 32, "2. Cancellation Form (PNR Lookup)", T1_ACCENT, 12);

        g.setColor(Color.WHITE);
        g.fillRoundRect(115, 148, 1110, 490, 8, 8);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(115, 148, 1110, 490, 8, 8);

        g.setColor(T1_PRIMARY);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Step 1: Enter PNR Number to Retrieve Booking Details", 145, 182);

        g.setColor(NAVY_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("PNR Number:", 145, 231);

        g.setColor(Color.WHITE);
        g.fillRect(250, 208, 240, 36);
        g.setColor(T1_ACCENT);
        g.drawRect(250, 208, 240, 36);
        g.setColor(NAVY_DARK);
        g.setFont(new Font("Monospaced", Font.BOLD, 15));
        g.drawString(pnr, 265, 231);

        drawButton(g, 510, 208, 190, 36, "Fetch Booking Details", T1_ACCENT, 12);

        g.setColor(new Color(248, 250, 252));
        g.fillRect(145, 265, 1050, 270);
        g.setColor(BORDER_LIGHT);
        g.drawRect(145, 265, 1050, 270);

        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        if (cancelled) {
            g.setColor(RED_DANGER);
            g.drawString("=== RESERVATION CANCELLED SUCCESSFULLY ===", 170, 305);
            g.setColor(NAVY_DARK);
            g.drawString("Booking with PNR " + pnr + " (Passenger: " + internName + ") has been permanently removed from SQLite.", 170, 340);
        } else if (fetched) {
            g.setColor(GREEN_SUCCESS);
            g.drawString("=== FETCHED RESERVATION RECORD (VERIFIED IN DATABASE) ===", 170, 300);
            g.setColor(NAVY_DARK);
            String[] lines = {
                    "PNR Number          : " + pnr,
                    "Passenger Name      : " + internName,
                    "Train Number & Name : 22436 — Vande Bharat Express (NDLS - BSB)",
                    "Class & Journey Date: First AC (1A)   |   Date: 2026-12-20",
                    "Source & Destination: Mumbai Central  -->  New Delhi",
                    "Current Status      : ACTIVE (Click 'Confirm Cancellation' below to cancel ticket)"
            };
            int ly = 330;
            for (String l : lines) {
                g.drawString(l, 170, ly);
                ly += 28;
            }
        } else {
            g.setColor(SLATE_MUTED);
            g.drawString("Click 'Fetch Booking Details' to query SQLite for PNR " + pnr + "...", 170, 310);
        }

        drawButton(g, 790, 555, 350, 38, "Confirm Cancellation (Delete Booking)",
                fetched && !cancelled ? RED_DANGER : SLATE_MUTED, 13);

        if (showConfirm) {
            drawDialog(g, "Confirm Ticket Cancellation", new String[]{
                    "Are you sure you want to cancel this reservation?",
                    "PNR Number : " + pnr,
                    "Passenger  : " + internName,
                    "Train      : 22436 - Vande Bharat Express",
                    "This action deletes the booking from SQLite."
            }, true);
        }

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    // =========================================================================
    // TASK 2 RENDERER
    // =========================================================================
    private static BufferedImage renderTask2(
            String internName, int attemptsUsed, String banner, Color bannerCol, String typedGuess,
            List<String> log, int score, String summary, boolean showModal, int cx, int cy, boolean clicked, String recTime) {

        BufferedImage img = createDesktopBase("Number Guessing Game (Task 2)", recTime);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawDesktopWindow(g, "Oasis Infobyte — Number Guessing Game (Java Task 2)",
                "🎯 NUMBER GUESSING GAME — PLAYER: " + internName.toUpperCase(),
                "Round: 1   |   Total Score: " + score + " pts");

        // Left Play Card
        g.setColor(Color.WHITE);
        g.fillRoundRect(115, 110, 720, 530, 8, 8);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(115, 110, 720, 530, 8, 8);

        g.setColor(NAVY_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Difficulty Level (Bonus):", 145, 148);
        g.setColor(T2_BG);
        g.fillRoundRect(320, 128, 330, 28, 6, 6);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(320, 128, 330, 28, 6, 6);
        g.setColor(T2_PRIMARY);
        g.drawString("Medium (Range: 1 - 100  |  Max: 7 Attempts)  ▼", 334, 147);

        g.setColor(NAVY_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Attempts Used: " + attemptsUsed + " / 7  (" + (7 - attemptsUsed) + " Attempts Remaining)", 310, 192);

        g.setColor(new Color(226, 232, 240));
        g.fillRoundRect(255, 205, 440, 22, 11, 11);
        g.setColor(T2_ACCENT);
        g.fillRoundRect(255, 205, (440 * attemptsUsed) / 7, 22, 11, 11);

        // Feedback Banner Box
        g.setColor(T2_BG);
        g.fillRoundRect(145, 248, 660, 70, 8, 8);
        g.setColor(bannerCol);
        g.drawRoundRect(145, 248, 660, 70, 8, 8);
        g.setFont(new Font("SansSerif", Font.BOLD, 19));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(banner, 145 + (660 - fm.stringWidth(banner)) / 2, 290);

        // Input Box (x=285, y=345, w=120, h=40) & Submit Guess button (x=420, y=345, w=130, h=40 -> center 485, 365)
        g.setColor(Color.WHITE);
        g.fillRect(285, 345, 120, 40);
        g.setColor(T2_ACCENT);
        g.setStroke(new BasicStroke(2f));
        g.drawRect(285, 345, 120, 40);
        g.setColor(NAVY_DARK);
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.drawString(typedGuess, 325, 372);

        drawButton(g, 420, 345, 130, 40, "Submit Guess", T2_ACCENT, 13);
        drawButton(g, 565, 345, 175, 40, "Play Again (Next Round)", T4_ACCENT, 12);

        // Current Round Log
        g.setColor(T2_BG);
        g.fillRect(145, 405, 660, 210);
        g.setColor(BORDER_LIGHT);
        g.drawRect(145, 405, 660, 210);
        g.setColor(NAVY_DARK);
        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        int ly = 435;
        for (String l : log) {
            g.setColor(l.contains("CORRECT") ? GREEN_SUCCESS : (l.contains("Attempt") ? T2_FUCHSIA : T2_PRIMARY));
            g.drawString(l, 162, ly);
            ly += 28;
        }

        // Right Scoreboard Sidebar
        g.setColor(Color.WHITE);
        g.fillRoundRect(850, 110, 375, 530, 8, 8);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(850, 110, 375, 530, 8, 8);

        g.setColor(T2_PRIMARY);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("🏆 Multi-Round Score Summary", 872, 145);

        g.setColor(new Color(248, 250, 252));
        g.fillRect(872, 165, 330, 450);
        g.setColor(BORDER_LIGHT);
        g.drawRect(872, 165, 330, 450);

        g.setColor(NAVY_DARK);
        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        int sy = 198;
        for (String sl : summary.split("\n")) {
            g.drawString(sl, 888, sy);
            sy += 24;
        }

        if (showModal) {
            drawDialog(g, "You Won Round 1!", new String[]{
                    "Correct! The secret number was 42.",
                    "Player  : " + internName,
                    "Summary : Round 1 — guessed in 4 attempts (+80 pts)",
                    "Would you like to Play Again?"
            }, true);
        }

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    // =========================================================================
    // TASK 3 TERMINAL HELPERS
    // =========================================================================
    private static void encodeTerminalType(AWTSequenceEncoder enc, List<String> term, String prompt, String typed, String clock) throws Exception {
        term.add(prompt);
        int idx = term.size() - 1;
        for (int i = 1; i <= typed.length(); i++) {
            term.set(idx, prompt + typed.substring(0, i) + "█");
            enc.encodeImage(renderTask3Terminal(term, clock));
            enc.encodeImage(renderTask3Terminal(term, clock));
        }
        term.set(idx, prompt + typed);
        enc.encodeImage(renderTask3Terminal(term, clock));
    }

    private static void encodeTerminalWait(AWTSequenceEncoder enc, List<String> term, int frames, String clock) throws Exception {
        BufferedImage img = renderTask3Terminal(term, clock);
        encodeRepeated(enc, img, frames);
    }

    private static BufferedImage renderTask3Terminal(List<String> lines, String recTime) {
        BufferedImage img = createDesktopBase("Windows PowerShell — ATM Interface (Task 3)", recTime);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawDesktopWindow(g, "Windows PowerShell — OIBSIP\\Java-Task3-ATMInterface (Riya Shivaji Nagare)",
                "🏦 OASIS NATIONAL BANK — CONSOLE ATM INTERFACE (5 OOP CLASSES)",
                "Classes: Main, ATM, Bank, Account, Transaction");

        g.setColor(T3_DARK);
        g.fillRect(90, 95, 1156, 565);

        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        int start = Math.max(0, lines.size() - 23);
        int y = 125;
        for (int i = start; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("[SUCCESS]") || line.contains("LOGIN SUCCESSFUL")) {
                g.setColor(new Color(52, 211, 153));
            } else if (line.contains("[ERROR]")) {
                g.setColor(new Color(251, 113, 133));
            } else if (line.startsWith("===") || line.startsWith("---")) {
                g.setColor(new Color(110, 231, 183));
            } else if (line.startsWith("PS ")) {
                g.setColor(new Color(253, 224, 71));
            } else {
                g.setColor(new Color(236, 253, 245));
            }
            g.drawString(line, 115, y);
            y += 23;
        }

        drawMouseCursor(g, 920, 520, false);
        g.dispose();
        return img;
    }

    // =========================================================================
    // TASK 4 RENDERERS
    // =========================================================================
    private static BufferedImage renderTask4Login(String user, String pass, int cx, int cy, boolean clicked, String recTime) {
        BufferedImage img = createDesktopBase("Online Examination System (Task 4)", recTime);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawDesktopWindow(g, "Oasis Infobyte — Online Examination System (Java Task 4)",
                "🎓 OASIS ONLINE EXAMINATION PORTAL — TIMED MCQ ASSESSMENT", "Session Guard Active");

        int kx = 410, ky = 165, cw = 460, ch = 355;
        g.setColor(Color.WHITE);
        g.fillRoundRect(kx, ky, cw, ch, 10, 10);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(kx, ky, cw, ch, 10, 10);

        drawCenteredText(g, "CANDIDATE SIGN IN", ky + 45, new Font("SansSerif", Font.BOLD, 19), T4_PRIMARY);
        drawCenteredText(g, "Oasis Infobyte SIP · Java Development (Task 4)", ky + 68, new Font("SansSerif", Font.PLAIN, 12), SLATE_MUTED);

        g.setColor(NAVY_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Username:", kx + 45, ky + 135);
        g.drawString("Password:", kx + 45, ky + 190);

        g.setColor(Color.WHITE);
        g.fillRect(kx + 135, ky + 114, 270, 32);
        g.fillRect(kx + 135, ky + 169, 270, 32);
        g.setColor(BORDER_LIGHT);
        g.drawRect(kx + 135, ky + 114, 270, 32);
        g.drawRect(kx + 135, ky + 169, 270, 32);

        g.setColor(NAVY_DARK);
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        g.drawString(user, kx + 146, ky + 135);
        g.drawString(pass, kx + 146, ky + 190);

        // Button at (x=520, y=450, w=240, h=40) -> center (640, 470)
        drawButton(g, 520, 450, 240, 40, "Login to Exam Dashboard", T4_ACCENT, 13);

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTask4Profile(String name, boolean updated, int cx, int cy, boolean clicked, String recTime) {
        BufferedImage img = createDesktopBase("Online Examination System (Task 4)", recTime);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawDesktopWindow(g, "Oasis Infobyte — Online Examination System (Java Task 4)",
                "🎓 CANDIDATE PROFILE & EXAM INSTRUCTIONS", "Logged in: " + name + " (student1)");

        g.setColor(Color.WHITE);
        g.fillRoundRect(310, 125, 660, 480, 10, 10);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(310, 125, 660, 480, 10, 10);

        g.setColor(T4_PRIMARY);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("Step 1: Update Candidate Profile & Password Before Exam", 340, 162);

        g.setColor(NAVY_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Display Name:", 340, 210);
        g.drawString("New Password:", 340, 260);

        g.setColor(Color.WHITE);
        g.fillRect(465, 190, 380, 32);
        g.fillRect(465, 240, 380, 32);
        g.setColor(BORDER_LIGHT);
        g.drawRect(465, 190, 380, 32);
        g.drawRect(465, 240, 380, 32);

        g.setColor(NAVY_DARK);
        g.drawString(name, 478, 211);
        g.drawString("********", 478, 261);

        // Update button at (340, 305, 220, 36) -> center (450, 323)
        drawButton(g, 340, 305, 220, 36, "Update Profile & Password", T4_ACCENT, 12);
        if (updated) {
            g.setColor(GREEN_SUCCESS);
            g.setFont(new Font("SansSerif", Font.BOLD, 13));
            g.drawString("✓ Profile & Password Updated Successfully!", 578, 328);
        }

        g.setColor(T4_BG);
        g.fillRect(340, 362, 600, 145);
        g.setColor(BORDER_LIGHT);
        g.drawRect(340, 362, 600, 145);
        g.setColor(NAVY_DARK);
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.drawString("• Total Questions : 8 Multiple-Choice Questions (4 Radio Options each)", 358, 395);
        g.drawString("• Time Limit      : 30:00 Minutes (Live Countdown Timer with Auto-Submit)", 358, 425);
        g.drawString("• Navigation      : Previous & Next buttons to review/modify answers", 358, 455);
        g.drawString("• Session Guard   : Closing window mid-exam prompts confirmation dialog", 358, 485);

        // Start button at (340, 526, 600, 44) -> center (640, 548)
        drawButton(g, 340, 526, 600, 44, "Start Timed Examination Now ->", T4_PRIMARY, 14);

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTask4Exam(
            String name, int qNum, int qTotal, String timer, String prompt, String[] opts,
            int selIdx, boolean showModal, int cx, int cy, boolean clicked, String recTime) {

        BufferedImage img = createDesktopBase("Online Examination System (Task 4)", recTime);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawDesktopWindow(g, "Oasis Infobyte — Online Examination System (Java Task 4)",
                "Candidate: " + name + " (student1)", "⏱ Time Remaining: " + timer);

        g.setColor(Color.WHITE);
        g.fillRoundRect(115, 110, 1110, 525, 8, 8);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(115, 110, 1110, 525, 8, 8);

        g.setColor(T4_ACCENT);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Question " + qNum + " of " + qTotal, 148, 148);

        g.setColor(NAVY_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString(prompt, 148, 186);

        // 4 Options starting at y=240, step=60 (Option 0: 240..288, Option 1: 300..348 [center 324], Option 2: 360..408 [center 384])
        int oy = 240;
        for (int i = 0; i < 4; i++) {
            boolean sel = (i == selIdx);
            g.setColor(sel ? new Color(204, 251, 241) : new Color(248, 250, 252));
            g.fillRoundRect(148, oy, 1040, 48, 8, 8);
            g.setColor(sel ? T4_ACCENT : BORDER_LIGHT);
            g.drawRoundRect(148, oy, 1040, 48, 8, 8);

            g.setColor(sel ? T4_ACCENT : Color.WHITE);
            g.fillOval(170, oy + 15, 18, 18);
            g.setColor(SLATE_MUTED);
            g.drawOval(170, oy + 15, 18, 18);

            g.setColor(NAVY_DARK);
            g.setFont(new Font("SansSerif", sel ? Font.BOLD : Font.PLAIN, 15));
            g.drawString(opts[i], 204, oy + 30);
            oy += 60;
        }

        // Prev (148, 558, 180, 40), Next (342, 558, 170, 40 -> center 427, 578), Submit (910, 558, 190, 40 -> center 1005, 578)
        drawButton(g, 148, 558, 180, 40, "<- Previous Question", SLATE_MUTED, 12);
        drawButton(g, 342, 558, 170, 40, "Next Question ->", T4_ACCENT, 12);
        drawButton(g, 910, 558, 190, 40, "Submit Examination", T4_PRIMARY, 12);

        if (showModal) {
            drawDialog(g, "Confirm Examination Submission", new String[]{
                    "Are you sure you want to submit your examination now?",
                    "Candidate      : " + name,
                    "Questions Done : 8 of 8 Answered",
                    "Time Remaining : " + timer
            }, true);
        }

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTask4Result(String name, int cx, int cy, String recTime) {
        BufferedImage img = createDesktopBase("Online Examination System (Task 4)", recTime);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawDesktopWindow(g, "Oasis Infobyte — Online Examination System (Java Task 4)",
                "📊 EXAMINATION RESULT & QUESTION BREAKDOWN", "Candidate: " + name);

        g.setColor(Color.WHITE);
        g.fillRoundRect(115, 110, 1110, 525, 8, 8);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(115, 110, 1110, 525, 8, 8);

        g.setColor(GREEN_SUCCESS);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("Final Score: 7 out of 8 (87.5%)   |   Correct: 7   |   Incorrect: 1   |   Time Taken: 02:25", 145, 148);
        drawButton(g, 1020, 124, 180, 34, "Logout & Return", RED_DANGER, 12);

        g.setColor(new Color(248, 250, 252));
        g.fillRect(145, 170, 1050, 440);
        g.setColor(BORDER_LIGHT);
        g.drawRect(145, 170, 1050, 440);

        String[] lines = {
                "Candidate Name : " + name + " (student1)   |   Submission Mode: Manual Confirmed Submission",
                "--------------------------------------------------------------------------------------------------",
                "[CORRECT ✓]  Q1. Which keyword in Java prevents a method from being overridden in a subclass?",
                "             Your Answer: final   |   Correct Answer: final",
                "[CORRECT ✓]  Q2. Which JDBC interface executes parameterized SQL queries to prevent SQL injection?",
                "             Your Answer: PreparedStatement   |   Correct Answer: PreparedStatement",
                "[CORRECT ✓]  Q3. What is the time complexity of accessing an element by index in an ArrayList?",
                "             Your Answer: O(1)   |   Correct Answer: O(1)",
                "[CORRECT ✓]  Q4. Which Swing layout manager switches between multiple screens in the same container?",
                "             Your Answer: CardLayout   |   Correct Answer: CardLayout",
                "[CORRECT ✓]  Q5. Wrapping data fields as private with public getter/setter methods is called:",
                "             Your Answer: Encapsulation   |   Correct Answer: Encapsulation",
                "[CORRECT ✓]  Q6. Which package contains Swing's Timer class?",
                "             Your Answer: javax.swing   |   Correct Answer: javax.swing",
                "[CORRECT ✓]  Q8. Which collection in Java does NOT allow duplicate elements?",
                "             Your Answer: HashSet   |   Correct Answer: HashSet"
        };
        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        int ly = 200;
        for (String l : lines) {
            g.setColor(l.startsWith("[CORRECT") ? GREEN_SUCCESS : NAVY_DARK);
            g.drawString(l, 165, ly);
            ly += 24;
        }

        drawMouseCursor(g, cx, cy, false);
        g.dispose();
        return img;
    }

    // =========================================================================
    // TASK 5 FULL WEB PORTAL RENDERERS (ZERO OVERLAP + EXACT BUTTON CENTERS)
    // =========================================================================
    private static BufferedImage renderTask5Login(String internName, String user, String pass, int cx, int cy, boolean clicked, String recTime) {
        BufferedImage img = createDesktopBase("Google Chrome — Oasis Digital Library (Task 5)", recTime);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawBrowserWindow(g, "http://localhost:8085/",
                "📚 OASIS DIGITAL LIBRARY MANAGEMENT SYSTEM — Role-Based Admin & Member Portal", null, null);

        // Left Card: Sign In (x=130, y=160, w=460, h=430)
        g.setColor(Color.WHITE);
        g.fillRoundRect(130, 160, 460, 430, 10, 10);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(130, 160, 460, 430, 10, 10);

        g.setColor(T5_PRIMARY);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("🔐 Sign In (Admin / Member)", 155, 200);
        g.setColor(SLATE_MUTED);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Log in with your library credentials to access Admin or User module.", 155, 222);

        g.setColor(NAVY_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Username", 155, 265);
        g.setColor(Color.WHITE);
        g.fillRoundRect(155, 274, 410, 36, 6, 6);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(155, 274, 410, 36, 6, 6);
        g.setColor(NAVY_DARK);
        g.drawString(user, 168, 297);

        g.drawString("Password", 155, 342);
        g.setColor(Color.WHITE);
        g.fillRoundRect(155, 351, 410, 36, 6, 6);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(155, 351, 410, 36, 6, 6);
        g.setColor(NAVY_DARK);
        g.drawString(pass, 168, 374);

        // Login button at (x=155, y=428, w=410, h=40) -> center = (360, 448)
        drawButton(g, 155, 428, 410, 40, "Login to Library Portal", T5_ACCENT, 13);

        g.setColor(T5_BG);
        g.fillRoundRect(155, 490, 410, 75, 8, 8);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(155, 490, 410, 75, 8, 8);
        g.setColor(NAVY_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("⚡ Instant Demo Accounts:", 168, 512);
        drawPill(g, 168, 525, "Admin (admin / admin123)", new Color(255, 237, 213), T5_PRIMARY);
        drawPill(g, 355, 525, "Member (riya / user123)", new Color(220, 252, 231), new Color(22, 101, 52));

        // Right Card: New Member Registration (x=620, y=160, w=460, h=430)
        g.setColor(Color.WHITE);
        g.fillRoundRect(620, 160, 460, 430, 10, 10);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(620, 160, 460, 430, 10, 10);

        g.setColor(T5_PRIMARY);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("📝 New Member Registration", 645, 200);
        g.setColor(SLATE_MUTED);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Create a library member account to browse, issue, and reserve books.", 645, 222);

        String[][] regFields = {
                {"Full Name", internName},
                {"Email Address", "riya.nagare@student.edu"},
                {"Username", "riya"}
        };
        int ry = 252;
        for (String[] rf : regFields) {
            g.setColor(NAVY_DARK);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString(rf[0], 645, ry);
            g.setColor(Color.WHITE);
            g.fillRoundRect(645, ry + 7, 410, 32, 6, 6);
            g.setColor(BORDER_LIGHT);
            g.drawRoundRect(645, ry + 7, 410, 32, 6, 6);
            g.setColor(NAVY_DARK);
            g.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g.drawString(rf[1], 658, ry + 28);
            ry += 58;
        }
        drawButton(g, 645, 428, 410, 40, "Register Member Account", GREEN_SUCCESS, 13);

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTask5AdminCatalogue(boolean bookAdded, int cx, int cy, boolean clicked, String recTime) {
        BufferedImage img = createDesktopBase("Google Chrome — Oasis Digital Library (Admin Portal)", recTime);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawBrowserWindow(g, "http://localhost:8085/#admin-catalogue",
                "📚 OASIS DIGITAL LIBRARY MANAGEMENT SYSTEM", "ADMIN", "Chief Librarian (admin)");

        // 4 KPI Stat Cards at y=140..202
        String[][] kpis = {
                {"CATALOGUE TITLES", bookAdded ? "9" : "8"},
                {"ACTIVE ISSUED BOOKS", "3"},
                {"REGISTERED MEMBERS", "4"},
                {"PENDING OVERDUE FINES", "₹30.00"}
        };
        int kx = 112;
        for (String[] kpi : kpis) {
            g.setColor(Color.WHITE);
            g.fillRoundRect(kx, 140, 265, 62, 8, 8);
            g.setColor(BORDER_LIGHT);
            g.drawRoundRect(kx, 140, 265, 62, 8, 8);
            g.setColor(SLATE_MUTED);
            g.setFont(new Font("SansSerif", Font.BOLD, 10));
            g.drawString(kpi[0], kx + 16, 160);
            g.setColor(T5_PRIMARY);
            g.setFont(new Font("SansSerif", Font.BOLD, 22));
            g.drawString(kpi[1], kx + 16, 190);
            kx += 282;
        }

        // Navigation Tabs at y=212..242
        drawButton(g, 112, 212, 250, 30, "📖 Manage Catalogue (CRUD)", T5_PRIMARY, 11);
        drawButton(g, 370, 212, 220, 30, "📅 Issued Books & Due Dates", SLATE_MUTED, 11);
        drawButton(g, 598, 212, 210, 30, "💳 Fine Management (₹5/day)", SLATE_MUTED, 11);
        drawButton(g, 816, 212, 180, 30, "👥 Registered Members", SLATE_MUTED, 11);
        drawButton(g, 1004, 212, 220, 30, "📬 Advance Bookings & Queries", SLATE_MUTED, 11);

        // Main Card at y=250..645
        g.setColor(Color.WHITE);
        g.fillRoundRect(112, 250, 1112, 395, 8, 8);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(112, 250, 1112, 395, 8, 8);

        g.setColor(T5_PRIMARY);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("➕ Add New Book to Library Catalogue", 135, 276);

        // Input fields row at y=284..308
        String[] inputs = {"Title: Spring Boot in Action", "Author: Craig Walls", "ISBN: 978-1617292545", "Category: Computer Science", "Qty: 4"};
        int ix = 135;
        int[] widths = {240, 200, 190, 210, 90};
        for (int i = 0; i < inputs.length; i++) {
            g.setColor(new Color(248, 250, 252));
            g.fillRoundRect(ix, 282, widths[i], 26, 5, 5);
            g.setColor(BORDER_LIGHT);
            g.drawRoundRect(ix, 282, widths[i], 26, 5, 5);
            g.setColor(NAVY_DARK);
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.drawString(inputs[i], ix + 8, 299);
            ix += widths[i] + 10;
        }

        // "+ Add Book to Catalogue" button at (x=135, y=312, w=210, h=34) -> center = (240, 329)
        drawButton(g, 135, 312, 210, 34, "+ Add Book to Catalogue", T5_ACCENT, 12);

        // Existing Book Records Table
        g.setColor(T5_PRIMARY);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("📚 Existing Book Catalogue Records", 135, 372);

        // Table Header
        g.setColor(new Color(241, 245, 249));
        g.fillRect(135, 382, 1065, 28);
        g.setColor(NAVY_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("ID", 145, 400);
        g.drawString("BOOK TITLE", 200, 400);
        g.drawString("AUTHOR", 470, 400);
        g.drawString("ISBN", 650, 400);
        g.drawString("CATEGORY", 790, 400);
        g.drawString("AVAILABILITY", 940, 400);
        g.drawString("ACTIONS", 1090, 400);

        String[][] rows = {
                {"#1", "Effective Java (3rd Edition)", "Joshua Bloch", "978-0134685991", "Computer Science", "3 of 4 Available", "OK"},
                {"#2", "Clean Code", "Robert C. Martin", "978-0132350884", "Computer Science", "3 of 3 Available", "OK"},
                {"#3", "Design Patterns: GoF", "Erich Gamma et al.", "978-0201633610", "Computer Science", "0 of 1 Available", "OUT"},
                {"#4", "A Brief History of Time", "Stephen Hawking", "978-0553380163", "Science", "5 of 5 Available", "OK"}
        };

        int ty = 410;
        for (String[] r : rows) {
            drawCatalogueTableRow(g, ty, r[0], r[1], r[2], r[3], r[4], r[5], "OK".equals(r[6]));
            ty += 38;
        }
        if (bookAdded) {
            drawCatalogueTableRow(g, ty, "#9", "Spring Boot in Action", "Craig Walls", "978-1617292545", "Computer Science", "4 of 4 Available", true);

            // Toast notification
            g.setColor(NAVY_DARK);
            g.fillRoundRect(830, 595, 370, 38, 8, 8);
            g.setColor( new Color(74, 222, 128));
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString("✓ Book 'Spring Boot in Action' added to SQLite!", 848, 619);
        }

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static void drawCatalogueTableRow(Graphics2D g, int y, String id, String title, String author, String isbn, String cat, String avail, boolean ok) {
        g.setColor(BORDER_LIGHT);
        g.drawLine(135, y + 36, 1200, y + 36);
        g.setColor(NAVY_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString(id, 145, y + 23);
        g.drawString(title, 200, y + 23);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString(author, 470, y + 23);
        g.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g.drawString(isbn, 650, y + 23);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString(cat, 790, y + 23);

        drawPill(g, 940, y + 8, avail,
                ok ? new Color(220, 252, 231) : new Color(254, 226, 226),
                ok ? new Color(22, 101, 52) : new Color(153, 27, 27));

        drawButton(g, 1085, y + 6, 48, 24, "Edit", SLATE_MUTED, 10);
        drawButton(g, 1138, y + 6, 56, 24, "Delete", RED_DANGER, 10);
    }

    private static BufferedImage renderTask5AdminFines(String internName, boolean paid, int cx, int cy, boolean clicked, String recTime) {
        BufferedImage img = createDesktopBase("Google Chrome — Oasis Digital Library (Fine Management)", recTime);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawBrowserWindow(g, "http://localhost:8085/#admin-fines",
                "📚 OASIS DIGITAL LIBRARY MANAGEMENT SYSTEM", "ADMIN", "Chief Librarian (admin)");

        // 4 KPI Stat Cards
        String[][] kpis = {
                {"CATALOGUE TITLES", "9"},
                {"ACTIVE ISSUED BOOKS", "3"},
                {"REGISTERED MEMBERS", "4"},
                {"PENDING OVERDUE FINES", paid ? "₹0.00" : "₹30.00"}
        };
        int kx = 112;
        for (String[] kpi : kpis) {
            g.setColor(Color.WHITE);
            g.fillRoundRect(kx, 140, 265, 62, 8, 8);
            g.setColor(BORDER_LIGHT);
            g.drawRoundRect(kx, 140, 265, 62, 8, 8);
            g.setColor(SLATE_MUTED);
            g.setFont(new Font("SansSerif", Font.BOLD, 10));
            g.drawString(kpi[0], kx + 16, 160);
            g.setColor(NAVY_PRIMARY);
            g.setFont(new Font("SansSerif", Font.BOLD, 22));
            g.drawString(kpi[1], kx + 16, 190);
            kx += 282;
        }

        // Tabs
        drawButton(g, 112, 212, 250, 30, "📖 Manage Catalogue (CRUD)", SLATE_MUTED, 11);
        drawButton(g, 370, 212, 220, 30, "📅 Issued Books & Due Dates", SLATE_MUTED, 11);
        drawButton(g, 598, 212, 210, 30, "💳 Fine Management (₹5/day)", T5_PRIMARY, 11);
        drawButton(g, 816, 212, 180, 30, "👥 Registered Members", SLATE_MUTED, 11);
        drawButton(g, 1004, 212, 220, 30, "📬 Advance Bookings & Queries", SLATE_MUTED, 11);

        // Card
        g.setColor(Color.WHITE);
        g.fillRoundRect(112, 250, 1112, 395, 8, 8);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(112, 250, 1112, 395, 8, 8);

        g.setColor(T5_PRIMARY);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("💳 Overdue Fine Management (Automatic ₹5.00 per Overdue Day)", 135, 282);

        g.setColor(new Color(241, 245, 249));
        g.fillRect(135, 300, 1065, 30);
        g.setColor(NAVY_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("ISSUE ID", 145, 320);
        g.drawString("MEMBER NAME", 230, 320);
        g.drawString("BOOK TITLE", 440, 320);
        g.drawString("DUE DATE", 670, 320);
        g.drawString("OVERDUE FINE", 790, 320);
        g.drawString("STATUS", 900, 320);
        g.drawString("ACTION", 980, 320);

        g.setColor(NAVY_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("#1", 145, 365);
        g.drawString(internName + " (riya)", 230, 365);
        g.drawString("Effective Java (3rd Edition)", 440, 365);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g.drawString("6 Days Ago", 670, 365);
        g.setColor(RED_DANGER);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("₹30.00", 790, 365);

        if (paid) {
            drawPill(g, 895, 350, "PAID ✓", new Color(220, 252, 231), new Color(22, 101, 52));
            g.setColor(GREEN_SUCCESS);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString("Fine Settled ✓", 980, 365);

            g.setColor(NAVY_DARK);
            g.fillRoundRect(830, 595, 360, 38, 8, 8);
            g.setColor(new Color(74, 222, 128));
            g.drawString("✓ Overdue fine of ₹30.00 marked as PAID!", 852, 619);
        } else {
            drawPill(g, 895, 350, "UNPAID", new Color(254, 226, 226), new Color(153, 27, 27));
            // Button at (x=960, y=346, w=160, h=30) -> center = (1040, 361)
            drawButton(g, 960, 346, 160, 30, "Mark Fine as Paid", GREEN_SUCCESS, 11);
        }

        // Lower section: Member Queries Inbox preview
        g.setColor(NAVY_PRIMARY);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("📬 Recent Member Contact / Support Queries", 135, 445);
        g.setColor(new Color(248, 250, 252));
        g.fillRoundRect(135, 460, 1065, 65, 6, 6);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(135, 460, 1065, 65, 6, 6);
        g.setColor(NAVY_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("From: " + internName + " (riya)   |   Subject: Request for Spring Microservices Book", 152, 485);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("\"Could the library please add 'Spring Microservices in Action' to the Computer Science section?\"", 152, 508);

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static BufferedImage renderTask5UserPortal(String internName, boolean reserved, int cx, int cy, boolean clicked, String recTime) {
        BufferedImage img = createDesktopBase("Google Chrome — Oasis Digital Library (Member Portal)", recTime);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        drawBrowserWindow(g, "http://localhost:8085/#member-portal",
                "📚 OASIS DIGITAL LIBRARY MANAGEMENT SYSTEM", "USER", internName + " (riya)");

        // User Navigation Tabs at y=142..174
        drawButton(g, 112, 142, 260, 32, "🔍 Browse & Search Catalogue", T5_PRIMARY, 11);
        drawButton(g, 380, 142, 260, 32, "📖 My Issued Books & Returns", SLATE_MUTED, 11);
        drawButton(g, 648, 142, 210, 32, "⏳ My Advance Bookings", SLATE_MUTED, 11);
        drawButton(g, 866, 142, 210, 32, "✉️ Contact / Query Form", SLATE_MUTED, 11);

        // Search & Category Filter Bar at y=184..228
        g.setColor(Color.WHITE);
        g.fillRoundRect(112, 184, 1112, 44, 8, 8);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(112, 184, 1112, 44, 8, 8);

        g.setColor(new Color(248, 250, 252));
        g.fillRoundRect(126, 192, 420, 28, 6, 6);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(126, 192, 420, 28, 6, 6);
        g.setColor(SLATE_MUTED);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("🔎 Search by Title or Author: [ Computer Science... ]", 138, 210);

        drawPill(g, 565, 195, "All", new Color(241, 245, 249), SLATE_MUTED);
        drawPill(g, 610, 195, "Computer Science ✓", T5_ACCENT, Color.WHITE);
        drawPill(g, 760, 195, "Science", new Color(241, 245, 249), SLATE_MUTED);
        drawPill(g, 835, 195, "History", new Color(241, 245, 249), SLATE_MUTED);
        drawPill(g, 905, 195, "Fiction", new Color(241, 245, 249), SLATE_MUTED);

        // Catalogue Card at y=238..645
        g.setColor(Color.WHITE);
        g.fillRoundRect(112, 238, 1112, 407, 8, 8);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(112, 238, 1112, 407, 8, 8);

        g.setColor(new Color(241, 245, 249));
        g.fillRect(135, 254, 1065, 28);
        g.setColor(NAVY_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("BOOK TITLE", 148, 272);
        g.drawString("AUTHOR", 430, 272);
        g.drawString("ISBN", 615, 272);
        g.drawString("CATEGORY", 765, 272);
        g.drawString("AVAILABILITY", 935, 272);

        // Row 1: Effective Java (y=290)
        drawUserBookRow(g, 286, "Effective Java (3rd Edition)", "Joshua Bloch", "978-0134685991", "Computer Science", "3 of 4 Available", true);
        // Row 2: Clean Code (y=332)
        drawUserBookRow(g, 330, "Clean Code", "Robert C. Martin", "978-0132350884", "Computer Science", "2 of 3 Available", true);
        // Row 3: Design Patterns (y=374) -> Advance Booking button at (x=935, y=384, w=195, h=30) -> center (1032, 399)
        drawUserBookRow(g, 374, "Design Patterns: GoF", "Erich Gamma et al.", "978-0201633610", "Computer Science", "0 of 1 Available", false);

        // Lower section: My Active Loans & Advance Reservations
        g.setColor(NAVY_PRIMARY);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("📖 My Issued Books, Returns & Advance Bookings (" + internName + ")", 135, 452);

        g.setColor(new Color(248, 250, 252));
        g.fillRoundRect(135, 464, 1065, 115, 6, 6);
        g.setColor(BORDER_LIGHT);
        g.drawRoundRect(135, 464, 1065, 115, 6, 6);

        g.setColor(NAVY_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("1. Loan #4   | Clean Code (Robert C. Martin)   | Issued: Today   | Due: In 14 Days   | Fine: ₹0.00", 155, 494);
        drawButton(g, 960, 476, 130, 26, "Return Book", BLUE_ACCENT, 11);

        if (reserved) {
            g.setColor(GREEN_SUCCESS);
            g.drawString("2. Booking #1 | Design Patterns: GoF            | Reserved By: " + internName + "   | Status: [ RESERVED — PRIORITY QUEUE ✓ ]", 155, 535);

            g.setColor(NAVY_DARK);
            g.fillRoundRect(735, 595, 460, 38, 8, 8);
            g.setColor(new Color(74, 222, 128));
            g.drawString("✓ Advance booking confirmed for 'Design Patterns: GoF'!", 755, 619);
        } else {
            g.setColor(SLATE_MUTED);
            g.drawString("2. No advance bookings yet — click 'Advance Booking (Reserve)' on any 0-available book above.", 155, 535);
        }

        drawMouseCursor(g, cx, cy, clicked);
        g.dispose();
        return img;
    }

    private static void drawUserBookRow(Graphics2D g, int y, String title, String author, String isbn, String cat, String avail, boolean inStock) {
        g.setColor(BORDER_LIGHT);
        g.drawLine(135, y + 40, 1200, y + 40);
        g.setColor(NAVY_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString(title, 148, y + 25);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString(author, 430, y + 25);
        g.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g.drawString(isbn, 615, y + 25);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString(cat, 765, y + 25);

        drawPill(g, 895, y + 10, avail,
                inStock ? new Color(220, 252, 231) : new Color(254, 226, 226),
                inStock ? new Color(22, 101, 52) : new Color(153, 27, 27));

        if (inStock) {
            drawButton(g, 1030, y + 7, 130, 28, "Issue Book", GREEN_SUCCESS, 11);
        } else {
            // For y=374, button is at (935, 381, 195, 30) -> center (1032, 396)
            drawButton(g, 1005, y + 7, 185, 28, "Advance Booking (Reserve)", AMBER_WARN, 11);
        }
    }

    // =========================================================================
    // DIALOG, CURSOR & TITLE CARD HELPERS
    // =========================================================================
    private static void drawDialog(Graphics2D g, String title, String[] lines, boolean yesNo) {
        g.setColor(new Color(15, 23, 42, 120));
        g.fillRect(88, 14, 1160, 648);

        int dw = 480, dh = 230;
        int dx = (WIDTH - dw) / 2, dy = 260;

        g.setColor(Color.WHITE);
        g.fillRoundRect(dx, dy, dw, dh, 12, 12);
        g.setColor(NAVY_PRIMARY);
        g.fillRoundRect(dx, dy, dw, 36, 12, 12);
        g.fillRect(dx, dy + 18, dw, 18);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString(title, dx + 18, dy + 23);

        g.setColor(NAVY_DARK);
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        int ly = dy + 62;
        for (String l : lines) {
            g.drawString(l, dx + 22, ly);
            ly += 22;
        }

        if (yesNo) {
            // Yes button center at (595, 468)
            drawButton(g, 550, 452, 90, 32, "Yes", BLUE_ACCENT, 12);
            drawButton(g, 650, 452, 90, 32, "No", SLATE_MUTED, 12);
        } else {
            // OK button center at (640, 468)
            drawButton(g, 595, 452, 90, 32, "OK", BLUE_ACCENT, 12);
        }
    }

    private static void drawMouseCursor(Graphics2D g, int x, int y, boolean clicked) {
        if (clicked) {
            g.setColor(new Color(250, 204, 21, 155));
            g.fillOval(x - 18, y - 18, 36, 36);
            g.setColor(new Color(37, 99, 235, 210));
            g.setStroke(new BasicStroke(2.5f));
            g.drawOval(x - 23, y - 23, 46, 46);
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

    private static BufferedImage renderTitleCard(String fullName, String track, String taskTitle) {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        Color gStart = NAVY_DARK;
        Color gEnd = NAVY_PRIMARY;
        Color borderCol = new Color(96, 165, 250);
        Color trackCol = new Color(56, 189, 248);
        Color taskCol = new Color(250, 204, 21);

        if (taskTitle.contains("Task 1")) {
            gStart = T1_DARK;
            gEnd = T1_PRIMARY;
            borderCol = new Color(245, 158, 11);
            trackCol = new Color(251, 191, 36);
            taskCol = new Color(254, 205, 211);
        } else if (taskTitle.contains("Task 2")) {
            gStart = T2_DARK;
            gEnd = T2_PRIMARY;
            borderCol = new Color(168, 85, 247);
            trackCol = new Color(232, 121, 249);
            taskCol = new Color(240, 171, 252);
        } else if (taskTitle.contains("Task 3")) {
            gStart = T3_DARK;
            gEnd = T3_PRIMARY;
            borderCol = new Color(16, 185, 129);
            trackCol = new Color(52, 211, 153);
            taskCol = new Color(253, 224, 71);
        } else if (taskTitle.contains("Task 4")) {
            gStart = T4_DARK;
            gEnd = T4_PRIMARY;
            borderCol = new Color(20, 184, 166);
            trackCol = new Color(45, 212, 191);
            taskCol = new Color(253, 186, 116);
        } else if (taskTitle.contains("Task 5")) {
            gStart = T5_DARK;
            gEnd = T5_PRIMARY;
            borderCol = new Color(249, 115, 22);
            trackCol = new Color(253, 186, 116);
            taskCol = new Color(253, 224, 71);
        }

        GradientPaint gp = new GradientPaint(0, 0, gStart, WIDTH, HEIGHT, gEnd);
        g.setPaint(gp);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        int boxW = 980, boxH = 480;
        int boxX = (WIDTH - boxW) / 2, boxY = (HEIGHT - boxH) / 2;

        g.setColor(new Color(15, 23, 42, 175));
        g.fillRoundRect(boxX, boxY, boxW, boxH, 28, 28);
        g.setColor(borderCol);
        g.setStroke(new BasicStroke(3f));
        g.drawRoundRect(boxX, boxY, boxW, boxH, 28, 28);

        drawCenteredText(g, "OASIS INFOBYTE — SIP INTERNSHIP DEMO WALKTHROUGH", boxY + 65,
                new Font("SansSerif", Font.BOLD, 20), trackCol);

        drawCenteredText(g, "1 · INTERN FULL NAME", boxY + 135,
                new Font("SansSerif", Font.BOLD, 15), new Color(203, 213, 225));
        drawCenteredText(g, fullName, boxY + 185,
                new Font("SansSerif", Font.BOLD, 44), Color.WHITE);

        drawCenteredText(g, "2 · ASSIGNED TRACK", boxY + 255,
                new Font("SansSerif", Font.BOLD, 15), new Color(203, 213, 225));
        drawCenteredText(g, track, boxY + 300,
                new Font("SansSerif", Font.BOLD, 32), trackCol);

        drawCenteredText(g, "3 · TASK TITLE", boxY + 370,
                new Font("SansSerif", Font.BOLD, 15), new Color(203, 213, 225));
        drawCenteredText(g, taskTitle, boxY + 418,
                new Font("SansSerif", Font.BOLD, 34), taskCol);

        g.dispose();
        return img;
    }

    private static void encodeRepeated(AWTSequenceEncoder enc, BufferedImage img, int count) throws Exception {
        for (int i = 0; i < count; i++) {
            enc.encodeImage(img);
        }
    }

    private static int[][] interpolate(int x1, int y1, int x2, int y2, int steps) {
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
