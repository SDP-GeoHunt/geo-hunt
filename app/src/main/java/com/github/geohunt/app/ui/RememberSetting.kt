package com.github.geohunt.app.ui

import androidx.compose.runtime.*
import com.github.geohunt.app.settings.Setting

@Composable
fun <T> rememberSetting(s: Setting<T>, readOnly: Boolean = true): MutableState<T> {
    val state = remember { mutableStateOf(s.defaultValue) }
    var isCollected by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        s.flow.collect { state.value = it }
    }

    if (!readOnly) {
        LaunchedEffect(state.value) {
            if (isCollected) s.setter(state.value)
            else isCollected = true
        }
    }

    return state
}