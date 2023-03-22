package com.github.geohunt.app.ui

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.utility.findActivity
import kotlin.coroutines.cancellation.CancellationException

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
    }
    return value
}

/**
 * Composable function that loads an object lazily and displays a progress indicator while the
 * object is being loaded. Once the object is loaded, it invokes the specified composable function
 * with the loaded object as its argument.
 *
 * @param lazyRef a function that returns a LazyRef<T> object representing the object to be loaded.
 * @param onFailure a function that is called when an exception occurs while loading the object.
 * @param renderer a composable function that is invoked with the loaded object as its argument.
 * @param T the type of the object being loaded.
 */
@Composable
fun <T> FetchComponent(lazyRef: () -> LazyRef<T>,
                       onFailure: (Throwable) -> Unit = {},
                       modifier: Modifier = Modifier,
                       renderer: @Composable (T) -> Unit)
{
    val currentActivity = LocalContext.current.findActivity()

    val result = remember {
        mutableStateOf<Result<T>?>(null)
    }

    // Fetch the current LazyRef<T> and register callbacks in case of not yet loaded
    LaunchedEffect(lazyRef) {
        val ref = lazyRef()
        if (ref.value != null) {
            result.value = Result.success(ref.value!!)
        }
        ref.fetch()
            .addOnSuccessListener(currentActivity) {
                result.value = Result.success(it)
            }
            .addOnFailureListener(currentActivity) {
                result.value = Result.failure(it)
            }
            .addOnCanceledListener(currentActivity) {
                result.value = Result.failure(CancellationException())
            }
    }

    // If the result is still loading
    if (result.value == null) {
        CircularProgressIndicator(
            modifier = modifier
        )
    }

    // If the result has been loaded or the operation failed
    else
    {
        val value = result.value!!

        // In case of failure launched the onFailure callback
        if (value.isFailure) {
            LaunchedEffect(lazyRef, value.exceptionOrNull()!!) {
                onFailure(value.exceptionOrNull()!!)
            }

            Text(
                modifier = modifier,
                text = "An exception has occurred",
                color = MaterialTheme.colors.error
            )
        }

        // Otherwise simply use provided function to compose
        else {
            renderer(value.getOrNull()!!)
        }
    }
}

