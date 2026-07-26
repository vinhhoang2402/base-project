package com.demo.projectbase.core.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState

abstract class BasePagingSource<Value : Any>(
    private val startPage: Int = 1,
    private val onError: (Throwable) -> Unit = {},
) : PagingSource<Int, Value>() {
    override fun getRefreshKey(state: PagingState<Int, Value>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Value> {
        val page = params.key ?: startPage
        return try {
            val data = fetch(page, params.loadSize)
            LoadResult.Page(
                data = data,
                prevKey = if (page == startPage) null else page - 1,
                nextKey = if (data.isEmpty()) null else page + 1,
            )
        } catch (e: Exception) {
            onError(e)
            LoadResult.Error(e)
        }
    }

    protected abstract suspend fun fetch(
        page: Int,
        pageSize: Int,
    ): List<Value>
}
