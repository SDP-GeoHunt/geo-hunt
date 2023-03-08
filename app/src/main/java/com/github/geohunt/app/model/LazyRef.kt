package com.github.geohunt.app.model

import androidx.databinding.Observable
import java.util.concurrent.CompletableFuture

/**
 * Interface used to represent indirection within the database in order to ease the process of
 * loading object on a need-to-know basis
 */
interface LazyRef<T> : Observable {

    /**
     * Identify the current resource within the database.
     *
     * Notice that this `id` must not change during the lifetime of the reference object
     */
    val id : String

    /**
     * The value of the underlying referenced object, or null if it is yet to be loaded
     */
    val value : T?

    /**
     * Value that defines whether or not the current object has been loaded
     */
    val isLoaded : Boolean
        get() = value != null

    /**
     * Fetch the corresponding value, returns a completable future completed once the task is done
     */
    fun fetch() : CompletableFuture<T>
}
