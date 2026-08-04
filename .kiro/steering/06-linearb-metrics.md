---
inclusion: auto
name: linearb-metrics
description: LinearB check workflow step (diff size, coverage, revert history, commit message format) plus the 2 real enforced git hooks: .githooks/pre-commit (commit size < 200 lines) and .githooks/commit-msg (type(scope): description format). Use before committing, or when asked about LinearB/commit size/commit message.
---

# 06 - LinearB Metrics & Best Practices

**File Size:** ~3kb | **Load Time:** 12s | **Context:** 230 tokens

---

## ⚙️ Enforced Now: LinearB Check (workflow step 3/4, before /security-review)

**Not reference-only anymore.** Run as an explicit step in the Before-Commit
workflow (see `commands/start.md` / `USAGE_GUIDE.md`), right after
`/code-review` and before `/security-review`. Pass when:

| Criterion | How to check | Hard-blocked by a git hook? |
|-----------|---------------|------------------------------|
| Diff size < 200 lines | `git diff --cached --numstat` | ✅ `.githooks/pre-commit` |
| Commit message = `type(scope): description` | eyeball it, or just commit — hook checks | ✅ `.githooks/commit-msg` |
| Coverage > 70% | `./gradlew testDebugUnitTest`, read the report | ❌ too slow to run on every commit |
| No recent revert commits (rework < 10%) | `git log --oneline -10 \| grep -i revert` | ❌ needs judgment, not just grep |

Two of the four are **real git hooks** — they fire automatically at `git commit`
regardless of whether you remembered to run the manual step:

**`.githooks/pre-commit`** — blocks (`exit 1`) any commit whose staged diff
exceeds **200 changed lines** (insertions + deletions).

**`.githooks/commit-msg`** — blocks (`exit 1`) any commit message whose first
line doesn't match `type(scope): description`
(`type` ∈ feat/fix/chore/refactor/test/docs/style/perf/build/ci/revert,
`scope` optional).

**One-time setup per clone** (hooks path isn't versioned by git, must be run once):
```bash
git config core.hooksPath .githooks
```

**When the size gate blocks:**
```
🔴 LinearB gate: staged commit is 282 lines, over the 200-line target.
Split into smaller, logical commits instead, e.g.:
  git reset
  git add <files for change 1> && git commit -m 'feat(ID): part 1'
  git add <files for change 2> && git commit -m 'feat(ID): part 2'
```

**When the message gate blocks:**
```
🔴 LinearB gate: commit message doesn't match required format.
Got:      "added some stuff"
Expected: type(scope): description
```

**Escape hatch for both (use sparingly):** `git commit --no-verify`

**Why coverage/revert-check aren't git hooks too:** running the full test suite
on every commit is too slow for a tight edit-commit loop, and "was this a
revert" needs human judgment about the last 10 commits, not a regex — both stay
a manual/agent-run step (LinearB check) instead of a hard block.

The remaining metrics below (cycle time, commit frequency, review speed) need
PR/deploy history, not diff content, so they stay reference targets only.

---

## What LinearB Measures

| Metric | Target | Enforced? | How to Improve |
|--------|--------|-----------|-----------------|
| **Commit Size** | < 200 lines | ✅ `.githooks/pre-commit` (hard block) | Split into logical commits |
| **Commit Message Format** | `type(scope): desc` | ✅ `.githooks/commit-msg` (hard block) | Follow the format |
| **Test Coverage** | > 70% | 🟡 LinearB check step (manual/agent-run) | Unit + integration tests |
| **Rework %** | < 10% | 🟡 LinearB check step (manual/agent-run) | Tests pass before commit |
| **Cycle Time** | < 5 days | ❌ reference only | Small commits, quick reviews |
| **Commit Frequency** | 1-3/day | ❌ reference only | Logical commits (not mega) |
| **File Complexity** | < 500 lines | ❌ reference only | Split oversized files |
| **Review Speed** | < 24h | ❌ reference only | Clear code, /code-review |

---

## LinearB Red Flags (DON'T DO THIS)

### ❌ Vibe Coding
```
Pushing AI-generated code without:
- Tests passing
- /verify run
- Code review done
= BREAKS YOUR SCORE
```

**Fix:** Always run before commit:
```bash
/code-review medium --fix
/verify
./gradlew ktlintFormat
```

---

## Cycle Time: The Most Important Metric

**Definition:** Time from first commit → production deployment

**LinearB Target:** < 5 days

**Your Workflow for < 5 days:**
```
Day 1: Code + Test + Push (4h)
Day 2: Review + Merge (24h max)
Day 3-5: QA + Deploy (2-3 days)
= ~3-4 days average
```

---

## Quick Checklist

```
✅ DO:
- Commits < 200 lines           (now enforced — see above)
- Reviews < 24h
- /verify before merge
- Tests > 70%
- Deploy < 5 days from commit

❌ DON'T:
- Mega commits (500+ lines)     (blocked by pre-commit hook)
- Slow reviews (10+ days)
- Ship untested code
- Skip /code-review
- Ignore LinearB red flags
```

---

**File:** `06-linearb-metrics.md`  
**Tokens:** 230
