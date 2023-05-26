package com.github.geohunt.app.ui.components.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import com.github.geohunt.app.ui.components.CircleLoadingAnimation

@Composable
fun <T> AwaitNullable(
    state: State<T?>,
    modifier: Modifier = Modifier,
    callback: @Composable (T) -> Unit
) {
    if (state.value != null) {
        callback(state.value!!)
    }
    else {
        CircleLoadingAnimation(
            modifier = modifier
        )
    }
}