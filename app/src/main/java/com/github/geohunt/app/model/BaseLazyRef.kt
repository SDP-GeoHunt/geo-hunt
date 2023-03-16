package com.github.geohunt.app.model

import androidx.databinding.BaseObservable
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Base class for implementing the `LazyRef` interface. This class handles caching and loading of data
 * on a need-to-know basis.
 *
 * @param T The type of the data being referenced.
 */
abstract class BaseLazyRef<T> : LazyRef<T>, BaseObservable() {
    /** The cached value of the referenced data. */
    @Volatile
    override var value: T ?= null
        set(value) {
            field = value
            notifyChange()
        }

    /** The task responsible for fetching the referenced data. */
    @Volatile
    private var task : Task<T> ?= null

    /** A lock used to ensure thread-safety while accessing the task. */
    private val lock = ReentrantLock()

    /**
     * Fetches the referenced data if it has not yet been loaded. Otherwise, returns the cached value.
     *
     * @return A task representing the loading of the referenced data.
     */
    override fun fetch(): Task<T> {
        if (value != null) {
            return Tasks.forResult(value)
        }

        return lock.withLock {
            if (task != null) {
                task!!
            }
            else {
                task = fetchValue().addOnSuccessListener {
                    value = it
                }
                task!!
            }
        }
    }

    /**
     * Fetches the initial value of the referenced data. This method should be implemented in subclasses.
     * Notice that it may only be called once
     *
     * @return A task representing the loading of the referenced data.
     */
    protected abstract fun fetchValue() : Task<T>
}