package com.github.geohunt.app.model.database.api

import com.github.geohunt.app.model.LazyRef
import java.time.LocalDateTime

/**
 * Establishes a claim-type relationship between a user and a challenge
 */
interface Claim {
    /**
     * Unique identifier of this specific claim
     */
    val id: String

    /**
     * Unique identifier of the challenge that was claimed by the corresponding user
     */
    val cid: LazyRef<Challenge>

    /**
     * Unique identifier of the user that claimed the corresponding challenge
     */
    val uid: String

    /**
     * Time at which the user claimed the challenge
     */
    val time: LocalDateTime

    /**
     * Location at which the user claimed the challenge. This is used to compute the score the user
     * obtained from the challenge
     */
    val location: Location
}