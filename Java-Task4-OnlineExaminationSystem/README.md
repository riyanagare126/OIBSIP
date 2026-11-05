# ☕ OIBSIP — Java Development Task 4: Online Examination System

## 📌 Project Overview
A GUI-based **Online Examination System** built with **Java Swing** featuring candidate authentication, profile & password update, a timed Multiple-Choice Question (MCQ) interface with `Next`/`Previous` navigation, automatic submission when the countdown timer expires, window-close session protection, and a comprehensive score breakdown screen.

---

## ✅ Feature Checklist Completed
- [x] **Login Screen:** Username + Password authentication (`student1` / `exam123`)
- [x] **Profile Update Screen:** Allows candidates to update their Display Name and Password before starting the exam
- [x] **MCQ Exam Screen:** Displays one MCQ at a time with 4 `JRadioButton` options (`ButtonGroup`)
- [x] **Navigation:** `Previous Question` and `Next Question` buttons preserving selected answers
- [x] **Countdown Timer & Auto-Submit:** Live `javax.swing.Timer` visible at all times (`30:00`); automatically submits the exam when time reaches `00:00` (includes a 1-click **"Set Timer to 5s"** demo button for fast video recording!)
- [x] **Manual Submit Button:** Prompts with a confirmation dialog (noting any unanswered questions)
- [x] **Result Screen:** Displays total score (`X out of Y`), time taken, and a question-by-question breakdown of correct/incorrect answers
- [x] **Session Management:** Clicking the window close button during an active exam triggers an *"Are you sure you want to quit?"* confirmation dialog
- [x] **Logout Button:** Returns cleanly to the Login screen from the Result screen

---

## 🚀 How to Compile & Run
```powershell
.\run.bat
```
- **Demo Credentials:** `student1` / `exam123` (or `student2` / `exam123`)

## 📸 Screenshots
- [`screenshots/1_login_screen.png`](screenshots/1_login_screen.png)
- [`screenshots/2_profile_update_screen.png`](screenshots/2_profile_update_screen.png)
- [`screenshots/3_exam_mcq_timer_screen.png`](screenshots/3_exam_mcq_timer_screen.png)
- [`screenshots/4_result_breakdown_screen.png`](screenshots/4_result_breakdown_screen.png)

