package com.github.geohunt.app.model.api

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents the JSON response from the Bored API.
 */
@Entity
data class BoredActivity(
    @ColumnInfo(name = "activity") val activity: String,
    val type: String,
    val participants: Int,
    val price: Double,
    val link: String,
    @PrimaryKey val key: String,
    val accessibility: Double
)