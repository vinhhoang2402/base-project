# Build real UI for Home's "Coming soon" sections, starting with World Map

## Context

`HomeScreen` currently lists all 9 remaining IA sections (`AppSection` enum) as tappable cards,
but every one routes to a generic `ComingSoonScreen` placeholder. Several backing engines these
sections need are already fully built and wired this session (ContentEngine's
Grade→Semester→Unit→Lesson + `getUnitsForWorld(worldId)` queries, ProgressEngine's per-skill
accuracy, RewardEngine's treasure chest/totals, SettingsRepository's grade/onboarding state) but
have no screen consuming them yet. The goal is to replace the placeholder with real screens.

The user then supplied a concrete ASCII mockup for **World Map** specifically: a header stats bar
(avatar name, hearts, level, grade, coins) above a vertical winding path of themed worlds
(Home→School→Zoo→Restaurant→Airport→Beach→Space, per `project_brightnest_academic_journey`),
each showing a lock state and a star rating, plus a persistent **bottom navigation bar** with 6
icons: Home 🏠, World Map 🗺️, Lessons 🎒, Rewards 🎁, Parent Dashboard 👨‍👩‍👧, Settings ⚙️.

This changes the plan in two ways from a flat Home-card-list:
1. **Navigation restructures** to a persistent bottom-nav shell (5 tabs, Home included) instead
   of Home hosting a scrollable list of all 9 sections. The bottom nav's 6 icons don't include
   Story/Mini Games/Flashcards/Speaking at all — confirming they stay internal Lesson Session
   steps, not top-level destinations (see "Deferred" below).
2. **World Map needs a few small new-but-real mechanics** it didn't have a data source for
   before (stars, lock/unlock, level) — addressed by deriving them from data that already exists,
   detailed below, rather than inventing new persisted state where avoidable.

## Scope decision: which sections get built, and in what order

**Bottom-nav tabs (5, replacing Home's card list):** Home, World Map, Lessons, Rewards,
Parent Dashboard, Settings.

**Deferred, not built this pass:** Story, Mini Games, Flashcards, Speaking. These only exist
today as *steps inside a guided Lesson Session* (`LessonSessionViewModel`/`LessonSessionScreen`),
not independent content — `ContentEngine` has no "list every vocabulary word / story / game
config across all lessons" query, everything is lesson-scoped. The mockup's bottom nav itself
confirms these were never meant to be top-level destinations. Left as-is (unreachable except
through a guided lesson) — a real "free practice" mode for them is a separate future design.

**Build order (small step + compile-check + commit each, per standing project preference):**
1. World Map (this is the one with a concrete mockup — build first)
2. Bottom-nav shell in `App.kt` (needed for World Map and the other tabs to be reachable at all)
3. Finish Rewards (`RewardContract.kt` already exists uncommitted from earlier this session)
4. Settings (also gains a simple child-name field, see below — needed by World Map's header)
5. Parent Dashboard
6. Lessons (Grade→Semester→Unit→Lesson drill-down browse)

## World Map design -- mapping the mockup to real data

**Header stats bar:**
- **Grade** — already real: `SettingsRepository.selectedGrade`.
- **Coins** — already real: `RewardEngine.totals.coins`.
- **Avatar name** ("Alex") — nothing captures a child's name today (`LessonSessionScreen`'s
  `childName` param has only ever been passed the hardcoded default `"friend"`). Adding a
  `childName: Flow<String?>` field to `SettingsRepository` (same DataStore pattern as
  `selectedGrade`) and a text field on the Settings screen (step 4) closes this gap and is the
  first real source for the name shown here and in the Lesson Session welcome message.
- **Level** ("Lv12") — derived, not stored: `level = 1 + totals.xp / 100`. No new persistence.
- **Hearts** ("❤️ 5") — **recommendation: omit from v1.** A depleting-lives/mistake-penalty
  mechanic (the usual meaning of hearts in this kind of UI) directly conflicts with the app's
  never-criticize design principle (no failure state is ever shown to the child anywhere else in
  this codebase — see `project_brightnest_ai_teacher`, and `SpeechEvaluator`/`ChoiceGameSession`
  etc. never surface "wrong"). A cosmetic, always-full heart icon would be the one genuinely fake
  stat in an app that's otherwise been strict about not faking data. Flagging this explicitly
  rather than silently building either version — easy to add back later once its real meaning
  (or a non-punitive equivalent) is decided.
- Avatar image itself: a generic placeholder icon (same spirit as `WordVisual`'s deterministic
  placeholder), not a real customizable Avatar — that's item #15 from the earlier audit, a
  separate, larger, currently-unscoped feature.

**World path (7 fixed worlds, lock state + stars):**
- Worlds are the fixed named list from `project_brightnest_academic_journey`, not a DB query —
  `WORLDS = listOf("home" to "Home", "school" to "School", "zoo" to "Zoo", "restaurant" to
  "Restaurant", "airport" to "Airport", "beach" to "Beach", "space" to "Space")`. Only `worldId =
  "home"` has real seeded content today (`content_bundle.json`); the rest honestly show "No
  lessons yet" the same way selecting Grade 2+ already does elsewhere in the app — not hidden.
- **Stars per world** (normalizing the mockup's inconsistent star counts to a consistent 0–3
  scale): needs "which lessons has the child completed" data, which doesn't fully exist yet --
  `ChildProgressEntity` only tracks a single linear `currentLessonId` pointer, not a completion
  set. But `ProgressEngine`'s `session_log` table (populated by `recordSessionCompletion`, wired
  last phase) already logs every completed session with its `lessonId` and `mode`. Add one new
  query, `ProgressDao.getCompletedLessonIds(): List<String>` (`SELECT DISTINCT lessonId FROM
  session_log WHERE mode = 'REVIEW'`), exposed via a new `ProgressEngine.completedLessonIds():
  Set<String>`. For a world: gather its lessons via `getUnitsForWorld(worldId)` →
  `getLessonsForUnit(...)` per unit, then `stars = round(3.0 * completedInWorld /
  totalLessonsInWorld)`, 0 if the world has no lessons yet.
