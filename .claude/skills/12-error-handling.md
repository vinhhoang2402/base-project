# 12 - Error Handling Strategies

**File Size:** ~1kb | **Load Time:** 5s | **Context:** 100 tokens

---

## ViewModel Error Handling

```kotlin
sealed class State {
    object Loading : State()
    data class Success(val data: List<Movie>) : State()
    data class Error(val message: String) : State()
}

// Emit error state instead of crashing
class MovieViewModel(repository: MovieRepository) : ViewModel() {
    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()
    
    fun loadMovies() {
        viewModelScope.launch {
            try {
                val movies = repository.getMovies()
                _state.value = State.Success(movies)
            } catch (e: Exception) {
                _state.value = State.Error(e.message ?: "Unknown error")
            }
        }
    }
}
```

---

## Data Layer Error Handling

```kotlin
// Repository handles network errors
class MovieRepository(private val service: MovieService) {
    suspend fun getMovies(): List<Movie> {
        return try {
            service.getPopularMovies().results.map { it.toDomain() }
        } catch (e: HttpException) {
            throw ApiException("HTTP ${e.code}: ${e.message}")
        } catch (e: IOException) {
            throw NetworkException("Network error: ${e.message}")
        }
    }
}
```

---

## Exception Hierarchy

```kotlin
// ✅ GOOD: Specific exceptions
sealed class AppException(message: String) : Exception(message)
class NetworkException(message: String) : AppException(message)
class ApiException(message: String) : AppException(message)
class DataException(message: String) : AppException(message)

// ❌ BAD: Generic exceptions
throw Exception("Something went wrong")
```

---

**File:** `12-error-handling.md`  
**Tokens:** 100
