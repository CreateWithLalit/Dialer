# Right Dialer - Advanced Mobile Communication System

## 🎓 Academic Project Overview
**Course:** Mobile Application Development (CS402)  
**Project Title:** Right Dialer - A Customizable Android Telecom Utility  
**Project Type:** Semester Capstone Project  

---

## 📝 Introduction
**Right Dialer** is a high-performance, feature-rich Android application developed to redefine the mobile calling experience. While stock dialers offer basic functionality, this project focuses on providing an extensible architecture that balances deep system integration with user-centric customization. 

Built on the modern Android Telecom Framework, Right Dialer serves as a primary `InCallService`, allowing it to manage the entire lifecycle of a telephone call—from initiation and ringing to active management and termination.

---

## 🚀 Key Features

### 1. Advanced Call Management
*   **Telecom Integration:** Full implementation of `InCallService` for low-level call handling.
*   **Multi-SIM Intelligence:** Native support for Dual-SIM devices with color-coded identification and SIM-specific memory.
*   **Conference Control:** Sophisticated UI for managing multi-party conference calls.
*   **T9 Predictive Dialing:** Optimized search algorithm for finding contacts directly via the numeric keypad.

### 2. Privacy & Security
*   **Integrated Call Blocking:** Block unwanted numbers, hidden IDs, and unknown callers directly within the app.
*   **Broadcast Bridge:** Secure data sharing with the "Dynamic Island" ecosystem via targeted intent broadcasting.

### 3. User Experience (UX)
*   **Dynamic UI Styles:** Multiple answer styles including classic buttons and various gesture-based sliders.
*   **Theming Engine:** Support for Material Design 3 (Material You) with dynamic accent color synchronization.
*   **In-Call Notes:** Ability to add and view persistent notes for specific callers during active sessions.

### 4. Third-Party Ecosystem
*   **Messenger Shortcuts:** Direct integration with WhatsApp, Telegram, Signal, and more.
*   **Timer Integration:** Automated "Call Back" reminders using background services.

---

## 🛠 Technology Stack
*   **Language:** Kotlin (100%)
*   **Architecture:** MVVM (Model-View-ViewModel) / Repository Pattern
*   **UI Framework:** Jetpack XML / ViewBinding
*   **Asynchronous Processing:** RxJava/RxAnimation, Kotlin Coroutines
*   **Communication:** EventBus (GreenRobot)
*   **Data Persistence:** Room Database, SharedPreferences
*   **Networking/APIs:** Android Telecom & Telephony API

---

## 📂 Project Structure
```text
com.goodwy.dialer
├── activities      # User interface controllers (Settings, Call, History)
├── services        # Background operations (InCallService, Timers)
├── helpers         # Core logic for Call Management and UI Utilities
├── models          # Data structures and entities
├── receivers       # System broadcast listeners
└── extensions      # Kotlin extension functions for clean code
```

---

## 🛠 Setup & Installation
1.  **Clone Repository:** `git clone https://github.com/your-repo/right-dialer.git`
2.  **Open in Android Studio:** Ensure you have the latest Arctic Fox or Giraffe build.
3.  **Sync Gradle:** Let the project download dependencies (Kotlin Serialization, RxAnimation, etc.).
4.  **Permissions:** Grant Phone, Contacts, and "Display over other apps" permissions upon launch.
5.  **Set as Default:** Go to `Settings > Apps > Default Apps > Phone App` and select **Right Dialer**.

---

## 🔮 Future Scope
*   Implementation of an AI-driven Spam Detection module.
*   Full Jetpack Compose migration for the In-Call UI.
*   Cloud synchronization for call history and settings.

---

## 👥 Contributors
*   **Lead Developer:** [Your Name/Goodwy]
*   **Design & UX:** [Contributor Name]

## 📜 Credits & References
*   Based on [Simple Dialer](https://github.com/SimpleMobileTools/Simple-Dialer) and [Fossify Phone](https://github.com/FossifyOrg/Phone).
*   Built using [Simple Commons](https://github.com/SimpleMobileTools/Simple-Commons) library.

---

<div align="center">
  <p><i>Developed with ❤️ for the Mobile Development Community</i></p>
</div>
