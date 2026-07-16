# QuizFlow — Product Requirements & Implementation Plan (PRD)

> **Assignment:** R0 – MCQ Quiz (DailyRounds take-home). Source spec: `~/Downloads/R0-Assignment.pdf`.
> **Status:** In progress — Phase 0 (scaffolding) complete, including the theming system below. See `docs/work/PRD.md` §14 for what shipped vs. plan.
> **Owner:** meghdut.mandal@autodesk.com · **Doubts contact (assignment):** samrat@dailyrounds.org

---

## 1. Summary

Build an Android app that loads **10 multiple-choice questions** from a JSON gist, runs a single-player quiz flow with answer reveal + streak tracking, and ends on a results screen with a restart option.

On top of the base assignment, this project **must** demonstrate:

- **MVVM** presentation pattern (Compose UI ↔ `ViewModel` ↔ immutable UI state).
- **Repository pattern** abstracting the data source behind a domain interface.
- **Clean Architecture** — strict `presentation → domain ← data` dependency direction, domain layer pure Kotlin (no Android/framework types).
- **Feature slicing** — code organized by feature first, then by layer.
- **Full automated test coverage** — every use case, repository, mapper, ViewModel, and UI screen/component has tests; coverage is measured and reported.

The provided mockups (dark theme, streak flames, progress bar, green/red reveal, results card) are **reference only**. We are free to reimagine the UX as long as the functional requirements hold.

---

## 2. Functional requirements (from the spec)

| # | Requirement | Acceptance criteria |
|---|---|---|
| F1 | **Launch & load** | On launch, fetch + parse JSON into `List<Question>`. Show a splash + loading indicator while data is prepared. Handle load failure with a retry. |
| F2 | **Question screen** | Show current question text + exactly 4 options, and a "Question X of 10" progress indicator. |
| F3 | **Answer reveal** | Tapping an option reveals both the correct answer and the user's selection (e.g. green = correct, red = wrong selection). Options become non-interactive after selection. |
| F4 | **Auto-advance** | 1 second after an answer is revealed (matching the per-question progress-bar fill), advance to the next question automatically. |
| F5 | **Skip** | Tapping "Skip" advances immediately to the next question (no reveal). |
| F6 | **Streak tracking** | Track consecutive correct answers. At a streak of **3**, a streak badge "lights up" with an engaging micro-interaction. Any **wrong** answer resets the current streak to 0. |
| F7 | **End of quiz** | After the 10th question, transition to a Results screen. |
| F8 | **Results** | Show **Correct/Total**, **longest streak achieved**, and (optional) **skipped count**. |
| F9 | **Restart** | "Restart Quiz" resets all counters and returns to Question 1. |

### Non-functional requirements (from the spec)

- **NF1** Animations & gestures (e.g. swipe to navigate/skip a question).
- **NF2** Clear separation of **UI / state / data** layers.
- **NF3** Consistent design system (colors, typography, spacing).
- **NF4** Material 3 components + accessibility best practices (TalkBack, touch targets, contrast, semantics).
- **NF5** Deliverables: git repo, fully running Android Studio project, `README.md` documenting the implementation.

### Added engineering bar (this project's own requirements)

- **E1** MVVM + Repository + Clean Architecture + feature slicing (see §5).
- **E2** Full test coverage of domain, data, presentation, and UI (see §8).
- **E3** Enable R8/minification for release (base template currently disables it — see `app/build.gradle.kts` `optimization { enable = false }`; flagged by the `r8-analyzer` skill).

---

## 3. Assumptions & open questions

These must be confirmed before/while implementing. Each is coded so it can be resolved and struck through.

- **Q1 — JSON URL. ✅ RESOLVED.** Source is the raw gist:
  `https://gist.githubusercontent.com/dr-samrat/53846277a8fcb034e482906ccc0d12b2/raw`
  Confirmed: a JSON **array of exactly 10** question objects. The data layer still ships a bundled `app/src/main/assets/questions.json` copy as an offline fallback + stable test fixture.
