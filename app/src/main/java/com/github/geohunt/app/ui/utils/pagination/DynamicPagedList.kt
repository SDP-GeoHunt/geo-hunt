package com.github.geohunt.app.ui.utils.pagination

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectIndexed

/**
 * Represents a dynamic [PagedList] which listens to Firebase database changes and updates its
 * state accordingly.
 */
class DynamicPagedList<T>(
    size: Int,
    val fetcher: (Int) -> Flow<T>,
    prefetchSize: Int = 0,
    coroutineScope: CoroutineScope
) : AbstractFinitePagedList<T>(size, prefetchSize, coroutineScope) {
    override suspend fun fetchAsync(index: Int, onComplete: () -> Unit) {
        fetcher(index).collectIndexed { i, value ->
            states[index].value = value
            if (i == 0) {
                onComplete()
            }
        }
    }
}

/**
 * Creates a [DynamicPagedList] from a [List].
 *
 * The given list must provided constant-time random access to be efficient.
 *
 * @param list The list used to fetch values from.
 * @param fetcher The list [Flow] fetcher.
 * @param coroutineScope The coroutine scope in which fetches are made.
 * @param prefetchSize The number of elements prefetched.
 */
fun <T, U> DynamicPagedList(
    list: List<T>,
    fetcher: (T) -> Flow<U>,
    coroutineScope: CoroutineScope,
    prefetchSize: Int = 0
): DynamicPagedList<U> {
    return DynamicPagedList(
        size = list.size,
        fetcher = { i -> fetcher(list[i]) },
        coroutineScope = coroutineScope,
        prefetchSize = prefetchSize
    )
}