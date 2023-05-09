package com.github.geohunt.app.data.network.firebase.models

import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.utility.DateUtils

data class FirebaseClaim(
    var uid: String? = null,
    var time: String? = null,
    var cid: String? = null,
    var photoUrl: String? = null,
    var location: Location? = null,
    var distance: Long = 0,
    var awardedPoints: Long = 0
)

internal fun FirebaseClaim.asExternalModel(id: String): Claim = Claim(
    id = id,
    parentChallengeId = cid!!,
    claimerId = uid!!,
    claimDate = DateUtils.localFromUtcIso8601(time!!),
    distance = distance,
    awardedPoints = awardedPoints,
    photoUrl = photoUrl!!
)

