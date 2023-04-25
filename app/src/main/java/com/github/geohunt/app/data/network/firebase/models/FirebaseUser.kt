package com.github.geohunt.app.data.network.firebase.models

import androidx.annotation.IntRange

/**
 * Firebase JSON representation of a GeoHunt user.
 *
 * As users are frequently fetched by various sources, there should be a minimal number of fields
 * stored directly on the user. Specifically, only fields that are of interest to everyone should
 * be stored there. This means that e.g. list of followers, posts and claims should be **denormalized**
 * to avoid being fetched unnecessarily.
 */
data class FirebaseUser(
    val id: String,
    val displayName: String?,
    val profilePictureUrl: String
) {
    /** Default constructor to comply with [com.google.firebase.database.DataSnapshot.getValue]'s requirements */
    constructor(): this(
        id = "",
        displayName = null,
        profilePictureUrl = ""
    )
}
