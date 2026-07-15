# QuizFlow — Product Requirements & Implementation Plan (PRD)

> **Assignment:** R0 – MCQ Quiz (DailyRounds take-home). Source spec: `~/Downloads/R0-Assignment.pdf`.
> **Status:** Draft v1 — planning. No feature code written yet (base project is the default Android Studio Compose template).
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
| F4 | **Auto-advance** | 2 seconds after an answer is revealed, advance to the next question automatically. |
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

- **Q1 — JSON URL.** The spec links a gist ("Question Json List") but the URL was not captured in the PDF text. **Action:** obtain the raw gist URL. Until then, the data layer targets a configurable base URL and ships a bundled `assets/questions.json` fallback for local dev + tests.
- **Q2 — JSON schema.** Assumed shape per question (to be verified against the real gist):
  ```json
  { "id": 1, "question": "What is the capital of France?",
    "options": ["Berlin", "Paris", "Madrid", "Rome"], "answer": "Paris" }
  ```
  The mapper (§6.2) will be defensive: accept `answer` as either the **option text** or a **0-based/1-based index**, and validate exactly 4 options.
- **Q3 — Does "Skip" break the streak?** Spec only says *wrong* answers reset streak. A skip is not a correct answer, so it interrupts "consecutive correct". **Decision (documented):** Skip does **not** count as correct or wrong, increments `skipped`, and **resets current streak to 0** (streak = consecutive *correct*). Easily toggled if the reviewer expects otherwise.
- **Q4 — Does "Total" in Correct/Total include skipped?** **Decision:** Total = 10 (all questions). Correct/Total = correct out of 10. Skipped shown separately.
- **Q5 — Auto-advance on the 10th question** goes to Results (not a non-existent Q11). Reveal still shows for 2s, then Results.
- **Q6 — Reveal duration** fixed at 2000 ms (constant, injected for tests so we can virtual-advance time).

---

## 4. Tech stack & key libraries

Add all versions to `gradle/libs.versions.toml` (version catalog). Use latest stable; verify with `android studio version-lookup` (from the installed `android-cli` skill) at implementation time.

| Concern | Choice | Notes |
|---|---|---|
| Language / UI | Kotlin + Jetpack Compose + Material 3 | Already in template. |
| Async | Coroutines + Flow | `StateFlow` for UI state. |
| DI | **Hilt** | Recommended by `testing-setup` skill for non-multiplatform; enables test doubles via `@TestInstallIn`. |
| Networking | **Retrofit + OkHttp** (logging interceptor) | Single GET; behind a data-source interface so tests never hit network. |
| Serialization | **kotlinx.serialization** (JSON) | + Retrofit converter; add serialization Gradle plugin. |
| Navigation | **Navigation Compose (type-safe, Nav2)** | Stable. `navigation-3` skill is installed but Nav3 is alpha; Nav3 noted as optional stretch (§11). |
| Lifecycle | `lifecycle-viewmodel-compose`, `lifecycle-runtime-compose` | `collectAsStateWithLifecycle`. |
| Splash | `androidx.core:core-splashscreen` | System splash + in-app loading state. |
| Build | KSP (Hilt), R8 enabled for release | See E3. |

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

- One `QuizViewModel` **scoped to the quiz nav graph** owns the `QuizSession` (single source of truth). Both `QuizScreen` and `ResultsScreen` observe it via `hiltViewModel()` scoped to the nav graph back stack entry, so session state (score, longest streak) survives the transition to results.
- Navigation states: `Loading → Quiz → Results`, with `Restart` resetting the session and navigating back to `Quiz` at index 0.
- Rationale over passing results as a nav arg: `Restart` needs to reset the same owning VM; graph-scoping keeps one owner and avoids duplicating session state. (Alternative — serialize `QuizResult` as a type-safe nav argument to a fully stateless results screen — is documented as a viable variant.)

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
- `advance(session): QuizSession` — `currentIndex++` (used after the 2s reveal).
- `result(session): QuizResult` — final tallies.
- `restart(session): QuizSession` — fresh session with the same questions, all counters 0.

These are exposed either as small use-case classes (injectable, matches the "use case" testing target) or as pure functions on `QuizSession`. **Decision:** thin use-case classes wrapping pure logic, so DI + the "use case tests" requirement are both satisfied.

Streak badge rule: badge is "lit" whenever `currentStreak >= 3`.

### 6.2 Data layer

