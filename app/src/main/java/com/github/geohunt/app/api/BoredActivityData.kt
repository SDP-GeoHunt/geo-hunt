package com.github.geohunt.app.api

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * Represents the JSON response from the Bored API.
 */
data class BoredActivityData(
    @ColumnInfo(name = "activity") val activity: String,
    val type: String,
    val participants: Int,
    val price: Double,
    val link: String,
    @PrimaryKey val key: String,
    val accessibility: Double
)