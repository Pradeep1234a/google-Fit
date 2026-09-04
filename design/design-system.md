# MOTIONIQ — Design System & Visual Architecture

> **Understand your movement. Not just your steps.**

This document defines the core visual language, color tokens, typography scale, component standards, and screen inventory for the MOTIONIQ Android application.

---

## 1. Directory Structure

```
design/
├── logo/
│   ├── motioniq-logo.png     # Master high-resolution brand logo
│   └── motioniq-icon.png     # Production app launcher icon
│
├── screens/                  # Comprehensive 20-screen UI inventory
│   ├── 01_Splash.png
│   ├── 02_Onboarding.png
│   ├── 03_Permissions.png
│   ├── 04_ProfileSetup.png
│   ├── 05_Home.png
│   ├── 06_ActivitySelect.png
│   ├── 07_ActiveTracking.png
│   ├── 08_ActivitySummary.png
│   ├── 09_History.png
│   ├── 10_Statistics.png
│   ├── 11_Goals.png
│   ├── 12_Achievements.png
│   ├── 13_Insights.png
│   ├── 14_Explore.png
│   ├── 15_Profile.png
│   ├── 16_Settings.png
│   ├── 17_HealthSync.png
│   ├── 18_Notifications.png
│   ├── 19_Help.png
│   └── 20_EmptyState.png
│
└── design-system.md          # Visual architecture & design specifications
```

---

## 2. Color System & Styling Tokens

MOTIONIQ uses a high-contrast, kinetic dark theme tailored for outdoor visibility, low OLED battery consumption, and visual impact.

| Token | Hex Code | Role / Usage |
| :--- | :--- | :--- |
| **Brand Navy** | `#011032` | Master brand canvas, splash background, launcher icon base |
| **Primary Kinetic Green** | `#00E676` | Primary action buttons, active workout ring, high-energy accents |
| **Teal Energy** | `#00897B` | Secondary branding, active progress tracks, subtle indicators |
| **Electric Blue / Cyan** | `#00B0FF` | GPS routes, cadence, distance metrics, secondary stat chips |
| **Deep Indigo** | `#5C6BC0` | Supporting indicators, background cards, inactive progress tracks |
| **Pulse Orange** | `#FF7043` | Active calorie burn, heart rate, alerts, milestone highlights |
| **Amber Warning** | `#FFB300` | Battery optimization notes, medium confidence warnings |
| **Surface Dark (Level 1)**| `#0A1931` | Cards, bottom navigation bar, floating dialogs |
| **Surface Dark (Level 2)**| `#112240` | Nested metric containers, chip backgrounds, search bars |
| **Text High-Emphasis** | `#FFFFFF` | Hero numbers, primary headings, active labels |
| **Text Medium-Emphasis** | `#CBD5E1` | Body text, secondary metrics, subtitles |
| **Text Low-Emphasis** | `#64748B` | Timestamp details, units, disabled states |

---

## 3. Typography Scale

Built on Material Design 3 type scales using modern geometric sans-serif typography.

| Style | Size | Weight | Tracking | Usage |
| :--- | :--- | :--- | :--- | :--- |
| **Hero Metric** | `48sp` – `56sp` | Black (900) | `-1.5sp` | Main step counter, workout distance digits |
| **Headline Large** | `32sp` | ExtraBold (800) | `-0.5sp` | Welcome splash, summary complete headers |
| **Title Large** | `22sp` | Bold (700) | `+0.5sp` | Screen titles, card titles, sheet headers |
| **Title Medium** | `18sp` | SemiBold (600) | `+0.2sp` | Section headers, activity selection titles |
| **Body Large** | `16sp` | Normal (400) | `+0.15sp` | Primary descriptions, onboarding copy |
| **Body Medium** | `14sp` | Normal (400) | `+0.25sp` | Metric explanations, list secondary texts |
| **Label Small** | `11sp` | Bold (700) | `+1.0sp` | ALL-CAPS metric units (`STEPS`, `KM`, `KCAL`, `PACE`) |

---

## 4. Spacing & Elevation Tokens