- **Q2 — JSON schema. ✅ RESOLVED (verified against the real gist).** Each question:
  ```json
  { "id": 1,
    "question": "What hidden feature do recent Android versions reveal ...?",
    "options": ["Flappy Bird–style game", "Virtual pet", "Hidden performance menu", "System UI tuner"],
    "correctOptionIndex": 0 }
  ```
  - `correctOptionIndex` is a **0-based index** into `options` (not answer text). Confirmed values across the set include 0 and 1.
  - Every question has **exactly 4** options; text may contain unicode (e.g. `–`, `’`). Ensure UTF-8 decoding.
  - The mapper (§6.2) maps `correctOptionIndex → Question.correctIndex` directly, and still **validates** (exactly 4 options; `0 ≤ correctOptionIndex ≤ 3`) so malformed future data fails with a typed error rather than a crash.
- **Q3 — Does "Skip" break the streak?** Spec only says *wrong* answers reset streak. A skip is not a correct answer, so it interrupts "consecutive correct". **Decision (documented):** Skip does **not** count as correct or wrong, increments `skipped`, and **resets current streak to 0** (streak = consecutive *correct*). Easily toggled if the reviewer expects otherwise.
- **Q4 — Does "Total" in Correct/Total include skipped?** **Decision:** Total = 10 (all questions). Correct/Total = correct out of 10. Skipped shown separately.
- **Q5 — Auto-advance on the 10th question** goes to Results (not a non-existent Q11). Reveal still shows for the 1s reveal duration, then Results.
- **Q6 — Reveal duration** fixed at 1000 ms (constant, injected for tests so we can virtual-advance time; matches the per-question progress-bar fill).

---

## 4. Tech stack & key libraries

All versions live in `gradle/libs.versions.toml` (version catalog); resolved and verified via a real build (not just researched) — see §14 for the compatibility issues hit and fixed along the way.

| Concern | Choice | Notes |
|---|---|---|
| Language / UI | Kotlin 2.2.10 + Jetpack Compose + Material 3 Expressive | `compileSdk` bumped to **37** (from 36) — required by the alpha Compose BOM (§14). |
| Async | Coroutines 1.10.2 + Flow | `StateFlow` for UI state. |
| DI | **Hilt 2.59.2** | First Hilt line with real AGP 9.x support (2.57.x predates it). KSP 2.3.6 (AGP-9-built-in-Kotlin compatible). |
| Networking | **Retrofit 3.0.0 + OkHttp 5.1.0** (logging interceptor) | Single GET against the raw gist URL; behind a data-source interface so tests never hit network. |
| Serialization | **kotlinx.serialization 1.9.0** (JSON) | + Retrofit converter; serialization Gradle plugin. |
| Navigation | **Jetpack Navigation 3** (`navigation3-runtime`/`-ui` 1.1.4) | Confirmed **stable** since 1.0.0 (Nov 2025) — no longer a "stretch," this is the primary plan (§11 updated). |
| Lifecycle | `lifecycle-*` 2.10.0 (`viewmodel-compose`, `runtime-compose`, `viewmodel-navigation3`) | `collectAsStateWithLifecycle`. |
| Splash | `androidx.core:core-splashscreen` 1.2.0 | System splash + `Theme.QuizFlow.Starting` (extends `Theme.SplashScreen`) for pre-API-31 compat. |
| Theming | Material3 Expressive (`MaterialExpressiveTheme` + `MotionScheme.expressive()`), dynamic (wallpaper) color on API 31+, persisted Light/Dark/System preference | New cross-cutting capability — see §13. |
| Build | KSP 2.3.6, Hilt 2.59.2, R8 enabled for release, `jvmTarget`/`compileOptions` = **17** | jvmTarget bumped from 11 (§14). |

**Testing libraries:** JUnit4, `kotlinx-coroutines-test`, **Turbine** (Flow assertions), **MockK** (only where a fake is impractical — fakes preferred), **Truth** (assertions), **Robolectric** (JVM-run Compose/UI tests), `androidx.compose.ui:ui-test-junit4`, `hilt-android-testing`, **Compose Preview Screenshot Testing** tool (screenshot tests), **Jacoco** (coverage).

---

## 5. Architecture

### 5.1 Layering (Clean Architecture)

```
presentation  ──depends-on──▶  domain  ◀──depends-on──  data
   (Compose, ViewModel,          (pure Kotlin:            (DTOs, Retrofit,
    UI state)                     models, use cases,       mappers, repo impls)
                                  repo interfaces)
```

