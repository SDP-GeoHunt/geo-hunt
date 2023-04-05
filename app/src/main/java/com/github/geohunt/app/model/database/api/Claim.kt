package com.github.geohunt.app.model.database.api

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import java.time.LocalDateTime

/**
 * Establishes a claim-type relationship between a user and a challenge
 */
interface Claim {
    /**
     * String that uniquely identify this claim within the database
     */
    val id: String

    /**
     * Reference to the challenge claimed by this instance
     */
    val challenge: LazyRef<Challenge>

    /**
     * Reference to the image of the claimed image
     */
    val image: LazyRef<Bitmap>

    /**
     * Reference to the user
     */
    val user: LazyRef<User>

    /**
     * Time at which the user claimed the challenge
     */
    val time: LocalDateTime

    /**
     * Distance between the actual location expressed in meter and the submitted one.
     */
    val distance : Long

    /**
     * Number of points awarded for this specific claim
     */
    val awardedPoints : Long

    /**
     * Location at which the user claimed the challenge. This is used to compute the score the user
     * obtained from the challenge
     */
    val location: Location
}