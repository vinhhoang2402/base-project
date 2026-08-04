---
inclusion: manual
---

# ProjectBase — Complete Usage Guide

**Commands, workflows by role, and the 17 reference skills — everything in one place.**

> ⚠️ Built-in slash commands: `/code-review`, `/security-review`, `/verify`,
> `/run`, `/simplify`, `/review`, `/init`.
> Project command files (also invocable as slash commands): `/start`,
> `/self-review`.
> Anything else (`/linearb`, …) is **not** a command — use the plain
> git/gradle steps shown below.
> The numbered `01…17` skills are **reference docs**, not commands — ask Claude to
> follow them (e.g. "follow `~/.kiro/steering/04-mvi-pattern.md`") or read the file.

---

## 🧰 Real Commands Reference

The only slash commands available in this project:

| Command | What it does |
|---------|--------------|
| `/code-review low\|medium\|high\|ultra` | Review current diff (add `--fix` to apply, `--comment` to post on PR) |
| `/security-review` | Security audit of pending changes on the branch |
| `/verify` | Drive the change end-to-end to confirm it works |
| `/run` | Launch the app to see a change working |
| `/simplify` | Clean up changed code (reuse/simplify/efficiency) |
| `/review <PR#>` | Review a GitHub pull request |
| `/init` | Generate/refresh CLAUDE.md |
| `/self-review` | Review your OWN working diff against the Android checklist (ARCH/MEM/SEC + BUG-1..14) — project command file |

**Not commands** (reference docs — ask Claude to follow them, or read the file):
- `~/.kiro/steering/*.md` → coding conventions (ViewBinding, MVI, Moshi, …)
- `~/.kiro/steering/android-review/` → Android review checklist + agent
- `06-linearb-metrics.md` → LinearB metric targets (no `/linearb` command exists)

---

## 👥 Workflows by Role

### 👨‍💻 Developer (you write code)

**Single commands, run when needed:**
```bash
/code-review medium      # review diff (add --fix to auto-apply)
/security-review         # security check
/verify                  # verify build & behavior
/run                     # launch app to see it working
./gradlew ktlintFormat   # format code
```

**Full workflow before commit:**
```
0. git config core.hooksPath .githooks   # one-time per clone — enables LinearB gates
1. git checkout main && git pull
   git checkout -b feature/JIRA-456      # start from your ticket
2. ./gradlew ktlintFormat                # format
3. /code-review medium                    # review
4. LinearB check                          # diff size, coverage, revert history, msg format
5. /security-review                       # security
6. /verify                                # verify build & behavior
7. git add . && git commit -m "feat(JIRA-456): description"
   ⚠️ 2 real git hooks fire here:
      - diff > 200 lines (.githooks/pre-commit)       → blocked, split or --no-verify
      - bad message format (.githooks/commit-msg)      → blocked, fix or --no-verify
8. git push -u origin feature/JIRA-456
```

**LinearB check (step 4) criteria — pass when:**
```
✅ Diff size < 200 lines        git diff --cached --numstat
✅ Coverage > 70%                ./gradlew testDebugUnitTest, read the report
✅ No recent revert commits      git log --oneline -10 | grep -i revert
✅ Commit message format ok      type(scope): description
```
Diff size + message format are hard-enforced by git hooks either way; this step
exists to catch coverage/revert problems *before* spending time on
/security-review and /verify.

**Daily rhythm:**
```
MORNING     git checkout -b feature/JIRA-456 → read acceptance criteria
CODING      write feature → /code-review low  (quick check)
MIDDAY      /code-review medium → fix blockers
AFTERNOON   ./gradlew ktlintFormat → /security-review → /verify
PRE-PUSH    /code-review medium → git commit → git push
```

---

### 👁️ Reviewer (you review team code)

**Single commands:**
```bash
./gradlew ktlintFormat   # lint check
/review 42               # review a GitHub PR
/code-review medium      # review current working diff
/security-review         # security scan
```

