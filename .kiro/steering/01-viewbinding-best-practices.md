---
inclusion: auto
name: viewbinding-best-practices
description: ViewBinding memory management and binding cleanup in Fragments/Activities. Use when creating or reviewing Fragments/Activities that use ViewBinding, or hunting a Fragment memory leak.
---

# 01 - ViewBinding Best Practices

**File Size:** ~1kb | **Load Time:** 5s | **Context:** 100 tokens

---

## The Pattern

```kotlin
abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // ⚠️ CRITICAL: Clear binding!
    }
}
```

---

## Golden Rules

| ✅ DO | ❌ DON'T |
|------|---------|
| Clear `_binding = null` in onDestroyView | Keep binding after onDestroyView |
| Use safe call `binding?.` if nullable | Use non-null reference to destroyed view |
| Access binding in setupViews/setupListeners | Access binding in background threads |
| Create binding once in onCreateView | Re-inflate binding multiple times |

---

## AutoClear Pattern (For Adapters, Dialogs)

```kotlin
// Instead of: private val adapter = Adapter()
// Use: 
private var adapter: MovieAdapter? by autoClear()

// Automatically clears on onDestroyView
// No memory leaks!
```

---

**File:** `01-viewbinding-best-practices.md`  
**Tokens:** 100
