package com.github.geohunt.app.model

import java.time.LocalDateTime

/**
 * Represents a bounty in the application, which is a collection of challenges that people can tackle in teams.
 *
 * @property bid The unique identifier of the bounty.
 * @property adminUid The unique identifier of the administrator who created the bounty.
 * @property startingDate The starting date and time of the bounty.
 * @property expirationDate The expiration date and time of the bounty.
 * @property location The average location of the challenges in the bounty.
 */
data class Bounty(
    val bid: String,
    val adminUid: String,
    val startingDate: LocalDateTime,
    val expirationDate: LocalDateTime,
    val location: Location
)
