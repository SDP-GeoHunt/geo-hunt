package com.github.geohunt.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import com.github.geohunt.app.model.LazyRef

/**
 * Extension of the remember API in order to observe the state of a lazy reference, notice that this
 * function will automatically perform the fetch for the lazy reference
 */
@Composable
fun <T> rememberLazyRef(lazyRef: () -> LazyRef<T>): MutableState<T?> {
    val value = remember { mutableStateOf<T?>(null) }
    val reference = remember {
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
 * Extension of the remember API in order to observe the state of a lazy reference, notice that this
 * function will automatically perform the fetch for the lazy reference. The value prior to the update
 * will be `default`
 *
 * @param default the value prior to the update
 */
@Composable
fun <T> rememberLazyRef(default: T, lazyRef: () -> LazyRef<T>) : MutableState<T> {
    val value = remember { mutableStateOf<T>(default) }
    val reference = remember {
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
