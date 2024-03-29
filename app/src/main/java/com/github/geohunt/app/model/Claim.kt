package com.github.geohunt.app.model

import androidx.annotation.IntRange
import java.time.LocalDateTime

/**
 * A claim is a user submission to a challenge created by another user. The user is prompted to
 * take a photo similar to the original challenge thumbnail to verify the claim.
 *
 * When claiming, the distance in meters from the original challenge target is used to compute the
 * number of awarded points. See [com.github.geohunt.app.model.points.PointCalculator] for more
 * details on the point calculations.
 */
data class Claim(
    val id: String,
    val parentChallengeId: String,
    val claimerId: String, // Either userId or teamId depending on the context
    val photoUrl: String,

    val claimDate: LocalDateTime,

    @IntRange(from = 0) val distance: Long,
    @IntRange(from = 0) val awardedPoints: Long
)