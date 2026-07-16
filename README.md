# Learn English Words

A vocabulary learning Android app built with Jetpack Compose and Material 3.

## Build and run

**Requirements:** Android Studio Ladybug or newer, JDK 17, Android SDK 36.

```bash
# Run unit tests
./gradlew testDebugUnitTest

# Run instrumented tests (requires connected device or emulator)
./gradlew connectedDebugAndroidTest

# Build debug APK
./gradlew assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

## Architecture

Single `app` module. One-way data flow: Compose UI → MVI Intent → ViewModel → Repository/domain service → Room/DataStore → Flow → ViewModel StateFlow → Compose UI.

### Key packages

| Package | Purpose |
|---|---|
| `core/model` | Immutable domain models (`WordEntry`, `QuizTask`, `QuizSettings`, …) |
| `core/time` | `TimeProvider` interface — injectable clock for deterministic tests |
| `core/util` | `Shuffler` interface — injectable shuffle for deterministic quiz tests |
| `data/catalog` | JSON DTOs, `AssetBundledCatalogSource`, `CatalogRepositoryImpl`, mapper |
| `data/local` | Room database, all DAOs, entities, and relations |
| `data/progress` | `ProgressRepositoryImpl` |
| `data/settings` | `SettingsRepositoryImpl` (Preferences DataStore) |
| `data/analytics` | `NoOpAnalyticsTracker` (Stage 1 stub) |
| `domain/catalog` | `BootstrapCatalog` — idempotent asset import, `CatalogRepository` interface |
| `domain/progress` | `ProgressAggregator` — pure denominator calculation, `ProgressRepository` interface |
| `domain/quiz` | `BuildQuizSession`, `EvaluateQuizAnswer` — pure domain services |
| `domain/settings` | `SettingsRepository` interface |
| `domain/analytics` | `AnalyticsTracker` interface |
| `feature/*` | MVI screens: startup, topics, subtopics, learn, quiz, progress, settings |
| `navigation` | `AppNavGraph`, route constants |
| `di` | Hilt modules |

### Stable identifiers

Words and subtopics are identified by slash-delimited composite keys derived from JSON positions:

```
subtopicUid = "topicKey/subtopicKey"          e.g. human/personal_data
wordUid     = "topicKey/subtopicKey/wordId"   e.g. human/personal_data/1
```

These IDs link content to progress rows and must remain stable across catalog updates.

### Progress calculation

The denominator is the number of *eligible tasks* (active word × direction pairs), not the number of `WordProgressEntity` rows. Words with no progress row count as unlearned. `ProgressAggregator` is a pure stateless object tested independently of Room.

### Quiz session

`BuildQuizSession` creates `QuizTask` objects from a word snapshot. `EvaluateQuizAnswer` implements the state machine (learned after first/second correct, streak-based recovery after two failures). Neither touches Room — the ViewModel calls `ProgressRepository.upsertProgress` after each evaluated answer.

### Room schema

`exportSchema = true`. Exported schema JSON lives in `app/schemas/`. Commit schema files alongside code; treat schema changes as migrations.

## Stage 2 (planned)

Stage 2 adds Firebase Remote Config–driven catalog updates and Firebase Analytics. The bundled asset catalog remains as an offline seed. Feature ViewModels already call `AnalyticsTracker` through the no-op implementation.
