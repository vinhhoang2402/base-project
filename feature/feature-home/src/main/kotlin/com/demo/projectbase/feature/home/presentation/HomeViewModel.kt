package com.demo.projectbase.feature.home.presentation

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.demo.projectbase.core.network.SecurePreferencesManager
import com.demo.projectbase.core.ui.base.BaseViewModel
import com.demo.projectbase.feature.home.domain.model.Movie
import com.demo.projectbase.feature.home.domain.usecase.GetPopularMoviesUseCase
import com.demo.projectbase.feature.home.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.Flow

class HomeViewModel(
    getPopularMovies: GetPopularMoviesUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val securePrefs: SecurePreferencesManager,
) : BaseViewModel<HomeContract.Intent, HomeContract.State, HomeContract.Effect>(HomeContract.State()) {
    val moviesPager: Flow<PagingData<Movie>> =
        getPopularMovies(
            onError = { handleApiResult<Unit>(Result.failure(it)) {} },
        ).cachedIn(viewModelScope)

    init {
        updateState { copy(isLoggedIn = securePrefs.getAccessToken() != null) }
    }

    override fun handleIntent(intent: HomeContract.Intent) {
        when (intent) {
            HomeContract.Intent.Logout -> logout()
            HomeContract.Intent.Login -> emitEffect(HomeContract.Effect.NavigateToLogin)
        }
    }

    private fun logout() {
        logoutUseCase()
        updateState { copy(isLoggedIn = false) }
    }
}
