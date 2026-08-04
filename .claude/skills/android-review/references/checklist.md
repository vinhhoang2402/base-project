# Android Review Checklist

**ProjectBase Architecture & Kotlin Best-Practice Rules**

---

## ARCHITECTURE (ARCH-*)

### ARCH-1: Layered Architecture Compliance
- **Rule:** Code follows presentation → domain → data layers
- **Applies to:** All code in feature modules
- **Severity:** Blocker
- **Example:** ViewModel should not directly access database or API

### ARCH-2: MVI Pattern Adherence
- **Rule:** UI follows Intent/State/Effect pattern via Contract
- **Applies to:** Fragment + ViewModel pairs
- **Severity:** Blocker
- **Example:** All user interactions → Intent → State emission

### ARCH-3: Repository Pattern Usage
- **Rule:** Data access through Repository interface, not direct API/DB access
- **Applies to:** Data layer code
- **Severity:** Blocker
- **Example:** ViewModel calls repository.getMovies(), not service.getMovies()

### ARCH-4: Unidirectional Data Flow (UDF)
- **Rule:** State flows down (ViewModel → UI), events flow up (UI → ViewModel)
- **Applies to:** ViewModel/Fragment interaction
- **Severity:** Blocker
- **Example:** UI observes StateFlow, never calls ViewModel methods directly

### ARCH-5: No Circular Dependencies
- **Rule:** Feature modules don't depend on each other; only on core modules
- **Applies to:** build.gradle.kts dependencies
- **Severity:** Blocker
- **Example:** feature-home should not depend on feature-auth

---

## KOTLIN CONVENTIONS (KOTLIN-*)

### KOTLIN-1: Naming Conventions
- **Rule:** Classes/Objects PascalCase, functions/vars camelCase, constants UPPER_SNAKE_CASE
- **Applies to:** All code
- **Severity:** Nit
- **Example:** `class MovieViewModel`, `fun loadMovies()`, `const val MAX_RETRIES`

### KOTLIN-2: Null Safety
- **Rule:** Non-nullable by default; use `?` only when necessary
- **Applies to:** All variable/parameter declarations
- **Severity:** Should Fix
- **Example:** `val name: String` not `val name: String?`

### KOTLIN-3: Scope Functions Usage
- **Rule:** Use `.let`, `.run`, `.apply`, `.also` idiomatically — neither for every
  statement (overuse) NOR skipped where they'd remove repeated temp locals /
  side-effect boilerplate (underuse)
- **Applies to:** Kotlin code
- **Severity:** Nit
- **Example (overuse):** don't wrap every single statement in `.let`
- **Example (underuse):** replace
  `val u = current.username; log(u); setSession(u)` with
  `current.username.also { log(it); setSession(it) }`
- **How to catch it:** a block that declares a temp `val` only to reuse it 2–3
  times in adjacent side-effects is a candidate for `.also`/`.apply`/`.run`

### KOTLIN-4: No Wildcard Imports
- **Rule:** Never use `import com.example.*`
- **Applies to:** All imports
- **Severity:** Should Fix
- **Example:** `import com.example.Movie` instead of `import com.example.*`

### KOTLIN-5: Proper Use of Data Classes
- **Rule:** Use `data class` for entities with only properties, not for business logic
- **Applies to:** Model classes
- **Severity:** Nit
- **Example:** `data class Movie(val id: Int, val title: String)`

### KOTLIN-6: String Templates Over Concatenation
- **Rule:** Build strings with templates (`"...$x..."` / `"${expr}"`), never `+`
  concatenation of literals and variables
- **Applies to:** All string building (logs, messages, URLs)
- **Severity:** Nit
- **Example:** `"User $username logged in"` not `"User " + username + " logged in"`
- **How to catch it:** grep the diff for `" +` / `+ "` joining a literal and a
  variable

---

## TESTING (TESTING-*)

### TESTING-1: Test Coverage ≥ 70%
- **Rule:** Minimum 70% code coverage for ViewModels, repositories, use cases
- **Applies to:** All feature modules
- **Severity:** Blocker
- **Example:** ViewModel tests cover at least 70% of code paths

