package com.github.geohunt.app.mocks

import com.github.geohunt.app.model.Claim
import java.time.LocalDateTime

fun MockClaim(
        id: String = "1",
        parentChallengeId: String = "1",
        claimerId: String = "1",
        photoUrl: String = "",
        claimDate: LocalDateTime = LocalDateTime.now(),
        distance: Long = 0L,
        awardedPoints: Long = 0L
) = Claim(
        id = id,
        parentChallengeId = parentChallengeId,
        claimerId = claimerId,
        photoUrl = photoUrl,
        claimDate = claimDate,
        distance = distance,
        awardedPoints = awardedPoints
)