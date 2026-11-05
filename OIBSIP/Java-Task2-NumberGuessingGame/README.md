# ☕ OIBSIP — Java Development Task 2: Number Guessing Game

## 📌 Project Overview
An interactive **Swing GUI Number Guessing Game** built in Java where the computer generates a secret random number and guides the player with `"Too High!"`, `"Too Low!"`, and `"Correct!"` feedback, complete with attempt limits, multi-round score tracking, and bonus difficulty tiers.

---

## ✅ Feature Checklist Completed
- [x] **Random Number Generation:** Generates a random number in the range `1 to 100` (default) at the start of each round
- [x] **GUI Input & Feedback:** Prompts user via `JTextField` and displays `"Too High!"`, `"Too Low!"`, or `"Correct!"`
- [x] **Visible Attempt Counter:** Live progress bar and attempt counter (`Attempts Used: X / Y`)
- [x] **Maximum Attempts Limit:** Ends round with a `"You Lost!"` message revealing the secret number if limit is reached
- [x] **Play Again Option:** Both a dialog prompt and a `"Play Again (Next Round)"` button
- [x] **Multi-Round Score Tracking:** Displays `"Round X — guessed in Y attempts"` across rounds
- [x] **(Bonus) Difficulty Levels:**
  - **Easy:** `1–50`, `10 attempts`
  - **Medium:** `1–100`, `7 attempts`
  - **Hard:** `1–200`, `5 attempts`

---

## 🚀 How to Compile & Run
```powershell
.\run.bat
```

## 📸 Screenshots
- [`screenshots/1_gameplay_hints.png`](screenshots/1_gameplay_hints.png)
- [`screenshots/2_round_won_summary.png`](screenshots/2_round_won_summary.png)