**Full workflow for a member PR:**
```
1. gh pr view 42                       # get PR info
2. /review 42                          # review → Blockers/Should Fix/Nits
3. gh pr comment 42 --body "..."       # post findings on GitHub
4. (wait for author to fix)
5. /review 42                          # re-review
6. gh pr review 42 --approve           # approve
7. gh pr merge 42 --squash --delete-branch
```

---

### 🔀 Solo Developer (code + review yourself)

```
MORNING     git checkout -b feature/JIRA-456 → code → /code-review low
AFTERNOON   /code-review medium → fix → ktlintFormat → /security-review → /verify
PRE-PUSH    /code-review medium (final) → git commit → git push
AFTER PUSH  wait for CI green → gh pr merge --squash --delete-branch
```

---

### ✅ Which workflow?

```
1. Writing code?          → Developer workflow
2. Reviewing team code?   → Reviewer workflow
3. Doing both?            → Solo Developer workflow
4. Need one check?        → Run a single command above
```

---

## 📚 Which Reference Skills to Follow

Markdown docs under `~/.kiro/steering/` — point Claude at them by name.

| Situation | Skills |
|-----------|--------|
| New feature setup | `01-viewbinding, 04-mvi, 02-moshi, 08-testing, 05-ktlint, 16-repository` |
| Code review | `05-ktlint, 06-linearb, 10-kotlin, 12-error-handling` |
| Performance issues | `07-feature-build, 13-memory-lifecycle, 11-coroutines, 17-baseline-profiles` |
| Testing coverage gap | `08-testing, 10-kotlin, 13-memory-lifecycle` |
| API integration | `03-retrofit, 02-moshi, 16-repository, 14-security` |
| Memory leaks | `01-viewbinding, 13-memory-lifecycle, 11-coroutines` |

---

## 🔍 Android Review Workflow

**Local (pre-commit hook):**
```
git commit
↓  1. KtLint format  2. Secret detection  3. File size  4. Android pattern scan
↓  ✅ pass → commit succeeds   ❌ fail → fix, re-commit
```

**Remote (GitHub PR — `.github/workflows/android-review.yml`):**
```
git push
↓  1. Diff size  2. KtLint  3. Post checklist comment  4. Coverage
↓  Review against: ARCH-1..5, KOTLIN-1..5, TESTING-1..5, MEM-1..5,
                   DATA-1..5, ASYNC-1..5, QUALITY-1..4, PERF-1..3, SEC-1..4
```

**Reviewing a diff:**
```bash
cat ~/.kiro/skills/android-review/references/checklist.md   # read checklist
/code-review medium                                          # working diff
/review 42                                                   # a GitHub PR
```
Findings group as **Blockers / Should Fix / Nits**:
```
## Blockers    - ARCH-5   | file:line | description | fix
## Should Fix  - KOTLIN-2 | file:line | description | fix
## Nits        - QUALITY-3| file:line | description | fix
```

---

## 📋 Pre-Commit Hook Checklist

**Setup (one-time per clone):** `git config core.hooksPath .githooks`

```
✅ 5. LinearB commit-size (< 200 changed lines) — REAL hook: .githooks/pre-commit
      Blocks the commit (exit 1) if exceeded. Bypass: git commit --no-verify.

⏳ 1. KtLint format      — not yet a real hook, run `./gradlew ktlintFormat` manually
⏳ 2. Secret detection   — not yet a real hook, check manually before committing
⏳ 3. File size check    — not yet a real hook, check manually (Kotlin < 500 lines)
⏳ 4. Android pattern    — not yet a real hook, covered by /code-review instead
```

Only item 5 (LinearB size gate) is currently enforced by an actual git hook.
Items 1-4 are aspirational/manual until wired into `.githooks/pre-commit` too —
don't assume they run automatically.

---

## 🎯 Reference Skill Examples

### 01 - ViewBinding
```
Check: _binding = null in onDestroyView() (MEM-1), adapter via autoClear() (MEM-2)

override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}
```

