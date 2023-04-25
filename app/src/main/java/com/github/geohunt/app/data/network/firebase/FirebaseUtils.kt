package com.github.geohunt.app.data.network.firebase

import com.google.firebase.database.DataSnapshot

/**
 * Converts a [Map]-like structure to a list, where keys are present in the list if and only if
 * their corresponding value is true.
 */
fun DataSnapshot.toList(): List<String> =
    @Suppress("UNCHECKED_CAST")
    (value as? Map<String, Boolean> ?: emptyMap()).mapNotNull { (key, value) -> key.takeIf { value } }
