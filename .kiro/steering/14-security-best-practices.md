---
inclusion: auto
name: security-best-practices
description: Data protection, secure storage (EncryptedSharedPreferences), and null-safety security practices. Use when handling tokens, secrets, or sensitive data.
---

# 14 - Security & Data Protection

**File Size:** ~2kb | **Load Time:** 10s | **Context:** 140 tokens

---

## Network Security

```kotlin
// ✅ GOOD: HTTPS only
val retrofit = Retrofit.Builder()
    .baseUrl("https://api.themoviedb.org/")  // HTTPS required
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

// Add security interceptor
val okHttpClient = OkHttpClient.Builder()
    .certificatePinner(CertificatePinner.Builder()
        .add("api.example.com", "sha256/...")
        .build())
    .build()
```

---

## Sensitive Data Protection

```kotlin
// ✅ GOOD: Never log sensitive data
class MovieService {
    suspend fun getMovies(): MovieResponse {
        // ❌ BAD: Logging sensitive data
        Log.d("API", "Response: $response")
        
        // ✅ GOOD: Log only what's necessary
        Log.d("MovieService", "Loaded ${movies.size} movies")
    }
}

// Encrypt sensitive data in storage
val encrypted = EncryptedSharedPreferences.create(context, ...)
encrypted.edit().putString("api_key", apiKey).apply()
```

---

## Permissions & Privacy

```kotlin
// ✅ GOOD: Request minimum permissions
// AndroidManifest.xml
<uses-permission android:name="android.permission.INTERNET" />

// Never store sensitive data
// ❌ BAD
val apiKey = BuildConfig.API_KEY  // Exposed in build

// ✅ GOOD
// Use server-side authentication
val token = getTokenFromServer()
```

---

## Null Safety for Security

```kotlin
// ✅ GOOD: Null-safe by default
val email: String = user.email  // Always has value
val phone: String? = user.phone // Only null if defined

// Reduces NullPointerException crashes
val message = email ?: "No email"  // Safe fallback
```

---

**File:** `14-security-best-practices.md`  
**Tokens:** 140
