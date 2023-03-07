package com.github.geohunt.app.utility

class DataPool<K, T>(fn: (K) -> T) {
    private val pool : Map<K, T> = HashMap<K, T>().withDefault(fn)

    operator fun get(key: K) : T {
        return pool.getValue(key)
    }
}
