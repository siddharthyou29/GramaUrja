# ⚡ Grama-Urja (ग्राम ऊर्जा)

Grama-Urja is a self-contained, high-performance Android application built with **Kotlin** and **Jetpack Compose**. Designed specifically for rural deployment and agricultural utility, it offers an intuitive, high-contrast, and outdoor-readable dashboard for monitoring and managing local power distribution, water pump zones, and crop-specific pump timer sequences.

---

## 🌟 Key Features

*   **☀️ Ultra-High Contrast Outdoor UI:** Features a sleek, custom dark theme featuring vibrant greens (`#1B5E20`) and high-luminance amber (`#FFB300`) to guarantee perfect readability under direct sunlight in open fields.
*   **📍 Smart Zone Selection:** Dynamic drop-down selection allowing users to seamlessly toggle between multiple power nodes like *Village Transformer A*, *River Pump Zone*, and *North Field Line*.
*   **🔄 Simulated Real-Time Sync:** Robust mock repository executing Kotlin Flow and Coroutines with network latencies to simulate secure cloud and hardware state synchronization.
*   **⏳ Crop-Specific Pump Timer:** Tailored countdown automation presets for major crops:
    *   🌾 **Rice:** 6 Hours
    *   🌱 **Sugarcane:** 8 Hours
    *   🌽 **Maize:** 4 Hours
*   **⏱️ Live Countdown Timer:** Real-time state-driven countdown timer (`HH:MM:SS`) with dynamic play/stop buttons to manage pump cycles precisely.
*   **🟢 Active Status Updates:** Relative time calculation ("Updated just now", "Updated 1 min ago") indicating when the last communication with the transformer occurred.

---

## 🛠️ Technology Stack & Architecture

This application is built using modern Android development practices and adheres strictly to clean code architecture:

*   **Language:** 100% Kotlin (`v1.8.10` / `v1.9.x`)
*   **UI Framework:** Jetpack Compose (Declarative UI)
*   **Design Language:** Material Design 3 (M3)
*   **State Management:** StateFlow, MutableStateFlow, and Compose state delegation (`by remember`)
*   **Concurrency:** Kotlin Coroutines & `LaunchedEffect` for lightweight, non-blocking timers and delays
*   **Architecture Pattern:** MVVM (Model-View-ViewModel) for solid separation of concerns and testability
*   **Gradle Wrapper:** Configured with Gradle `8.4` for optimal Java 21 compatibility

---

## 📂 Project Structure

```text
GramaUrja/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/gramaurja/
│   │   │   └── MainActivity.kt        <-- The core application, UI, ViewModel, and Repository
│   │   └── AndroidManifest.xml        <-- Android App Configuration
│   └── build.gradle.kts               <-- App dependencies and build parameters
├── gradle/wrapper/                    <-- Standardised Gradle execution configurations
├── build.gradle.kts                   <-- Main project plugins
├── settings.gradle.kts                <-- Repository and module registry
└── README.md                          <-- This project documentation
```

---

## 🚀 Setting Up & Running the Project

### Prerequisites
*   **Android Studio** (Hedgehog 2023.1.1 or newer recommended)
*   **JDK 21** (or compatible Android SDK toolchain)

### Steps to Run
1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/siddharthyou29/GramaUrja.git
    cd GramaUrja
    ```
2.  **Open in Android Studio:**
    *   Launch Android Studio, select **Open**, and navigate to your cloned `GramaUrja` directory.
3.  **Gradle Sync:**
    *   The IDE will automatically read the [gradle-wrapper.properties](gradle/wrapper/gradle-wrapper.properties) and download Gradle `8.4` (ensuring compatibility with Java 21 and the project configurations).
4.  **Launch the App:**
    *   Connect your Android phone (with USB Debugging enabled) or start an Emulator.
    *   Click the **Green Play Button (Run)** at the top toolbar of Android Studio!

---

## 🎨 Theme & Colors

```kotlin
val DarkGreen = Color(0xFF1B5E20)     // Primary Action & Active State
val BrightAmber = Color(0xFFFFB300)    // High-Luminance Warning & Accents
val DarkBackground = Color(0xFF121212) // Eye-strain Reduction Base
val LightText = Color(0xFFFFFFFF)      // High Contrast Text
```

---

## 📜 License
This project is open-source and available under the **MIT License**.