### 02 - Moshi Models
```
Check: {Feature}Response (DATA-1), @JsonClass(generateAdapter=true) (DATA-2),
       @Json(name="…") (DATA-3), .toDomain() mapper (DATA-4)

@JsonClass(generateAdapter = true)
data class MovieResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "poster_path") val posterPath: String
)
fun MovieResponse.toDomain() = Movie(id = id, posterUrl = "https://image.../w500$posterPath")
```

### 04 - MVI Pattern
```
Check: Contract with Intent/State/Effect (ARCH-2), immutable State,
       renderState() + handleEffect() (ARCH-4)

interface MovieContract {
    data class State(val isLoading: Boolean, val movies: List<Movie>)
    sealed interface Intent { data object LoadMovies : Intent }
    sealed interface Effect { data class NavigateTo(val id: Int) : Effect }
}
class MovieViewModel : BaseViewModel<Intent, State, Effect>() {
    override fun handleIntent(intent: Intent) {
        when (intent) { Intent.LoadMovies -> loadMovies() }
    }
}
```

### 08 - Testing
```
Check: state tested (TESTING-3), fakes not mocks (TESTING-2), coverage > 70% (TESTING-1)

@Test
fun `loadMovies emits success state`() = runTest {
    val viewModel = MovieViewModel(FakeMovieRepository())
    viewModel.handleIntent(Intent.LoadMovies)
    assertThat(viewModel.state.value).isInstanceOf(State.Success::class.java)
}
```

### 11 - Coroutines & StateFlow
```
Check: viewModelScope (ASYNC-1), StateFlow (ASYNC-2), .stateIn() (ASYNC-3), error handling (ASYNC-5)

val state: StateFlow<State> = repo.getMoviesFlow()
    .map { it.toDomain() }
    .catch { e -> emit(State.Error(e.message)) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), State.Loading)
```

### 16 - Repository Pattern
```
Check: Remote + Local DataSource interfaces (ARCH-3), offline-first

class MovieRepositoryImpl(
    private val remote: RemoteMovieDataSource,
    private val local: LocalMovieDataSource
) : MovieRepository {
    override suspend fun getPopularMovies(page: Int): List<Movie> = try {
        remote.getPopularMovies(page)
            .also { local.saveMovies(it.map { it.toEntity() }) }
            .map { it.toDomain() }
    } catch (e: Exception) {
        local.getMovies().map { it.toDomain() }
    }
}
```

---

## 🚨 Troubleshooting

**Pre-commit fails:**
```bash
./gradlew ktlintFormat && git add .                        # format
git diff --cached | grep -i "password\|token\|api.key"     # secrets
git diff --cached --name-only | while read f; do echo "$(wc -l < "$f") $f"; done  # sizes
git commit -m "feat: message"
```

**GitHub Actions review seems wrong:**
```bash
cat ~/.kiro/skills/android-review/references/checklist.md   # read checklist
/code-review medium                                          # re-run locally
gh pr comment <PR#> --body "..."                            # reply on PR
```

---

## 📊 Metrics (reference: 06-linearb-metrics.md)

There is **no `/linearb` command** — this is a target reference only.
```
Cycle time < 5 days | Commit freq 1-3/day | Rework < 10% | Coverage > 70%

./gradlew testDebugUnitTest    # coverage locally, target > 70%
```

---

## ✨ Pro Tips

1. Use `./scripts/build-feature.sh <name>` instead of a full build.
2. Run `/code-review` + `/security-review` before every commit.
3. Small commits (< 200 lines) get faster, clearer reviews.
4. Write tests before code for a TDD flow.
5. Use `/simplify` for a pure quality cleanup (no bug hunting).

---

## 🎯 Ready to Merge When

```
✅ Pre-commit checks pass          ✅ /code-review medium clean
✅ No Blockers from review          ✅ /security-review clear
✅ Tests pass (> 70% coverage)      ✅ Should Fix items addressed
```

---

**Version:** 4.0 | **Updated:** 2026-07-26
