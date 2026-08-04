# 05 - KtLint Rules & Formatting

**File Size:** ~1kb | **Load Time:** 5s | **Context:** 100 tokens

---

## Configuration (.editorconfig)

```ini
[*.kt]
max_line_length = 120
indent_size = 4
indent_style = space

ktlint_standard = enable
ktlint_standard_no-wildcard-imports = enable
ktlint_standard_function-expression-body = enable
ktlint_standard_parameter-list-wrapping = enable
```

---

## Before Commit

```bash
# Format all files
./gradlew ktlintFormat

# Check without formatting
./gradlew ktlint
```

---

## Common Rules

```
❌ BAD:
  import com.example.*
  fun load() = viewModel.state.collect { ... }
  fun setName(name: String): Unit { this.name = name }

✅ GOOD:
  import com.example.Movie
  import com.example.Actor
  fun load() {
      viewModel.state.collect { ... }
  }
  fun setName(name: String) {
      this.name = name
  }
```

---

**File:** `05-ktlint-rules.md`  
**Tokens:** 100