- **domain** has **zero** Android/framework/library-UI dependencies (no `Context`, no Compose, no Retrofit). Pure Kotlin + coroutines only. This is what makes streak/scoring logic trivially unit-testable.
- **data** implements domain repository interfaces; owns DTOs, API, mappers.
- **presentation** depends only on domain (use cases + models), never on `data`.
- Wiring happens in DI modules (`core/di`), which is the only place that knows all three layers.

### 5.2 Feature-sliced package structure (single `:app` module)

Package-by-feature, then by layer. This maps 1:1 onto Gradle modules later if desired (see §11).

```
com.shanu.quizflow
├── QuizFlowApplication.kt              @HiltAndroidApp
├── MainActivity.kt                     @AndroidEntryPoint, edge-to-edge, hosts NavGraph
├── core/                               shared, cross-feature
│   ├── di/                             AppModule, NetworkModule, DispatchersModule
│   ├── network/                        Retrofit/OkHttp providers, ApiResult
│   ├── result/                         DataResult<T> (Success/Error), AppError types
│   ├── coroutines/                     DispatcherProvider (injectable dispatchers)
│   └── ui/
│       ├── theme/                      Theme.kt, Color.kt, Type.kt, Dimens.kt (design tokens)
│       └── components/                 shared composables (buttons, progress, badge)
└── feature/
    └── quiz/
        ├── data/
        │   ├── remote/                 QuizApi (Retrofit), QuizRemoteDataSource(+Impl)
        │   ├── remote/dto/             QuestionDto
        │   ├── local/                  AssetQuestionDataSource (fallback, dev/tests)
        │   ├── mapper/                 QuestionMapper (Dto → domain)
        │   └── repository/             QuizRepositoryImpl
        ├── domain/
        │   ├── model/                  Question, QuizSession, QuizResult, AnswerOutcome
        │   ├── repository/             QuizRepository (interface)
        │   └── usecase/                GetQuestionsUseCase, AnswerQuestionUseCase,
        │                               SkipQuestionUseCase, AdvanceQuizUseCase,
        │                               RestartQuizUseCase (or QuizSession pure ops)
        └── presentation/
            ├── navigation/             QuizNavGraph, Route (type-safe)
            ├── loading/                LoadingScreen
            ├── quiz/                   QuizViewModel, QuizUiState, QuizScreen, components
            └── results/               ResultsScreen (stateless; reads final QuizResult)
```

### 5.3 State ownership & navigation

- One `QuizViewModel` **shared across `Loading`/`Quiz`/`Results` via the enclosing Activity's `ViewModelStore`** owns the `QuizSession` (single source of truth). `QuizFlowHost`'s `NavDisplay` installs only `rememberSaveableStateHolderNavEntryDecorator()` — no `ViewModelStoreNavEntryDecorator` — so each route's `hiltViewModel()` call resolves to the same Activity-scoped instance rather than a per-back-stack-entry one. This is Activity scoping, not Navigation-3 graph scoping; see the KDoc on `QuizFlowHost` for the exact mechanism and the caveat against adding a per-entry `ViewModelStore` decorator later.
- Navigation states: `Loading → Quiz → Results`, with `Restart` resetting the session and navigating back to `Quiz` at index 0.
- Rationale over passing results as a nav arg: `Restart` needs to reset the same owning VM; Activity-scoping keeps one owner and avoids duplicating session state. (Alternative — serialize `QuizResult` as a type-safe nav argument to a fully stateless results screen — is documented as a viable variant.)

---

## 6. Detailed design

### 6.1 Domain model (pure Kotlin)

```kotlin
data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,          // exactly 4
    val correctIndex: Int,              // 0..3
)

enum class Selection { NONE, CORRECT, WRONG, SKIPPED }

data class AnswerRecord(val questionId: Int, val selectedIndex: Int?, val outcome: Selection)

data class QuizSession(
    val questions: List<Question>,
    val currentIndex: Int = 0,
    val correctCount: Int = 0,
    val skippedCount: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val records: List<AnswerRecord> = emptyList(),
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
    val isFinished: Boolean get() = currentIndex >= questions.size
    val total: Int get() = questions.size
}

data class QuizResult(
    val correct: Int, val total: Int, val skipped: Int, val longestStreak: Int,
)
```

**Pure transition functions** (the heart of the streak logic; live in domain, exhaustively unit-tested):

