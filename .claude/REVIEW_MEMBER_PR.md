# How to Review a Team Member's PR

**Step-by-step guide for code review using Android checklist**

---

## 📋 Scenario: Member pushes feature/payments PR

### Step 1: Understand the PR (2 minutes)

```bash
# Fetch PR info
gh pr view 42

# Output:
# Title: feat(payments): add payment processing
# Author: @team-member
# Additions: +250 lines, Deletions: -10 lines
# Status: Open
```

### Step 2: Check GitHub Actions Results (1 minute)

GitHub Actions automatically runs when PR is created:

```
✅ android-review.yml results posted as comment:
   - KtLint: PASSED
   - Diff size: 260 lines (≥ 50, needs agent review)
   - Pattern scan: WARNINGS FOUND
   - Test coverage: 65% (needs improvement to 70%)

⚠️ Checklist posted:
   - ARCH-1 to ARCH-5: Check architecture layers
   - KOTLIN-1 to KOTLIN-5: Check naming conventions
   - ... (all 41 rules listed)
```

### Step 3: Pull the Branch Locally (2 minutes)

```bash
# Check out member's branch
gh pr checkout 42

# Or manually:
git fetch origin feature/payments
git checkout feature/payments
```

### Step 4: Review Diff (5-10 minutes)

**Option A: Terminal review**

```bash
# See what changed vs main
git diff main...feature/payments --stat

# Output:
#  feature/payments/data/model/PaymentResponse.kt       | 25 ++
#  feature/payments/data/repository/PaymentRepository.kt| 45 ++
#  feature/payments/presentation/PaymentFragment.kt     | 80 ++
#  feature/payments/presentation/PaymentViewModel.kt    | 50 ++
#  feature/payments/src/test/.../PaymentViewModelTest.kt| 55 ++
#  Total: 255 additions

# Walk through diff
git diff main...feature/payments | less
```

**Option B: IDE review**

```
Open PR comparison in GitHub:
https://github.com/yourrepo/pull/42/files
→ Easier to see context, but terminal is faster
```

### Step 5: Run Android Review Locally (5 minutes)

Since diff ≥ 50 lines, use agent review:

```bash
# Load the android-review checklist
cat .claude/skills/android-review/references/checklist.md

# Review the PR (or the current working diff)
/review 42
# or:  /code-review medium

# Or inline review yourself:
# Walk through each rule in checklist
```

---

## 🔍 Detailed Review Checklist (15 minutes)

Walk through each category and check the code:

### ARCH Rules (Architecture)

**ARCH-1: Layered Architecture**
```kotlin
// Check: Is code in right layer?

✅ GOOD:
  feature/payments/presentation/PaymentViewModel.kt
  feature/payments/domain/GetPaymentStatusUseCase.kt
  feature/payments/data/repository/PaymentRepository.kt

❌ BAD:
  feature/payments/PaymentService.kt (API calls in presentation)
  PaymentViewModel directly accessing API (should use repository)
```

**ARCH-2: MVI Pattern**
```kotlin
// Check: Does it follow Intent/State/Effect?

✅ GOOD:
  interface PaymentContract {
      data class State(val isProcessing: Boolean, val result: PaymentResult?)
      sealed interface Intent {
          data class ProcessPayment(val amount: Double) : Intent
      }
      sealed interface Effect {
          data class PaymentSuccess(val id: String) : Effect
      }
  }

❌ BAD:
  class PaymentViewModel {
      fun processPayment(amount: Double) { ... } // No Intent/State
  }
```

**ARCH-3: Repository Pattern**
```kotlin
// Check: Is data layer abstracted?

✅ GOOD:
  class PaymentViewModel(private val repository: PaymentRepository)
  // Uses repository interface

❌ BAD:
  class PaymentViewModel {
      private val service = Retrofit.create(PaymentService::class.java)
      // Direct API access in ViewModel
  }
```

**ARCH-4: UDF Pattern**
```kotlin
// Check: Does state flow down, events flow up?

✅ GOOD:
  // State flows down (ViewModel → UI)
  val state: StateFlow<State> = _state.asStateFlow()
  
  // Events flow up (UI → ViewModel)
  binding.btnPay.setOnClickListener {
      viewModel.handleIntent(Intent.ProcessPayment(amount))
  }

❌ BAD:
  // UI directly changes state
  viewModel.isProcessing.value = true
  
  // ViewModel calls UI methods
  fragment.showSuccess()
```

**ARCH-5: No Circular Dependencies**
```gradle
// Check: build.gradle.kts

✅ GOOD:
  // feature-payments only depends on core modules
  dependencies {
      implementation(project(":core:core-ui"))
      implementation(project(":core:core-network"))
  }

❌ BAD:
  // feature-payments depends on another feature
  dependencies {
      implementation(project(":feature:feature-auth"))
  }
```

