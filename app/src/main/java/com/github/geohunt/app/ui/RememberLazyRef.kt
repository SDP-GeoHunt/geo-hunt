package com.github.geohunt.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
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
    val value = remember { mutableStateOf<T?>(null) }
    remember {
        val ref = lazyRef()
        ref.fetch()
        ref.addOnPropertyChangedCallback(object: OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                value.value = ref.value
            }
        })
        value.value = ref.value
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
        ref.addOnPropertyChangedCallback(object: OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                value.value = ref.value ?: default
            }
        })
        value.value = ref.value ?: default
        ref
    }
    return value
}
