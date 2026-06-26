package com.demo.projectbase.core.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState

fun <T> List<T>.handleLocalLoadMore(pageCount: Int, offset: Int): List<T> = when {
    size <= pageCount && offset > 0 -> emptyList()
    size <= pageCount -> this
    offset + pageCount > size -> if (size - offset > 0) subList(offset, size) else emptyList()
    else -> subList(offset, offset + pageCount)
}

// For backends that don't support pagination — fetches all data once, paginates in memory.
// Key = offset (Int). On refresh (new PagingSource instance), fetchAll() is called again.
abstract class BaseLocalPagingSource<Value : Any>(
    private val onError: (Throwable) -> Unit = {},
) : PagingSource<Int, Value>() {

    private var cachedData: List<Value>? = null

    override fun getRefreshKey(state: PagingState<Int, Value>): Int? =
        state.anchorPosition?.let { anchor ->
            val page = state.closestPageToPosition(anchor)
            page?.prevKey?.plus(state.config.pageSize)
                ?: page?.nextKey?.minus(state.config.pageSize)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Value> {
        val offset = params.key ?: 0
        return try {
            val data = cachedData ?: fetchAll().also { cachedData = it }
            val page = data.handleLocalLoadMore(params.loadSize, offset)
            val nextOffset = offset + params.loadSize
            LoadResult.Page(
                data = page,
                prevKey = if (offset == 0) null else maxOf(0, offset - params.loadSize),
                nextKey = if (page.isEmpty() || nextOffset >= data.size) null else nextOffset,
            )
        } catch (e: Exception) {
            onError(e)
            LoadResult.Error(e)
        }
    }

    protected abstract suspend fun fetchAll(): List<Value>
}
