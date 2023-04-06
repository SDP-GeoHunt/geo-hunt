package com.github.geohunt.app.utility

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.tasks.await

/**
 * Converts the given [DataSnapshot] from Firebase to a map.
 *
 * @return The map, where the keys present in the map are mapped to their corresponding Firebase values.
 */
inline fun <reified T> DataSnapshot.toMap(): Map<String, T>
{
    val map = mutableMapOf<String, T>()
    for (child in this.children) {
        if (child.value is T) {
            map[child.key!!] = child.value as T
        }
    }
    return map
}

/**
 * Queries this [Query], awaits the result and casts it to the given type.
 *
 * @return The result of type [T]
 */
suspend inline fun <reified T> Query.queryAs(): T? = this.get().await().getValue<T>()