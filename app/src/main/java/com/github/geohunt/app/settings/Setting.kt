package com.github.geohunt.app.settings

import kotlinx.coroutines.flow.Flow

data class Setting<T>(val flow: Flow<T>, val defaultValue: T, val setter: suspend (T) -> Unit)
