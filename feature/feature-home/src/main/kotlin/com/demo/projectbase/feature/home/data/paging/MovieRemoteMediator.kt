package com.demo.projectbase.feature.home.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.room.withTransaction
import com.demo.projectbase.core.network.paging.BaseRemoteMediator
import com.demo.projectbase.core.network.paging.RemotePage
import com.demo.projectbase.feature.home.data.source.local.HomeDatabase
import com.demo.projectbase.feature.home.data.source.local.entity.MovieEntity
import com.demo.projectbase.feature.home.data.source.local.entity.RemoteKeyEntity
import com.demo.projectbase.feature.home.data.source.local.entity.toEntity
import com.demo.projectbase.feature.home.data.source.remote.MovieRemoteDataSource
import com.demo.projectbase.feature.home.domain.model.Movie

private const val POPULAR_MOVIES_KEY = "popular_movies"

@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val remoteDataSource: MovieRemoteDataSource,
    private val database: HomeDatabase,
    onError: (Throwable) -> Unit,
) : BaseRemoteMediator<Movie, MovieEntity>(onError) {
    private val movieDao = database.movieDao()
    private val remoteKeyDao = database.remoteKeyDao()

    override suspend fun localCount() = movieDao.count()

    override suspend fun getNextPage() = remoteKeyDao.getKey(POPULAR_MOVIES_KEY)?.nextPage

    override suspend fun fetch(page: Int): Result<RemotePage<Movie>> =
        remoteDataSource.getPopularMovies(page).map { (movies, totalPages) ->
            RemotePage(movies, totalPages)
        }

    override suspend fun withTransaction(block: suspend () -> Unit) = database.withTransaction(block)

    override suspend fun clearAll() {
        movieDao.deleteAll()
        remoteKeyDao.deleteAll()
    }

    override suspend fun saveNextPage(page: Int?) {
        remoteKeyDao.save(RemoteKeyEntity(POPULAR_MOVIES_KEY, page))
    }

    override suspend fun saveItems(items: List<Movie>) {
        movieDao.insertAll(items.map { it.toEntity() })
    }
}
