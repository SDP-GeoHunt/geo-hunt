package com.github.geohunt.app.data.settings

import kotlinx.serialization.Serializable

/**
 * Possible themes for the application
 */
enum class Theme {
    SYSTEM, LIGHT, DARK
}

@Serializable
data class AppSettings(val theme: Theme = Theme.SYSTEM)