### TESTING-2: Use Fakes, Not Mocks
- **Rule:** Repository testing uses FakeRepository, not Mockk mocks
- **Applies to:** Repository tests
- **Severity:** Should Fix
- **Example:** `class FakeMovieRepository : MovieRepository { ... }`

### TESTING-3: ViewModel State & Effect Testing
- **Rule:** Tests verify state emission and effects, not method calls
- **Applies to:** ViewModel unit tests
- **Severity:** Should Fix
- **Example:** Assert `viewModel.state.value` matches expected state

### TESTING-4: Lifecycle-Aware Testing
- **Rule:** UI tests use `repeatOnLifecycle()` or `collectAsStateWithLifecycle()`
- **Applies to:** Fragment/Compose tests
- **Severity:** Should Fix
- **Example:** Launch collection in `repeatOnLifecycle(State.STARTED)`

### TESTING-5: Test Isolation
- **Rule:** Each test is independent; no state sharing between tests
- **Applies to:** All test files
- **Severity:** Should Fix
- **Example:** Each `@Test` resets mocks/fakes via setup

---

## MEMORY & LIFECYCLE (MEM-*)

### MEM-1: Binding Cleanup in onDestroyView
- **Rule:** Fragment sets `_binding = null` in onDestroyView
- **Applies to:** All Fragment classes
- **Severity:** Blocker
- **Example:** `override fun onDestroyView() { super.onDestroyView(); _binding = null }`

### MEM-2: AutoClear for Adapter References
- **Rule:** Adapter properties use `by autoClear()` delegate
- **Applies to:** Fragment adapter properties
- **Severity:** Should Fix
- **Example:** `private var adapter: MovieAdapter? by autoClear()`

### MEM-3: No Context/Activity in ViewModel
- **Rule:** ViewModel never holds references to Context or Activity
- **Applies to:** ViewModel classes
- **Severity:** Blocker
- **Example:** Use ApplicationContext only if absolutely necessary

### MEM-4: Lifecycle-Aware Flow Collection
- **Rule:** Flows collected in `repeatOnLifecycle()` or `collectAsStateWithLifecycle()`
- **Applies to:** UI layer Flow/StateFlow collection
- **Severity:** Should Fix
- **Example:** `repeatOnLifecycle(Lifecycle.State.STARTED) { flow.collect { ... } }`

### MEM-5: ViewModelScope for Coroutines
- **Rule:** All coroutines launched in `viewModelScope`, never GlobalScope
- **Applies to:** ViewModel coroutine launches
- **Severity:** Blocker
- **Example:** `viewModelScope.launch { ... }`

---

## VIEW BINDING (VB-*)

### VB-1: ViewBinding Only — No findViewById / Synthetics
- **Rule:** Access views exclusively through the generated ViewBinding
  (`FragmentXBinding`, `DialogXBinding`, `ItemXBinding`); never `findViewById`,
  Kotlin synthetics, or manual `inflate(R.layout…)` + view lookup
- **Applies to:** All Fragments, dialogs, custom views, adapters/ViewHolders
- **Severity:** Should Fix
- **Example (real bug, a33d8af):** `TermsDialog` did
  `LayoutInflater.from(context).inflate(R.layout.dialog_terms, null)` then
  `view.findViewById<Button>(R.id.btnAccept)` — should be
  `DialogTermsBinding.inflate(LayoutInflater.from(context))` + `binding.btnAccept`
- **How to catch it:** grep the diff for `findViewById`,
  `kotlinx.android.synthetic`, or `inflate(R.layout` followed by manual lookups

---

## DATA MODELS (DATA-*)

