package com.github.geohunt.app.model.database.api

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import java.time.LocalDateTime

/**
 * Interface defining the principal attributes for any challenge
 */
interface Challenge
{
    /**
     * The string identifier of this specific challenge
     */
    val cid: String

    /**
     * Lazy reference of the user that created this challenge
     */
    val author: LazyRef<User>

    /**
     * Date of the publication of this challenge
     */
    val publishedDate: LocalDateTime

    /**
     * Date at which this challenge will be expired
     */
    val expirationDate: LocalDateTime?

    /**
     * Lazy reference representing the thumbnail, that is the photo of taken at the location
     * where the challenge was submitted
     */
    val thumbnail: LazyRef<Bitmap>

    /**
     * An approximated location where the challenge was published
     */
    val coarseLocation: Location

    /**
     * The true position of the challenge, in theory should be hidden from the all of the users
     * but the author
     */
    val correctLocation: Location

    /**
     * A list of reference to all of the claims from users
     */
    val claims: List<LazyRef<Claim>>

    /**
     * A list of references to all of the likes from users
     */
    val likes: List<LazyRef<User>>

    /**
     * The number of likes for this challenge
     */
    var nbLikes: Int
}
