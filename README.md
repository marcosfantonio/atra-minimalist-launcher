# Atra Minimalist Launcher
*From the Latin "Atra" (Dark/Black) — Pure focus, zero distractions.*

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-32%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=32)

## The Vision
Modern smartphone interfaces are engineered to hijack our attention. Default launchers are cluttered with colorful icons, notification badges, and widgets designed to induce doom-scrolling and digital consumerism. 

While searching for a solution, I tested numerous minimalist launchers. However, most share a fatal flaw: they compromise on UX (User Experience). They often look crude, feel unfinished, or lack the typographical finesse required for a pleasant daily driver. I wanted productivity, but I also demanded a cohesive, premium aesthetic.

The inspiration for Atra came from dedicated E-ink devices. Their interfaces are inherently simple, cohesive, and elegant. Unfortunately, porting that exact experience to a standard Android device often results in a disjointed user experience.

## Enter Atra
<img width="540" height="1200" alt="atra1" src="https://github.com/user-attachments/assets/afc688c4-99db-4d22-87c4-d16702ae82dd" /> <img width="540" height="1200" alt="atra2" src="https://github.com/user-attachments/assets/62f7b70f-f8d9-4324-898e-2c66e55627e1" /> <img width="540" height="1200" alt="atra3" src="https://github.com/user-attachments/assets/37624c58-4d15-4b6d-8638-0dead1a136c8" />

Atra is a minimalist Android launcher built from the ground up to bridge this gap. It applies the high-contrast, low-information philosophy of E-ink displays directly to modern OLED screens.

The result is a launcher that reclaims your focus without sacrificing design. It provides a striking, polished aesthetic that combines brutalist functionality with sophisticated looks. As a technical byproduct, the true-black UI significantly reduces OLED pixel illumination, extending your device's battery life.

## Design System & Visual Identity
<img width="540" height="1200" alt="atra4" src="https://github.com/user-attachments/asset<img width="540" height="1200" alt="atra6" src="https://github.com/user-attachments/assets/58115d90-43b0-4176-a759-614628a6e758" />
s/c8c5560a-1b9d-45cd-8427-280672f6c474" /><img width="540" height="1200" alt="atra5" src="https://github.com/user-attachments/assets/c62002d7-ae53-4ca7-9bc3-263dacfe4b1a" />


Atra is not just a technical experiment; it is a design-driven product. As a Graphic and UX Designer, I approached this launcher with a strict focus on visual hierarchy and cognitive ergonomics. 

Every monochrome nuance, the typographic scale, and especially the Atra logo are not accidental. They are the result of a comprehensive visual identity study. Before writing a single line of Kotlin, the aesthetics were carefully prototyped to ensure that true minimalism didn't mean "empty" or "cheap", but rather **intentional**. The design process was treated with the same rigor as the software architecture, ensuring that the final product is as visually striking as it is functional.

## Under the Hood (Tech Stack)
Built with modern Android development standards to ensure a lightweight footprint and fluid performance:
* **Android SDK (12.0+)**
* **Kotlin:** Leveraging coroutines for asynchronous app indexing.
* **Jetpack Compose:** For a fully declarative, responsive, and state-driven UI.

## Features (MVP Phase)
* **Monochrome Architecture:** Pure E-ink inspired interface.
* **Cognitive Load Reduction:** No widgets, no colorful icons, zero distractions.
* **Instant Indexing:** Blazing fast app listing and execution.
* **OLED Optimized:** True black background for maximum battery efficiency.

## Installation
You can install the latest compiled APK directly from the [Releases Tab](#).
1. Download the `atra-release.apk`.
2. Install the application on your Android 12.0+ device.
3. Press your device's Home button and set Atra as your default Home App.

## Roadmap
Atra is in active development. Future updates will focus on granular control over your digital environment:
* [ ] **App Hiding:** Conceal distracting applications from the main view.
* [ ] **Custom Typography:** Support for system-wide font customization.
* [ ] **Gesture Engine:** Swipe actions for quick access and navigation.
* [ ] **Local Analytics:** Private tracking of screen time and app usage.

## Contact & License
**Developer:** [Marcos Farias "Fantonio"] – devfantonio@gmail.com
**LinkedIn:** [https://www.linkedin.com/in/marcosfantonio/]

This project is open-source and distributed under the **Apache 2.0 License**. You are free to use, modify, and distribute the software, provided you state the changes and include the original copyright notice.
