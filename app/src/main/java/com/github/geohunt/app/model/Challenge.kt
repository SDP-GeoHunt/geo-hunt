package com.github.geohunt.app.model

import com.github.geohunt.app.model.database.api.Location
import java.time.LocalDateTime

/**
 * A GeoHunt challenge is a competition created by a user, which dares other users to find the
 * location and post a claim in exchange for points.
 *
 * A challenge contains a thumbnail photo describing the target location, a publication date,
 * an approximate ("coarse") location, a difficulty level, and an optional expiration date.
 *
 * In the future, additional fields may be added, such as e.g. a visibility level. Prefer then using
 * named arguments over positional when constructing a [Challenge] instance.
 */
data class Challenge(
    val id: String,
    val authorId: String,

    val photoUrl: String,

    val location: Location,
    val publishedDate: LocalDateTime,
    val expirationDate: LocalDateTime?,
    val difficulty: Difficulty,
    val description: String?
) {
    enum class Difficulty {
        EASY,
        MEDIUM,
        HARD
    }
}
