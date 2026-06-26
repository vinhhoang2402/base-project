package com.demo.projectbase.feature.home.domain.usecase

import androidx.paging.PagingData
import com.demo.projectbase.feature.home.domain.model.Movie
import com.demo.projectbase.feature.home.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetPopularMoviesUseCase(private val repository: MovieRepository) {
    operator fun invoke(onError: (Throwable) -> Unit): Flow<PagingData<Movie>> =
        repository.getPopularMoviesPager(onError)
}
