package com.github.geohunt.app.model

import com.github.geohunt.app.utility.toCompletableFuture
import com.google.android.gms.tasks.Task

/**
 * Interface used to represent indirection within the database in order to ease the process of
 * loading object on a need-to-know basis
 *
 * @param T the type of parameter being referenced
 */
interface LazyRef<T> : androidx.databinding.Observable {

    /**
     * Identifies the current resource within the database.
     *
     * Notice that this `id` must not change during the lifetime of the reference object.
     */
    val id : String

    /**
     * The value of the underlying referenced object, or null if it is yet to be loaded.
     */
    val value : T?

    /**
     * Value that defines whether or not the current object has been loaded
     */
    val isLoaded : Boolean
        get() = value != null

    /**
     * Fetches the corresponding value on a background thread, returning a Task<T> that completes
     * once the task is done
     *
     * Notice that this returns a `Task<T>` object instead of a `CompletableFuture<T>` as task
     * is a more general object and is not linked to an execution context. Use
     * [Task.toCompletableFuture] to convert the task to a completable future
     */
    fun fetch() : Task<T>
}
