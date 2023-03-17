package com.github.geohunt.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.github.geohunt.app.model.LazyRef

/**
 * Composable function that creates a [MutableState] object that remembers a [LazyRef].
 * The lazy reference is initialized with the provided [lazyRef] function, and the value is fetched asynchronously
 * from the reference. The [MutableState] object will be updated with the value from the reference whenever the
 * reference changes, and the new value will be recomposed.
 *
 * @param lazyRef A function that returns a [LazyRef] instance for type [T].
 * @return A [MutableState] object that remembers the current value of the lazy reference.
 */
@Composable
fun <T> rememberLazyRef(lazyRef: () -> LazyRef<T>): MutableState<T?> {
    val value = remember {
        mutableStateOf<T?>(null)
    }
    remember {
        val ref = lazyRef()
        value.value = ref.value
        ref.fetch()
            .addOnSuccessListener {
                value.value = it
            }
        ref
    }
    return value
}

/**
 * Composable function that creates a [MutableState] object that remembers a [LazyRef].
 * The lazy reference is initialized with the provided [lazyRef] function, and the value is fetched asynchronously
 * from the reference. The [MutableState] object will be updated with the value from the reference whenever the
 * reference changes, and the new value will be recomposed.
 *
 * @param default the value that the mutable object should take while awaiting the lazy reference
 * @param lazyRef A function that returns a [LazyRef] instance for type [T].
 * @return A [MutableState] object that remembers the current value of the lazy reference.
 */
@Composable
fun <T> rememberLazyRef(default: T, lazyRef: () -> LazyRef<T>) : MutableState<T> {
    val value = remember { mutableStateOf<T>(default) }
    remember {
        val ref = lazyRef()
        value.value = ref.value ?: default
        ref.fetch()
            .addOnSuccessListener {
                value.value = it
            }
        ref
    }
    return value
}
