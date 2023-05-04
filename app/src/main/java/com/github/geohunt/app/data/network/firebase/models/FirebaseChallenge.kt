package com.github.geohunt.app.data.network.firebase.models

import com.github.geohunt.app.model.Location

/**
 * Firebase JSON representation of a [com.github.geohunt.app.model.Challenge].
 *
 * Claims are stored in a separate table, and can be fetched using the
 * [com.github.geohunt.app.data.repository.ChallengeRepository.getClaims] method.
 *
 * Dates are encoded in ISO 8601, UTC time.
 */
data class FirebaseChallenge(
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
        authorId = "",
        photoUrl = "",
        location = Location(0.0, 0.0),
        publishedDate = "",
        expirationDate = null,
        difficulty = "",
        description = null
    )
}
