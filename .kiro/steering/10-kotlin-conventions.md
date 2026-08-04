---
inclusion: auto
name: kotlin-conventions
description: Kotlin naming/organization conventions, null safety, scope functions (let/run/with/apply/also) decision table, and property delegates (by lazy, by viewModel, custom ReadWriteProperty delegates). Use when writing or reviewing any Kotlin code for idiomatic style.
---

# 10 - Kotlin Conventions & Best Practices

**File Size:** ~3kb | **Load Time:** 12s | **Context:** 220 tokens

---

## Naming Conventions

```kotlin
// ✅ GOOD
package com.demo.projectbase.feature.home

class MovieViewModel(...)           // PascalCase
fun loadMovies() { }               // camelCase
val movieList: List<Movie>         // camelCase
const val MAX_RETRIES = 3          // UPPER_SNAKE_CASE
interface MovieRepository { }      // PascalCase
enum class LoadState { ... }       // PascalCase

// ❌ BAD
class movie_view_model { }         // snake_case
fun LoadMovies() { }               // PascalCase
val MOVIE_LIST: List<Movie>        // UPPER_SNAKE_CASE for variable
```

---

## Code Organization

```kotlin
// Structure: Properties → Constructor → Lifecycle → Public Methods → Private Methods
class MovieViewModel : ViewModel() {
    // 1. Properties
    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()
    
    // 2. Constructor/Init
    init { loadMovies() }
    
    // 3. Public methods
    fun handleIntent(intent: Intent) { ... }
    
    // 4. Private methods
    private fun loadMovies() { ... }
}
```

---

## Null Safety

```kotlin
// ✅ GOOD: Non-nullable by default
val name: String = "John"          // Never null
val email: String? = null          // Nullable when needed
email?.let { sendEmail(it) }       // Safe call

// ❌ BAD
val name: String? = "John"         // Unnecessary nullable
val email: String = null           // Type mismatch
val result = email!!               // Avoid !! operator
```

---

## Scope Functions Cheat Sheet

5 functions, pick by **what you need back** + **how you refer to the receiver**:

| Function | Refers to receiver as | Returns | Use for |
|----------|-----------------------|---------|---------|
| `let`    | `it` (or named lambda arg) | lambda result | transform a value, null-safe chaining |
| `run`    | `this`                 | lambda result | compute a value using several receiver members |
| `with`   | `this` (not an extension, takes receiver as arg) | lambda result | group calls on an existing non-null object |
| `apply`  | `this`                 | the receiver   | configure/build an object (builder-style) |
| `also`   | `it`                   | the receiver   | side effect (logging, validation) mid-chain |

```kotlin
// let: null-safe transform
val length: Int? = email?.let { it.trim().length }

// run: compute from receiver members, don't need the object itself after
val summary = user.run { "$name <$email>" }

// with: same as run but object is already non-null, not chained
with(binding.tvTitle) {
    text = state.title
    isVisible = state.showTitle
}

// apply: configure and return the same object (builder pattern)
val request = Request.Builder().apply {
    url(endpoint)
    addHeader("Authorization", token)
}.build()

// also: side effect without breaking the chain
val movies = repository.getMovies()
    .also { Log.d(TAG, "Loaded ${it.size} movies") }
    .map { it.toDomain() }
```

**Rule of thumb:** if you're about to write `.let { it.foo(); it.bar() }` on the
same receiver more than once, switch to `apply`/`run`/`with` instead — repeated
`it.` is a sign you want `this`.

---

## Property Delegates

Prefer `by` delegation over manual boilerplate for recurring patterns.

**`by lazy` — expensive one-time init, computed on first access:**
```kotlin
// core/core-network/.../SecurePreferencesManager.kt
private val prefs: SharedPreferences by lazy {
    EncryptedSharedPreferences.create(context, PREFS_NAME, masterKey, ...)
}
```

**Framework delegates — don't hand-roll DI/lookup boilerplate:**
```kotlin
// Koin ViewModel injection (feature/.../HomeFragment.kt, RegisterFragment.kt)
override val viewModel: HomeViewModel by viewModel()
```

**Custom delegate — implement `ReadWriteProperty` when you need
lifecycle-aware get/set logic reused across classes:**
```kotlin
// core/core-ui/.../AutoClear.kt — nulls out a Fragment-scoped field on ON_DESTROY
class AutoClear<T> : ReadWriteProperty<Fragment, T?> {
    private var value: T? = null
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T? = value
    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T?) { this.value = value }
}
fun <T> autoClear() = AutoClear<T>()

// Usage: no manual onDestroyView cleanup needed
private var adapter: MovieAdapter? by autoClear()
```

**When to reach for `Delegates.observable`/`Delegates.vetoable`:** only if you
need to react to every mutation of a plain var (e.g. re-render on change)
without going through a full StateFlow — rare in this codebase since
MVI ViewModels already centralize state in `StateFlow`; don't introduce it as
a substitute for `updateState {}` in a ViewModel.

---

## Kotlin Idioms

```kotlin
// Use `when` instead of if-else chains
val status = when (response.code) {
    200 -> "Success"
    400 -> "Bad Request"
    500 -> "Server Error"
    else -> "Unknown"
}

// String templates, NOT `+` concatenation (checklist KOTLIN-6)
val msg = "User $username logged in"          // ✅
val bad = "User " + username + " logged in"   // ❌
```

---

## Backing Fields

Expose immutable read-only state; keep the mutable source private.

```kotlin
// ✅ GOOD: private mutable backing field + public read-only view
private val _state = MutableStateFlow(State())
val state: StateFlow<State> = _state.asStateFlow()

// ❌ BAD: exposing the mutable type lets the UI mutate state directly
val state = MutableStateFlow(State())
```

---

## ⭐ User Preference — idiomatic Kotlin for reuse & maintenance

The user explicitly wants code (and reviews) to favour these idioms because they
cut boilerplate and make code easier to reuse and maintain — treat **missing
them as a real review finding, not just style**, and flag **under-use** as well
as misuse:

- **Scope functions** (`let`/`run`/`apply`/`also`) to remove repeated temp locals
  and group side-effects — see cheat sheet above (checklist KOTLIN-3).
- **Delegates** — `by lazy`, `by viewModel()`, `by autoClear()`, custom
  `ReadWriteProperty` — see Property Delegates above.
- **Backing fields** — `private val _x` (mutable) exposed as read-only `val x`.
- **String templates** over `+` concatenation (checklist KOTLIN-6).

When writing code, apply these proactively; when reviewing, cite KOTLIN-3 /
KOTLIN-6 for violations.

---

**File:** `10-kotlin-conventions.md`  
**Tokens:** 220
