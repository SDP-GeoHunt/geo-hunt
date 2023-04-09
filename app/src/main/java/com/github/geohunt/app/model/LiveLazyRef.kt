package com.github.geohunt.app.model


abstract class LiveLazyRef<T>: BaseLazyRef<T>() {
    /**
     * Starts listening the database for changes.
     */
    abstract fun addListener(callback: (T) -> Any?): LiveLazyRefListener
}

/**
 * Represents a listener.
 */
abstract class LiveLazyRefListener {
    /**
     * Stops listening.
     */
    abstract fun stop()
}