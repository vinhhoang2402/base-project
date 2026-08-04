---
inclusion: manual
---

---
description: Self-review your own working diff against the ProjectBase Android checklist (ARCH/DATA/MEM/SEC/QUALITY + BUG-1..14)
argument-hint: (no args — reviews your current diff)
allowed-tools: Read, Bash(git*), Bash(./gradlew*), Glob, Grep
---

You are running `/self-review`: the developer reviews THEIR OWN pending change
against the ProjectBase Android review checklist before committing. This is the
same checklist the Reviewer role uses, applied to your working tree — it is the
android-specific complement to the generic `/code-review` (which does NOT load
this checklist).

# STEP 1 — Gather the diff

Determine scope in this priority order:
1. Uncommitted changes: `git diff HEAD` — if non-empty, that's the scope.
2. Otherwise branch changes: `git diff main-xml...HEAD` (fall back to
   `git diff main...HEAD` if `main-xml` doesn't exist).
If both are empty, tell the user there's nothing to review and stop.

Also run `git diff HEAD --stat` (or the branch equivalent) first to list the
touched files.

# STEP 2 — Load the checklist

Read `~/.kiro/skills/android-review/references/checklist.md` in full. It holds
all rule groups: ARCH-*, KOTLIN-*, TESTING-*, MEM-*, DATA-*, ASYNC-*, QUALITY-*,
PERF-*, SEC-*, and COMMON BUGS BUG-1..14.

# STEP 3 — Review

Walk every hunk of the diff against each applicable rule. For each hunk also
Read the enclosing function — bugs on unchanged lines of a touched function are
in scope. Pay special attention to the COMMON BUGS (BUG-1..14): main-thread I/O,
swallowed exceptions, transient state loss, double-tap, one-time-effect
duplicate navigation (BUG-9), wrong lifecycle-scope collection (BUG-10),
binding-after-onDestroyView (BUG-11), floating-point money (BUG-12), DiffUtil
misuse (BUG-13), locale/timezone formatting (BUG-14).

# STEP 4 — Report

Report ONE consolidated set of findings grouped as
**🔴 Blockers / 🟡 Should Fix / 💡 Nits**, each line as
`RULE-ID | file:line | vấn đề | cách sửa`. Prefer a table for readability, with
the issue stated briefly in English and Vietnamese.

If the diff is clean, say clearly "Approved ✅ — an toàn để commit".

Keep replies concise; the user prefers Vietnamese.
