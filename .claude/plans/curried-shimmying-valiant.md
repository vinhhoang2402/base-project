# Wire A-PreA1 curriculum-data into the running app

## Context

The last several sessions produced a complete, validated JSON dataset for
Band A-PreA1 under
`shared/src/commonMain/composeResources/files/curriculum-data/A-PreA1/`
(band.json, units.json, `lessons/*.json`, vocabulary.json,
grammar_patterns.json, assessmentTasks.json, `lessonContents/*.json`,
spiral_review_mappings.json). None of it is wired to any Kotlin code —
`grep -rl "curriculum-data" shared/src/commonMain/kotlin` returns zero
hits. The app currently boots into a temporary `CurriculumProgressScreen`
with a single hardcoded button that opens one hardcoded lesson
(`lessonId = "band-a-prea1-u2-l1"`, which no longer exists after the
content-bundle cleanup commit) via `App.kt`'s temporary 2-route `NavHost`.

Goal for this pass: make Band A-PreA1 actually playable end-to-end —
Onboarding → real lesson list for the learner's band → real
`LessonSessionScreen` — using **only the existing Room schema and existing
lesson-step UI**. No new UI components, no new DB tables. Richer fields
that have no consuming UI yet (grammarPatterns, DOK-tagged
assessmentTasks, spiralReviewMappings) are read where useful (see below)
but not persisted as new tables — reversible later once a feature
actually needs them (e.g. Parent Dashboard, Progress Engine).

Band A-A1 is intentionally out of scope for this pass (per your
confirmation) — its file layout differs (per-unit array files vs A-PreA1's
per-lesson files) and would follow the same pattern once A-PreA1 is
proven working.

## Data mapping (curriculum-data/A-PreA1 → existing Room schema)

