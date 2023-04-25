package com.github.geohunt.app.data.network.firebase.models

import com.github.geohunt.app.model.database.api.Location

/**
 * Firebase JSON representation of a [com.github.geohunt.app.data.model.Challenge].
 *
 * Claims are stored in a separate table, and can be fetched using the
 * [com.github.geohunt.app.data.repository.ChallengeRepository.getClaims] method.
 *
 * Dates are encoded in ISO 8601, UTC time.
 */
data class FirebaseChallenge(
    val id: String,
    val authorId: String,

    val photoUrl: String,

    val location: Location,
    val publishedDate: String,
    val expirationDate: String?,
    val difficulty: String,
    val description: String?
) {
    /** Default constructor to comply with [com.google.firebase.database.DataSnapshot.getValue]'s requirements */
    constructor(): this(
        id = "",
        authorId = "",
        photoUrl = "",
        location = Location(0.0, 0.0),
        publishedDate = "",
        expirationDate = "",
        difficulty = "",
        description = null
    )
}