### DATA Rules (Models)

**DATA-1: Feature-Specific Model Naming**
```kotlin
// Check: Are models named after feature?

✅ GOOD:
  data class PaymentResponse(...)  // Feature-specific
  data class PaymentEntity(...)
  data class PaymentRequest(...)

❌ BAD:
  data class Response(...)         // Generic - will collide!
  data class Entity(...)
```

**DATA-2: Moshi @JsonClass**
```kotlin
// Check: Does API model have @JsonClass?

✅ GOOD:
  @JsonClass(generateAdapter = true)
  data class PaymentResponse(...)

❌ BAD:
  data class PaymentResponse(...)  // Missing annotation
```

**DATA-3: @Json for snake_case**
```kotlin
// Check: API fields mapped correctly?

✅ GOOD:
  @Json(name = "payment_id") val paymentId: String
  @Json(name = "transaction_date") val transactionDate: String

❌ BAD:
  @Json(name = "payment_id") val payment_id: String  // Wrong case
  val paymentId: String  // Missing @Json
```

**DATA-4: Mapper Functions**
```kotlin
// Check: Is there API → Domain mapper?

✅ GOOD:
  fun PaymentResponse.toDomain() = Payment(
      id = paymentId,
      amount = amount,
      status = status.toPaymentStatus()
  )

❌ BAD:
  // Using API model directly in domain
  val payment = PaymentResponse(...)  // Wrong type
```

### TESTING Rules

**TESTING-1: ≥ 70% Coverage**
```bash
# Check test file exists and coverage

✅ GOOD:
  feature/payments/src/test/.../PaymentViewModelTest.kt
  feature/payments/src/test/.../PaymentRepositoryTest.kt
  Coverage: 75%

❌ BAD:
  No test files found
  Coverage: 40%
```

**TESTING-2: Use Fakes Not Mocks**
```kotlin
// Check: Are tests using Fakes?

✅ GOOD:
  class FakePaymentRepository : PaymentRepository {
      override suspend fun processPayment(amount: Double): PaymentResult {
          return PaymentResult.Success(id = "test-123")
      }
  }
  
  @Test
  fun testSuccess() {
      val fake = FakePaymentRepository()
      val viewModel = PaymentViewModel(fake)
  }

❌ BAD:
  val mockRepository = mockk<PaymentRepository>()
  every { mockRepository.processPayment(any()) } returns ...
  // Mocks don't exercise real logic
```

**TESTING-3: ViewModel State Testing**
```kotlin
// Check: Do tests verify state?

✅ GOOD:
  @Test
  fun `processPayment emits success state` = runTest {
      viewModel.handleIntent(Intent.ProcessPayment(100.0))
      
      assertThat(viewModel.state.value)
          .isInstanceOf(State.Success::class.java)
  }

❌ BAD:
  @Test
  fun `processPayment is called` {
      verify { viewModel.processPayment(100.0) } // Wrong - tests method call, not state
  }
```

### MEM Rules (Memory)

**MEM-1: Binding Cleanup**
```kotlin
// Check: Does Fragment clear binding?

✅ GOOD:
  override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
  }

❌ BAD:
  // Missing binding cleanup (memory leak)
```

**MEM-5: ViewModelScope**
```kotlin
// Check: Are coroutines in viewModelScope?

✅ GOOD:
  viewModelScope.launch {
      repository.processPayment()
  }

❌ BAD:
  GlobalScope.launch {  // Memory leak!
      repository.processPayment()
  }
```

### QUALITY Rules

**QUALITY-1: KtLint Compliance**
```
Check: Did GitHub Actions KtLint pass?

✅ GOOD: KtLint: PASSED in workflow

❌ BAD: KtLint: FAILED
  member needs to run:
  ./gradlew ktlintFormat
```

**QUALITY-2: File Size**
```
Check: Are files under 500 lines?

✅ GOOD:
  PaymentViewModel.kt: 80 lines
  PaymentFragment.kt: 120 lines
  PaymentRepository.kt: 95 lines

❌ BAD:
  PaymentFragment.kt: 600 lines (split it!)
```

---

## 📝 Posting Review Findings

### Example 1: Has Blockers

```markdown
## Android Review - Feature Payments PR

### 🔴 Blockers (Must Fix Before Merge)

**ARCH-3** | feature/payments/data/repository/PaymentRepository.kt:1-50
❌ Repository directly calls PaymentService.create() instead of using data source abstraction
📝 **Required fix:**
```kotlin
// WRONG:
class PaymentRepository {
    val service = PaymentService.create()
}

// RIGHT:
interface RemotePaymentDataSource {
    suspend fun processPayment(amount: Double): PaymentResponse
}

