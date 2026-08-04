# 11 - Coroutines & StateFlow Best Practices

**File Size:** ~2kb | **Load Time:** 10s | **Context:** 160 tokens

---

## Structured Concurrency

```kotlin
class MovieViewModel : ViewModel() {
    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()
    
    // ✅ GOOD: Launch in viewModelScope
    fun loadMovies() {
        viewModelScope.launch {
            try {
                val movies = repository.getMovies()
                _state.value = State.Success(movies)
            } catch (e: Exception) {
                _state.value = State.Error(e.message)
            }
        }
    }
    
    // ❌ BAD: Launch in GlobalScope (memory leak!)
    fun badLoadMovies() {
        GlobalScope.launch {
            val movies = repository.getMovies()
        }
    }
}
```

---

## StateFlow Best Practices

```kotlin
// Transform data layer Flow to StateFlow
class MovieViewModel(repository: MovieRepository) : ViewModel() {
    val movies: StateFlow<List<Movie>> = repository.getMoviesFlow()
        .map { it.toDomainModels() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),  // Stop upstream when no collectors
            initialValue = emptyList()
        )
}

// Collect with lifecycle awareness
class MovieFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    renderState(state)
                }
            }
        }
    }
}
```

---

## Error Handling

```kotlin
// Use try-catch in coroutines
viewModelScope.launch {
    try {
        val result = repository.fetchData()
        _state.value = State.Success(result)
    } catch (e: IOException) {
        _state.value = State.Error("Network error")
    } catch (e: HttpException) {
        _state.value = State.Error("API error: ${e.code}")
    }
}

// Use Flow.catch() operator
repository.getDataFlow()
    .catch { e ->
        _state.value = State.Error(e.message)
    }
    .collect { data ->
        _state.value = State.Success(data)
    }
```

---

**File:** `11-coroutines-flows.md`  
**Tokens:** 160
