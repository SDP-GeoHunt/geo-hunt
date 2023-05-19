package com.github.geohunt.app.ui.utils.pagination

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Base implementation class for extending [FinitePagedList].
 */
abstract class AbstractFinitePagedList<T>(
    val size: Int,
    val prefetchSize: Int = 0,
    private val coroutineScope: CoroutineScope
) : FinitePagedList<T> {
    protected val states = List<MutableStateFlow<T?>>(size) { MutableStateFlow(null) }
    private val currentFetches: MutableSet<Int> = mutableSetOf()

    override fun size(): Int = size
    override fun getState(index: Int): StateFlow<T?> {
        requireCorrectIndex(index)
        return states[index]
    }

    override fun isFetched(index: Int): Boolean {
        requireCorrectIndex(index)
        return states[index].value != null
    }

    override fun isFetching(index: Int): Boolean {
        requireCorrectIndex(index)
        return currentFetches.contains(index)
    }

    /**
     * Asynchronously fetches the element at the given index.
     */
    protected abstract suspend fun fetchAsync(index: Int, onComplete: () -> Unit)

    override fun fetch(index: Int) {
        requireCorrectIndex(index)

        // Fetch the current element
        currentFetches.add(index)
        coroutineScope.launch {
            fetchAsync(index, onComplete = {
                currentFetches.remove(index)
            })
        }

        // Pre-fetch the next elements
        for (i in (index + 1)..(index + prefetchSize)) {
            fetchIfNecessary(i)
        }
    }
}