class PaymentRepository(
    private val remote: RemotePaymentDataSource,
    private val local: LocalPaymentDataSource
) { ... }
```

**DATA-1** | feature/payments/data/model/Response.kt:1
❌ Generic model name "Response" will collide with other modules
📝 **Required fix:** Rename to `PaymentResponse.kt` and update all references

**MEM-5** | feature/payments/presentation/PaymentViewModel.kt:45
❌ Using GlobalScope.launch instead of viewModelScope (memory leak)
📝 **Required fix:** Change `GlobalScope.launch` to `viewModelScope.launch`

### 🟡 Should Fix (Recommended)

**DATA-2** | feature/payments/data/model/PaymentResponse.kt:8
⚠️ Missing @JsonClass(generateAdapter = true) on API model
📝 **Recommended fix:**
```kotlin
@JsonClass(generateAdapter = true)
data class PaymentResponse(...)
```

**TESTING-1** | feature/payments/src/test/.../PaymentViewModelTest.kt:1
⚠️ Test coverage is 65%, target is 70%
📝 **Recommended fix:** Add tests for error cases in PaymentViewModel

### 💡 Nits

**KOTLIN-1** | feature/payments/presentation/PaymentFragment.kt:20
💭 Variable naming: `pm` should be `paymentManager` (clarity)

---

## ✅ Approve Comment (No Issues)

If all checks pass:

```markdown
## Android Review - Approved ✅

**All checks passed:**
- ✅ Architecture layers correct (ARCH-1 to ARCH-5)
- ✅ Kotlin conventions followed (KOTLIN-1 to KOTLIN-5)
- ✅ Testing at 72% coverage (TESTING-1 to TESTING-5)
- ✅ Memory safe (MEM-1 to MEM-5)
- ✅ Models properly named and mapped (DATA-1 to DATA-5)
- ✅ All coroutines in viewModelScope (ASYNC-1 to ASYNC-5)
- ✅ Code quality excellent (QUALITY-1 to QUALITY-4)
- ✅ Performance optimized (PERF-1 to PERF-3)
- ✅ Security best practices (SEC-1 to SEC-4)

Ready to merge! 🚀
```

---

## 🔄 Iterating on Feedback

### Member Gets Review, What Next?

**Member sees 3 Blockers:**
```
1. Fix ARCH-3 (Repository abstraction)
2. Fix DATA-1 (Rename Response → PaymentResponse)
3. Fix MEM-5 (GlobalScope → viewModelScope)
```

**Member's workflow:**

```bash
# Pull latest feedback comment
gh pr view 42 --comments

# Make fixes locally
# Fix ARCH-3: Create DataSource interfaces
# Fix DATA-1: Rename file + update imports
# Fix MEM-5: Replace GlobalScope

# Commit & push
./gradlew ktlintFormat
git add .
git commit -m "fix: address review feedback"
git push origin feature/payments

# GitHub Actions re-runs automatically
# Posts new android-review comment
# Check if all Blockers resolved
```

**If Blockers still not fixed:**
```
Post follow-up comment:
"Still seeing ARCH-3 issue - RemotePaymentDataSource 
interface not found. Did the changes push successfully?"
```

**After all Blockers fixed:**
```
Post approval comment:
"Blockers resolved ✅ Looks good to merge!"

Then approve PR in GitHub
```

---

## ⏱️ Time Breakdown for Reviewing

```
- Understand PR (check title, description): 2 min
- View GitHub Actions results: 1 min
- Check out branch locally: 2 min
- Review diff manually/IDE: 5-10 min
- Run android-review locally: 5 min
- Walk checklist in detail: 10-15 min
- Write findings comment: 5 min

Total: 30-40 minutes per PR
```

**Speed tips:**
- Start with GitHub Actions results (catches most issues)
- Focus on Blockers first (can skip Nits if short on time)
- Use android-review agent for large diffs (≥ 50 lines)
- Trust pre-commit hooks for formatting issues

---

## 📊 Review Statistics

Track how you're doing as a reviewer:

```
✅ Blockers found per PR: 2-3 (aim to catch critical issues)
✅ Should Fix items: 3-5 (help with best practices)
✅ False positives: < 1 (don't cry wolf on Nits)
✅ Approval speed: < 40 min (including iterations)

✅ Member fix time: 15-30 min (for 3 Blockers)
```

---

## 🎯 Perfect Review Workflow

```
1. member: push feature/payments
   ↓
2. CI: github-actions/android-review runs automatically
   ↓
3. you: read GH comment, check findings
   ↓
4. you: run android-review locally for deep dive
   ↓
5. you: post detailed findings (Blockers > Should Fix > Nits)
   ↓
6. member: fix issues, commit, push
   ↓
7. CI: github-actions/android-review re-runs
   ↓
8. you: verify all Blockers fixed
   ↓
9. you: approve PR
   ↓
10. member: merge when ready
```

---

**Master this process and code quality stays high! 🏆**
