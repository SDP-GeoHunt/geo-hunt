package com.github.geohunt.app.model

import com.google.android.gms.tasks.Task


abstract class LiveLazyRef<T>: BaseLazyRef<T>() {
    /**
     * Starts listening the database for changes.
     */
    abstract fun addListener(callback: (T) -> Any?): LiveLazyRefListener

    companion object {
        /**
         * Creates a LiveLazyRef view for a LazyRef. Every listener will never be triggered.
         */
        fun <T> fromLazyRef(l: LazyRef<T>): LiveLazyRef<T> {
            return object: LiveLazyRef<T>() {
                override fun addListener(callback: (T) -> Any?): LiveLazyRefListener {
                    return object: LiveLazyRefListener() {
                        override fun stop() { }
                    }
                }

                override fun fetchValue(): Task<T> {
                    return l.fetch()
                }

                override val id: String
                    get() = l.id


            }
        }
    }
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