No schema changes. Reuse the **grade=0 sentinel** convention the old
Band-A-PreA1 bundle already established (see `ContentSeeder.kt`'s existing
comment: "grade=0 is a sentinel... no pedagogical authority"):
`gradeNumber = 0`, `semesterNumber = 1`, `unitNumber` = the unit's number
(1-5), `lessonNumber` = the lesson's number (1-5), `lessonId` = the
existing string id (`"A-PreA1-U{n}-L{m}"`, lowercased to match the app's
existing `lessonId` casing convention if needed — check against
`LessonSessionViewModel`'s current lookup casing before finalizing).

This means the existing `CurriculumDao.getUnitsForGradeSemester(0, 1)` and
`getLessonsForUnit(0, 1, unitNumber)` queries work unmodified for the new
lesson list UI — no new DAO methods needed.

Per source file, target entity:

| Source | Target | Notes |
|---|---|---|
| `units.json` | `UnitEntity` | `topic = theme`, `worldId` = a fixed placeholder (e.g. `"band-a-prea1"`) since World-map theming isn't relevant here |
| `lessons/A-PreA1-U{n}-L{m}.json` | `LessonEntity` | `overviewDescription` from lesson `title`; `overviewObjectives` from `learningStepSequence[].contentDescription` joined |
| `lessonContents/A-PreA1-U{n}-L{m}-content.json` `vocabularyFlashcards` (ids) resolved against `vocabulary.json` | `VocabularyEntity` | New vocab schema has **no `imageAsset`/`audioAsset`/`translation`/`phonetic` fields** (it's pedagogical-metadata-only). Synthesize placeholder asset paths by convention (`"images/vocab/{word}.png"`, `"audio/vocab/{word}.wav"`) — matches the fact that none of this session's `audio/...` paths in `listeningTasks` correspond to real files either. Real asset production is a separate, later content task; the pipeline must not block on it. |
| `lessonContents.storyText` (single string) | `StorySceneEntity` | Wrap as **one scene** (`sequenceOrder = 0`, `speaker = null`, no choices). New data has no multi-scene/branching story structure. If `storyText` is null (phonics lessons), insert no scene — the STORY step will render empty; verify in-app whether this needs an explicit skip (see Verification). |
| `lessonContents.miniGameRule`/`miniGameInstructions` + `vocabularyFlashcards` | `MiniGameConfigEntity` | New data has no structured game config (match/drag/sequence) — every `miniGameRule` describes a tap-to-match action anyway. Synthesize a single `type = "CHOICE"` config: word audio/text → matching image, built from the lesson's own vocabulary list. This reuses `ChoiceGameContent` as-is. |
| `assessmentTasks.json` entries with `format = "SpokenResponse"` and matching `lessonId` | `MiniGameConfigEntity` (`type = "SPEAK"`) | This is the actual clean source for the Speaking step's target phrase — use `correctAnswer` as the phrase to pronounce. (`speakingScenario`/`speakingDialogueFrame` in lessonContents are free-text scene descriptions, not usable as a structured target.) |
| `lessonContents.quizQuestions[]` (`prompt`, `correctAnswers`) | `QuizQuestionEntity` | New data has no distractor `options` array (it's tap/spoken-response, not MCQ). Synthesize options = correct answer + up to 3 distractors sampled from other vocabulary words in the same lesson; `correctIndex` = fixed position. Reuses existing `ChoiceGameContent`/QUIZ step UI unmodified. |
| `grammar_patterns.json`, `spiral_review_mappings.json`, DOK/Bloom's fields on `assessmentTasks.json` | *(not persisted)* | No UI reads these yet. Loader may read them for logging/validation but does not insert new rows. |

## Files to change

1. **New file** `shared/src/commonMain/kotlin/com/brightnest/app/domain/content/PreA1ContentLoader.kt`
   — reads the `curriculum-data/A-PreA1/*` files via `Res.readBytes`
   (paths built from `units.json`'s known unit count × each unit's
   `lessonsCount`, since there's no directory-listing API), cross-references
   lesson ↔ lessonContent ↔ vocabulary ↔ assessmentTasks by id, and returns
   a `List<LessonJson>` (existing private data class in `ContentSeeder.kt` —
   promote it from `private` to internal/public so this new file can build
   it) using the mapping table above.

2. **`ContentSeeder.kt`** — replace the deleted
   `"files/content_bundle_band_a_prea1*.json"` entries in `BUNDLE_FILES`
   with a call into `PreA1ContentLoader` to get its `List<LessonJson>`,
   merged into the same `allLessons` list the existing insert loop already
   processes. No changes to the insert logic itself.

3. **New file**
   `shared/src/commonMain/kotlin/com/brightnest/app/presentation/ui/curriculum/BandLessonsScreen.kt`
   (+ a small ViewModel) — two-level list: unit cards (5, from
   `getUnitsForGradeSemester(0, 1)`) → tap a unit → its 5 lessons (from
   `getLessonsForUnit`) → tap a lesson → navigate to
   `LessonSessionScreen(mode = LessonMode.PREVIEW, lessonId = ...)`.
   Matches the existing "unit cards, hide bottom nav on detail" pattern
   already used elsewhere in the app.

4. **`OnboardingViewModel.kt`** — in `finish()`, persist the determined
   `Band`'s id (e.g. `"A-PreA1"`) via `SettingsRepository` (new key)
   before marking onboarding complete.

5. **`App.kt`** — replace the temporary 2-route `NavHost`
   (`CurriculumProgressScreen` → hardcoded lesson) with:
   `Onboarding (if not completed) → BandLessonsScreen → LessonSessionScreen`.
   `CurriculumProgressScreen` drops out of the live flow (it was always a
   temporary placeholder per its own doc comment).

## Known limitations (explicitly deferred, not blocking)

- Vocabulary flashcard images/audio will be placeholder paths (404 in UI)
  until real media assets are produced — separate content task.
- Story step for phonics-only lessons (no `storyText`) may render empty;
  confirmed via manual run, fixed only if it's visibly broken rather than
  gracefully auto-skipped.
- Quiz/mini-game distractor options are synthesized, not authored —
  acceptable for structural playability, not final content quality.

## Verification

1. `./gradlew :shared:compileDebugKotlinAndroid` (or equivalent) to
   confirm the new loader and screens compile.
2. Run the app on the Android emulator (per the `run` skill / existing
   project run pattern): complete Onboarding with an age in the A-PreA1
   range (5-7), confirm it lands on `BandLessonsScreen` showing 5 unit
   cards, tap into Unit 1, tap Lesson 1, confirm `LessonSessionScreen`
   loads and steps through Vocabulary → Story → Mini-game → Speaking →
   Flashcards → Quiz without crashing, using real seeded content (not the
   old hardcoded lesson).
3. Spot-check a phonics-only lesson (e.g. U1-L5) to see how the empty
   Story step behaves; adjust if visibly broken.
