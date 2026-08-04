# 13 - Memory Management & Lifecycle

**File Size:** ~2kb | **Load Time:** 10s | **Context:** 140 tokens

---

## ViewHolder Memory Safety

```kotlin
// ✅ GOOD: Clear binding in onDestroyView
class MovieFragment : Fragment() {
    private var _binding: FragmentMovieBinding? = null
    private val binding get() = _binding!!
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMovieBinding.bind(view)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // CRITICAL: Prevent memory leak
    }
}

// ✅ GOOD: AutoClear for adapters
private var adapter: MovieAdapter? by autoClear()
```

---

## ViewModel Memory Safety

```kotlin
// ✅ GOOD: Never hold references to Context/Activity
class MovieViewModel : ViewModel() {
    private val context: Context? = null  // ❌ NEVER
    
    // Use ApplicationContext if absolutely needed
    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MovieViewModel(app.applicationContext) as T
        }
    }
}

// ✅ GOOD: Use lifecycle-aware coroutines
viewModelScope.launch {  // Auto-cancels when ViewModel cleared
    repository.getDataFlow().collect { data ->
        _state.value = State.Success(data)
    }
}
```

---

## Lifecycle-Aware Collection

```kotlin
// ✅ GOOD: Lifecycle-aware in Compose
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

**File:** `13-memory-lifecycle.md`  
**Tokens:** 140