- `answer(session, selectedIndex): AnsweredResult` — computes correct/wrong; if correct → `correctCount++`, `currentStreak++`, `longestStreak = max(longestStreak, currentStreak)`; if wrong → `currentStreak = 0`. Records the answer. **Does not** advance the index (reveal happens first). Returns the correct index + outcome for UI highlighting.
- `skip(session): QuizSession` — `skippedCount++`, `currentStreak = 0` (see Q3), records a skip, advances index.
- `advance(session): QuizSession` — `currentIndex++` (used after the reveal duration elapses).
- `result(session): QuizResult` — final tallies.
- `restart(session): QuizSession` — fresh session with the same questions, all counters 0.

These are exposed either as small use-case classes (injectable, matches the "use case" testing target) or as pure functions on `QuizSession`. **Decision:** thin use-case classes wrapping pure logic, so DI + the "use case tests" requirement are both satisfied.

Streak badge rule: badge is "lit" whenever `currentStreak >= 3`.

### 6.2 Data layer

- `QuestionDto` (`@Serializable`) mirrors the gist JSON (per Q2):
  ```kotlin
  @Serializable
  data class QuestionDto(
      val id: Int,
      val question: String,
      val options: List<String>,
      val correctOptionIndex: Int,
  )
  ```
- `QuizApi` — `@GET` returning `List<QuestionDto>` from the gist raw URL (Q1).
- `QuizRemoteDataSource` interface + `QuizRemoteDataSourceImpl` (wraps `QuizApi`, maps exceptions to `AppError`).
- `AssetQuestionDataSource` — reads bundled `app/src/main/assets/questions.json` (fallback + a stable source for tests).
- `QuestionMapper` — `QuestionDto → Question`: `text = question`, `correctIndex = correctOptionIndex`; **validates** exactly 4 options and `0 ≤ correctOptionIndex ≤ 3`; throws a typed `MappingError` on malformed data.
- `QuizRepositoryImpl : QuizRepository` — orchestrates data source(s), returns `DataResult<List<Question>>` (`Success`/`Error`), on a background dispatcher from `DispatcherProvider`.

### 6.3 Presentation layer

```kotlin
sealed interface QuizUiState {
    data object Loading : QuizUiState
    data class Error(val message: String) : QuizUiState
    data class Question(
        val questionNumber: Int, val totalQuestions: Int,
        val text: String, val options: List<OptionUi>,
        val phase: Phase,                 // ANSWERING | REVEALING
        val currentStreak: Int, val streakActive: Boolean,  // >=3
    ) : QuizUiState
    data class Finished(val result: QuizResult) : QuizUiState
}

data class OptionUi(val text: String, val state: OptionState)  // DEFAULT|CORRECT|WRONG|DIMMED
enum class Phase { ANSWERING, REVEALING }
```

`QuizViewModel`:
- Loads questions on init (`Loading` → `Question`/`Error`).
- `onOptionSelected(index)` → runs `AnswerQuestionUseCase`, emits `REVEALING` state with highlights, launches a **cancelable** `viewModelScope` job that `delay(revealDurationMs)` then advances (or emits `Finished` on the last question).
- `onSkip()` → cancels any pending reveal, runs `SkipQuestionUseCase`, advances (or finishes).
- `onRestart()` → `RestartQuizUseCase`, back to `Question` at index 0.
- `revealDurationMs` and `DispatcherProvider` are injected so tests can virtual-advance the clock.

### 6.4 UI (Compose)

- `LoadingScreen` — splash handoff + progress indicator; `Error` shows retry.
- `QuizScreen` — progress ("X of 10"), animated streak badge (flame/lightning that "ignites" at 3 with scale/pulse), question card, four option cards with animated color reveal, "Skip" button. **Swipe-left gesture** = skip (NF1). Content-transition animation between questions.
- `ResultsScreen` — Correct/Total, longest streak, skipped, "Restart Quiz". Celebratory animation on high scores.
- **Design tokens** centralized in `core/ui/theme` (`Dimens`, color roles, typography) for NF3.
- **Accessibility** (NF4): semantic `contentDescription`s, `role = Button`, ≥48dp touch targets, live-region announcement of correct/wrong on reveal, sufficient contrast for green/red states (pair color with icon/text, not color alone).

---

## 7. Error handling & edge cases

