# R1 Assignment Upgrade (PRDv2 Specification & Implementation)

This document provides a comprehensive technical overview of the **R1 Assignment Upgrade (PRDv2)** for **QuizFlow**, an Android application built with Jetpack Compose, Clean Architecture, MVVM, Room Database, and Navigation 3.

---

## 1. Executive Overview

The R1 Upgrade transforms QuizFlow from a simple single-quiz prototype into a production-grade, multi-module, offline-first quiz application. 

### Core Requirements Implemented:
1. **Multi-Module Start Screen & Progress Tracking:**
   - Displays all quiz subjects/modules with dynamic progress status (**Not Started**, **In Progress**, **Completed**).
   - Shows module completion percentage, high scores, and last attempted scores.
   - Calculates overall progress across all modules (completion %, total score, average score).
2. **Offline-First Persistence with Room:**
   - Local database (`QuizFlowDatabase`) caching subjects, questions, module progress, and active quiz session state.
   - Single source of truth model: app fetches remote data on sync, caches to Room, and observes Room reactive streams (`Flow`).
3. **Session Interruption & Quick Resume:**
   - Automatically saves active quiz state (current question index, correct count, skipped count, streak) to Room.
   - Animated **Quick Resume Bar** appears on the Module List screen when an active session is pending, allowing 1-tap resumption.
4. **Resilient Network & DTO Layer:**
   - Custom OkHttp `jsonSanitizerInterceptor` stripping malformed trailing commas from remote Gist HTTP responses (`security.json`).
   - Flexible DTO deserialization handling remote JSON schema variations (mixed String/Int options, flexible element parsing).
   - Room DB offline-first single source of truth for seamless offline operation.
5. **System Adaptability & Edge-to-Edge:**
   - Full support for 3-button system navigation bars and gesture navigation bars via `.navigationBarsPadding()`.
   - Responsive scrolling containers (`verticalScroll`, `LazyColumn`) preventing UI cut-off in landscape and small screen sizes.
6. **Robust Test Suite:**
   - **164 unit tests** covering Domain Use Cases, ViewModels, Repository implementations, Room DAOs, DTO serialization, and Compose UI components.

---

## 2. System Architecture

QuizFlow follows **Clean Architecture**, **MVVM**, and the **Repository Pattern**, feature-sliced inside the `:app` module.

```
com.shanu.quizflow
├── QuizFlowApplication.kt          @HiltAndroidApp
├── MainActivity.kt                 @AndroidEntryPoint; splash, edge-to-edge, Compose host
├── core/                           Cross-cutting infrastructure
│   ├── database/                   Room DB (QuizFlowDatabase)
│   ├── di/                         Hilt modules (DatabaseModule, NetworkModule, DispatchersModule)
│   ├── network/                    Retrofit providers, jsonSanitizerInterceptor & kotlinx.serialization JSON
│   ├── result/                     DataResult<T> (Success/Error) + Throwable.toAppError()
│   ├── coroutines/                 Injectable DispatcherProvider
│   ├── settings/                   Theme & dynamic color preference feature
│   └── ui/                         Design tokens (Dimens, Color, Type) and shared composables
└── feature/quiz/                   Quiz feature slice
    ├── data/                       DAOs, Entities, DTOs, Mappers, Repositories
    │   ├── di/                     QuizRepositoryModule (Hilt binding)
    │   ├── local/                  Room Entities & DAOs
    │   ├── mapper/                 QuestionMapper, SubjectMapper, MappingError
    │   ├── remote/                 QuizApi, QuizRemoteDataSource
    │   └── repository/             QuizRepositoryImpl, ModuleProgressRepositoryImpl
    ├── domain/                     Pure Kotlin Domain Layer
    │   ├── model/                  Question, Subject, ModuleProgress, QuizSession, QuizResult
    │   ├── repository/             QuizRepository, ModuleProgressRepository (Interfaces)
    │   └── usecase/                Single-responsibility Use Cases (11 total)
    └── presentation/               UI screens, ViewModels, Navigation 3
        ├── common/                 LoadingScreen, QuizSkeleton
        ├── modulelist/             ModuleListScreen, ModuleListViewModel, QuickResumeBar, ModuleCard
        ├── navigation/             Routes.kt, QuizFlowHost.kt (Navigation 3)
        ├── quiz/                   QuizScreen, QuizViewModel, OptionCard, QuestionProgressBar
        └── results/                ResultsScreen, ResultsViewModel
```

