package com.github.geohunt.app.api

/**
 * Represents the JSON response from the Bored API.
 */
data class BoredActivityData(
    val activity: String,
    val type: String,
    val participants: Int,
    val price: Double,
    val link: String,
    val key: String,
    val accessibility: Double
)