package com.github.geohunt.app.model.database.api

import java.time.LocalDateTime

interface Challenge
{
    /**
     * The challenge's id.
     */
    val cid: String

    /**
     * The author's id
     */
    val uid: String

    /**
     * Publication date
     */
    val published: LocalDateTime

    /**
     * Expiration date
     */
    val expirationDate: LocalDateTime?

    val thumbnail: PictureImage

    /**
     * The approximate location of the challenge
     */
    val coarseLocation: Location

    /**
     * The true position of the challenge
     */
    val correctLocation: Location

    val claims: List<String>
}

