package com.github.geohunt.app.settings

import kotlinx.serialization.Serializable

enum class ThemeSetting {
    SYSTEM, LIGHT, DARK
}

@Serializable
data class AppSettings(
    val theme: ThemeSetting = ThemeSetting.SYSTEM
)