package com.github.geohunt.app.model

class DataPool<K, T>(private val factory: (K) -> T) {
    // HashMap to store instances of type T with their keys of type K
    private val map = HashMap<K, T>()

    /**
     * Retrieve an instance of type T for the given key. If an instance with the given key
     * already exists in the pool, return it. Otherwise, create a new instance using the
     * factory function and add it to the pool.
     * @param key the key to identify the instance
     * @return the instance of type T
     */
    fun get(key: K) : T {
        map[key]?.let { return it }
        val newInstance = factory(key)
        map[key] = newInstance
        return newInstance
    }
}