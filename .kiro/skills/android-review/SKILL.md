---
name: android-review
description: Domain-specific Android/Kotlin code review against ProjectBase enterprise standards (architecture, Kotlin conventions, testing, memory, data models, security). Use when reviewing Android/Kotlin code changes for pattern compliance; not a replacement for general or security review.
---

# Android Review Skill

**Domain-specific review for Android/Kotlin best-practice compliance**

---

## Purpose

Reviews code changes against ProjectBase Android/Kotlin standards derived from:
- NOW in Android (Google reference app)
- Google Android Architecture Guidelines
- Kotlin Official Conventions
- ProjectBase 16 enterprise skills

**NOT a replacement for:**
- General code review (/code-review)
- Security review (/security-review)
- Business logic review (human review)

---

## Procedure

### 1. Determine Scope

Priority order:

```bash
# 1. Uncommitted changes
git diff HEAD

# 2. Branch changes (if no uncommitted)
git diff main...HEAD

# 3. PR changes (if PR number provided)
gh pr diff 42
```

### 2. Size Diff & Choose Review Type

```
< 50 lines → INLINE REVIEW (read checklist, walk diff)
≥ 50 lines → AGENT REVIEW (android-reviewer agent)
```

**Threshold exceptions:**
- Multi-file/module changes: use agent even if < 50 lines
- Single-file changes: inline even if > 50 lines

### 3. Inline Review Process

1. Read `references/checklist.md` (all rules)
2. Walk diff against each applicable rule
3. Group findings:
   - **Blocker** (must fix before merge)
   - **Should Fix** (strongly recommended)
   - **Nit** (nice to have)
4. Each finding: `RULE-ID | file:line | description | fix`

### 4. Agent Review Process

```bash
# Dispatch to android-reviewer agent
@claude-code review-android --scope="git diff main...HEAD"
```

Agent will:
1. Fetch full diff using scope command
2. Read checklist.md
3. Walk diff against all rules
4. Return grouped findings

### 5. Relay Findings

**Format:**

```
## Blockers
- ARCH-5 | feature/home/data/model/MovieResponse.kt:12 | Generic model name "Response" causes collision | Rename to "MovieResponse"
- TESTING-3 | feature/home/src/test/.../MovieViewModelTest.kt:45 | Missing error state test | Add test case for State.Error

## Should Fix
- PERF-2 | feature/home/presentation/HomeFragment.kt:28 | Adapter not cleared in onDestroyView | Add: adapter = null

## Nits
- KOTLIN-1 | feature/home/data/repository/MovieRepository.kt:8 | Unnecessary nullable type | Change val x: String? to val x: String
```

---

## Checklist Reference

See `references/checklist.md` for:
- All rule IDs (ARCH-*, KOTLIN-*, TESTING-*, etc)
- Rule descriptions
- Severity levels
- When rule applies

---

## Integration

**Pre-commit:**
```bash
git commit  # Triggers pre-commit hook
# → Runs /code-review medium
# → Runs /security-review
# → Runs android-review (if changes qualify)
```

**GitHub Actions (PR):**
```yaml
- uses: android-review-action
  with:
    checklist: ~/.kiro/skills/android-review/references/checklist.md
```

---

## When to Use

✅ **Use android-review when:**
- Reviewing Android/Kotlin code changes
- Need pattern compliance check
- Want checklist-based review

❌ **Don't use for:**
- Business logic review (human review)
- Security audit (/security-review)
- General code quality (use /code-review)

---

**Always run alongside general review, not instead of it.**
