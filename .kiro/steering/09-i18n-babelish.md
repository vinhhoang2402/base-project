---
inclusion: auto
name: i18n-babelish
description: String localization workflow: Babelish CSV pipeline (translations.csv -> generate-strings.sh) covers ONLY the app/ module. Feature-module strings.xml files are English-only and hand-maintained, out of Babelish scope. Use when adding or reviewing any Android string resource.
---

# 09 - i18n Babelish Workflow

**File Size:** ~1kb | **Load Time:** 5s | **Context:** 120 tokens

---

## ⚠️ Scope: `app/` module ONLY

The Babelish/CSV pipeline (`translations.csv` → `./scripts/generate-strings.sh`)
only generates strings under `app/src/main/res/`. It does **not** touch
feature-module resources (`feature/*/src/main/res/values/strings.xml`).

**Feature-module strings are English-only, hand-maintained directly in
`strings.xml`** — do NOT add feature-module string keys to `translations.csv`,
they will not be picked up. Adding Vietnamese/French translations for a feature
module currently requires manually creating `feature/<name>/src/main/res/values-vi/`,
`values-fr/` and duplicating keys by hand (no tooling support yet).

---

## CSV Format (translations.csv) — app/ module only

```csv
key,en,vi,fr,de
app_name,My App,Ứng dụng,Mon Application,Meine App
hello,Hello,Xin chào,Bonjour,Hallo
error_network,Network Error,Lỗi Mạng,Erreur Réseau,Netzwerkfehler
```

---

## Generate Strings (app/ module only)

```bash
./scripts/generate-strings.sh
```

Output:
```
app/src/main/res/
├── values/strings.xml (English)
├── values-vi/strings.xml (Vietnamese)
├── values-fr/strings.xml (French)
└── values-de/strings.xml (German)
```

---

## Use in Code

```kotlin
// Fragment/Activity
binding.tvTitle.text = getString(R.string.app_name)
binding.tvError.text = getString(R.string.error_network)

// ViewModel (context required)
val message = context.getString(R.string.hello)
```

---

## Workflow

**For `app/` module strings:**
1. Add translation to `translations.csv`
2. Run `./scripts/generate-strings.sh`
3. Use `getString(R.string.key)` in code
4. Commit both CSV + strings.xml

**For feature-module strings** (e.g. `feature/feature-auth`, `feature/feature-home`):
1. Add the English string directly to `feature/<name>/src/main/res/values/strings.xml`
2. Use `getString(R.string.key)` in code
3. No CSV step — this module is not covered by Babelish yet
4. If localization is genuinely needed, flag it and confirm scope with the team
   rather than silently inventing a parallel CSV pipeline for feature modules

---

**File:** `09-i18n-babelish.md`
**Tokens:** 120
