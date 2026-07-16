# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.refreshing.learnenglishwords.ExampleUnitTest"

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Lint
./gradlew lint
```

## Architecture & Structure

This is an early-stage Android app built with **Jetpack Compose** and **Kotlin**.

- **Package**: `com.refreshing.learnenglishwords`
- **Min SDK**: 28, **Target SDK**: 36
- **Build system**: Gradle with version catalog (`gradle/libs.versions.toml`)
- **AGP**: 8.13.2, **Kotlin**: 2.0.21

### Key files
- `app/src/main/java/.../MainActivity.kt` — single entry point, sets up Compose content with `LearnEnglishWordsTheme`
- `app/src/main/java/.../ui/theme/` — Material3 theme (Color, Type, Theme)
- `app/src/main/AndroidManifest.xml` — app manifest

The app is currently a scaffold with a single `MainActivity`. UI is built entirely with Jetpack Compose (no XML layouts). Material3 is used for theming.
