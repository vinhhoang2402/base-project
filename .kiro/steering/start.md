---
inclusion: manual
---

---
description: Start a work session — pick a role (Developer/Reviewer) and follow the matching flow
argument-hint: (no args — interactive)
allowed-tools: AskUserQuestion, Read, Write, Edit, Bash(git*), Bash(gh*), Bash(./gradlew*), Glob, Grep
---

You are running the `/start` session bootstrap. Do NOT skip the questions — always
use the AskUserQuestion tool for each choice below. Follow this exactly.

# STEP 1 — Ask the role

Use AskUserQuestion:
- Question: "Bạn đang ở role nào cho phiên làm việc này?"
- Options:
  1. **Developer** — "Tôi viết code (fix bug hoặc làm task mới)"
  2. **Reviewer** — "Tôi review code của người khác theo commit/PR"

Branch on the answer.

---

# ROLE A — DEVELOPER

## A1. Fix bug or new task?
Use AskUserQuestion:
- Question: "Bạn muốn làm gì?"
- Options:
  1. **Fix bug** — "Sửa lỗi có sẵn"
  2. **New task** — "Làm task/feature mới"

## A2. Collect the info
Ask the user (plain chat message, wait for their reply) for:
- **ID**: mã ticket (vd `JIRA-456` cho task, `BUG-123` cho bug)
- **Title**: tiêu đề ngắn
- **Description**: mô tả chi tiết
- **Acceptance criteria** (task) hoặc **Steps to reproduce + Expected/Actual** (bug)

## A3. Write the info to a file
- New task  → write to `.kiro/tasks/<ID>.md`
- Fix bug   → write to `.kiro/bugs/<ID>.md`

Use this template (fill from A2):

```markdown
# <ID> — <Title>

- **Type:** task | bug
- **Status:** in-progress
- **Branch:** feature/<ID>   (or fix/<ID> for a bug)
- **Created:** <today's date>

## Description
<description>

## Acceptance Criteria  (task)   /   Steps to Reproduce  (bug)
- ...

## Notes / Progress
- ...
```

After writing, confirm the file path back to the user and suggest the branch:
`git checkout main && git pull && git checkout -b feature/<ID>` (or `fix/<ID>`).

## A3.5. Implement the task (auto)
This is where the actual work happens — do NOT jump straight to the commit checklist.

1. Make sure you are on the task branch `feature/<ID>` (or `fix/<ID>`); if not,
   create it as suggested above before touching any code.
2. Explore the relevant module(s) to understand the existing architecture and
   conventions before writing anything. Read the matching skills under
   `~/.kiro/steering/` (MVI, repository pattern, error handling, security, etc.).
3. Implement the change to satisfy every Acceptance Criterion (task) or to fix
   the reported bug and cover the Steps to Reproduce. Match the surrounding code
   style; add/adjust tests where the module already has them.
4. As you go, keep `.kiro/tasks/<ID>.md` (or `bugs/<ID>.md`) updated — tick off
   criteria and jot progress under **Notes / Progress**.
5. When the implementation is complete, tell the user what you changed (files +
   summary) and that they can type **ready** to see the before-commit workflow.

## A4. Wait for "ready", then SHOW the workflow (do NOT auto-run)
Tell the user: "Khi mình code xong, gõ **ready** để hiện workflow trước khi commit."

When the user says `ready`, print this checklist for them to run manually
(the user chose to run commands themselves — do not execute them automatically):

```
DEVELOPER — Before Commit
  0. (one-time per clone) git config core.hooksPath .githooks
  1. ./gradlew ktlintFormat        # format
  2. /code-review medium           # generic: correctness + cleanup (add --fix to auto-apply)
  3. /self-review                  # android checklist on your own diff (ARCH/MEM/SEC + BUG-1..14)
  4. LinearB check                 # see criteria below — before /security-review
  5. /security-review              # security
  6. /verify                       # verify build & behavior
  7. git add . && git commit -m "feat(<ID>): ..."   (fix(<ID>): ... for a bug)
     ⚠️ 2 REAL git hooks fire here (.githooks/pre-commit + .githooks/commit-msg):
        - diff > 200 changed lines        → blocked, split commits or --no-verify
        - message not "type(scope): desc" → blocked, fix message or --no-verify
  8. git push -u origin feature/<ID>

LinearB check (step 4) — pass when:
  ✅ Diff size < 200 lines            (git diff --cached --numstat; hook re-checks at commit)
  ✅ Coverage > 70%                   (./gradlew testDebugUnitTest, read the report)
  ✅ No recent revert commits         (git log --oneline -10 | grep -i revert)
  ✅ Commit message will be "type(scope): description" (hook re-checks at commit)
  Diff size and message format are hard-blocked by git hooks regardless — this
  step exists to catch coverage/revert issues *before* wasting time on
  /security-review and /verify. See 06-linearb-metrics.md for why coverage/revert
  aren't hooks (too slow / too fuzzy to hard-block on).
```

Also remind them: the file `.kiro/tasks/<ID>.md` (or bugs/) is there to update
Status → done and jot progress notes.

---

# ROLE B — REVIEWER

## B1. Review by commit hashes or PR?
Use AskUserQuestion:
- Question: "Bạn muốn review theo gì?"
- Options:
  1. **Commit hashes** — "Dán một hoặc nhiều mã commit để review (gộp chung)"
  2. **PR number** — "Review nguyên một Pull Request trên GitHub"

## B2a. If Commit hashes
- Ask the user: "Dán các commit hash muốn review, cách nhau bởi dấu cách hoặc
  xuống dòng (vd `3f48174 a1b2c3d e4f5678`):" and wait.
- When they paste the hashes, treat them as ONE combined change set and run the
  review flow:
  1. For each hash, `git show <hash> --stat` to see the scope; then read the
     combined diff. Prefer a single `git diff <oldest>^..<newest>` when the hashes
     are contiguous, otherwise `git show <hash>` per hash — the goal is to review
     all listed commits together as one unit.
  2. Read the checklist: `~/.kiro/skills/android-review/references/checklist.md`.
  3. Review the combined diff against the checklist and report ONE consolidated
     set of findings grouped as **🔴 Blockers / 🟡 Should Fix / 💡 Nits**, each as
     `RULE-ID | file:line | vấn đề | cách sửa`.
  4. Nếu diff sạch, nói rõ "Approved ✅".

## B2b. If PR number
- Ask the user: "Nhập PR number (vd `42`):" and wait.
- When they give the number, run `/review <PR#>` (invoke the review skill) and,
  if helpful, `gh pr view <PR#>` for context, then report findings in the same
  grouped format as B2a.

---

# RULES
- Always ask via AskUserQuestion for role/branch choices; never assume.
- Never invent commands — only use the real ones listed in `~/.kiro/steering/usage-guide.md`.
- Keep replies concise; the user prefers Vietnamese.
