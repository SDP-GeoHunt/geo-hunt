package com.github.geohunt.app.model

import java.util.concurrent.CompletableFuture

/**
 * Interface used to represent indirection within the database in order to ease the process of
 * loading object on a need-to-know basis
 */
interface LazyRef<T> {

    /**
     * Identifier uniquely identifying the current resource in the database and local storage. This may
     * not change for a given reference
     */
    val id : String

    /**
     * The value of the reference if loaded otherwise null
     */
    val value : T?

    /**
     * Check whether or not the current object as been loaded
     */
    val isLoaded : Boolean
        get() = value != null

    /**
     * Load corresponding value
     */
    fun load() : CompletableFuture<T>
}
