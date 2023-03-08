package com.github.geohunt.app.model

import androidx.databinding.BaseObservable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * A Base abstract class for [LazyRef] to ease the process of implementing new [LazyRef]
 */
abstract class BaseLazyRef<T> : LazyRef<T>, BaseObservable() {
    @Volatile
    override var value: T ?= null
        set(value) {
            field = value
            notifyChange()
        }

    @Volatile
    private var completableFuture : CompletableFuture<T> ?= null
    private val lock = ReentrantLock()

    override fun fetch(): CompletableFuture<T> {
        if (value != null) {
            return CompletableFuture.completedFuture(value)
        }

        return lock.withLock {
            if (completableFuture != null) {
                completableFuture!!
            }
            else {
                val future = fetchValue().thenApply {
                    value = it
                    it
                }
                completableFuture = future
                future
            }
        }
    }

    /**
     * Function used to fetch the value the first time ever
     */
    protected abstract fun fetchValue() : CompletableFuture<T>
}