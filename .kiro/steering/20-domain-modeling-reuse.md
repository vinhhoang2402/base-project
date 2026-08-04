---
inclusion: auto
name: domain-modeling-reuse
description: Avoiding primitive obsession with inline value classes (UserId, Email), using sealed classes/interfaces for restricted hierarchies, extension functions for reuse instead of Util/Helper objects, and writing testable pure functions with constructor injection. Use when modeling a new domain type, writing a mapper/util, or reviewing for reuse and testability.
---

# 20 - Domain Modeling: Value Classes, Sealed Types, Extension Reuse

**File Size:** ~2kb | **Load Time:** 10s | **Context:** 170 tokens

---

## Primitive Obsession → Inline Value Classes

```kotlin
// ❌ BAD: id and email are both String — nothing stops an accidental swap
data class User(val id: String, val email: String, val name: String)
fun greet(id: String, email: String) { ... }
greet(email = user.id, id = user.email)   // compiles! silently wrong

// ✅ GOOD: distinct types, zero runtime cost (inlined to the wrapped String)
@JvmInline value class UserId(val value: String)
@JvmInline value class Email(val value: String)
data class User(val id: UserId, val email: Email, val name: String)
fun greet(id: UserId, email: Email) { ... }
greet(email = user.id, id = user.email)   // ❌ compile error now — caught immediately
```

**When to use:** identifiers/domain types reused across many call sites
(`UserId`, `Email`, `MovieId`) where mixing them up is a real risk. **Skip it**
for a `String` only ever used inside one function — not worth the ceremony.

**Limitations:** boxing still happens through generics, nullable types
(`UserId?`), and interfaces — it's not 100% free in every context. Keep value
classes as pure single-property wrappers; no business logic inside.

---

## Sealed Types for Restricted Hierarchies

Already the backbone of this codebase's MVI `Intent`/`State`/`Effect`
(see `04-mvi-pattern.md`) and outcome modeling (`BiometricAuthOutcome`:
Success/Failed/Canceled/Lockout/Error). Generalize the same instinct anywhere a
value can only be one of a known, closed set — prefer `sealed interface`/
`sealed class` over a `String status` field, a nullable + separate `Boolean`
flag, or an `Int` error code. The compiler forces exhaustive `when` handling;
a stringly-typed status can't.

---

## Extension Functions for Reuse

```kotlin
// ❌ BAD: static-style util class
object MovieMapper {
    fun toDomain(response: MovieResponse): Movie = ...
}

// ✅ GOOD: extension function — reads at the call site, discoverable via autocomplete
fun MovieResponse.toDomain(): Movie = Movie(id = id, posterUrl = "...$posterPath")
```

This is already the mapper convention in this repo (`02-moshi-models.md`,
`16-repository-pattern.md`). **Rule of thumb:** if you're about to write a
`Util`/`Helper` object with one method taking the target type as its first
param, write an extension function on that type instead.

---

## Testability Tie-In

Pure functions (no hidden/shared state) + constructor-injected dependencies
(see `16-repository-pattern.md` Fakes) = deterministic tests. Avoid top-level
`var` or singleton mutable state for anything business-logic-related — it
becomes shared state across test runs in the same JVM, causing flaky tests
that pass/fail depending on execution order.

---

**File:** `20-domain-modeling-reuse.md`
**Tokens:** 170