- Network/parse failure → `Error` state with retry button; never crash on malformed JSON (mapper throws typed error, repo converts to `DataResult.Error`).
- Empty or ≠10 questions → surface an error (assignment assumes exactly 10; validate and fail gracefully with a message).
- Rapid taps during reveal → options non-interactive in `REVEALING`; skip cancels pending auto-advance.
- Process death / config change → `QuizSession` restored via `SavedStateHandle` (stretch; at minimum, ViewModel survives config changes).
- Double navigation to Results guarded by state (`Finished` is terminal until restart).

---

## 8. Testing strategy (E2 — full coverage)

Guided by the installed `testing-setup` skill. **Fakes-first**; mock only when a fake is impractical.

### 8.1 Domain (JVM unit tests — `src/test`)
- **Use cases / session logic** (highest priority, near-100%):
  - `answer`: correct increments count+streak+longest; wrong resets streak; longest streak preserved across resets.
  - Streak badge threshold: lit at exactly 3, stays lit at 4+, off after a wrong answer.
  - `skip`: increments skipped, resets streak, advances.
  - `advance`, `isFinished` at index 10, `result` tallies, `restart` zeroes everything.
  - Property-style tables covering answer sequences (e.g. C,C,C,W,C,C,C,C → longest=4? verify) .

### 8.2 Data (JVM unit tests)
- `QuestionMapper`: answer-as-text, answer-as-index (0- and 1-based), wrong option count, unresolvable answer → typed error.
- DTO deserialization with kotlinx.serialization (valid + malformed JSON fixtures).
- `QuizRepositoryImpl` with a **fake** remote data source: success, empty, error propagation, dispatcher usage.

### 8.3 Presentation (JVM unit tests — coroutines-test + Turbine)
- `QuizViewModel`: `Loading→Question` on success; `Loading→Error` on failure + retry recovers.
- Option select → `REVEALING` with correct highlights; after virtual-advancing the reveal duration → next `Question`; last question → `Finished`.
- Skip cancels pending auto-advance and advances immediately.
- Streak flag flips to active at 3 and off after a wrong answer.
- `Finished.result` matches expected tallies; `Restart` returns to first question with zeroed state.

### 8.4 UI (Compose tests via Robolectric — `src/test`; instrumented only where device needed)
- `QuizScreen`: renders text + 4 options + "X of 10"; tapping an option shows correct/wrong colors and disables further taps; Skip button advances; streak badge appears at streak 3; swipe-left skips.
- `ResultsScreen`: shows correct/total, longest streak, skipped; Restart click invokes callback.
- `LoadingScreen`/`Error`: progress shown; retry click invokes callback.
- Use semantic matchers first; `testTag` only when matchers get unwieldy. Verify state restoration.

### 8.5 Screenshot tests (Compose Preview Screenshot Testing tool)
- QuestionScreen states: unanswered, correct-revealed, wrong-revealed, streak-badge-lit.
- ResultsScreen: typical + perfect score.
- Component-level: OptionCard states, StreakBadge on/off. Reviewer records reference images (do not auto-approve diffs).

### 8.6 Coverage
- **Jacoco** report task; exclude generated Hilt/Compose classes, DI modules boilerplate, `MainActivity`, and pure-config files. Target: high coverage on domain (≈100%), data, and ViewModels; meaningful UI behavior coverage. Document the command in the README.

---

## 9. Build & tooling changes

