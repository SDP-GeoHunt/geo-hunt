package com.github.geohunt.app.model.database.api

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef

/**
 * Define the profile information of a user as stored in the database
 */
interface User {
    /**
     * A string that uniquely identify this specific user in the database
     */
    val uid: String

    /**
     * Name to be displayed to the user if non null, notice that users should
     * prefer using [name] to [displayName]
     */
    val displayName: String?

    /**
     * Name of the user, a more general approaches than [displayName] because handle
     * the case where the user did not specify his own name
     */
    val name: String
        get() = displayName ?: "@$uid"

    /**
     * Holds a lazy-reference to a profile picture that can be loaded on a need-to-know basis
     */
    val profilePicture: LazyRef<Bitmap>?
    /**
     * Hash of the profile picture
     */
    val profilePictureHash: Int?

    /**
     * List of challenges the user had published
     */
    val challenges: List<LazyRef<Challenge>>

    /**
     * List of hunts the user had published
     */
    val hunts: List<LazyRef<Challenge>>

    /**
     * Number of followers this user has
     */
    val numberOfFollowers: Int

    /**
     * List of all of the user that the current user is following,
     */
    val follows: List<LazyRef<User>>

    /**
     * Current score of the user
     */
    val score: Long

    /**
     * Check whether or not the current user is the special Point-Of-Interest user. For more details
     * refer to [Database.getPOIUserID]
     */
    val isPOIUser : Boolean

    /**
     * The list of challenges liked by this user
     */
    var likes: List<LazyRef<Challenge>>

    /**
     * Visibility of user's profile
     */
    val profileVisibility: ProfileVisibility
}
