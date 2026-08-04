# 07 - Feature-Level Builds (Fast Development)

**File Size:** ~1kb | **Load Time:** 5s | **Context:** 90 tokens

---

## Why Feature Builds Matter

| Build Type | Time | Token Cost | Use Case |
|-----------|------|-----------|----------|
| Full app | 2-3 min | ❌ Slow | Production release only |
| Feature | 30-45s | ✅ Fast | Development loop |
| Unit test | 15-30s | ✅ Very fast | Code review checks |

---

## Quick Commands

```bash
# Build single feature (FAST - use this!)
./scripts/build-feature.sh home

# Test single feature
./gradlew :feature:feature-home:testDebugUnitTest

# Full app (rare - only for release)
./gradlew build
```

---

## How It Works

```gradle
// feature/feature-home/build.gradle.kts
plugins {
    id("android.library")      // ← NOT "android.application"
}

dependencies {
    implementation(project(":core:core-ui"))
    // Only imports this feature needs
}
```

Each feature is library → compiles independently → 10x faster!

---

## DevEx Hack

```bash
# Add to .zshrc/.bashrc
alias build-home='./scripts/build-feature.sh home'
alias build-auth='./scripts/build-feature.sh auth'

# Then: just type "build-home" ✨
```

---

**File:** `07-feature-build.md`  
**Tokens:** 90
