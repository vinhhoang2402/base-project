# 17 - Baseline Profiles (Performance Optimization)

**File Size:** ~2kb | **Load Time:** 10s | **Context:** 150 tokens

---

## What Are Baseline Profiles?

```
Problem: App cold start is slow
- First time user opens app: 1-2 seconds

Solution: Baseline Profiles
- Tell compiler which code to pre-compile
- Result: ~30% faster startup ⚡
```

---

## Setup Baseline Profiles

**Step 1: Create macrobenchmark module**

```gradle
// build.gradle.kts (app)
plugins {
    id("com.android.application")
    id("androidx.baselineprofile") version "1.2.0"
}

baselineProfile {
    enable = true
}
```

**Step 2: Create baseline profile file**

```
app/src/main/baseline-prof.txt
```

**Step 3: Add rules (example)**

```
# ActivityName (critical paths)
com/demo/projectbase/MainActivity
com/demo/projectbase/feature/home/presentation/HomeFragment

# Classes to precompile
com/demo/projectbase/core/ui/base/BaseViewModel
com/demo/projectbase/feature/home/presentation/HomeViewModel

# Methods to inline
com/demo/projectbase/feature/home/data/MovieRepository#getPopularMovies(I)
com/demo/projectbase/feature/home/domain/GetMoviesUseCase#invoke()
```

---

## Macrobenchmark Test

```kotlin
@RunWith(AndroidTestRunner::class)
class StartupBenchmarks {
    
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()
    
    @Test
    fun startup() = baselineProfileRule.measureRepeated(
        packageName = "com.demo.projectbase",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD
    ) {
        // Navigate to home
        pressHome()
        startActivityAndWait()
    }
}
```

---

## What to Profile

```
🔴 CRITICAL (must profile):
  - MainActivity launch
  - Home screen render
  - Navigation transitions
  - First API call response handling

🟡 IMPORTANT (profile if slow):
  - Database queries
  - ViewModel initialization
  - List loading

🟢 OPTIONAL (low priority):
  - Settings screen
  - Rare features
  - Helper functions
```

---

## Performance Impact

```
Before Baseline Profiles:
Cold start: 1500ms
Warm start: 800ms

After Baseline Profiles:
Cold start: 1050ms (30% improvement!)
Warm start: 600ms (25% improvement!)
```

---

## Best Practices

```
✅ DO:
  - Profile real user paths
  - Test on low-end devices
  - Include all Activities/Fragments in critical path
  - Update profiles when code changes significantly

❌ DON'T:
  - Profile rarely-used features
  - Over-profile (diminishing returns)
  - Forget to test on actual devices
  - Ignore battery/thermal impact
```

---

## CI/CD Integration

```yaml
# .github/workflows/baseline-profiles.yml
name: Generate Baseline Profiles

on: [push]

jobs:
  baseline:
    runs-on: macos-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - run: ./gradlew generateBaselineProfile
      - uses: actions/upload-artifact@v3
        with:
          name: baseline-profiles
          path: app/src/main/baseline-prof.txt
```

---

**File:** `17-baseline-profiles.md`  
**Tokens:** 150  
**Impact:** ~30% startup improvement
