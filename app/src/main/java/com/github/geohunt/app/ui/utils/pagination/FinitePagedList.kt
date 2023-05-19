package com.github.geohunt.app.ui.utils.pagination

import kotlinx.coroutines.flow.StateFlow

/**
 * Represents a paged list with a finite size.
 */
interface FinitePagedList<T>: PagedList<T> {
    /**
     * Returns the size of the paged list.
     */
    fun size(): Int

    override fun isCorrectIndex(index: Int): Boolean = index in 0 until size()

    companion object {
        fun <T> empty(): FinitePagedList<T> = object : FinitePagedList<T> {
            override fun size(): Int = 0

            override fun getState(index: Int): StateFlow<T?> {
                throw UnsupportedOperationException()
            }

            override fun fetch(index: Int) {}
            override fun isFetching(index: Int): Boolean = false
            override fun isFetched(index: Int): Boolean = true
        }
    }
}