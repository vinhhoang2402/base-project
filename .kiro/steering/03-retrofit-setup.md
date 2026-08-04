---
inclusion: auto
name: retrofit-setup
description: Retrofit + Moshi network client configuration conventions. Use when wiring up a new API service or Retrofit instance.
---

# 03 - Retrofit Setup

**File Size:** ~1kb | **Load Time:** 5s | **Context:** 110 tokens

---

## Configuration

```kotlin
// Single Moshi instance
val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// Retrofit client
val retrofitClient = Retrofit.Builder()
    .baseUrl("https://api.themoviedb.org/")
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
    .build()

// Inject via Koin
single { moshi }
single { retrofitClient }
```

---

## Service Interface

```kotlin
interface MovieService {
    @GET("3/movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): MovieResponse
}
```

---

## Error Handling

```kotlin
try {
    val result = service.getPopularMovies(API_KEY)
    // Handle success
} catch (e: IOException) {
    // Network error
} catch (e: HttpException) {
    // API error (4xx, 5xx)
}
```

---

**File:** `03-retrofit-setup.md`  
**Tokens:** 110
