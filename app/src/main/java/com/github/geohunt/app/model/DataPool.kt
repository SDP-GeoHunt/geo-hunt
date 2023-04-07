package com.github.geohunt.app.model

import android.util.Log

/**
 * A DataPool is a container for instances of type T that can be retrieved using a key of type K.
 * If an instance with the given key already exists in the pool, it is returned. Otherwise, a new
 * instance is created using the factory function and added to the pool.
 * 
 * @param factory the function used to create new instances of type T
 * @param K the type of the keys used to identify instances in the pool
 * @param T the type of the instances stored in the pool
 */
class DataPool<K, T>(private val factory: (K) -> T) {
    // HashMap to store instances of type T with their keys of type K
    private val map = HashMap<K, T>()

    /**
     * Retrieve an instance of type T for the given key. If an instance with the given key
     * already exists in the pool, return it. Otherwise, create a new instance using the
     * factory function and add it to the pool.
     *
     * @param key the key to identify the instance
     * @return the instance of type T
     */
    fun get(key: K) : T {
        map[key]?.let { return it }
        val newInstance = factory(key)
        map[key] = newInstance
        return newInstance
    }

    /**
     * Register a specific key/value pair in the database. Notice that this function may overwrite
     * the entry if the key is already present in the current instance
     *
     * @param key the key to be registered in the [DataPool]
     * @param value the associated value to be register in the [DataPool]
     */
    fun register(key: K, value: T) {
        if (map.containsKey(key)) {
            Log.w("GeoHunt", "Register an object more than once in the DataPool with key $key")
        }
        map[key] = value
    }
}