- **Lock/unlock**: world *N+1* unlocks once world *N* has at least 1 star. Simple, derived at
  read time (no new persisted "unlocked" flag), consistent with "never permanently blocks
  progress on a stored flag that can drift from reality."
- Tapping an unlocked world's lesson navigates to `LessonSessionScreen(lessonId = ..., mode =
  LessonMode.PREVIEW)` — the screen already accepts an explicit `lessonId` and `childName`.

**Files:** `presentation/ui/worldmap/{WorldMapContract,WorldMapViewModel,WorldMapScreen}.kt`,
following the exact same MVI trio pattern as every other screen this session
(`BaseViewModel`/`BaseScreen`/`koinViewModel()`).

## Bottom-nav shell (`App.kt` restructure)

Two-level `NavHost`: the existing outer one keeps `onboarding` / `main` / `lesson_session` as
full-screen destinations (so an active lesson still covers the whole screen, no bottom bar
visible mid-lesson, matching how the mockup shows no nav bar inside a lesson). `main` becomes a
new composable wrapping a Material3 `Scaffold` with a `bottomBar = { NavigationBar { ... } }` and
its *own* inner `NavHost` for the 5 tabs (Home/World Map/Lessons/Rewards/Parent
Dashboard/Settings). `HomeScreen` drops its `AppSection` card-list body (that responsibility
moves to the bottom bar) and keeps just the Today's Mission card + `RewardSummaryRow`.
`AppSection` enum either shrinks to the 5 tabs or is replaced by a small `BottomNavTab` enum in
the same spot (`presentation/navigation/`) — exact naming decided during implementation, not
worth deciding now.

## Rewards / Settings / Parent Dashboard / Lessons (steps 3, 4, 5, 6)

Same shape as before this mockup changed the plan -- unchanged from the original scope decision:

- **Rewards**: finish the already-started `RewardContract.kt` with `RewardViewModel` (inject
  `RewardEngine`, collect `totals`, handle `OpenTreasureChest` → `openTreasureChest()`) and
  `RewardScreen` (reuse `RewardSummaryRow`, treasure chest card with friendly "keep earning
  coins" messaging when unaffordable — never a harsh "not enough" framing, badges/stickers as
  plain `Set<String>` rows, no art assets yet).
- **Settings**: `SettingsViewModel` injects `SettingsRepository` + `ContentEngine`; shows/edits
  child name (new) and selected grade (existing `Card`-list pattern reused from Onboarding's
  `ChooseGradeStepContent`).
- **Parent Dashboard**: `ParentDashboardViewModel` injects `ProgressEngine` + `RewardEngine`;
  one row per `SkillTag` (Vocabulary/Reading/Listening/Speaking) accuracy, a Study Time card
  (`weeklyStudyMinutes`), a Weak Skill callout worded encouragingly (never "weak"/"bad" framing,
  matching AI Teacher tone even though this screen is parent-facing).
- **Lessons**: drill-down Grade → Semester → Unit → Lesson browse built on `ContentEngine`'s 4
  existing query methods (`getGrades/getSemesters/getUnits/getLessonsForUnit`), same visual
  pattern as World Map's unit/lesson lists. Tapping a lesson navigates to
  `LessonSessionScreen(lessonId = ..., mode = LessonMode.PREVIEW)`.

## Verification

Same discipline as every phase this session: after each screen/step,
`./gradlew :shared:compileDebugKotlinAndroid` and `:shared:compileKotlinIosSimulatorArm64` must
both succeed, plus a final `:androidApp:assembleDebug`. No device/emulator is available in this
environment (no `adb`/`simctl`), so this stays compile+assemble-verified only, stated explicitly
when reporting -- not click-tested. Commit each screen/step separately once its compile check
passes, per the standing "commit per phase" preference.
