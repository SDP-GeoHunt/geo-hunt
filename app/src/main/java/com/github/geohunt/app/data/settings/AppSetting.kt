package com.github.geohunt.app.data.settings

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * This represents an app settings for the view models.
 */
data class AppSetting<T>(val flow: Flow<T>, val defaultValue: T, val setter: suspend (T) -> Unit) {
    /**
     * Creates a new Mutable State flow that will reflect every change of the flow.
     */
    fun toOneWayMutableStateFlow(coroutineScope: CoroutineScope): MutableStateFlow<T> {
        val msf = MutableStateFlow(defaultValue)
        coroutineScope.launch {
            flow.collect { msf.value = it }
        }
        return msf
    }
}