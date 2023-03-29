package com.github.geohunt.app.model.database.api

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef

/**
 * Define the profile information of a user as stored in the database
 */
interface User {
    val uid: String

    var displayName: String?

    val name: String
        get() = displayName ?: ("@" + uid)

    val profilePicture: LazyRef<Bitmap>

    val challenges: List<LazyRef<Challenge>>

    val hunts: List<LazyRef<Challenge>>

    /**
     * The number of users that follow this user.
     */
    val followers: Int

    /**
     * For a given UID, returns true if and only if this user follows the user with the given UID.
     *
     * By default, a user follows nobody, hence it returns false on all keys.
     */
    val follows: Map<String, Boolean>

    var score: Number
}
