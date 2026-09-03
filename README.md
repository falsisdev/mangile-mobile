<div align="center">

# Mangile Mobile 📱

Official Android & iOS mobile application for the Mangile platform, built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose_Multiplatform-1.7.3-green.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)

</div>

---

## 📖 About

**Mangile Mobile** brings the official [Mangile](https://github.com/falsisdev/mangile) web ecosystem to mobile devices. It offers a fast, fluid, and native reading experience for Manga, Manhwa, Manhua, and Light Novels on Android and iOS devices.

Built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**, over 95% of the codebase is shared across Android and iOS platforms.

---

## ✨ Features

- 📚 **Manga, Manhwa & Light Novel Support**: Complete access to Mangile library and external database.
- 🖼️ **Advanced Manga & Webtoon Reader**:
  - **Webtoon Mode**: Smooth, vertical continuous scrolling with auto-detected format.
  - **Paged Mode**: Classic horizontal swipe reading.
  - **Pinch-to-Zoom**: Fluid gesture zooming and panning.
- 📖 **Novel Reader**:
  - Customizable typography (Font size, line height, font family).
  - Sanity PortableText rendering with headings, blockquotes, and styled text.
- 🔍 **Explore & Search**: Real-time debounced search, format, sort, and tag filtering.
- 🎨 **Material Design 3 & Material You (Monet)**:
  - System dynamic wallpaper color theme support (Material You).
  - Multiple preset accent themes (Emerald, Indigo, Rose, Amber, Ocean).
  - Dark / Light / System appearance modes.
- ⚡ **High Performance & Asynchronous Loading**: Progressive UI rendering for instant response.

---

## 🛠 Tech Stack

- **Framework**: Kotlin Multiplatform & Compose Multiplatform (Material 3)
- **Networking**: Ktor Client 3.0 (Ktor Darwin & Ktor OkHttp)
- **Image Loading**: Coil 3 (Multiplatform)
- **Serialization**: `kotlinx.serialization`
- **Asynchronous**: `kotlinx.coroutines`

---

## 🚀 Getting Started

### Prerequisites

- macOS (for iOS builds)
- JDK 21
- Android Studio Ladybug / Koala (or newer)
- Xcode 15+ (for iOS builds)

### Clone the repository

```bash
git clone https://github.com/falsisdev/mangile-mobile.git
cd mangile-mobile
```

### Build & Install Android App

```bash
./gradlew installDebug
```

### Run iOS App

```bash
open iosApp/iosApp.xcodeproj
```

---

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

Developed by **Falsis**

GitHub: [github.com/falsisdev](https://github.com/falsisdev)

---

<div align="center">

Made with ❤️ for manga & novel readers.

</div>