- Add version-catalog entries + the KSP and kotlinx-serialization Gradle plugins.
- `app/build.gradle.kts`: enable Compose + Hilt + KSP; **enable R8** for release (`optimization { enable = true }` / minify + shrink) — addresses E3 and the `r8-analyzer` finding. Keep rules live in `app/src/main/keepRules/*.keep` (AGP 9.3 auto-combines them; no `proguardFiles` wiring).
- `compileSdk` bumped **36 → 37** and `jvmTarget`/`compileOptions` bumped **11 → 17** — both required once Material3 Expressive pulled in the alpha Compose BOM (see §13/§14).
- Screenshot testing plugin + Jacoco plugin wiring.
- Bundle `app/src/main/assets/questions.json` (fallback + test fixture).
- CI: `.github/workflows/ci.yml` — PR checks (unit tests + lint + debug assemble) and a release-gate job (R8-enabled `assembleRelease`) on push to `master` (the repo's actual default branch — an initial `main` assumption was wrong and silently meant the workflow never triggered; fixed).

---

## 10. Milestones / implementation plan

Each phase is independently reviewable and ships with its own tests (test-alongside, not test-after).

- **Phase 0 — Scaffolding. ✅ DONE.** Version catalog + plugins (Hilt, KSP, serialization, Retrofit, Navigation 3, splash, DataStore, test libs, Jacoco, screenshot). `QuizFlowApplication`, DI modules, `DispatcherProvider`, `DataResult`/`AppError`, retheme to `core/ui/theme` + `Dimens`, package skeleton, enable R8, bundle `questions.json`, CI pipeline. Plus the **theming capability** (§13), added mid-phase at explicit request: Material3 Expressive, dynamic color, persisted Light/Dark/System toggle (`core/settings/{domain,data,presentation}`), with full tests. *Deliverable:* `./gradlew assembleDebug`, `assembleRelease` (R8 on), and `testDebugUnitTest` all green — verified, not just planned.
- **Phase 1 — Domain.** Models + use cases/session logic. *Tests:* §8.1 (complete before moving on). *Deliverable:* streak/scoring fully specified and proven by tests.
- **Phase 2 — Data.** DTO, mapper, API, remote + asset data sources, repository impl. *Tests:* §8.2. *Deliverable:* questions load from JSON (asset fallback until Q1 resolved).
- **Phase 3 — Presentation logic.** `QuizUiState`, `QuizViewModel`, injected reveal duration + dispatchers. *Tests:* §8.3. *Deliverable:* full flow provable headlessly (load → answer → reveal → advance → finish → restart).
- **Phase 4 — UI & navigation.** Loading/Quiz/Results screens, components, type-safe nav graph, animations, swipe gesture, splash. *Tests:* §8.4 + §8.5. *Deliverable:* end-to-end runnable app.
- **Phase 5 — Polish & docs.** Accessibility pass, error/retry, edge cases, Jacoco report, `README.md`, verify against acceptance matrix (§2). Resolve open questions (§3).

---

## 11. Risks & stretch goals

- **Navigation 3** is confirmed stable (1.0.0 since Nov 2025) — no longer a risk; it's the primary navigation choice (not a stretch). Adaptive multi-pane layouts via `adaptive-navigation3` remain an explicit non-goal/stretch for this linear 3-screen quiz.
- **Bleeding-edge dependency stack.** AGP 9.3 + alpha Compose BOM + Material3 Expressive surfaced several real compatibility issues during Phase 0 (documented in §14) — expect more of these in later phases when adding Navigation 3 and screenshot-testing code; verify with a real build early and often rather than trusting research alone.
- **Screenshot testing setup** can be fiddly across environments — budget time; follow the `testing-setup` skill's referenced setup doc strictly. The plugin is alpha (`0.0.1-alpha15`).
- **JSON schema uncertainty** (Q1/Q2) — ✅ resolved; see §3.
- **Modularization** (`:core`, `:feature:quiz` Gradle modules) — the package structure (§5.2) already isolates layers so this is a mechanical follow-up if the reviewer wants multi-module.
- **Persistence** of best score across launches (DataStore) — out of scope but noted; the theming feature (§13) already establishes the DataStore + Hilt wiring pattern this would reuse.

---

## 12. Acceptance / Definition of Done

- [x] All of F1–F9, NF1–NF5 satisfied and demonstrable in the running app.
- [x] Clean Architecture boundaries hold (domain has no Android deps); MVVM + repository + feature slicing in place (E1).
- [x] Every use case, repository, mapper, ViewModel, and UI screen/component has tests; Jacoco report generated (E2). ~71% instruction coverage overall; domain/use-case/mapper layers 96–100%.
- [x] R8 enabled for release; release build succeeds (E3). Verified via `./gradlew assembleRelease`.
- [x] Open questions §3 resolved (esp. real JSON URL/schema).
- [x] `README.md` documents architecture, how to build/run, how to run each test suite + coverage, and design decisions/assumptions.
- [x] Runs on min SDK 29 → target 36 (compileSdk 37); edge-to-edge verified (per `edge-to-edge` skill checklist).
- [x] Theme toggle (§13) works correctly and persists across restarts; verified against real Nav3 screens (wired into `QuizFlowTopBar`, used by Loading/Quiz/Results).
- [ ] CI green on PR (lint + unit tests + debug build) and on `master` (release gate). Not independently re-verified this session after the redesign changes — check the latest Actions run before merging.

---

## 13. Theming: Material3 Expressive, dynamic color, Light/Dark/System

Added mid-Phase-0 at explicit request, on top of the base assignment's design freedom ("reimagine the solution"). Treated as a genuine cross-cutting capability, not a quiz-feature concern — lives in `core/settings/`, not `feature/quiz/`.

### 13.1 Requirements
- Use **dynamic (wallpaper-derived) color** on Android 12+ (`dynamicLightColorScheme`/`dynamicDarkColorScheme`), falling back to the explicit "QuizFlow Expressive" (light) / "Earth & Ether" (dark) color roles below API 31 or when the user disables dynamic color.
- Use **Material3 Expressive** (`MaterialExpressiveTheme`, `MotionScheme.expressive()`) instead of the classic `MaterialTheme`.
- Give the user a way to force Light, force Dark, or follow System — **persisted** across app restarts.
- Surfaced as a single cycling icon button (sun/moon/auto) in a top bar — user-chosen UI pattern over a dropdown or a dedicated settings sheet.

### 13.2 Design
- **Domain** (`core/settings/domain/`): `ThemeMode` enum (`LIGHT`, `DARK`, `SYSTEM`) with a pure `next()` cycling function; `ThemePreferenceRepository` interface (`Flow<ThemeMode>` + `suspend setThemeMode`); `ObserveThemeModeUseCase`/`SetThemeModeUseCase` — ViewModels never inject the repository directly, only use cases (standing convention going forward for every feature, not just this one).
- **Data** (`core/settings/data/`): `ThemePreferenceRepositoryImpl` backed by `androidx.datastore:datastore-preferences`; a single `stringPreferencesKey`, falls back to `SYSTEM` on a missing/corrupt value.
- **DI** (`core/di/`): `DataStoreModule` (provides the singleton `DataStore<Preferences>` via `preferencesDataStore` delegate), `SettingsModule` (`@Binds` the repository).
- **Presentation** (`core/settings/presentation/ThemeViewModel.kt`): exposes `themeMode: StateFlow<ThemeMode>` and `onToggleTheme()`. **Uses `SharingStarted.Eagerly`, not `WhileSubscribed`** — `onToggleTheme` reads `themeMode.value` synchronously, which is only correct if the upstream preference has already been collected at least once; `WhileSubscribed` caused two real test failures (toggle computed off the seed default instead of the persisted value) before this fix. See §14.
- **UI** (`core/ui/components/ThemeToggleButton.kt`): stateless; icon + content description reflect current mode and *what tapping will do next* (accessibility — screen readers announce the resulting action, not just current state).
- `core/ui/theme/Theme.kt` (`QuizFlowTheme`) takes a `themeMode: ThemeMode` param and resolves dark/light using it (`SYSTEM` falls back to `isSystemInDarkTheme()`).
- Wired into `MainActivity`'s placeholder screen for now (top bar + toggle button) — Phase 4 moves the same `ThemeToggleButton` into the real `QuizScreen`/`ResultsScreen` top bars.

### 13.3 Tests (full coverage, per E2)
`ThemeModeTest` (cycling), `FakeThemePreferenceRepository` + `ObserveThemeModeUseCaseTest`/`SetThemeModeUseCaseTest`, `ThemePreferenceRepositoryImplTest` (real temp-file-backed DataStore — default/persist/corrupt-value fallback), `ThemeViewModelTest` (all three cycle transitions + persistence call count). 16 tests, all passing.

---

## 14. Build & dependency compatibility learnings (Phase 0)

This bleeding-edge stack (AGP 9.3, alpha Compose BOM, Kotlin 2.2.10) surfaced several real, non-obvious compatibility issues while getting `assembleDebug`/`assembleRelease`/`testDebugUnitTest` green. Recorded here so later phases don't re-discover them:

1. **`agp = "9.3.0"`, not 9.2.1.** The catalog was already on 9.3.0 (the original PRD draft assumed 9.2.1 from research); this is why the AGP-9.3-only `optimization { enable = ... }` DSL compiles.
2. **Don't apply `org.jetbrains.kotlin.android` explicitly.** AGP 9 has **built-in Kotlin** — applying the base Kotlin Android plugin alongside `kotlin-compose` throws `Cannot add extension with name 'kotlin'`. Only `kotlin-compose` (the Compose compiler plugin) is needed; it works standalone under built-in Kotlin.
3. **Hilt must be ≥2.59.2**, not 2.57.2 as originally researched — 2.57.x predates real AGP 9 support (`Android BaseExtension not found`); 2.59.2 is the first line that works, with the 2.59.0 `ComponentTreeDeps` regression fixed.
4. **KSP must be ≥2.3.6 as a plain version string**, not the `<kotlin>-<impl>` pinned form (`2.2.10-2.0.2`). KSP versioning decoupled from Kotlin's version starting at 2.3.0; older KSP builds register generated sources via the legacy `kotlin.sourceSets` DSL, which AGP 9's built-in Kotlin rejects (`Using kotlin.sourceSets DSL ... is not allowed`). Fixed via the installed `agp-9-upgrade` skill, not guesswork.
5. **Compose Preview Screenshot Testing plugin** needs **both** `android.experimental.enableScreenshotTest=true` in `gradle.properties` **and** `experimentalProperties["android.experimental.enableScreenshotTest"] = true` in the module's `android {}` block — the properties-file flag alone isn't sufficient.
6. **Material3 Expressive (`MaterialExpressiveTheme`, `MotionScheme`) isn't public in the stable Compose BOM `2026.02.01`** — those symbols are `internal` until material3 1.5.0-alpha. Fix: switch the `androidx.compose:compose-bom` coordinate to **`androidx.compose:compose-bom-alpha`**, pinned to the latest alpha (`2026.06.01` per Google's Maven metadata at the time) — same version-catalog `version.ref`, just a different artifact name.
7. **The alpha Compose BOM requires `compileSdk 37+`** (`checkDebugAarMetadata` fails otherwise) — bumped from 36. `targetSdk`/`minSdk` left untouched (compile-time-only requirement).
8. **`jvmTarget`/`compileOptions` bumped 11 → 17** — required once Hilt/KSP/AndroidX libraries at this vintage are class-file 61+; must set both `compileOptions.sourceCompatibility/targetCompatibility` and Kotlin's `compilerOptions.jvmTarget` to the same value or the Java/Kotlin compile tasks disagree.
9. **`hiltViewModel()` from `androidx.hilt.navigation.compose` is deprecated** in `hilt-navigation-compose` 1.3.0 in favor of a nav-independent `androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()` (new artifact `androidx.hilt:hilt-lifecycle-viewmodel-compose`) — use the new import going forward (already applied in `MainActivity`; carry into Phase 4's Nav3 screens).
10. **A real design bug caught by tests, not by inspection:** `ThemeViewModel.onToggleTheme()` reading `themeMode.value` with `SharingStarted.WhileSubscribed(5_000)` returns the seed default if nothing has subscribed yet — silently computes the wrong "next" mode. Two unit tests failed non-obviously (`Light to Dark` and `Dark to System`, but not `System to Light` — which coincidentally matched the seed). Root-caused by tracing the actual emitted values rather than assuming flakiness; fixed with `SharingStarted.Eagerly`.

**Practical takeaway for later phases:** research (including subagent research) gets version numbers approximately right but cannot substitute for actually running `./gradlew assembleDebug` against this specific bleeding-edge combination — several of the above (Hilt/KSP/BOM versions, the two required screenshot-plugin flags, the built-in-Kotlin DSL conflict) were only discovered by building and reading the real compiler/Gradle errors.

---

## 15. Notes on process

- **Version control:** repo is git-initialized (branch `devlopment`, pushed to origin). Any invasive git write (reset/force-push/branch delete) is still **proposed for approval first**; routine add/commit/push proceeds when explicitly requested.
- **Android skills:** `edge-to-edge`, `testing-setup`, `navigation-3`, `adaptive`, `r8-analyzer`, `android-intent-security`, `styles`, `agp-9-upgrade` are installed under `.claude/skills/` — consult the relevant one per phase (see `CLAUDE.md`). The `agp-9-upgrade` skill was added mid-Phase-0 specifically to resolve the built-in-Kotlin/KSP conflict (§14.4) rather than guessing at a fix.
- **CI:** `.github/workflows/ci.yml` triggers on `master` (the repo's real default branch — confirmed via `git remote -v` / `gh repo view` after `git init`). Dev work happens on `devlopment` (branch name as chosen), merged to `master` via PR.
- **Branching:** `devlopment` (typo in name is intentional/as-chosen) is the working branch; `master` tracks `origin/master` and is only advanced via PR merge.
