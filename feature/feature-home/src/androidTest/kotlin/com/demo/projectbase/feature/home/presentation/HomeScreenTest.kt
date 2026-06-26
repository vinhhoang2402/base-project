package com.demo.projectbase.feature.home.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit4.runners.AndroidJUnit4
import com.demo.projectbase.core.ui.theme.ProjectBaseTheme
import com.demo.projectbase.feature.home.domain.model.Movie
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun setContent(
        state: HomeContract.State = HomeContract.State(),
        movies: List<Movie> = emptyList(),
        onIntent: (HomeContract.Intent) -> Unit = {},
    ) {
        composeRule.setContent {
            val lazyMovies = flowOf(PagingData.from(movies)).collectAsLazyPagingItems()
            ProjectBaseTheme {
                HomeScreenContent(
                    state = state,
                    movies = lazyMovies,
                    onIntent = onIntent,
                )
            }
        }
    }

    // ── TopBar ──────────────────────────────────────────────────────────────

    @Test
    fun topBar_showsAppTitle() {
        setContent()
        composeRule.onNodeWithText("Movies").assertIsDisplayed()
    }

    @Test
    fun topBar_showsLoginButtonWhenNotLoggedIn() {
        setContent(state = HomeContract.State(isLoggedIn = false))
        composeRule.onNodeWithTag(HomeTestTags.LOGIN_BUTTON).assertIsDisplayed()
    }

    @Test
    fun topBar_hidesLogoutButtonWhenNotLoggedIn() {
        setContent(state = HomeContract.State(isLoggedIn = false))
        composeRule.onNodeWithTag(HomeTestTags.LOGOUT_BUTTON).assertDoesNotExist()
    }

    @Test
    fun topBar_showsLogoutButtonWhenLoggedIn() {
        setContent(state = HomeContract.State(isLoggedIn = true))
        composeRule.onNodeWithTag(HomeTestTags.LOGOUT_BUTTON).assertIsDisplayed()
    }

    @Test
    fun topBar_hidesLoginButtonWhenLoggedIn() {
        setContent(state = HomeContract.State(isLoggedIn = true))
        composeRule.onNodeWithTag(HomeTestTags.LOGIN_BUTTON).assertDoesNotExist()
    }

    @Test
    fun topBar_loginButtonDispatchesLoginIntent() {
        val intents = mutableListOf<HomeContract.Intent>()
        setContent(
            state = HomeContract.State(isLoggedIn = false),
            onIntent = { intents.add(it) },
        )
        composeRule.onNodeWithTag(HomeTestTags.LOGIN_BUTTON).performClick()
        assertTrue(intents.contains(HomeContract.Intent.Login))
    }

    @Test
    fun topBar_logoutButtonDispatchesLogoutIntent() {
        val intents = mutableListOf<HomeContract.Intent>()
        setContent(
            state = HomeContract.State(isLoggedIn = true),
            onIntent = { intents.add(it) },
        )
        composeRule.onNodeWithTag(HomeTestTags.LOGOUT_BUTTON).performClick()
        assertTrue(intents.contains(HomeContract.Intent.Logout))
    }

    // ── Empty state ──────────────────────────────────────────────────────────

    @Test
    fun body_showsEmptyStateWhenNoMovies() {
        setContent(movies = emptyList())
        composeRule.onNodeWithTag(HomeTestTags.EMPTY_STATE).assertIsDisplayed()
        composeRule.onNodeWithText("No movies found").assertIsDisplayed()
    }

    @Test
    fun body_hidesMovieGridWhenEmpty() {
        setContent(movies = emptyList())
        composeRule.onNodeWithTag(HomeTestTags.MOVIE_GRID).assertDoesNotExist()
    }

    // ── Movie grid ───────────────────────────────────────────────────────────

    @Test
    fun body_showsMovieGridWhenMoviesLoaded() {
        setContent(movies = fakeMovies)
        composeRule.onNodeWithTag(HomeTestTags.MOVIE_GRID).assertIsDisplayed()
    }

    @Test
    fun body_hidesEmptyStateWhenMoviesLoaded() {
        setContent(movies = fakeMovies)
        composeRule.onNodeWithTag(HomeTestTags.EMPTY_STATE).assertDoesNotExist()
    }

    @Test
    fun body_showsMovieTitles() {
        setContent(movies = fakeMovies)
        composeRule.onNodeWithText("Movie One").assertIsDisplayed()
        composeRule.onNodeWithText("Movie Two").assertIsDisplayed()
    }

    @Test
    fun body_showsMovieRatings() {
        setContent(movies = fakeMovies)
        composeRule.onNodeWithText("7.5").assertIsDisplayed()
        composeRule.onNodeWithText("8.2").assertIsDisplayed()
    }
}

private val fakeMovies = listOf(
    Movie(id = 1, title = "Movie One", overview = "Overview 1", posterUrl = null, voteAverage = 7.5, releaseDate = "2024-01-01"),
    Movie(id = 2, title = "Movie Two", overview = "Overview 2", posterUrl = null, voteAverage = 8.2, releaseDate = "2024-02-01"),
)
