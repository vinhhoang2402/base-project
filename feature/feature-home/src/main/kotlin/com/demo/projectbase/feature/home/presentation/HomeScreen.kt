package com.demo.projectbase.feature.home.presentation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.demo.projectbase.core.ui.base.BaseEffect
import com.demo.projectbase.feature.home.domain.model.Movie
import org.koin.androidx.compose.koinViewModel

object HomeTestTags {
    const val LOADING = "home_loading"
    const val MOVIE_GRID = "home_movie_grid"
    const val EMPTY_STATE = "home_empty_state"
    const val LOGIN_BUTTON = "home_login_button"
    const val LOGOUT_BUTTON = "home_logout_button"
}

@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onSessionExpired: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val movies = viewModel.moviesPager.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.baseEffect.collect { effect ->
            when (effect) {
                is BaseEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                BaseEffect.SessionExpired -> onSessionExpired()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HomeContract.Effect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    HomeScreenContent(
        state = state,
        movies = movies,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun HomeScreenContent(
    state: HomeContract.State,
    movies: LazyPagingItems<Movie>,
    onIntent: (HomeContract.Intent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        topBar = { HomeTopBar(state = state, onIntent = onIntent) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        HomeBody(
            movies = movies,
            modifier = Modifier.padding(padding),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    state: HomeContract.State,
    onIntent: (HomeContract.Intent) -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = "Movies",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        actions = {
            if (state.isLoggedIn) {
                IconButton(
                    onClick = { onIntent(HomeContract.Intent.Logout) },
                    modifier = Modifier.testTag(HomeTestTags.LOGOUT_BUTTON),
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Logout",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            } else {
                TextButton(
                    onClick = { onIntent(HomeContract.Intent.Login) },
                    modifier = Modifier.testTag(HomeTestTags.LOGIN_BUTTON),
                ) {
                    Text(
                        text = "Login",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}

@Composable
private fun HomeBody(
    movies: LazyPagingItems<Movie>,
    modifier: Modifier = Modifier,
) {
    val refreshState = movies.loadState.refresh
    val isRefreshing = refreshState is LoadState.Loading
    val hasError = refreshState is LoadState.Error && movies.itemCount == 0
    val isEmpty = refreshState is LoadState.NotLoading
        && movies.loadState.append.endOfPaginationReached
        && movies.itemCount == 0
    val showFullScreenLoading = movies.itemCount == 0 && !hasError && !isEmpty
    val isMediatorRefreshing = movies.loadState.mediator?.refresh is LoadState.Loading

    when {
        showFullScreenLoading -> {
            ShimmerMovieGrid(
                modifier = modifier.testTag(HomeTestTags.LOADING),
            )
        }
        isEmpty || hasError -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (hasError) "Failed to load movies" else "No movies found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag(HomeTestTags.EMPTY_STATE),
                    )
                    if (hasError) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { movies.retry() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
        else -> {
            if (isMediatorRefreshing) {
                LinearProgressIndicator(modifier = modifier.fillMaxWidth())
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier
                    .fillMaxSize()
                    .testTag(HomeTestTags.MOVIE_GRID),
            ) {
                items(
                    count = movies.itemCount,
                    key = movies.itemKey { it.id },
                ) { index ->
                    movies[index]?.let { MovieCard(movie = it, modifier = Modifier.animateItem()) }
                }
                when (val appendState = movies.loadState.append) {
                    is LoadState.Loading -> item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                    is LoadState.Error -> item(span = { GridItemSpan(maxLineSpan) }) {
                        AppendErrorItem(
                            message = appendState.error.message ?: "Load failed",
                            onRetry = { movies.retry() },
                        )
                    }
                    is LoadState.NotLoading -> Unit
                }
            }
        }
    }
}

@Composable
private fun ShimmerMovieGrid(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = -300f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_translate",
    )
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceVariant,
        ),
        start = Offset(translateX, 0f),
        end = Offset(translateX + 300f, 300f),
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize(),
        userScrollEnabled = false,
    ) {
        items(10) {
            ShimmerMovieCard(brush = shimmerBrush)
        }
    }
}

@Composable
private fun ShimmerMovieCard(brush: Brush) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .background(brush),
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(14.dp)
                        .background(brush, RoundedCornerShape(4.dp)),
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(10.dp)
                        .background(brush, RoundedCornerShape(4.dp)),
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun AppendErrorItem(message: String, onRetry: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun MovieCard(movie: Movie, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", movie.voteAverage),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