- `QuestionDto` (`@Serializable`) mirrors the gist JSON (per Q2).
- `QuizApi` — `@GET` returning `List<QuestionDto>`.
- `QuizRemoteDataSource` interface + `QuizRemoteDataSourceImpl` (wraps `QuizApi`, maps exceptions to `AppError`).
- `AssetQuestionDataSource` — reads bundled `app/src/main/assets/questions.json` (fallback + a stable source for tests).
- `QuestionMapper` — `QuestionDto → Question`; resolves `answer` (text or index) to `correctIndex`; validates 4 options + a resolvable answer; throws a typed `MappingError` on malformed data.
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
- Option select → `REVEALING` with correct highlights; after virtual-advancing 2s → next `Question`; last question → `Finished`.
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
- `app/build.gradle.kts`: enable Compose + Hilt + KSP; **enable R8** for release (`optimization { enable = true }` / minify + shrink) — addresses E3 and the `r8-analyzer` finding. Add a minimal `proguard-rules.pro` (keep kotlinx-serialization + model classes as needed).
- Bump `compileSdk`/BOM only if a chosen API requires it (the `styles` skill needs SDK 37 / BOM 2026.04.01 — **not** used here, so no bump required).
- Screenshot testing plugin + Jacoco plugin wiring.
- Bundle `app/src/main/assets/questions.json` (fallback + test fixture).

---

## 10. Milestones / implementation plan

Each phase is independently reviewable and ships with its own tests (test-alongside, not test-after).

- **Phase 0 — Scaffolding.** Version catalog + plugins (Hilt, KSP, serialization, Retrofit, nav, splash, test libs, Jacoco). `QuizFlowApplication`, DI modules, `DispatcherProvider`, theme tokens, package skeleton, enable R8. *Deliverable:* builds green, DI graph resolves.
- **Phase 1 — Domain.** Models + use cases/session logic. *Tests:* §8.1 (complete before moving on). *Deliverable:* streak/scoring fully specified and proven by tests.
- **Phase 2 — Data.** DTO, mapper, API, remote + asset data sources, repository impl. *Tests:* §8.2. *Deliverable:* questions load from JSON (asset fallback until Q1 resolved).
- **Phase 3 — Presentation logic.** `QuizUiState`, `QuizViewModel`, injected reveal duration + dispatchers. *Tests:* §8.3. *Deliverable:* full flow provable headlessly (load → answer → reveal → advance → finish → restart).
- **Phase 4 — UI & navigation.** Loading/Quiz/Results screens, components, type-safe nav graph, animations, swipe gesture, splash. *Tests:* §8.4 + §8.5. *Deliverable:* end-to-end runnable app.
- **Phase 5 — Polish & docs.** Accessibility pass, error/retry, edge cases, Jacoco report, `README.md`, verify against acceptance matrix (§2). Resolve open questions (§3).

---

## 11. Risks & stretch goals

- **Nav3 (alpha).** `navigation-3` + `adaptive` skills favor Nav3 for multi-pane/adaptive. For a 3-screen linear quiz, stable Nav2 type-safe navigation is lower-risk. *Stretch:* migrate to Nav3 and add tablet/foldable adaptive layouts using the `adaptive` skill.
- **Screenshot testing setup** can be fiddly across environments — budget time; follow the `testing-setup` skill's referenced setup doc strictly.
- **JSON schema uncertainty** (Q1/Q2) — mitigated by defensive mapper + asset fallback; confirm early.
- **Modularization** (`:core`, `:feature:quiz` Gradle modules) — the package structure (§5.2) already isolates layers so this is a mechanical follow-up if the reviewer wants multi-module.
- **Persistence** of best score across launches (DataStore) — out of scope but noted.

---

## 12. Acceptance / Definition of Done

- [ ] All of F1–F9, NF1–NF5 satisfied and demonstrable in the running app.
- [ ] Clean Architecture boundaries hold (domain has no Android deps); MVVM + repository + feature slicing in place (E1).
- [ ] Every use case, repository, mapper, ViewModel, and UI screen/component has tests; Jacoco report generated (E2).
- [ ] R8 enabled for release; release build succeeds (E3).
- [ ] Open questions §3 resolved (esp. real JSON URL/schema).
- [ ] `README.md` documents architecture, how to build/run, how to run each test suite + coverage, and design decisions/assumptions.
- [ ] Runs on min SDK 29 → target 36; edge-to-edge verified (per `edge-to-edge` skill checklist).

---

## 13. Notes on process

- **Version control:** repo is not yet `git init`-ed. Any git write (init/add/commit/branch/push) will be **proposed for approval first**; only read-only git is run without asking.
- **Android skills:** `edge-to-edge`, `testing-setup`, `navigation-3`, `adaptive`, `r8-analyzer`, `android-intent-security`, `styles` are installed under `.claude/skills/` — consult the relevant one per phase (see `CLAUDE.md`).
