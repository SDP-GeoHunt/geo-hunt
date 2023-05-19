package com.github.geohunt.app.ui.utils.pagination

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * Represents a finite scrolling paged list which fetches element on demand, using the given
 * [fetcher].
 *
 * The elements are fetched only once, and do not react to database modifications. For a reactive
 * paged list, see [DynamicPagedList].
 *
 * Additionally, the paged list also supports pre-fetching to optimize the user experience. A key
 * assumption for the pre-fetcher is that elements are fetched linearly.
 *
 * @see [FinitePagedList]
 */
class StaticPagedList<T>(
    size: Int,
    val fetcher: suspend (Int) -> T,
    coroutineScope: CoroutineScope,
    prefetchSize: Int = 0
) : AbstractFinitePagedList<T>(size, prefetchSize, coroutineScope) {
    override suspend fun fetchAsync(index: Int, onComplete: () -> Unit) {
        states[index].value = fetcher(index)
        onComplete()
    }
}

/**
 * Creates a [StaticPagedList] from a [List].
 *
 * The given list must provided constant-time random access to be efficient.
 *
 * @param list The list used to fetch values from.
 * @param fetcher The list fetcher.
 * @param coroutineScope The coroutine scope in which fetches are made.
 * @param prefetchSize The number of elements prefetched.
 */
fun <T, U> StaticPagedList(
    list: List<T>,
    fetcher: suspend (T) -> U,
    coroutineScope: CoroutineScope,
    prefetchSize: Int = 0
): StaticPagedList<U> {
    return StaticPagedList(
        size = list.size,
        fetcher = { i -> fetcher(list[i]) },
        coroutineScope = coroutineScope,
        prefetchSize = prefetchSize
    )
}