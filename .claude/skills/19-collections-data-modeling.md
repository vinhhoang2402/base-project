# 19 - Collections & Data Modeling

**File Size:** ~2kb | **Load Time:** 10s | **Context:** 160 tokens

---

## Pick the Right Collection

| Need | Use | Why |
|------|-----|-----|
| Ordered, duplicates allowed, index access | `List` | default `ArrayList`, O(1) random access |
| Uniqueness / fast `in` membership check | `Set` | hash-backed `in` is O(1), not O(n) like `List.contains` |
| Key → value lookup | `Map` | O(1) get-by-key |
| Frequent add/remove at both ends (queue/stack) | `ArrayDeque` | faster than `ArrayList` for that shape |

`setOf`/`mapOf` already preserve insertion order (`LinkedHashSet`/`LinkedHashMap`
under the hood). Only reach for plain `HashSet`/`HashMap` explicitly in
performance-critical mutable code where insertion order truly doesn't matter —
it's marginally faster/lighter.

---

## Anti-pattern: nested `Map` as a membership check

```kotlin
// ❌ BAD: nested Map used only to answer "is this movie favorited by this user?"
val favorites: Map<String, Map<String, Boolean>> = mapOf(
    userId to mapOf(movieId to true)
)
val isFavorite = favorites[userId]?.get(movieId) == true

// ✅ GOOD: it's a membership check → model it as a Set of a small domain type
data class FavoriteKey(val userId: String, val movieId: String)
val favorites: Set<FavoriteKey> = setOf(FavoriteKey(userId, movieId))
val isFavorite = FavoriteKey(userId, movieId) in favorites
```

**Rule of thumb:** if the inner `Map`'s value is `Boolean`/`Unit`/always-present,
you don't need a `Map` — you need a `Set`. Reach for nested `Map` only when the
inner value carries real, varying data (e.g. `Map<UserId, Map<MovieId, Rating>>`
where `Rating` differs per pair).

---

## Sequence vs List for chained operations

```kotlin
// ❌ BAD: each step allocates a new intermediate List
movies.filter { it.isPopular }.map { it.toDomain() }.take(10)

// ✅ GOOD: lazy, single pass, no intermediate allocations — call toList() once at the end
movies.asSequence()
    .filter { it.isPopular }
    .map { it.toDomain() }
    .take(10)
    .toList()
```

Use `Sequence` when chaining 3+ operations over a collection that could be
large (paging results, DB query results); for a short one-off `map`/`filter`
on a small list, plain `List` operations are fine and more readable.

---

**File:** `19-collections-data-modeling.md`
**Tokens:** 160
