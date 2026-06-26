package com.demo.projectbase.feature.home.domain.repository

import androidx.paging.PagingData
import com.demo.projectbase.feature.home.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getPopularMoviesPager(onError: (Throwable) -> Unit): Flow<PagingData<Movie>>
}
