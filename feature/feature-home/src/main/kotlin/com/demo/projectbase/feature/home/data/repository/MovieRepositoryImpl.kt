package com.demo.projectbase.feature.home.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.demo.projectbase.feature.home.data.paging.MoviePagingSource
import com.demo.projectbase.feature.home.data.paging.MovieRemoteMediator
import com.demo.projectbase.feature.home.data.source.local.HomeDatabase
import com.demo.projectbase.feature.home.data.source.local.entity.toDomain
import com.demo.projectbase.feature.home.data.source.remote.MovieRemoteDataSource
import com.demo.projectbase.feature.home.domain.model.Movie
import com.demo.projectbase.feature.home.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieRepositoryImpl(
    private val remoteDataSource: MovieRemoteDataSource,
    private val database: HomeDatabase,
) : MovieRepository {
    override fun getPopularMoviesPager(onError: (Throwable) -> Unit): Flow<PagingData<Movie>> = remoteOnlyPager(onError)
    // swap to remoteOnlyPager(onError) to skip Room caching

    // Offline-first: Room cache + RemoteMediator
    @OptIn(ExperimentalPagingApi::class)
    private fun offlineFirstPager(onError: (Throwable) -> Unit): Flow<PagingData<Movie>> =
        Pager(
            config = offlineFirstPagingConfig(),
            remoteMediator = MovieRemoteMediator(remoteDataSource, database, onError),
            pagingSourceFactory = { database.movieDao().pagingSource() },
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }

    // Remote-only: no Room, no offline support
    private fun remoteOnlyPager(onError: (Throwable) -> Unit): Flow<PagingData<Movie>> =
        Pager(
            config = remoteOnlyPagingConfig(),
            pagingSourceFactory = { MoviePagingSource(remoteDataSource, onError) },
        ).flow

    // initialLoadSize = pageSize: load đúng 1 page mỗi lần, không tự trigger APPEND
    private fun remoteOnlyPagingConfig() =
        PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
            prefetchDistance = 4,
            enablePlaceholders = false,
        )

    // initialLoadSize = pageSize * 2: bù cho Room PagingSource invalidation không rớt items
    private fun offlineFirstPagingConfig() =
        PagingConfig(
            pageSize = 20,
            initialLoadSize = 40,
            prefetchDistance = 4,
            enablePlaceholders = false,
        )
}
