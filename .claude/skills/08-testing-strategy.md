# 08 - Testing Strategy (> 70% Coverage)

**File Size:** ~2kb | **Load Time:** 10s | **Context:** 150 tokens

---

## Test Structure

```
feature-home/src/test/kotlin/
├── presentation/
│   └── MovieViewModelTest.kt      (ViewModel + State)
├── domain/
│   └── GetMoviesUseCaseTest.kt    (Business logic)
└── data/
    └── MovieRepositoryTest.kt     (Repository + fakes)
```

---

## ViewModel Test

```kotlin
@Test
fun `loadMovies emits success state` = runTest {
    val viewModel = MovieViewModel(mockRepository)
    
    viewModel.handleIntent(Intent.LoadMovies)
    
    assertThat(viewModel.state.value).isInstanceOf(State.Success::class.java)
    assertThat(viewModel.state.value.movies).isNotEmpty()
}
```

---

## Repository Test

```kotlin
@Test
fun `getMovies returns domain models` = runTest {
    val fakeService = FakeMovieService()
    val repository = MovieRepository(fakeService)
    
    val result = repository.getMovies()
    
    assertThat(result).containsExactly(expected)
}
```

---

## Coverage Targets

```
- ViewModels: 40% (critical path)
- Repositories: 20% (with fakes)
- UseCases: 10% (business logic)
- Total: > 70%

NOT required: Getters/setters, trivial code
```

---

**File:** `08-testing-strategy.md`  
**Tokens:** 150
