package com.github.geohunt.app.data.network.firebase.models

import com.github.geohunt.app.model.database.api.Location

internal data class FirebaseClaim(
    var uid: String? = null,
    var time: String? = null,
    var cid: String? = null,
    var photoUrl: String? = null,
    var location: Location? = null,
    var distance: Long = 0,
    var awardedPoints: Long = 0
)

