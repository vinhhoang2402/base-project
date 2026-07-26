package com.demo.projectbase.core.network

import kotlinx.coroutines.CancellationException

suspend fun <T> safeSuspend(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
