package com.github.geohunt.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.em
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
    var refId by remember { mutableStateOf("???")  }
    var result by remember { mutableStateOf<Result<T>?>(null) }

    // Fetch the current LazyRef<T> and register callbacks in case of not yet loaded
    LaunchedEffect(lazyRef) {
        val ref = lazyRef()
        refId = ref.id
        if (ref.value != null) {
            result = Result.success(ref.value!!)
        }
        ref.fetch()
            .addOnSuccessListener(currentActivity) {
                result = Result.success(it)
            }
            .addOnFailureListener(currentActivity) {
                result = Result.failure(it)
            }
            .addOnCanceledListener(currentActivity) {
                result = Result.failure(CancellationException())
            }
    }

    // If the result is still loading
    if (result == null) {
        CircularProgressIndicator(
            modifier = modifier
                .testTag("circular-progress-indicator")
        )
    }

    // If the result has been loaded or the operation failed
    else
    {
        val value = result!!

        // In case of failure launched the onFailure callback
        if (value.isFailure) {
            LaunchedEffect(lazyRef, value.exceptionOrNull()!!) {
                onFailure(value.exceptionOrNull()!!)
            }

            Column(modifier = modifier) {
                Text(
                    modifier = modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 4.em,
                    text = "An exception has occurred",
                    color = MaterialTheme.colors.error
                )

                Text(
                    modifier = modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 2.em,
                    text = "failed to fetch reference @$refId",
                    color = MaterialTheme.colors.error
                )
            }
        }

        // Otherwise simply use provided function to compose
        else {
            renderer(value.getOrNull()!!)
        }
    }
}

