package com.github.geohunt.app.utility

import androidx.compose.runtime.*
import com.github.geohunt.app.settings.Setting
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KSuspendFunction1

@Composable
fun <T> rememberSetting(s: Setting<T>, readOnly: Boolean = true): MutableState<T> {
    val state = remember { mutableStateOf(s.defaultValue) }
    var isCollected by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        s.flow.collect { state.value = it; isCollected = true }
    }

    if (!readOnly) {
        LaunchedEffect(state.value) {
            if (isCollected) s.setter(state.value)
        }
    }

    return state
}