---
inclusion: auto
name: common-bugs-checklist
description: Condensed scan list of recurring runtime bug patterns (main-thread I/O, silent exception swallowing, transient state loss, storage field misuse, double-tap submission, locale casing, notifyDataSetChanged, unbounded retry) to catch during code review or verification. Use during /code-review or /verify, or before finishing any change.
---

# 21 - Common Bugs Checklist (catch early in /code-review + /verify)

**File Size:** ~1kb | **Load Time:** 8s | **Context:** 140 tokens

---

Full rule details + real examples: `skills/android-review/references/checklist.md`
(section `BUG-*`). This is the condensed scan list — run through it during
`/code-review` and `/verify`, not just architecture/style checks.

| ID | Check | Red flag to grep/scan for |
|----|-------|---------------------------|
| BUG-1 | Main-thread blocking I/O outside a coroutine | storage/DB/network call in `handleIntent` not wrapped in `viewModelScope.launch` |
| BUG-2 | Silent exception swallowing | empty `catch (e: Exception) {}`, or a storage/network call with NO try/catch or `Result` at all |
| BUG-3 | Transient state lost on next recompute | a flag flipped by an error branch, then silently recomputed elsewhere from a source that doesn't know about it |
| BUG-4 | Storage field repurposed for a different meaning | a value passed to a setter/field whose name doesn't match what's actually stored |
| BUG-5 | Double-tap / duplicate submission | submit button's `isEnabled` not tied to `state.isLoading` |
| BUG-6 | Locale-sensitive string casing | `.toUpperCase()`/`.toLowerCase()` with no `Locale` arg |
| BUG-7 | `notifyDataSetChanged()` instead of DiffUtil | raw `notifyDataSetChanged(` in an adapter |
| BUG-8 | Unbounded retry/loop | `while`/recursive retry with no counter or timeout |

**Rule of thumb:** BUG-1 through BUG-4 came from a real bug found in this
codebase's own review history — they're not hypothetical. Treat them as
Blocker/Should Fix, not Nit.

---

**File:** `21-common-bugs-checklist.md`
**Tokens:** 140
