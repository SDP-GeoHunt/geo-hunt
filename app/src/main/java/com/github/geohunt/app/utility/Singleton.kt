package com.github.geohunt.app.utility

/**
 * A generic singleton class that allows for getting, setting, and mocking its value.
 *
 * @param default The default value to use when a new instance is created.
 * @param T The type of the value held by the singleton.
 */
class Singleton<T>(private val default: T) {
    // The current value of the singleton
    private var value : T = default

    /**
     * Gets the current value of the singleton.
     * @return The current value of the singleton.
     */
    fun get() : T {
        return value
    }

    /**
     * Sets the value of the singleton to the given value.
     * @param newValue The new value to set for the singleton.
     */
    fun set(newValue : T?) {
        value = newValue ?: default
    }

    /**
     * Temporarily sets the value of the singleton to the given value for mocking purposes.
     * @param newValue The value to temporarily set for the singleton.
     * @return An AutoCloseable that will restore the original value when closed.
     */
    fun mocked(newValue: T?) : AutoCloseable {
        val previousValue = value
        set(newValue)
        return AutoCloseable { set(previousValue) }
    }
}