### Layer Dependency Rules:
- **`domain`** is pure Kotlin with zero dependencies on Android, Compose, or Retrofit.
- **`data`** implements `domain` repository interfaces, accessing Room DAOs and Retrofit services.
- **`presentation`** consumes `domain` Use Cases and exposes immutable `StateFlow` states to Compose screens. ViewModels never access Data layer classes directly.

---

## 3. Database Schema (Room)

The database is named `quizflow.db` (`version = 1`).

### Tables:
1. **`subjects`** (`SubjectEntity`)
   - `id` (PK, String), `title` (String), `description` (String), `questions_url` (String), `display_order` (Int).
2. **`questions`** (`QuestionEntity`)
   - `id` (PK, Int), `subject_id` (FK/Index, String), `question_text` (String), `option0`..`option3` (String), `correct_option_index` (Int).
3. **`module_progress`** (`ModuleProgressEntity`)
   - `subject_id` (PK, String), `is_completed` (Boolean), `last_score` (Int), `high_score` (Int), `longest_streak` (Int), `total_questions` (Int), `last_attempted_at` (Long).
4. **`quiz_session_state`** (`QuizSessionStateEntity`)
   - `subject_id` (PK, String), `current_index` (Int), `correct_count` (Int), `skipped_count` (Int), `current_streak` (Int), `longest_streak` (Int).

### Dedicated Single-Responsibility DAOs:
- `SubjectDao`: `observeAll()`, `getAll()`, `upsertAll()`.
- `QuestionDao`: `getBySubjectId()`, `upsertAll()`, `deleteBySubjectId()`.
- `ModuleProgressDao`: `getBySubjectId()`, `observeAll()`, `upsert()`.
- `QuizSessionStateDao`: `getSessionState()`, `observeAllSessionStates()`, `upsertSessionState()`, `deleteSessionState()`.

---

## 4. UI & Visual Polish

- **Edge-to-Edge System Bar Insets:** All bottom bars (`QuizScreen`, `ResultsScreen`, `QuickResumeBar`) apply `.navigationBarsPadding()`, ensuring full compatibility with 3-button system navigation bars and gesture bars alike.
- **Shadow Tolerance:** `QuickResumeBar` utilizes 32.dp vertical tolerance padding on its outer layer, preventing elevation shadow cropping during slide-up and fade animations.
- **Staggered Animations:** List items and question options reveal with staggered spring pop-in animations (`rememberStaggeredReveal`).
- **Material 3 Expressive Theming:** Supports Light / Dark / System themes and Android 12+ wallpaper dynamic color (`QuizFlowTheme`).

---

## 5. Verification & Testing

The application includes a comprehensive test suite of **164 unit tests**:
- **Domain Tests:** 11 Use Case test suites verifying single-responsibility business rules.
- **Data Tests:** `QuizRepositoryImplTest`, `QuizApiTest`, and `QuestionDtoSerializationTest` (9 JSON parsing edge cases).
- **ViewModel Tests:** `ModuleListViewModelTest`, `QuizViewModelTest`, `ResultsViewModelTest` with coroutines-test `StandardTestDispatcher`.
- **UI & Component Tests:** `QuizScreenTest`, `OptionCardTest`, `QuestionProgressBarTest`, `StreakBadgeTest`, `SkipButtonTest`, `QuizFlowTopBarTest`, `ThemeToggleButtonTest`.

All tests pass 100% cleanly:
```bash
./gradlew testDebugUnitTest
```
