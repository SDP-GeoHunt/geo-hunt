package com.github.geohunt.app.ui.utils.pagination

import kotlinx.coroutines.flow.StateFlow

/**
 * Represents a scrolling paged list, where elements are fetched on demand.
 */
interface PagedList<T> {
    /**
     * Returns true if the element index is correct.
     *
     * The interpretation of what a "valid index" is may vary between implementations, and thus is
     * left to subclasses implementing this interface.
     */
    fun isCorrectIndex(index: Int): Boolean

    /**
     * Throws [IndexOutOfBoundsException] if [isCorrectIndex] is false
     */
    fun requireCorrectIndex(index: Int) {
        if (!isCorrectIndex(index)) {
            throw IndexOutOfBoundsException()
        }
    }

    /**
     * Returns the current state of the element at the given index.
     *
     * This method has no side-effects, it does not [fetch] the element.
     */
    fun getState(index: Int): StateFlow<T?>

    /**
     * Fetch the element at the given index.
     */
    fun fetch(index: Int)

    /**
     * Fetches the element at the given index if if is not fetched yet.
     *
     * This method doesn't check that the index is correct to avoid prefetching edge cases.
     */
    fun fetchIfNecessary(index: Int) {
        if (isCorrectIndex(index) && !isFetched(index) && !isFetching(index)) {
            fetch(index)
        }
    }

    /**
     * Return true if we are currently fetching the given index.
     */
    fun isFetching(index: Int): Boolean

    /**
     * Returns true if the element at the given index was fetched.
     */
    fun isFetched(index: Int): Boolean

    /**
     * Returns the state of the element at the given index, and fetches it asynchronously if the
     * element was not previously fetched.
     */
    fun get(index: Int): StateFlow<T?> {
        if (!isCorrectIndex(index)) {
            throw IndexOutOfBoundsException()
        }

        fetchIfNecessary(index)
        return getState(index)
    }
}
