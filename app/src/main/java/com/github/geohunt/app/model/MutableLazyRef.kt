package com.github.geohunt.app.model

import java.util.concurrent.CompletableFuture

/**
 * A mutable version of the [LazyRef] interface, representing a lazily-loaded object that can
 * be modified
 *
 * @param T the type of object being loaded lazily
 */
interface MutableLazyRef<T> : LazyRef<T> {
    /**
     * Sets the value of the corresponding reference
     *
     * @param value the new value of the property
     */
    fun set(value: T) : CompletableFuture<Void>
}