### DATA-1: Feature-Specific Model Naming
- **Rule:** Models prefixed with feature/domain name (MovieResponse, not Response)
- **Applies to:** data/model/*.kt files
- **Severity:** Blocker
- **Example:** `data class MovieResponse`, `data class UserAuthEntity`

### DATA-2: Moshi @JsonClass Annotation
- **Rule:** All API models have `@JsonClass(generateAdapter = true)`
- **Applies to:** data/model/*Response.kt
- **Severity:** Should Fix
- **Example:** `@JsonClass(generateAdapter = true) data class MovieResponse(...)`

### DATA-3: @Json for Snake_Case Fields
- **Rule:** API fields use `@Json(name = "snake_case")` for mapping
- **Applies to:** API model properties
- **Severity:** Should Fix
- **Example:** `@Json(name = "vote_average") val voteAverage: Double`

### DATA-4: Mapper Functions
- **Rule:** API models include `.toDomain()` mapper to domain models
- **Applies to:** data/model/*Response.kt
- **Severity:** Should Fix
- **Example:** `fun MovieResponse.toDomain() = Movie(...)`

### DATA-5: Separate API & Domain Models
- **Rule:** Don't reuse API models as domain models
- **Applies to:** Model definitions
- **Severity:** Should Fix
- **Example:** MovieResponse (API) → Movie (domain) via mapper

---

## ASYNC & COROUTINES (ASYNC-*)

### ASYNC-1: Structured Concurrency
- **Rule:** All coroutines in `viewModelScope`, `lifecycleScope`, or other structured scope
- **Applies to:** Coroutine launches
- **Severity:** Blocker
- **Example:** `viewModelScope.launch { ... }` not `launch { ... }`

### ASYNC-2: StateFlow for State Management
- **Rule:** ViewModel exposes state via `StateFlow<State>`
- **Applies to:** ViewModel properties
- **Severity:** Blocker
- **Example:** `val state: StateFlow<UiState> = _state.asStateFlow()`

### ASYNC-3: stateIn Operator for Flow Conversion
- **Rule:** Repository Flows converted to StateFlow via `.stateIn(viewModelScope, ...)`
- **Applies to:** ViewModel Flow → StateFlow conversion
- **Severity:** Should Fix
- **Example:** `repository.getDataFlow().stateIn(viewModelScope, ...)`

### ASYNC-4: Suspending Functions Over Callbacks
- **Rule:** Use `suspend fun` in data layer; no callback-based APIs
- **Applies to:** Repository/DataSource method signatures
- **Severity:** Should Fix
- **Example:** `suspend fun getMovies(): List<Movie>` not callback-based

### ASYNC-5: Try-Catch in Coroutines
- **Rule:** Exceptions caught in coroutines, state emitted on error
- **Applies to:** viewModelScope.launch blocks
- **Severity:** Should Fix
- **Example:** Catch exception → `_state.value = State.Error(...)`

---

## CODE QUALITY (QUALITY-*)

### QUALITY-1: KtLint Compliance
- **Rule:** Code passes `./gradlew ktlintFormat` without warnings
- **Applies to:** All Kotlin code
- **Severity:** Should Fix
- **Example:** Max line length 120, proper spacing, no wildcard imports

### QUALITY-2: File Size Limit
- **Rule:** Kotlin files < 500 lines; split into multiple files if larger
- **Applies to:** All .kt files
- **Severity:** Should Fix
- **Example:** Split 600-line Activity into Fragment + ViewModel

### QUALITY-3: Function Size Limit
- **Rule:** Functions < 50 lines; extract smaller functions if larger
- **Applies to:** All functions
- **Severity:** Nit
- **Example:** Break large function into smaller helper functions

### QUALITY-4: Comments Only for Why
- **Rule:** Comments explain WHY, not WHAT; code itself is documentation
- **Applies to:** All comments
- **Severity:** Nit
- **Example:** `// Account for timezone offset in API response` not `// Get response`

---

## PERFORMANCE (PERF-*)

### PERF-1: Lazy Initialization
- **Rule:** ViewModels/Adapters initialized only when needed, not at class level
- **Applies to:** Fragment properties
- **Severity:** Should Fix
- **Example:** Initialize in `setupViews()`, not at class declaration

### PERF-2: Feature-Level Builds
- **Rule:** Individual feature modules build independently via `./scripts/build-feature.sh`
- **Applies to:** build.gradle.kts (features use `id("android.library")`)
- **Severity:** Should Fix
- **Example:** `plugins { id("android.library") }` not `id("android.application")`

### PERF-3: Baseline Profiles
- **Rule:** App includes baseline-prof.txt with critical paths
- **Applies to:** app/src/main/baseline-prof.txt
- **Severity:** Nit
- **Example:** List MainActivity, critical Fragments, ViewModel classes

---

## SECURITY (SEC-*)

### SEC-1: HTTPS Only
- **Rule:** All API endpoints use HTTPS, never HTTP
- **Applies to:** Retrofit configuration
- **Severity:** Blocker
- **Example:** `.baseUrl("https://api.example.com/")`

### SEC-2: No Sensitive Data in Logs
- **Rule:** Never log API keys, tokens, user data
- **Applies to:** All Log.d/Log.i statements
- **Severity:** Blocker
- **Example:** `Log.d("API", "Response received")` not `Log.d("API", response)`

### SEC-3: Secrets in Environment/Build Config
- **Rule:** Secrets loaded from BuildConfig or secure storage, never hardcoded
- **Applies to:** API keys, tokens, passwords
- **Severity:** Blocker
- **Example:** `val apiKey = BuildConfig.API_KEY` not `const val API_KEY = "xyz"`

### SEC-4: Null Safety for Crash Prevention
- **Rule:** Proper null handling prevents NullPointerException crashes
- **Applies to:** All variable handling
- **Severity:** Should Fix
- **Example:** `val email = user.email ?: "no-email"`

---

## COMMON BUGS (BUG-*)

Recurring runtime bug patterns to check during review, not just
style/architecture. These rules are applied by the **Reviewer flow**
(`/start` → Reviewer) and the **`android-review` skill / `/review`**, which load
this checklist. They are NOT auto-loaded by the generic `/code-review` or
`/verify` — a developer self-reviewing must run the android-review skill /
`/review` to apply them. Each one below was either seen in this codebase or is a
well-known Kotlin/Android gotcha.

### BUG-1: Main-Thread Blocking I/O Outside a Coroutine
- **Rule:** Disk/Keystore/DB access must be inside `viewModelScope.launch` (+ `Dispatchers.IO`
  for anything not already suspend), never called directly from `handleIntent`/`onViewCreated`
- **Applies to:** Any call into `SecurePreferencesManager`, Room DAOs, file I/O
- **Severity:** Blocker
- **Example (real bug, BASE-789):** `Intent.ScreenStarted -> refreshBiometricAvailability()`
  called `securePrefs.getAccessToken()` synchronously on the calling thread —
  first access lazily builds `EncryptedSharedPreferences` (Keystore + file I/O),
  causing jank/possible ANR on first screen load.
- **How to catch it:** grep the diff for calls into storage/DB/network from a
  `handleIntent` branch that ISN'T wrapped in `viewModelScope.launch`.

### BUG-2: Silent Exception Swallowing
- **Rule:** Never `catch (e: Exception) {}` (empty body) or catch-and-ignore;
  always map to an error state, rethrow, or log with context
- **Applies to:** All try/catch blocks
- **Severity:** Blocker
- **Example:** an empty catch around `EncryptedSharedPreferences` access means a
  `GeneralSecurityException` after a Keystore key invalidation crashes the app
  instead of falling back to the password form — the opposite bug (missing
  try/catch entirely) is just as bad; both need an explicit `Result`/error path.
- **How to catch it:** search the diff for `catch (` blocks with an empty or
  log-only body, and for storage/network calls with NO surrounding try/catch
  or `Result` wrapping at all.

### BUG-3: Transient State Lost on Next Recompute
- **Rule:** A transient condition (lockout, rate-limit, one-time warning) must
  be stored as state, not silently overwritten the next time the source-of-truth
  is recomputed from scratch
- **Applies to:** ViewModel state derived from a checker/repository call
- **Severity:** Should Fix
- **Example (real bug, BASE-789):** biometric `Lockout` set
  `isBiometricLoginAvailable = false` in the current `State`, but the next
  `ScreenStarted` recomputed availability from `BiometricManager.canAuthenticate()`
  alone — which doesn't know about the lockout — so the button silently
  reappeared and the user re-triggered the same lockout error.
- **How to catch it:** when a `when`/error branch flips a boolean/flag in
  `State`, check whether another code path recomputes that same flag from a
  different source without accounting for the flip.

### BUG-4: Storage Field Repurposed for a Different Meaning
- **Rule:** Never store a value in a field whose name implies something else
  (e.g. an email in a `refreshToken` slot) — add a real field instead
- **Applies to:** Any shared key-value store (`SharedPreferences`, `Bundle`, cache)
- **Severity:** Should Fix
- **Example (real bug, BASE-789):** `securePrefs.saveTokens(sessionId, email)`
  stored the user's email in the "refresh token" parameter; reading it back via
  `getRefreshToken()` to mean "email" silently breaks the moment real refresh-token
  rotation is added.
- **How to catch it:** check whether a value passed to a setter/field matches
  what the field's name/type says it holds.

### BUG-5: Double-Tap / Duplicate Submission
- **Rule:** Disable the action button (or debounce the click) while a request
  is in flight, don't just show a loading spinner alongside a still-clickable button
- **Applies to:** Any button triggering a network/DB write (login, submit, pay)
- **Severity:** Should Fix
- **Example:** `btnRegister.isEnabled` not tied to `state.isLoading` → rapid
  double-tap fires two `register()` calls, possibly creating two accounts/orders
- **How to catch it:** find the submit button's click listener and confirm
  `isEnabled`/debounce is driven by the loading state, not just visual feedback

### BUG-6: Locale-Sensitive String Casing
- **Rule:** Use `.uppercase()`/`.lowercase()` (locale-free) instead of the
  deprecated `.toUpperCase()`/`.toLowerCase()` with no `Locale` argument
- **Applies to:** Any case-conversion on user-facing or comparison strings
- **Severity:** Nit (Should Fix if used for a comparison/lookup key)
- **Example:** on a Turkish-locale device, `"i".toUpperCase()` produces `İ`, not
  `I` — a case-insensitive key lookup or email comparison silently fails
- **How to catch it:** grep for `.toUpperCase()`/`.toLowerCase()` without a
  `Locale` argument

### BUG-7: `notifyDataSetChanged()` Instead of DiffUtil
- **Rule:** RecyclerView adapters use `DiffUtil`/`ListAdapter.submitList()`, not
  `notifyDataSetChanged()`
- **Applies to:** All `RecyclerView.Adapter` implementations
- **Severity:** Should Fix
- **Example:** full rebind on every update loses scroll position, item
  animations, and re-triggers image loads for every visible row
- **How to catch it:** grep the diff for `notifyDataSetChanged(` — this
  project's `BaseListAdapter`/`BasePagingListAdapter` already wrap DiffUtil, so
  a raw call almost always means a Fragment is bypassing them

### BUG-8: Unbounded Retry / Loop
- **Rule:** Any retry loop needs a max-attempt cap and backoff; never loop on a
  failure condition with no exit
- **Applies to:** Network retry logic, polling loops
- **Severity:** Blocker
- **Example:** `while (result.isFailure) { result = retry() }` with no counter —
  a persistently-down endpoint spins the coroutine forever, burning battery/CPU
- **How to catch it:** any `while`/recursive retry — confirm there's a counter
  or timeout bounding it

### BUG-9: One-Time Effect via a Replaying Flow (Duplicate Navigation)
- **Rule:** One-shot effects (navigate, toast, dialog) must go through a
  non-replaying stream (`Channel.receiveAsFlow()`), never `StateFlow` or
  `SharedFlow(replay ≥ 1)`; and each user action must emit the effect exactly once
- **Applies to:** ViewModel Effect/event channels + their emit sites
- **Severity:** Blocker
- **Example (real bug, BASE-1024):** `emitEffect(NavigateToHome)` sent twice on a
  successful login opened Home twice; more generally, exposing effects as a
  replayed `StateFlow` re-delivers the last nav event on every config change /
  back-stack return, re-navigating.
- **How to catch it:** confirm effects use `Channel(...).receiveAsFlow()` (this
  project's `BaseViewModel` already does), and grep emit sites for the same effect
  sent twice in one branch.

### BUG-10: Flow Collected on the Wrong Fragment Lifecycle Scope
- **Rule:** In a Fragment, collect UI flows on `viewLifecycleOwner.lifecycleScope`
  inside `repeatOnLifecycle(STARTED)`, never on the Fragment's own `lifecycleScope`
- **Applies to:** All Fragment flow/state/effect collection
- **Severity:** Blocker
- **Example:** collecting on `lifecycleScope` (Fragment scope) keeps running while
  the view is destroyed (back stack) — touching `binding` then NPE-crashes, and a
  second collector is added when the view is recreated (double render / double nav)
- **How to catch it:** grep the diff for `lifecycleScope.launch` inside a Fragment
  that touches `binding`/views; it should be `viewLifecycleOwner.lifecycleScope`.

### BUG-11: Binding / View Access After onDestroyView
- **Rule:** Never touch `binding` or views from a callback/coroutine that can fire
  after `onDestroyView` (async result, delayed post, Flow without STARTED gating)
- **Applies to:** Fragments using ViewBinding + async work
- **Severity:** Blocker
- **Example:** a network callback calls `binding.progressBar.isVisible = false`
  after the user navigated away → `IllegalStateException`/NPE because `_binding`
  is already null
- **How to catch it:** check every `binding.` access reachable from an async
  continuation is either lifecycle-gated or null-guarded.

### BUG-12: Floating-Point for Money / Exact Values
- **Rule:** Never use `Float`/`Double` for currency, quantities, or any value
  needing exact arithmetic — use `BigDecimal` or integer minor-units (cents)
- **Applies to:** Payment, pricing, totals, tax calculations
- **Severity:** Should Fix (Blocker on a payment path)
- **Example:** `0.1 + 0.2 != 0.3` in `Double`; summing prices as `Double` drifts
  by cents and fails reconciliation/assertions
- **How to catch it:** grep model/UI for `Double`/`Float` fields holding money or
  counts that get added/multiplied.

### BUG-13: DiffUtil Comparing the Wrong Thing
- **Rule:** `areItemsTheSame` compares stable IDs; `areContentsTheSame` compares
  full value equality (use `data class` equals, not reference `===`)
- **Applies to:** `DiffUtil.ItemCallback` / `ListAdapter` implementations
- **Severity:** Should Fix
- **Example:** `areContentsTheSame = { a, b -> a === b }` on a freshly-mapped list
  always returns false → every row rebinds (lost scroll/animations); or comparing
  by `id` in both callbacks → content changes never re-render (stale UI)
- **How to catch it:** read both callbacks — items by ID, contents by `==`.

### BUG-14: Locale/Timezone-Dependent Date & Number Formatting
- **Rule:** Parse/format server timestamps with an explicit `Locale`/timezone
  (usually UTC + `Locale.US`), not the device default
- **Applies to:** `SimpleDateFormat`, `DateTimeFormatter`, number/currency formatting
- **Severity:** Should Fix
- **Example:** `SimpleDateFormat("dd/MM/yyyy")` with no `Locale` parses/renders
  differently on an Arabic-locale device (non-Gregorian digits) → wrong dates or
  parse crashes; a UTC server time shown in device tz shifts the day
- **How to catch it:** grep for `SimpleDateFormat(`/`DateTimeFormatter.ofPattern(`
  without a `Locale`, and any `Date()`/`toString()` used for a displayed time.

---

## Severity Escalation

Default severity can be raised/lowered with justification:

```
RAISE: "This blocks a critical user path" → Blocker
LOWER: "Stylistic, no behavioral impact" → Nit
```

---

**Last Updated:** 2026-07-26  
**Based on:** Google Android Guidelines + NOW in Android + ProjectBase 16 Skills