| Dimension | Token | Applied Components |
| :--- | :--- | :--- |
| **4dp** | `space-xxs` | Icon-to-text gaps, badge padding |
| **8dp** | `space-xs` | Filter chip padding, metric card inner spacing |
| **12dp** | `space-s` | List item spacing, inner card padding |
| **16dp** | `space-m` | Screen horizontal margins, card padding |
| **20dp** | `space-l` | Top bar padding, section dividers |
| **24dp** | `space-xl` | Screen edge padding, bottom sheet top padding |
| **32dp** | `space-xxl`| Hero metric vertical margins |

### Corner Radii
- **Cards & Containers**: `16dp` – `20dp` (rounded organic rectangles)
- **Buttons & Action Bars**: `12dp` – `16dp`
- **Chips & Pills**: `50%` (fully circular pills)
- **Bottom Sheets**: `24dp` (top-left and top-right corners)

---

## 5. Screen Inventory & UX Architecture

| # | Screen File | Key UI Elements | Primary Interaction |
| :-: | :--- | :--- | :--- |
| **01** | `01_Splash.png` | MOTIONIQ logo, kinetic pulse wave, dark navy canvas | Auto-advances to Onboarding or Home |
| **02** | `02_Onboarding.png` | Value proposition carousel, animated step preview | `GET STARTED` CTA button |
| **03** | `03_Permissions.png` | Contextual permission cards (Motion, GPS, Notifications) | `ALLOW ALL` & individual toggle flow |
| **04** | `04_ProfileSetup.png` | Biometrics setup: height, weight, daily step goal | Slider / number inputs + `COMPLETE` |
| **05** | `05_Home.png` | Circular goal ring, live step count, quick start action | Quick Start, Explore, Today's list |
| **06** | `06_ActivitySelect.png` | Visual cards: Walking, Running, Cycling, Sports | Tap activity card to initialize workout |
| **07** | `07_ActiveTracking.png` | Real-time GPS canvas, HUD metrics (Pace, Dist, Time) | `PAUSE`, `RESUME`, `FINISH` controls |
| **08** | `08_ActivitySummary.png` | Post-workout celebration, route polyline preview, splits | `SAVE WORKOUT` or `DISCARD` |
| **09** | `09_History.png` | Horizontal calendar picker, daily summary card, activity list | Date selection, activity detail inspection |
| **10** | `10_Statistics.png` | Daily, Weekly, Monthly bar charts, goal completion rate | Segmented control (Day / Week / Month) |
| **11** | `11_Goals.png` | Daily step, distance, and active minute goal sliders | Save target changes |
| **12** | `12_Achievements.png` | Milestone badges (10K Club, Century Streak, Marathon) | Badge detail modal |
| **13** | `13_Insights.png` | Movement pattern analysis, cadence trends, peak hours | Informational charts & recommendations |
| **14** | `14_Explore.png` | Live GPS location proximity park list, filter chips (<1km, <3km) | `START WALKING HERE` routing trigger |
| **15** | `15_Profile.png` | User credentials, hardware sensor diagnostics, reset data | Edit profile details |
| **16** | `16_Settings.png` | Units system (km/mi), battery optimization, privacy toggles | System preference switches |
| **17** | `17_HealthSync.png` | Health Connect bridge status, duplicate prevention, sync logs | Sync enable toggle, permissions settings |
| **18** | `18_Notifications.png` | Daily recap alerts, idle reminders, workout alerts | Alert preference toggles |
| **19** | `19_Help.png` | Sensor FAQ, battery whitelist guide, step count accuracy tips | Expandable FAQ accordions |
| **20** | `20_EmptyState.png` | Clean illustration for zero-workout states with direct CTA | `START YOUR FIRST WALK` button |

---

## 6. Iconography & Asset Guidelines

1. **Brand Logo (`design/logo/motioniq-logo.png`)**: Master asset with navy background `#011032`, dynamic runner silhouette, kinetic pulse waveform, and top-right destination pin.
2. **App Launcher Icon (`design/logo/motioniq-icon.png`)**: Centered at optical centroid $(625, 630)$ on 108dp canvas with safe-zone scaling to ensure 100% visibility on all OEM launchers (Pixel circle, Samsung squircle, OnePlus teardrop).
3. **Screen References (`design/screens/`)**: High-fidelity pixel references representing target UI implementations across all 20 screens.
