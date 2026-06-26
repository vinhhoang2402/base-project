package com.demo.projectbase.core.network.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator

data class RemotePage<T>(
    val items: List<T>,
    val totalPages: Int = Int.MAX_VALUE,
)

@OptIn(ExperimentalPagingApi::class)
abstract class BaseRemoteMediator<Value : Any, Entity : Any>(
    private val onError: (Throwable) -> Unit = {},
) : RemoteMediator<Int, Entity>() {

    override suspend fun initialize(): InitializeAction =
        if (localCount() == 0 || getNextPage() == null)
            InitializeAction.LAUNCH_INITIAL_REFRESH
        else InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Entity>,
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> getNextPage()
                ?: return MediatorResult.Success(endOfPaginationReached = true)
        }

        return fetch(page).fold(
            onSuccess = { (items, totalPages) ->
                val endOfPagination = page >= totalPages
                withTransaction {
                    if (loadType == LoadType.REFRESH) clearAll()
                    saveNextPage(if (endOfPagination) null else page + 1)
                    saveItems(items)
                }
                MediatorResult.Success(endOfPaginationReached = endOfPagination)
            },
            onFailure = { error ->
                onError(error)
                MediatorResult.Error(error)
            },
        )
    }

    protected abstract suspend fun localCount(): Int
    protected abstract suspend fun getNextPage(): Int?
    protected abstract suspend fun fetch(page: Int): Result<RemotePage<Value>>
    protected abstract suspend fun withTransaction(block: suspend () -> Unit)
    protected abstract suspend fun clearAll()
    protected abstract suspend fun saveNextPage(page: Int?)
    protected abstract suspend fun saveItems(items: List<Value>)
}
