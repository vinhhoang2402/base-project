# 04 - MVI Pattern (Architecture)

**File Size:** ~2kb | **Load Time:** 10s | **Context:** 180 tokens

---

## Structure

```kotlin
// Contract defines Intent/State/Effect
interface MovieContract {
    data class State(
        val isLoading: Boolean = false,
        val movies: List<Movie> = emptyList(),
        val error: String? = null
    )
    
    sealed interface Intent {
        data object LoadMovies : Intent
        data class OpenDetails(val movieId: Int) : Intent
    }
    
    sealed interface Effect {
        data class NavigateToDetails(val movieId: Int) : Effect
        data class ShowError(val message: String) : Effect
    }
}
```

---

## ViewModel

```kotlin
class MovieViewModel(
    private val repository: MovieRepository
) : BaseViewModel<Intent, State, Effect>() {

    override fun handleIntent(intent: Intent) {
        when (intent) {
            Intent.LoadMovies -> loadMovies()
            is Intent.OpenDetails -> emit(Effect.NavigateToDetails(intent.movieId))
        }
    }
    
    private fun loadMovies() {
        setState { copy(isLoading = true) }
        // Load + setState when done
    }
}
```

---

## Fragment

```kotlin
class MovieFragment : BaseMviFragment<...>(...) {
    override fun renderState(state: State) {
        binding.progressBar.isVisible = state.isLoading
        adapter.submitList(state.movies)
    }
    
    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.NavigateToDetails -> navigate(effect.movieId)
            is Effect.ShowError -> showError(effect.message)
        }
    }
}
```

---

**File:** `04-mvi-pattern.md`  
**Tokens:** 180
