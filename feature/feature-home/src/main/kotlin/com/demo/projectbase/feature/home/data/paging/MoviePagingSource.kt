package com.demo.projectbase.feature.home.data.paging

import com.demo.projectbase.core.network.paging.BasePagingSource
import com.demo.projectbase.feature.home.data.source.remote.MovieRemoteDataSource
import com.demo.projectbase.feature.home.domain.model.Movie

class MoviePagingSource(
    private val remoteDataSource: MovieRemoteDataSource,
    onError: (Throwable) -> Unit = {},
) : BasePagingSource<Movie>(onError = onError) {
    override suspend fun fetch(
        page: Int,
        pageSize: Int,
    ): List<Movie> = remoteDataSource.getPopularMovies(page).getOrThrow().movies
}
