# 16 - Repository Pattern & Data Layer Abstraction

**File Size:** ~3kb | **Load Time:** 15s | **Context:** 200 tokens

---

## Why Repository Pattern?

```
❌ OLD (Direct Access):
ViewModel → Service → API
Problem: ViewModel knows about API, hard to test, no caching

✅ NEW (Repository):
ViewModel → Repository → (Service + Database)
Benefits: Single source of truth, testable, cacheable, flexible
```

---

## Complete Repository Architecture

```kotlin
// 1. INTERFACES (Data sources abstraction)
interface RemoteMovieDataSource {
    suspend fun getPopularMovies(page: Int): MovieResponse
}

interface LocalMovieDataSource {
    suspend fun getMovies(): List<MovieEntity>
    suspend fun saveMovies(movies: List<MovieEntity>)
    fun getMoviesFlow(): Flow<List<MovieEntity>>
}

// 2. IMPLEMENTATIONS (Retrofit + Room)
class RemoteMovieDataSourceImpl(
    private val service: MovieService
) : RemoteMovieDataSource {
    override suspend fun getPopularMovies(page: Int): MovieResponse {
        return service.getPopularMovies(page = page)
    }
}

class LocalMovieDataSourceImpl(
    private val dao: MovieDao
) : LocalMovieDataSource {
    override suspend fun getMovies(): List<MovieEntity> {
        return dao.getAllMovies()
    }
    
    override suspend fun saveMovies(movies: List<MovieEntity>) {
        dao.insertMovies(movies)
    }
    
    override fun getMoviesFlow(): Flow<List<MovieEntity>> {
        return dao.getAllMoviesFlow()
    }
}

// 3. REPOSITORY (Orchestrates sources)
interface MovieRepository {
    suspend fun getPopularMovies(page: Int): List<Movie>
    fun getPopularMoviesFlow(page: Int): Flow<List<Movie>>
}

class MovieRepositoryImpl(
    private val remoteDataSource: RemoteMovieDataSource,
    private val localDataSource: LocalMovieDataSource
) : MovieRepository {
    
    // Strategy: Cache from local first, sync from remote
    override suspend fun getPopularMovies(page: Int): List<Movie> {
        return try {
            // Fetch from remote
            val remote = remoteDataSource.getPopularMovies(page)
            val entities = remote.results.map { it.toEntity() }
            
            // Save to local
            localDataSource.saveMovies(entities)
            
            // Return as domain models
            entities.map { it.toDomain() }
        } catch (e: Exception) {
            // Fallback to local cache
            localDataSource.getMovies().map { it.toDomain() }
        }
    }
    
    // Stream from local (always up-to-date)
    override fun getPopularMoviesFlow(page: Int): Flow<List<Movie>> {
        return localDataSource.getMoviesFlow()
            .map { entities -> entities.map { it.toDomain() } }
    }
}
```

---

## Dependency Injection Setup (Hilt)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object MovieModule {
    
    @Singleton
    @Provides
    fun provideRemoteDataSource(
        service: MovieService
    ): RemoteMovieDataSource = RemoteMovieDataSourceImpl(service)
    
    @Singleton
    @Provides
    fun provideLocalDataSource(
        dao: MovieDao
    ): LocalMovieDataSource = LocalMovieDataSourceImpl(dao)
    
    @Singleton
    @Provides
    fun provideMovieRepository(
        remote: RemoteMovieDataSource,
        local: LocalMovieDataSource
    ): MovieRepository = MovieRepositoryImpl(remote, local)
}
```

---

## Testing with Fakes

```kotlin
// Fake implementation for testing
class FakeMovieRepository : MovieRepository {
    private var shouldThrow = false
    private val movies = mutableListOf<Movie>()
    
    fun setShouldThrow(value: Boolean) {
        shouldThrow = value
    }
    
    override suspend fun getPopularMovies(page: Int): List<Movie> {
        if (shouldThrow) throw IOException("Network error")
        return movies
    }
    
    override fun getPopularMoviesFlow(page: Int): Flow<List<Movie>> {
        return flowOf(movies)
    }
}

// Test
@Test
fun `loadMovies handles error gracefully` = runTest {
    val fakeRepo = FakeMovieRepository().apply {
        setShouldThrow(true)
    }
    val viewModel = MovieViewModel(fakeRepo)
    
    viewModel.handleIntent(Intent.LoadMovies)
    
    assertThat(viewModel.state.value).isInstanceOf(State.Error::class.java)
}
```

---

## Benefits Summary

| Benefit | How It Works |
|---------|-------------|
| **Single Source of Truth** | Local DB is authoritative |
| **Offline Support** | Serve from cache when offline |
| **Testable** | Inject fakes, no network calls |
| **Flexible** | Swap data sources easily |
| **Scalable** | Add new sources without changing ViewModel |
| **Cacheable** | Control cache strategy centrally |

---

**File:** `16-repository-pattern.md`  
**Tokens:** 200  
**Difficulty:** Advanced
